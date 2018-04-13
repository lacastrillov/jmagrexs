/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import java.util.List;

/**
 *
 * @author grupot
 */
public interface ObjectExplorerService {
    
    List<Object> getAllObjectsByPath(Class type, String path);
    
}
