package org.novo.datasync.canal;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import lombok.extern.slf4j.Slf4j;
import org.novo.datasync.model.CanalBinlogEvent;
import org.novo.datasync.model.FileDoc;
import org.novo.datasync.repository.FileDocRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author novo
 * @since 2023/11/10
 */
@Component
@Slf4j
public class FileDataSyncHandler {

    private final FileDocRepository fileDocRepository;

    public FileDataSyncHandler(FileDocRepository fileDocRepository) {
        this.fileDocRepository = fileDocRepository;
    }

    public void syncData(CanalBinlogEvent message) {
        List<Map<String, Object>> data = message.getData();
        if (!message.getDatabase().equals("cloudshare")) {
            return;
        }
        for (Map<String, Object> obj : data) {
            String jsonString = JSON.toJSONString(obj);
            // TODO fastjson2 升级 https://github.com/alibaba/fastjson2/issues/1615
            FileDoc fileDoc = JSON.parseObject(jsonString, FileDoc.class, JSONReader.Feature.SupportSmartMatch);
            String type = message.getType();
            switch (type) {
                case "UPDATE" -> {
                    fileDocRepository.save(fileDoc);
                    log.info("UPDATE 事件同步 {}", fileDoc);
                }
                case "INSERT" -> {
                    fileDocRepository.save(fileDoc);
                    log.info("INSERT 事件同步 {}", fileDoc);
                }
                case "DELETE" -> {
                    fileDocRepository.delete(fileDoc);
                    log.info("DELETE 事件同步 {}", fileDoc);
                }
            }
        }
    }
}
