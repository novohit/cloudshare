package com.cloudshare.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author novo
 * @since 2023/11/1
 */
@Slf4j
public class SnowflakeUtil {

    private static final Snowflake snowflake;

    static {
        try {
            InetAddress host = Inet4Address.getLocalHost();
            String hostAddress = host.getHostAddress();
            // 对机器ip进行哈希取模 哈希可能为负 先取绝对值 workId极小概率会重复
            int workerId = Math.abs((hostAddress.hashCode()) % 1024);
            int highBits = workerId >> 5; // 获取高五位
            int lowBits = workerId & 0x1F;  // 获取低五位
            snowflake = IdUtil.getSnowflake(highBits, lowBits);
            log.info("snowflake worker id:[{}]", workerId);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static long nextId() {
        return snowflake.nextId();
    }
}
