package com.cloudshare.storage.local.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * @author novo
 * @since 2023/10/10
 */
public class LocalStorageUtil {


    /**
     * 将文件的输入流写入到文件中
     * 使用底层的sendfile零拷贝来提高传输效率
     *
     * @param inputStream
     * @param target
     * @param totalSize
     */
    public static void writeStream2File(InputStream inputStream, File target, Long totalSize) throws IOException {
        FileUtil.touch(target);
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
                FileChannel outputChannel = randomAccessFile.getChannel();
                ReadableByteChannel inputChannel = Channels.newChannel(inputStream)
        ) {
            outputChannel.transferFrom(inputChannel, 0L, totalSize);
        }
    }
}
