package com.cloudshare.server.file.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author novo
 * @since 2023/10/7
 */
public enum FileType {

    DIR(suffix -> false),

    ARCHIVE(suffix -> {
        List<String> suffixes = Arrays.asList(".rar", ".zip", ".cab", ".iso", ".jar", ".ace", ".7z", ".tar", ".gz", ".arj", ".lah", ".uue", ".bz2", ".z", ".war");
        return suffixes.contains(suffix);
    }),

    EXCEL(suffix -> {
        List<String> suffixes = Arrays.asList(".xlsx", ".xls");
        return suffixes.contains(suffix);
    }),

    WORD(suffix -> {
        List<String> suffixes = Arrays.asList(".docx", ".doc");
        return suffixes.contains(suffix);
    }),

    PPT(suffix -> {
        List<String> suffixes = Arrays.asList(".ppt", ".pptx");
        return suffixes.contains(suffix);
    }),

    PDF(suffix -> {
        List<String> suffixes = Arrays.asList(".pdf");
        return suffixes.contains(suffix);
    }),

    TXT(suffix -> {
        List<String> suffixes = Arrays.asList(".txt");
        return suffixes.contains(suffix);
    }),


    CSV(suffix -> {
        List<String> suffixes = Arrays.asList(".csv");
        return suffixes.contains(suffix);
    }),


    IMAGE(suffix -> {
        List<String> suffixes = Arrays.asList(".bmp", ".gif", ".png", ".ico", ".eps", ".psd", ".tga", ".tiff", ".jpg", ".jpeg");
        return suffixes.contains(suffix);
    }),

    AUDIO(suffix -> {
        List<String> suffixes = Arrays.asList(".mp3", ".mkv", ".mpg", ".rm", ".wma");
        return suffixes.contains(suffix);
    }),


    VIDEO(suffix -> {
        List<String> suffixes = Arrays.asList(".avi", ".3gp", ".mp4", ".flv", ".rmvb", ".mov");
        return suffixes.contains(suffix);
    }),


    SOURCE_CODE(suffix -> {
        List<String> suffixes = Arrays.asList(".java", ".obj", ".h", ".c", ".html", ".net", ".php", ".css", ".js", ".ftl", ".jsp", ".asp");
        return suffixes.contains(suffix);
    }),

    UNKNOWN(Objects::isNull);

    private final Predicate<String> predicate;

    FileType(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    public static FileType suffix2Type(String suffix) {
        return Arrays.stream(FileType.values())
                .filter(fileType -> fileType.predicate.test(suffix))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知的文件类型: " + suffix));
    }

}
