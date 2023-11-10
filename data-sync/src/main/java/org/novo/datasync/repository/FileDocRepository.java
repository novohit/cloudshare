package org.novo.datasync.repository;

import org.novo.datasync.model.FileDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author novo
 * @since 2023/11/10
 */
public interface FileDocRepository extends ElasticsearchRepository<FileDoc, Long> {
}
