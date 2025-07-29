package com.huyle.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;

public interface GenericMapper<Request, Entity, Response> {
    Entity toEntity(Request dto);

    Response toDTO(Entity entity);

    List<Response> toDTOs(List<Entity> entities);

    List<Entity> toEntities(List<Request> dtos);

    @BeanMapping(ignoreByDefault = false)
    void updateEntityFromDto(Request dto, @MappingTarget Entity entity);
}