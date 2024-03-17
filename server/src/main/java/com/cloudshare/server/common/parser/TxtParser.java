package com.cloudshare.server.common.parser;

import com.cloudshare.server.enums.FileType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author novo
 * @since 2023/12/3
 */
@Component
public class TxtParser implements TextParser {

    @Override
    public FileType mark() {
        return FileType.TXT;
    }

    @Override
    public String toText(InputStream inputStream) throws IOException {
        StringBuilder res = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            String text = new String(buffer, 0, bytesRead);
            res.append(text);
        }
        return res.toString();
    }
}
