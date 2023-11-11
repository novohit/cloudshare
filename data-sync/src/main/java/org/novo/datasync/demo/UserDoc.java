package org.novo.datasync.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author novo
 * @since 2023/11/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "user", createIndex = true)
public class UserDoc {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String username;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String password;
}
