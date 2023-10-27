package com.cloudshare.storage.aliyun;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.cloudshare.storage.aliyun.config.AliyunOssStorageProperties;
import com.cloudshare.storage.core.AbstractStorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.ReadContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.core.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author novo
 * @since 2023/10/10
 */
@Slf4j
public class AliyunOssStorageEngine extends AbstractStorageEngine {

    private final AliyunOssStorageProperties properties;

    private final OSS ossClient;

    public AliyunOssStorageEngine(AliyunOssStorageProperties properties, OSS ossClient) {
        this.properties = properties;
        this.ossClient = ossClient;
    }

    @Override
    protected void doStore(StoreContext context) throws IOException {
        String fileNameWithSuffix = context.getFileNameWithSuffix();
        String bucketName = properties.getBucketName();
        String path = generateFilePath("cloudshare", fileNameWithSuffix);
        String realPath = "";
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, context.getInputStream());
            putObjectRequest.setProcess("true");
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            if (result.getResponse().getStatusCode() == 200) {
                //realPath = result.getResponse().getUri();
                realPath = String.format("https://%s.%s/%s", bucketName, properties.getEndpoint(), path);
//                log.info("文件上传成功 url:[{}]", realPath);
            } else {
                log.info("文件上传失败:[{}]", result.getResponse());
            }
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message:" + oe.getErrorMessage());
            log.error("Error Code:" + oe.getErrorCode());
            log.error("Request ID:" + oe.getRequestId());
            log.error("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message:" + ce.getMessage());
        }
        context.setRealPath(realPath);
    }

    @Override
    protected void doDelete(DeleteContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreChunkContext context) throws IOException {

    }

    @Override
    protected void doMergeChunk(MergeChunkContext context) throws IOException {

    }

    @Override
    protected void doRead(ReadContext context) throws IOException {

    }

    public void shutdown() {
        ossClient.shutdown();
    }

    @Override
    protected String generateFilePath(String basePath, String fileNameWithSuffix) {
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
                getSuffix(fileNameWithSuffix);
    }
}
