
package com.lacv.jmagrexs.modules.mail.services;

import java.util.Map;
import org.apache.velocity.VelocityContext;

/**
 * @author desarrollador
 */
public interface MailingService {
    
    String processTemplate(String template, VelocityContext context, boolean withHeaderAndFooter);
    
    boolean sendMail(String mail, String text, String subject);
    
    boolean sendTemplateMail(String mail, String alias, String subject, Map<String,String> data);
    
    boolean sendVelocityMail(String mail, String velocityFileName, String subject, VelocityContext context, boolean withHeaderAndFooter);
    
}
