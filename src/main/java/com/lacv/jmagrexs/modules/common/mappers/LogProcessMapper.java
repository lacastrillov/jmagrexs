/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.common.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.common.dtos.LogProcessDto;
import com.lacv.jmagrexs.modules.common.entities.LogProcess;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("logProcessMapper")
public class LogProcessMapper extends EntityMapperImpl<LogProcess, LogProcessDto> implements EntityMapper<LogProcess, LogProcessDto> {
    
    
    @Override
    public LogProcessDto entityToDto(LogProcess entity) {
        LogProcessDto dto= new LogProcessDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setClientId(entity.getClientId());
            dto.setDataIn(entity.getDataIn());
            dto.setDataOut(entity.getDataOut());
            dto.setOutputDataFormat(entity.getOutputDataFormat());
            dto.setDuration(entity.getDuration());
            dto.setMainProcessRef(entity.getMainProcessRef());
            dto.setMessage(entity.getMessage());
            dto.setProcessName(entity.getProcessName());
            dto.setRecordTime(entity.getRecordTime());
            dto.setRegistrationDate(entity.getRegistrationDate());
            dto.setSuccess(entity.getSuccess());
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<LogProcessDto> listEntitiesToListDtos(List<LogProcess> entities){
        List<LogProcessDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(LogProcess entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
