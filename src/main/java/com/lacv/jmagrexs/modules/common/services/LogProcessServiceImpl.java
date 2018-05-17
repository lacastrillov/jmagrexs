/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.common.services;


import com.lacv.jmagrexs.modules.common.daos.LogProcessJpa;
import com.lacv.jmagrexs.modules.common.entities.LogProcess;
import com.lacv.jmagrexs.modules.common.mappers.LogProcessMapper;
import com.lacv.jmagrexs.modules.common.services.LogProcessService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("logProcessService")
public class LogProcessServiceImpl extends EntityServiceImpl<LogProcess> implements LogProcessService {
    
    @Autowired
    public LogProcessJpa logProcessJpa;
    
    @Autowired
    public LogProcessMapper logProcessMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return logProcessJpa;
    }
    
}
