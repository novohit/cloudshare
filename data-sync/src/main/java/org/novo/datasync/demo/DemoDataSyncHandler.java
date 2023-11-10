package org.novo.datasync.demo;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.novo.datasync.model.CanalBinlogEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author novo
 * @since 2023/11/10
 */
@Component
@Slf4j
public class DemoDataSyncHandler {

    private final UserDocRepository userDocRepository;

    public DemoDataSyncHandler(UserDocRepository userDocRepository) {
        this.userDocRepository = userDocRepository;
    }

    public void syncData(CanalBinlogEvent message) {
        List<Map<String, Object>> data = message.getData();
        for (Map<String, Object> obj : data) {
            UserDoc userDoc = JSON.parseObject(JSON.toJSONString(obj), UserDoc.class);
            String type = message.getType();
            switch (type) {
                case "UPDATE" -> {
                    userDocRepository.save(userDoc);
                    log.info("UPDATE 事件同步 {}", userDoc);
                }
                case "INSERT" -> {
                    userDocRepository.save(userDoc);
                    log.info("INSERT 事件同步 {}", userDoc);
                }
                case "DELETE" -> {
                    userDocRepository.delete(userDoc);
                    log.info("DELETE 事件同步 {}", userDoc);
                }
            }
        }
    }
}
