package com.cloudshare.storage.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.cloudshare.storage.core.AbstractStorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.ReadContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.local.config.LocalStorageEngineProperties;
import com.cloudshare.storage.local.util.LocalStorageUtil;
import org.springframework.cache.CacheManager;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/10
 */
public class LocalStorageEngine extends AbstractStorageEngine {

    private final LocalStorageEngineProperties localStorageEngineProperties;

    public LocalStorageEngine(LocalStorageEngineProperties localStorageEngineProperties, CacheManager cacheManager) {
        super(cacheManager);
        this.localStorageEngineProperties = localStorageEngineProperties;
    }

    @Override
    protected void doStore(StoreContext context) throws IOException {
        String basePath = localStorageEngineProperties.getBasePath();
        String fileName = context.getFileName();
        String path = generateFilePath(basePath, fileName);
        File file = new File(path);
        LocalStorageUtil.writeStream2File(context.getInputStream(), file, context.getTotalSize());
        context.setRealPath(file.getAbsolutePath());
    }

    @Override
    protected void doDelete(DeleteContext context) throws IOException {
        try {
            for (String path : context.getRealPathList()) {
                FileUtil.del(path);
            }
        } catch (IORuntimeException e) {
            throw new IOException(e);
        }
    }

    protected void doStoreChunk(StoreChunkContext context) throws IOException {
        String chunkPath = localStorageEngineProperties.getChunkPath();
        String prefixPath = getCache().get(context.getMd5(), String.class);
        if (!StringUtils.hasText(prefixPath)) {
            String fileName = context.getFileName();
            prefixPath = generateFilePath(chunkPath, fileName);
            getCache().put(context.getMd5(), prefixPath);
        }
        File file = new File(prefixPath + ".bak." + context.getChunkNum());
        LocalStorageUtil.writeStream2File(context.getInputStream(), file, context.getTotalSize());
        context.setChunkInfo(file.getAbsolutePath());
    }

    protected void doMergeChunk(MergeChunkContext context) throws IOException {
        String basePath = localStorageEngineProperties.getBasePath();
        String fileName = context.getFileName();
        String path = generateFilePath(basePath, fileName);
        File file = new File(path);

        List<String> chunkRealPathList = context.getChunkInfoList();
        // 合并
        LocalStorageUtil.mergeFile(file, chunkRealPathList);
        // TODO 合并后删除分片交给上游定时删除
        context.setRealPath(file.getAbsolutePath());
    }

    protected void doRead(ReadContext context) throws IOException {
        File file = new File(context.getRealPath());
        long position = 0L;
        long size = file.length();
        if (context.getPosition() != 0) position = context.getPosition();
        if (context.getSize() != 0) size = context.getSize();
        LocalStorageUtil.readStreamFromFile(context.getOutputStream(), file, position, size);
    }

    @Override
    public void shutdown() {

    }
}
