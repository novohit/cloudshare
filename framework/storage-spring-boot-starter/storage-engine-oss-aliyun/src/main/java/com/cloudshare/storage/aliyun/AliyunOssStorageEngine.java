package com.cloudshare.storage.aliyun;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import com.cloudshare.storage.aliyun.config.AliyunOssStorageProperties;
import com.cloudshare.storage.core.AbstractStorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.ReadContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.core.util.UUIDUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author novo
 * @since 2023/10/10
 */
@Slf4j
public class AliyunOssStorageEngine extends AbstractStorageEngine {

    // TODO 改分布式
    private final Map<String, ChunkUploadEntity> uploadEntityMap = new ConcurrentHashMap<>();

    private final AliyunOssStorageProperties properties;

    private final OSS ossClient;

    public AliyunOssStorageEngine(AliyunOssStorageProperties properties, OSS ossClient) {
        this.properties = properties;
        this.ossClient = ossClient;
    }

    @Override
    protected void doStore(StoreContext context) throws IOException {
        String fileName = context.getFileName();
        String path = generateFilePath("cloudshare", fileName);
        String bucketName = properties.getBucketName();
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, path, context.getInputStream());
            request.setProcess("true");
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(Mimetypes.getInstance().getMimetype(fileName));
            request.setMetadata(metadata);

            PutObjectResult result = ossClient.putObject(request);
            if (result.getResponse().getStatusCode() == 200) {
                String realPath = result.getResponse().getUri();
                log.debug("文件上传成功 url:[{}]", realPath);
                context.setRealPath(realPath);
            } else {
                log.error("文件上传失败:[{}]", result.getResponse());
            }
        } catch (OSSException | ClientException e) {
            log.error("阿里云OSS上传异常", e);
            throw new IOException(e);
        }
    }

    @Override
    protected void doDelete(DeleteContext context) throws IOException {

    }

    /**
     * TODO 锁优化
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected synchronized void doStoreChunk(StoreChunkContext context) throws IOException {
        ChunkUploadEntity entity = uploadEntityMap.get(context.getMd5());
        if (entity == null) {
            // 分片上传初始化
            entity = initChunkUpload(context);
            log.info("{}", entity);
        }
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(properties.getBucketName());
        uploadPartRequest.setKey(entity.getObjectKey());
        uploadPartRequest.setUploadId(entity.getUploadId());
        uploadPartRequest.setPartSize(context.getTotalSize());
        uploadPartRequest.setPartNumber(context.getChunkNum());
        uploadPartRequest.setInputStream(context.getInputStream());
        try {
            UploadPartResult result = ossClient.uploadPart(uploadPartRequest);
            PartETag partETag = result.getPartETag();
            ChunkInfo chunkInfo = new ChunkInfo();
            BeanUtils.copyProperties(partETag, chunkInfo);
            chunkInfo.setUploadId(entity.getUploadId());
            chunkInfo.setObjectKey(entity.getObjectKey());
            String info = JSON.toJSONString(chunkInfo);
            context.setChunkInfo(info);
        } catch (OSSException | ClientException | JSONException e) {
            log.error("阿里云OSS上传异常", e);
            throw new IOException(e);
        }
    }

    @Override
    protected void doMergeChunk(MergeChunkContext context) throws IOException {
        List<String> info = context.getChunkInfoList();
        try {
            List<PartETag> partETags = new ArrayList<>();
            String objectKey = null;
            String uploadId = null;
            for (String json : info) {
                ChunkInfo chunkInfo = JSON.parseObject(json, ChunkInfo.class);
                PartETag partETag = new PartETag(
                        chunkInfo.getPartNumber(),
                        chunkInfo.getETag(),
                        chunkInfo.partSize,
                        chunkInfo.getPartCRC()
                );
                partETags.add(partETag);
                objectKey = chunkInfo.getObjectKey();
                uploadId = chunkInfo.getUploadId();
            }
            CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
                    properties.getBucketName(),
                    objectKey,
                    uploadId,
                    partETags);
            request.setProcess("true");

            CompleteMultipartUploadResult result = ossClient.completeMultipartUpload(request);
            // 合并后的 url 带 uploadId 参数
            URL url = new URL(result.getResponse().getUri());
            String realPath = url.getProtocol() + "://" + url.getHost() + url.getPath();
            context.setRealPath(realPath);
        } catch (OSSException | ClientException | JSONException e) {
            log.error("阿里云OSS分片合并异常", e);
            throw new IOException(e);
        }
    }

    @Override
    protected void doRead(ReadContext context) throws IOException {

    }

    public void shutdown() {
        ossClient.shutdown();
    }

    @Override
    protected String generateFilePath(String basePath, String fileName) {
        LocalDate now = LocalDate.now();
        return basePath +
                "/" +
                now.getYear() +
                "/" +
                now.getMonthValue() +
                "/" +
                now.getDayOfMonth() +
                "/" +
                UUIDUtil.shortUUID() +
                getSuffix(fileName);
    }

    /**
     * 分片上传初始化
     */
    private ChunkUploadEntity initChunkUpload(StoreChunkContext context) {
        String fileName = context.getFileName();
        String path = generateFilePath("cloudshare", fileName);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(properties.getBucketName(), path);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(Mimetypes.getInstance().getMimetype(fileName));
        request.setObjectMetadata(metadata);

        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        ChunkUploadEntity entity = new ChunkUploadEntity(uploadId, path);
        uploadEntityMap.put(context.getMd5(), entity);
        return entity;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static final class ChunkUploadEntity {

        /**
         * 分片上传全局唯一id
         */
        private String uploadId;

        /**
         * 上传名称
         */
        private String objectKey;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static final class ChunkInfo {

        /**
         * 分片上传全局唯一id
         */
        private String uploadId;

        /**
         * 上传名称
         */
        private String objectKey;

        private int partNumber;

        private String eTag;

        private long partSize;

        private Long partCRC;
    }
}
