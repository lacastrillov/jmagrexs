/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.WebresourceAuthorizationDto;
import com.lacv.jmagrexs.modules.security.entities.WebresourceAuthorization;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("webresourceAuthorizationMapper")
public class WebresourceAuthorizationMapper extends EntityMapperImpl<WebresourceAuthorization, WebresourceAuthorizationDto> implements EntityMapper<WebresourceAuthorization, WebresourceAuthorizationDto> {

    @Autowired
    AuthorizationMapper authorizationMapper;
    
    @Autowired
    WebResourceMapper webResourceMapper;
    
    
    @Override
    public WebresourceAuthorizationDto entityToDto(WebresourceAuthorization entity) {
        WebresourceAuthorizationDto dto= new WebresourceAuthorizationDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setAuthorization(authorizationMapper.entityToDto(entity.getAuthorization()));
            dto.setWebResource(webResourceMapper.entityToDto(entity.getWebResource()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<WebresourceAuthorizationDto> listEntitiesToListDtos(List<WebresourceAuthorization> entities){
        List<WebresourceAuthorizationDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(WebresourceAuthorization entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
