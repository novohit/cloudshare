package com.cloudshare.server;

import com.cloudshare.server.file.controller.response.FileListVO;
import com.cloudshare.server.file.converter.FileConverter;
import com.cloudshare.server.file.enums.FileType;
import com.cloudshare.server.file.model.FileDocument;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author novo
 * @since 2023/10/8
 */
@SpringBootTest
@Slf4j
public class MapStructTest {

    @Autowired
    private FileConverter fileConverter;

    @Test
    void test() {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setType(FileType.DIR);
        fileDocument.setSize(10L);
        fileDocument.setPath("/");
        fileDocument.setUserId(0L);
        fileDocument.setId(1L);
        FileListVO repDTO = fileConverter.DO2VO(fileDocument);
        log.info("{}", repDTO);
    }
}
