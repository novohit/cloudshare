package com.cloudshare.server.enums;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author novo
 * @since 2023/10/7
 */
public enum FileType implements Serializable {

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

    MD(suffix -> {
        List<String> suffixes = Arrays.asList(".md");
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
        List<String> suffixes = Arrays.asList(".java", ".go", ".cpp", ".py", ".obj", ".h", ".c", ".html", ".net", ".php", ".css", ".js", ".jsp", ".asp", ".sql");
        return suffixes.contains(suffix);
    }),

    UNKNOWN(suffix -> true);

    private final Predicate<String> predicate;

    FileType(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    public static FileType suffix2Type(String suffix) {
        return Arrays.stream(FileType.values())
                // 先按 ordinal 升序 然后找到第一个匹配的
                .sorted(Comparator.comparingInt(FileType::ordinal))
                .filter(fileType -> fileType.predicate.test(suffix))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知的文件类型: " + suffix));
    }

}
