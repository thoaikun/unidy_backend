package com.unidy.backend.mapper;

import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.CampaignDto;
import com.unidy.backend.domains.entity.Campaign;
import com.unidy.backend.domains.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring")
public interface DtoMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserInformation(UserDto newData, @MappingTarget User data);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updatePostInformation(PostRequest newData, @MappingTarget PostNode data);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCampaignInformation(CampaignDto newData, @MappingTarget Campaign data);
}
