/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.security.dtos.UserDto;
import com.lacv.jmagrexs.modules.security.entities.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("userMapper")
public class UserMapper extends EntityMapperImpl<User, UserDto> implements EntityMapper<User, UserDto> {

    
    @Override
    public UserDto entityToDto(User entity) {
        UserDto dto= new UserDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setEmail(entity.getEmail());
            dto.setGender(entity.getGender());
            dto.setLink(entity.getLink());
            dto.setFirstName(entity.getFirstName());
            dto.setLastName(entity.getLastName());
            dto.setIdDocument(entity.getIdDocument());
            dto.setDocumentType(entity.getDocumentType());
            dto.setPhone(entity.getPhone());
            dto.setCell(entity.getCell());
            dto.setAddress(entity.getAddress());
            dto.setCity(entity.getCity());
            dto.setUsername(entity.getUsername());
            dto.setPassword(entity.getPassword());
            dto.setPasswordExpiration(entity.getPasswordExpiration());
            dto.setRegistrationDate(entity.getRegistrationDate());
            dto.setStatus(entity.getStatus());
            dto.setFailedAttempts(entity.getFailedAttempts());
            dto.setLastLogin(entity.getLastLogin());
            dto.setUrlPhoto(entity.getUrlPhoto());
            dto.setBirthday(entity.getBirthday());
            dto.setTokenUser(entity.getTokenUser());
            dto.setVerified(entity.getVerified());
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<UserDto> listEntitiesToListDtos(List<User> entities){
        List<UserDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(User entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
