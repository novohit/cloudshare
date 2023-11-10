package com.cloudshare.server.share.convert;

import com.cloudshare.server.share.controller.response.ShareVO;
import com.cloudshare.server.share.model.Share;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "pv", constant = "0")
    @Mapping(target = "download", constant = "0")
    @Mapping(target = "fileName", expression = "java(share.getFileDocument().getName())")
    @Mapping(target = "url", expression = "java(share.getShortLink().getShortUrl())")
    ShareVO DO2VO(Share share);


    List<ShareVO> DOList2VOList(List<Share> shareList);

}
