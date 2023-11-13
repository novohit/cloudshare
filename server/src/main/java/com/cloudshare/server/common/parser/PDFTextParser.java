package com.cloudshare.server.common.parser;

import com.cloudshare.server.common.util.PDFUtil;
import com.cloudshare.server.file.enums.FileType;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author novo
 * @since 2023/11/13
 */
@Component
public class PDFTextParser implements TextParser {

    @Override
    public FileType mark() {
        return FileType.PDF;
    }

    @Override
    public String toText(InputStream inputStream) {
        return PDFUtil.PDF2Text(inputStream);
    }
}
