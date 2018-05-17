/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.services;


import com.lacv.jmagrexs.modules.security.daos.UserJpa;
import com.lacv.jmagrexs.modules.security.entities.User;
import com.lacv.jmagrexs.modules.security.mappers.UserMapper;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author lcastrillo
 */
@Service("userService")
public class UserServiceImpl extends EntityServiceImpl<User> implements UserService {
    
    @Autowired
    public UserJpa userJpa;
    
    @Autowired
    public UserMapper userMapper;
    
    @Override
    public GenericDao getGenericDao(){
        return userJpa;
    }
    
    
}
