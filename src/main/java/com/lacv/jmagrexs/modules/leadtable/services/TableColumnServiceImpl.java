/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.services;


import com.lacv.jmagrexs.modules.leadtable.daos.TableColumnJpa;
import com.lacv.jmagrexs.modules.leadtable.entities.TableColumn;
import com.lacv.jmagrexs.modules.leadtable.mappers.TableColumnMapper;
import com.lacv.jmagrexs.modules.leadtable.services.TableColumnService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("tableColumnService")
public class TableColumnServiceImpl extends EntityServiceImpl<TableColumn> implements TableColumnService {
    
    @Autowired
    public TableColumnJpa tableColumnJpa;
    
    @Autowired
    public TableColumnMapper tableColumnMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return tableColumnJpa;
    }
    
}
