/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.mail.daos;


import com.lacv.jmagrexs.modules.mail.entities.MailTemplate;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class MailTemplateJpaController extends JPAGenericDao<MailTemplate> implements MailTemplateJpa {

    
}
