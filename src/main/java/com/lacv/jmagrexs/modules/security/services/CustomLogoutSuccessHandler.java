/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.modules.security.services;

import com.lacv.jmagrexs.dto.ConnectionResponse;
import com.lacv.jmagrexs.dto.RESTServiceDto;
import com.lacv.jmagrexs.util.RESTServiceConnection;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author grupot
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    
    private final String[] CONTEXT_APPS= {"/rest","/vista"};

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication a) throws IOException, ServletException {
        System.out.println("onLogoutSuccess ");
        for(String contextApp: CONTEXT_APPS){
            String domain= request.getRequestURL().toString().replaceAll(request.getRequestURI(), "");
            String endpoint= domain+contextApp+"/ajax/account/logout";
            System.out.println("Logout + "+endpoint);
            RESTServiceDto service= new RESTServiceDto("logout", endpoint, HttpMethod.GET, null);
            RESTServiceConnection authenticateConn= new RESTServiceConnection(service);

            ConnectionResponse connResponse= authenticateConn.get(null, null, null);
            System.out.println("Logout Response: "+connResponse.getCode());
        }
        
    }
}
