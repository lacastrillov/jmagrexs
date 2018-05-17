/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.mail.services;


import com.lacv.jmagrexs.modules.mail.daos.MailTemplateJpa;
import com.lacv.jmagrexs.modules.mail.entities.MailTemplate;
import com.lacv.jmagrexs.modules.mail.mappers.MailTemplateMapper;
import com.lacv.jmagrexs.modules.mail.services.MailTemplateService;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("mailTemplateService")
public class MailTemplateServiceImpl extends EntityServiceImpl<MailTemplate> implements MailTemplateService {
    
    @Autowired
    public MailTemplateJpa mailTemplateJpa;
    
    @Autowired
    public MailTemplateMapper mailTemplateMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return mailTemplateJpa;
    }
    
}
