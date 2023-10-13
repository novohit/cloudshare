package com.cloudshare.storage.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.cloudshare.storage.core.AbstractStorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.local.util.LocalStorageUtil;
import com.cloudshare.storage.local.config.LocalStorageEngineProperties;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author novo
 * @since 2023/10/10
 */
public class LocalStorageEngine extends AbstractStorageEngine {

    // TODO 改分布式
    private final Map<String, String> chunkNameMap = new ConcurrentHashMap<>();

    private final LocalStorageEngineProperties localStorageEngineProperties;

    public LocalStorageEngine(LocalStorageEngineProperties localStorageEngineProperties) {
        this.localStorageEngineProperties = localStorageEngineProperties;
    }

    @Override
    protected void doStore(StoreContext context) throws IOException {
        String basePath = localStorageEngineProperties.getBasePath();
        String fileNameWithSuffix = context.getFileNameWithSuffix();
        String path = generateFilePath(basePath, fileNameWithSuffix);
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
        String prefixPath = chunkNameMap.get(context.getMd5());
        if (!StringUtils.hasText(prefixPath)) {
            String fileNameWithSuffix = context.getFileNameWithSuffix();
            prefixPath = generateFilePath(chunkPath, fileNameWithSuffix);
            chunkNameMap.put(context.getMd5(), prefixPath);
        }
        File file = new File(prefixPath + ".bak." + context.getChunk());
        LocalStorageUtil.writeStream2File(context.getInputStream(), file, context.getTotalSize());
        context.setRealPath(file.getAbsolutePath());
    }
}
