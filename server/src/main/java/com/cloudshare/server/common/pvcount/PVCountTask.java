package com.cloudshare.server.common.pvcount;

import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.link.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author novo
 * @since 2023/11/7
 */
@Component
@Slf4j
public class PVCountTask {

    private final ShortLinkService shortLinkService;

    private final StringRedisTemplate stringRedisTemplate;

    public PVCountTask(ShortLinkService shortLinkService, StringRedisTemplate stringRedisTemplate) {
        this.shortLinkService = shortLinkService;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Scheduled(cron = "0 */5 * * * ?")
    public void dataSync() {
        Set<String> keys = stringRedisTemplate.keys(BizConstant.LINK_PV_PREFIX + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            List<String> strings = keys.stream().toList();
            List<String> values = stringRedisTemplate.opsForValue().multiGet(strings);
            for (int i = 0; i < strings.size(); i++) {
                String key = strings.get(i);
                String code = key.replaceFirst(BizConstant.LINK_PV_PREFIX, "");
                shortLinkService.savePV(code, Long.parseLong(values.get(i)));
            }
        }
    }
}
