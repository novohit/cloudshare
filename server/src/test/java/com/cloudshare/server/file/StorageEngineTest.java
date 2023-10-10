package com.cloudshare.server.file;

import cn.hutool.core.io.FileUtil;
import com.cloudshare.storage.core.StorageEngine;
import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.StoreContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author novo
 * @since 2023/10/10
 */
@SpringBootTest
@Slf4j
public class StorageEngineTest {

    @Autowired
    private StorageEngine storageEngine;

    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "temp";

    @Test
    void storeTest() {
        File file = FileUtil.touch(BASE_PATH + File.separator + "测试文本.txt");
        FileUtil.appendString("hello world!\r\n", file, "UTF-8");

        StoreContext context = new StoreContext();
        context.setFileNameWithSuffix("测试文本.txt");
        context.setTotalSize(file.length());
        try {
            InputStream inputStream = new FileInputStream(file);
            context.setInputStream(inputStream);
            System.out.println(System.getProperty("user.dir"));
            // 调用存储引擎来存储文件
            storageEngine.store(context);
            log.info("{}", context.getRealPath());
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
        }
    }

    @Test
    void deleteTest() {
        DeleteContext context = new DeleteContext();
        List<File> fileList = FileUtil.loopFiles(BASE_PATH);
        List<String> pathList = fileList
                .stream()
                .map(File::getAbsolutePath)
                .toList();
        if (fileList.size() > 2) {
            context.setRealPathList(pathList);
            try {
                storageEngine.delete(context);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
