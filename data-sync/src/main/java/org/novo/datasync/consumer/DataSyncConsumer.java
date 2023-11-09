package org.novo.datasync.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author novo
 * @since 2023/11/9
 */
@Component
@Slf4j
public class DataSyncConsumer {

    @KafkaListener(topics = {"cloudshare-datasync"}, groupId = "cloudshare-datasync-001", concurrency = "1")
    public void msgHandler(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("接收成功,topic:[{}],partition:[{}],offset:[{}],res:[{}]", record.topic(), record.partition(), record.offset(), record.value());
        // 手动提交
        ack.acknowledge();
    }
}
