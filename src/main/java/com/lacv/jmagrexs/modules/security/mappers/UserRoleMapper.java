/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.UserRoleDto;
import com.lacv.jmagrexs.modules.security.entities.UserRole;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("userRoleMapper")
public class UserRoleMapper extends EntityMapperImpl<UserRole, UserRoleDto> implements EntityMapper<UserRole, UserRoleDto> {

    @Autowired
    UserMapper userMapper;
    
    @Autowired
    RoleMapper roleMapper;
    
    @Override
    public UserRoleDto entityToDto(UserRole entity) {
        UserRoleDto dto= new UserRoleDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setRole(roleMapper.entityToDto(entity.getRole()));
            dto.setUser(userMapper.entityToDto(entity.getUser()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<UserRoleDto> listEntitiesToListDtos(List<UserRole> entities){
        List<UserRoleDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(UserRole entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
