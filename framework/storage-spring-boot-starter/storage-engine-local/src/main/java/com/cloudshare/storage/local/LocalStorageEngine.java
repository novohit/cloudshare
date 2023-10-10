package com.cloudshare.storage.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.cloudshare.storage.core.AbstractStorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.local.util.LocalStorageUtil;
import com.cloudshare.storage.local.config.LocalStorageEngineProperties;

import java.io.File;
import java.io.IOException;

/**
 * @author novo
 * @since 2023/10/10
 */
public class LocalStorageEngine extends AbstractStorageEngine {

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
}
