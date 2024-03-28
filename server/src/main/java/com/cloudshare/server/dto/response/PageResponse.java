package com.cloudshare.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author novo
 * @since 2024/3/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    long total;

    List<T> list;
}
