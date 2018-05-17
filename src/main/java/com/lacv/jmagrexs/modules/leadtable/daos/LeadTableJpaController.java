/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.leadtable.daos;


import com.lacv.jmagrexs.modules.leadtable.entities.LeadTable;
import com.lacv.jmagrexs.dao.JPAGenericDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lacastrillov
 */
@Repository
public class LeadTableJpaController extends JPAGenericDao<LeadTable> implements LeadTableJpa {

    
}
