package com.cloudshare.server.common.parser;

import com.cloudshare.server.file.enums.FileType;

import java.io.InputStream;

/**
 * @author novo
 * @since 2023/11/13
 */
public interface TextParser {

    /**
     * 执行策略标识
     */
    default FileType mark() {
        return null;
    }

    String toText(InputStream inputStream);
}
