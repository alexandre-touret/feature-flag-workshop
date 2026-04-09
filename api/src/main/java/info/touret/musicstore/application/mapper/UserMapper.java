package info.touret.musicstore.application.mapper;

import info.touret.musicstore.application.data.UserDto;
import info.touret.musicstore.domain.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User toUser(UserDto userDto);
}
