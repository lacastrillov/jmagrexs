/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service;

import com.dot.gcpbasedot.dto.RESTServiceDto;
import com.dot.gcpbasedot.dto.SOAPServiceDto;
import java.io.IOException;
import javax.xml.soap.SOAPException;

/**
 *
 * @author grupot
 */
public interface ExternalService {
    
    boolean isRESTService(String processName);
    
    boolean isSOAPService(String processName);
    
    RESTServiceDto getRESTService(String processName);
    
    SOAPServiceDto getSOAPService(String processName);
    
    Object callService(String processName, Object data);
    
    Object callRESTService(String processName, Object data) throws IOException;
    
    String callSOAPService(String processName, Object data) throws SOAPException, IOException;
    
}
