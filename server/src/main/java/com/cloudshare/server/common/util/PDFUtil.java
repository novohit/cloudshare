package com.cloudshare.server.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author novo
 * @since 2023/11/13
 */
@Slf4j
public class PDFUtil {

    public static String PDF2Text(InputStream inputStream) {
        try (PDDocument doc = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            log.info("Text size: {}, characters: \n{}", text.length(), text);
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
