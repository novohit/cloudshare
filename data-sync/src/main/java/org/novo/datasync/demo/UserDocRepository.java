package org.novo.datasync.demo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author novo
 * @since 2023/11/10
 */
public interface UserDocRepository extends ElasticsearchRepository<UserDoc, Long> {
}
