package com.cloudshare.server.link.service;

import com.cloudshare.server.link.model.ShortLink;
import com.cloudshare.server.link.repository.ShortLinkRepository;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author novo
 * @since 2023/11/10
 */
@Service
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Value("${cloudshare.short-link.url}")
    private String domain;

    public ShortLinkService(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }


    public ShortLink findOneByCode(String code) {
        return shortLinkRepository.findOneByCode(code);
    }


    public String create(Long shareId, String url) {
        long murmurHash32 = murmurHash32(url);
        // 62进制转换
        String code62 = encodeToBase62(murmurHash32);
        ShortLink shortLink = new ShortLink();
        shortLink.setCode(code62);
        shortLink.setShortUrl(domain + code62);
        shortLink.setOriginalUrl(url);
        shortLink.setShareId(shareId);
        shortLinkRepository.save(shortLink);
        return shortLink.getShortUrl();
    }

    /**
     * google murmurhash算法
     *
     * @param value
     * @return
     */
    @SuppressWarnings(value = {"all"})
    public static long murmurHash32(String value) {
        long murmurHash32 = Hashing.murmur3_32().hashUnencodedChars(value).padToLong();
        return murmurHash32;
    }


    private String encodeToBase62(long num) {
        // 特判一下 因为CHARS数组可以不按照顺序排列，打乱可以提高安全性
        // 正常情况不会为0
        if (num == 0) {
            return String.valueOf(CHARS.charAt(0));
        }
        // StringBuffer线程安全，StringBuilder线程不安全
        StringBuffer sb = new StringBuffer();
        while (num > 0) {
            int i = (int) (num % 62);
            sb.append(CHARS.charAt(i));
            num = num / 62;
        }

        return sb.reverse().toString();
    }
}
