
package com.lacv.jmagrexs.modules.mail.services;

import com.lacv.jmagrexs.modules.mail.entities.Mail;
import com.lacv.jmagrexs.modules.mail.entities.MailTemplate;
import com.lacv.jmagrexs.modules.mail.services.MailService;
import com.lacv.jmagrexs.modules.mail.services.MailTemplateService;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.lacv.jmagrexs.modules.mail.services.MailingService;
import java.util.Map;
import org.springframework.web.util.HtmlUtils;

/**
 * @author desarrollador
 */
@Service("mailingService")
public class MailingServiceImpl implements MailingService {

    private static final String COMPONENTS_DIR = "/mail/components/";

    private static final String TEMPLATES_DIR = "/mail/templates/";

    protected static final Logger LOGGER = Logger.getLogger(MailingServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private MailService mailService;
    
    @Autowired
    private MailTemplateService mailTemplateService;

    @Autowired
    @Value("${mail.username}")
    private String username;

    @Autowired
    @Value("${mail.userlabel}")
    private String userlabel;
    
    @Autowired
    @Value("${domain.url}")
    private String domainUrl;

    /**
     * Retrona el contenido del template de velocity dado el nombre del mismo.
     * @param template nombre del template, no es la ruta del mismo.
     * @param context contexto con objetos para el template.
     * @param withHeaderAndFooter
     * @return el template procesado.
     */
    @Override
    public String processTemplate(String template, VelocityContext context, boolean withHeaderAndFooter) {
        StringWriter sw = new StringWriter();
        StringBuilder content = new StringBuilder("");
        try {
            Template vtemplate = velocityEngine.getTemplate(TEMPLATES_DIR + template);
            LOGGER.info("TEMPLATES_DIR + template " + TEMPLATES_DIR + template);
            vtemplate.merge(context, sw);
            if (withHeaderAndFooter) {
                StringWriter swFoot = new StringWriter();
                StringWriter swHead = new StringWriter();
                Template vtemplateHeader = velocityEngine.getTemplate(COMPONENTS_DIR + "header_mail.vm");
                vtemplateHeader.merge(context, swHead);
                Template vtemplateFooter = velocityEngine.getTemplate(COMPONENTS_DIR + "footer_mail.vm");
                vtemplateFooter.merge(context, swFoot);
                content.append(swHead.toString()).append(sw.toString()).append(swFoot.toString());
            } else {
                content.append(sw.toString());
            }

            return content.toString();
        } catch (ResourceNotFoundException | ParseErrorException | MethodInvocationException e) {
            LOGGER.error("Error getVelocityEngine() ", e);
            return "";
        }
    }
    
    /**
     * @param mailTo
     * @param text
     * @param subject
     * @return 
     */
    @Override
    public boolean sendMail(String mailTo, String text, String subject) {
        return this.sendMail(mailTo, text, subject, null);
    }

    /**
     * 
     * @param mailTo
     * @param text
     * @param subject
     * @param mailTemplate
     * @return 
     */
    private boolean sendMail(String mailTo, String text, String subject, MailTemplate mailTemplate) {
        boolean sent;
        String from = userlabel + " <" + username + ">";
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = new MimeBodyPart();
        Mail mail = new Mail();
        mail.setSubject(subject);
        mail.setMailTo(mailTo);
        mail.setSendDate(new Date());
        mail.setMessage(text);
        mail.setMailFrom(from);
        if(mailTemplate!=null){
            mail.setMailTemplate(mailTemplate);
        }
        try {
            messageBodyPart.setContent(text, "text/html");
            multipart.addBodyPart(messageBodyPart);
            MimeMessage message = mailSender.createMimeMessage();
            message.setSubject(HtmlUtils.htmlUnescape(subject),"UTF-8");
            message.setFrom(new InternetAddress(from));
            String[] toList = mailTo.split(",");
            for (String to : toList) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            message.setContent(multipart);
            mailSender.send(message);
            mail.setResult("Enviado");
            sent= true;
        } catch (MessagingException ex) {
            mail.setResult("Error");
            java.util.logging.Logger.getLogger(MailingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            sent= false;
        }
        mailService.create(mail);
        return sent;
    }
    
    /**
     *
     * @param mail
     * @param alias
     * @param subject
     * @param data
     * @return
     */
    @Override
    public boolean sendTemplateMail(String mail, String alias, String subject, Map<String,String> data) {
        MailTemplate mailTemplate= mailTemplateService.loadByParameter("alias", alias);
        data.put("domainUrl", domainUrl);
        if(mailTemplate!=null && mailTemplate.getStatus().equals("Active")){
            String message = mailTemplate.getContent();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if(entry.getValue()!=null){
                    message= message.replace("%%" + entry.getKey() + "%%" , entry.getValue());
                }
            }
            boolean result= sendMail(mail, message, subject, mailTemplate);
            if(result){
                mailTemplate.setTotalSent(mailTemplate.getTotalSent()+1);
                mailTemplateService.update(mailTemplate);
            }
            return result;
        }else{
            return false;
        }
    }

    /**
     * @param mail
     * @param velocityFileName
     * @param subject
     * @param context
     * @param withHeaderAndFooter
     * @return
     */
    @Override
    public boolean sendVelocityMail(String mail, String velocityFileName, String subject, VelocityContext context, boolean withHeaderAndFooter) {
        context.put("domainUrl", domainUrl);
        String message = processTemplate(velocityFileName, context, withHeaderAndFooter);
        return sendMail(mail, message, subject, null);
    }
    
}
