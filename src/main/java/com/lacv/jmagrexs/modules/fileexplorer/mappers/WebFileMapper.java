/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.fileexplorer.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.fileexplorer.dtos.WebFileDto;
import com.lacv.jmagrexs.modules.fileexplorer.entities.WebFile;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("webFileMapper")
public class WebFileMapper extends EntityMapperImpl<WebFile, WebFileDto> implements EntityMapper<WebFile, WebFileDto> {

    
    @Override
    public WebFileDto entityToDto(WebFile entity) {
        WebFileDto dto= new WebFileDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setAccess(entity.getAccess());
            dto.setAuthor(entity.getAuthor());
            dto.setCreationDate(entity.getCreationDate());
            dto.setIcon(entity.getIcon());
            dto.setLocation(entity.getLocation());
            dto.setPath(entity.getPath());
            dto.setModificationDate(entity.getModificationDate());
            dto.setName(entity.getName());
            dto.setSize(entity.getSize());
            dto.setType(entity.getType());
            dto.setWebFile((WebFileDto) entityToDto(entity.getWebFile()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<WebFileDto> listEntitiesToListDtos(List<WebFile> entities){
        List<WebFileDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(WebFile entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
