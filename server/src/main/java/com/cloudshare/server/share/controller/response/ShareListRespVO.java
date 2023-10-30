package com.cloudshare.server.share.controller.response;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/30
 */
public record ShareListRespVO(
        List<ShareVO> data
) {
}
