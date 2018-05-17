/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.mail.daos;


import com.lacv.jmagrexs.modules.mail.entities.Mail;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class MailJpaController extends JPAGenericDao<Mail> implements MailJpa {

    
}
