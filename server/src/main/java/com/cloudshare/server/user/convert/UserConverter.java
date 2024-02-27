package com.cloudshare.server.user.convert;

import com.cloudshare.server.auth.UserContext;
import com.cloudshare.server.user.controller.request.UserInfoRepVO;
import com.cloudshare.server.user.model.User;
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
