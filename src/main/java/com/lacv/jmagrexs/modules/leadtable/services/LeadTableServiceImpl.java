/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.leadtable.services;


import com.lacv.jmagrexs.modules.leadtable.daos.LeadTableJpa;
import com.lacv.jmagrexs.modules.leadtable.entities.LeadTable;
import com.lacv.jmagrexs.modules.leadtable.mappers.LeadTableMapper;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("leadTableService")
public class LeadTableServiceImpl extends EntityServiceImpl<LeadTable> implements LeadTableService {
    
    @Autowired
    public LeadTableJpa leadTableJpa;
    
    @Autowired
    public LeadTableMapper leadTableMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return leadTableJpa;
    }
    
}
