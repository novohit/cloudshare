package com.cloudshare.storage.core;

import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.StoreContext;
import com.cloudshare.storage.core.util.UUIDUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

/**
 * @author novo
 * @since 2023/10/10
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Override
    public void store(StoreContext context) throws IOException {
        Assert.isTrue(StringUtils.hasText(context.getFileNameWithSuffix()), "filename must be not null or empty");
        Assert.notNull(context.getInputStream(), "inputStream must be not null");
        Assert.isTrue(context.getTotalSize() > 0, "totalSize must be > 0");
        doStore(context);
    }

    @Override
    public void delete(DeleteContext context) throws IOException {
        Assert.notNull(context.getRealPathList(), "list must be not null");
        doDelete(context);
    }

    protected abstract void doStore(StoreContext context) throws IOException;

    protected abstract void doDelete(DeleteContext context) throws IOException;

    protected String getSuffix(String fileName) {
        Assert.isTrue(StringUtils.hasText(fileName), "filename must be not null or empty");
        int index = fileName.lastIndexOf(".");
        if (index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index).toLowerCase();
    }

    /**
     * 路径生成逻辑 basePath/2023/10/10/97fe16vu.txt
     *
     * @param basePath
     * @param fileNameWithSuffix
     * @return
     */
    protected String generateFilePath(String basePath, String fileNameWithSuffix) {
        LocalDate now = LocalDate.now();
        return basePath +
                File.separator +
                now.getYear() +
                File.separator +
                now.getMonthValue() +
                File.separator +
                now.getDayOfMonth() +
                File.separator +
                UUIDUtil.shortUUID() +
                getSuffix(fileNameWithSuffix);
    }
}
