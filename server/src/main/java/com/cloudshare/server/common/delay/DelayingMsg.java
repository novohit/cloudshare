package com.cloudshare.server.common.delay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author novo
 * @since 2023/11/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelayingMsg {

    private Long messageId;

    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳+延迟时间
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
