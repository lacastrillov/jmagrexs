package com.lacv.jmagrexs.modules.security.services;

import com.lacv.jmagrexs.components.ServerDomain;
import com.lacv.jmagrexs.dto.RESTServiceDto;
import com.lacv.jmagrexs.util.RESTServiceConnection;
import com.lacv.jmagrexs.modules.security.dtos.security.UserDetailsDto;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.security.web.util.ThrowableCauseExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.NestedServletException;

/**
 * Clase que valida las peticiones Ajax, debe ser tenido en cuenta desde los js
 * Retorna 901, cuando no se tiene sesion, para que se utilice este coódi en el
 * ajax en caso de un redirect
 *
 * @author Harley Aranda Insoftar.
 * @copy Todos los derechos reservados Metrocuadrado.
 * @version 1.0
 */
@Component
public class CustomSecurityFilter extends GenericFilterBean {
    
    @Autowired
    SecurityService securityService;
    
    @Autowired
    ServerDomain serverDomain;
    
    private String[] accessControlModifiers;
    
    private List accessControlModifiersList;

    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
    
    @PostConstruct
    public void init(){
        accessControlModifiers= new String[]{
            serverDomain.getApplicationContext()+"/rest/webResource/create.htm",
            serverDomain.getApplicationContext()+"/rest/webResource/update.htm",
            serverDomain.getApplicationContext()+"/rest/webResource/delete.htm",
            serverDomain.getApplicationContext()+"/rest/webResource/delete/byfilter.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceRole/create.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceRole/update.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceRole/delete.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceRole/delete/byfilter.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceAuthorization/create.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceAuthorization/update.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceAuthorization/delete.htm",
            serverDomain.getApplicationContext()+"/rest/webresourceAuthorization/delete/byfilter.htm"
        };
        accessControlModifiersList= Arrays.asList(accessControlModifiers);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("x-frame-options", "allow");
        serverDomain.initDomain(req);
        try {
            String requestURI= req.getRequestURI();
            logger.info("CustomSecurityFilter IN: "+requestURI);
            
            if(req.getHeader("Authorization")!=null){
                securityService.connect(req.getHeader("Authorization"));
            }else if(req.getHeader("Auth-Token")!=null){
                securityService.connectByToken(req.getHeader("Auth-Token"));
            }
            
            boolean continueAccess= securityService.checkAccessResource(requestURI);
            
            if(continueAccess){
                chain.doFilter(request, response);
                
                if(accessControlModifiersList.contains(requestURI)){
                    securityService.reconfigureAccessControl();
                    replicateAccessControl();
                }
            }else{
                accessDenied(req, resp);
            }
            logger.info("CustomSecurityFilter END ");
        } catch (AuthenticationException ex){
            resp.sendError(403, ex.getMessage());
        } catch (Exception ex) {
            logger.error("CustomSecurityFilter ex ", ex);
            
            Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
            RuntimeException ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);

            if (ase == null) {
                ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
            }
            if (ase != null) {
                logger.error("AuthenticationException ase ", ase);
                throw ase;
            }
        }
    }
    
    private void replicateAccessControl(){
        String emptyRoot= serverDomain.getContextPath().replace(serverDomain.getApplicationContext(), "");
        if(!emptyRoot.equals("") && !serverDomain.getModules().contains("")){
            serverDomain.getModules().add("");
        }
        for(String module: serverDomain.getModules()){
            if(!module.equals(serverDomain.getContextPath())){
                String reconfigureAccessControlUrl= serverDomain.getDomainWithPort()+serverDomain.getApplicationContext()+
                        module+"/account/reconfigureAccessControl";
                logger.info("replicateAccessControl "+reconfigureAccessControlUrl);
                RESTServiceDto restService= new RESTServiceDto("ReconfigureAccessControlUrl", reconfigureAccessControlUrl, HttpMethod.GET, null);
                RESTServiceConnection restServiceConnection= new RESTServiceConnection(restService);
                try {
                    restServiceConnection.get(null, null, null);
                } catch (IOException ex) {
                    logger.error("replyAccessControl ", ex);
                }
            }
        }
            
    }

    private boolean accessDenied(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDetailsDto userDetails= securityService.getUserDetails();
        String ajaxHeader = req.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(ajaxHeader)) {
            resp.sendError(403, "Acceso denegado");
        } else if(userDetails!=null) {
            resp.sendRedirect(serverDomain.getApplicationContext()+"/account/denied");
        } else {
            String queryString= "";
            if(req.getQueryString()!=null){
                queryString= "?" + URLDecoder.decode(req.getQueryString(), "UTF-8");
            }
            String redirectUrl= req.getRequestURI() + queryString;
            resp.sendRedirect(serverDomain.getApplicationContext()+"/account/home?redirect="+Base64.encodeBase64String(redirectUrl.getBytes("UTF-8")));
        }
        
        return false;
    }

    private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {

        @Override
        protected void initExtractorMap() {
            super.initExtractorMap();
            registerExtractor(ServletException.class, new ThrowableCauseExtractor() {

                @Override
                public Throwable extractCause(Throwable throwable) {
                    verifyThrowableHierarchy(throwable, NestedServletException.class);
                    return ((NestedServletException) throwable).getRootCause();
                }
                
            });
        }
        
    }
    
}
