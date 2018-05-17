/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.AuthorizationDto;
import com.lacv.jmagrexs.modules.security.entities.Authorization;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("authorizationMapper")
public class AuthorizationMapper extends EntityMapperImpl<Authorization, AuthorizationDto> implements EntityMapper<Authorization, AuthorizationDto> {

    
    @Override
    public AuthorizationDto entityToDto(Authorization entity) {
        AuthorizationDto dto= new AuthorizationDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setDescription(entity.getDescription());
            dto.setName(entity.getName());
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<AuthorizationDto> listEntitiesToListDtos(List<Authorization> entities){
        List<AuthorizationDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(Authorization entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
