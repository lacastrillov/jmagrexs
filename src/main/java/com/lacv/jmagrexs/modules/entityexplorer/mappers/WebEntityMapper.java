/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.entityexplorer.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.entityexplorer.dtos.WebEntityDto;
import com.lacv.jmagrexs.modules.entityexplorer.entities.WebEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("webEntityMapper")
public class WebEntityMapper extends EntityMapperImpl<WebEntity, WebEntityDto> implements EntityMapper<WebEntity, WebEntityDto> {

    
    @Override
    public WebEntityDto entityToDto(WebEntity entity) {
        WebEntityDto dto= new WebEntityDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setAuthor(entity.getAuthor());
            dto.setCreationDate(entity.getCreationDate());
            dto.setIcon(entity.getIcon());
            dto.setPath(entity.getPath());
            dto.setLocation(entity.getLocation());
            dto.setModificationDate(entity.getModificationDate());
            dto.setName(entity.getName());
            dto.setEntityId(entity.getEntityId());
            dto.setEntityOrder(entity.getEntityOrder());
            dto.setEntityRef(entity.getEntityRef());
            dto.setEntityName(entity.getEntityName());
            dto.setStatus(entity.getStatus());
            dto.setWebEntity((WebEntityDto) entityToDto(entity.getWebEntity()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<WebEntityDto> listEntitiesToListDtos(List<WebEntity> entities){
        List<WebEntityDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(WebEntity entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
