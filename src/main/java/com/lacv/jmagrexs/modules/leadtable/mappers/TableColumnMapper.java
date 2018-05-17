/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.leadtable.mappers;

import com.lacv.jmagrexs.mapper.EntityMapper;
import com.lacv.jmagrexs.mapper.EntityMapperImpl;
import com.lacv.jmagrexs.modules.leadtable.dtos.TableColumnDto;
import com.lacv.jmagrexs.modules.leadtable.entities.TableColumn;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author lcastrillo
 */
@Component("tableColumnMapper")
public class TableColumnMapper extends EntityMapperImpl<TableColumn, TableColumnDto> implements EntityMapper<TableColumn, TableColumnDto> {

    @Autowired
    LeadTableMapper leadTableMapper;
    
    @Override
    public TableColumnDto entityToDto(TableColumn entity) {
        TableColumnDto dto= new TableColumnDto();
        if(entity!=null){
            dto.setId(entity.getId());
            dto.setColumnAlias(entity.getColumnAlias());
            dto.setColumnOrder(entity.getColumnOrder());
            dto.setColumnSize(entity.getColumnSize());
            dto.setNotNull(entity.getNotNull());
            dto.setDataType(entity.getDataType());
            dto.setDefaultValue(entity.getDefaultValue());
            dto.setFieldType(entity.getFieldType());
            dto.setName(entity.getName());
            dto.setOptions(entity.getOptions());
            dto.setWidth(entity.getWidth());
            dto.setLeadTable(leadTableMapper.entityToDto(entity.getLeadTable()));
        }
        return dto;
    }
    
    /**
     *
     * @param entities
     * @return
     */
    @Override
    public List<TableColumnDto> listEntitiesToListDtos(List<TableColumn> entities){
        List<TableColumnDto> dtos= new ArrayList<>();
        if(entities!=null){
            for(TableColumn entity: entities){
                dtos.add(entityToDto(entity));
            }
        }
        return dtos;
    }
    
}
