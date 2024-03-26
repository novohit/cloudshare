package com.cloudshare.server.manager.redis;

import java.util.List;

/**
 * @author novo
 * @since 2024/3/26
 */
public interface RedisManager {

    /**
     * 获取最近的历史记录
     *
     * @param key
     * @param size
     * @return
     */
    List<String> getRecentHistory(String key, int size);

    /**
     * 保存历史记录
     *
     * @param key
     * @param keyword
     */
    void saveHistory(String key, String keyword);
}
