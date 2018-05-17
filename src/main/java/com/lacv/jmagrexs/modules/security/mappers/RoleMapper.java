/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.RoleDto;
import com.lacv.jmagrexs.modules.security.entities.Role;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("roleMapper")
public class RoleMapper extends EntityMapperImpl<Role, RoleDto> implements EntityMapper<Role, RoleDto> {

    
    @Override
    public RoleDto entityToDto(Role entity) {
        RoleDto dto= new RoleDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setDescription(entity.getDescription());
            dto.setName(entity.getName());
            dto.setHomePage(entity.getHomePage());
            dto.setPriorityCheck(entity.getPriorityCheck());
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<RoleDto> listEntitiesToListDtos(List<Role> entities){
        List<RoleDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(Role entity: entities){
                dtos.add((RoleDto) entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
