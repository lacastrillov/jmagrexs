/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.RoleAuthorizationDto;
import com.lacv.jmagrexs.modules.security.entities.RoleAuthorization;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("roleAuthorizationMapper")
public class RoleAuthorizationMapper extends EntityMapperImpl<RoleAuthorization, RoleAuthorizationDto> implements EntityMapper<RoleAuthorization, RoleAuthorizationDto> {

    @Autowired
    AuthorizationMapper authorizationMapper;
    
    @Autowired
    RoleMapper roleMapper;
    
    
    @Override
    public RoleAuthorizationDto entityToDto(RoleAuthorization entity) {
        RoleAuthorizationDto dto= new RoleAuthorizationDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setAuthorization(authorizationMapper.entityToDto(entity.getAuthorization()));
            dto.setRole(roleMapper.entityToDto(entity.getRole()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<RoleAuthorizationDto> listEntitiesToListDtos(List<RoleAuthorization> entities){
        List<RoleAuthorizationDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(RoleAuthorization entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
