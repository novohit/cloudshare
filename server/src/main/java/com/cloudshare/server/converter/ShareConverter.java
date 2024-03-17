package com.cloudshare.server.converter;

import com.cloudshare.server.dto.response.ShareVO;
import com.cloudshare.server.model.Share;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "pv", expression = "java(pv)")
    @Mapping(target = "fileName", expression = "java(share.getFileDocument().getName())")
    @Mapping(target = "url", expression = "java(share.getShortLink().getShortUrl())")
    ShareVO DO2VO(Share share, Long pv);


    List<ShareVO> DOList2VOList(List<Share> shareList);

}
