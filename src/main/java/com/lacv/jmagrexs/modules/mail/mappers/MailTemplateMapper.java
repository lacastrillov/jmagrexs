/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.mail.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.mail.dtos.MailTemplateDto;
import com.lacv.jmagrexs.modules.mail.entities.MailTemplate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("mailTemplateMapper")
public class MailTemplateMapper extends EntityMapperImpl<MailTemplate, MailTemplateDto> implements EntityMapper<MailTemplate, MailTemplateDto> {

    
    @Override
    public MailTemplateDto entityToDto(MailTemplate entity) {
        MailTemplateDto dto= new MailTemplateDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setAlias(entity.getAlias());
            dto.setContent(entity.getContent());
            dto.setName(entity.getName());
            dto.setStatus(entity.getStatus());
            dto.setTotalSent(entity.getTotalSent());
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<MailTemplateDto> listEntitiesToListDtos(List <MailTemplate> entities){
        List<MailTemplateDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(MailTemplate entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
