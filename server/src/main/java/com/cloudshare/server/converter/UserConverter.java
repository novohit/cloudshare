package com.cloudshare.server.converter;

import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.dto.requset.UserInfoRepVO;
import com.cloudshare.server.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author novo
 * @since 2023/10/8
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserInfoRepVO DO2VO(User user);

    @Mapping(target = "rootName", constant = "")
    @Mapping(target = "rootId", constant = "0L")
    UserInfoRepVO Context2VO(UserContext user);
}
