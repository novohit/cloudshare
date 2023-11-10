package org.novo.datasync.consumer;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.novo.datasync.canal.FileDataSyncHandler;
import org.novo.datasync.model.CanalBinlogEvent;
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

    private final FileDataSyncHandler dataSyncHandler;

    public DataSyncConsumer(FileDataSyncHandler dataSyncHandler) {
        this.dataSyncHandler = dataSyncHandler;
    }

    @KafkaListener(topics = {"cloudshare-datasync"}, groupId = "cloudshare-datasync-001", concurrency = "1")
    public void msgHandler(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.debug("接收成功,topic:[{}],partition:[{}],offset:[{}],res:[{}]", record.topic(), record.partition(), record.offset(), record.value());
        CanalBinlogEvent message = JSON.parseObject((String) record.value(), CanalBinlogEvent.class);
        dataSyncHandler.syncData(message);
        // 手动提交
        ack.acknowledge();
    }
}
