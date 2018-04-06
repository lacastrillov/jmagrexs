/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.dto.SOAPServiceDto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This is an example of a simple SOAP Client class to send request body to a SOAP Server.
 *
 * Useful when you want to test a SOAP server and you don't want to generate all SOAP client class from the WSDL.
 *
 * @author kdelfour
 */
public class SimpleSOAPClient {

    private static final Logger LOG = Logger.getLogger(SimpleSOAPClient.class);

    private SOAPServiceDto soapService;
    
    private SOAPConnection soapConnection = null;

    /**
     * A constructor who create a SOAP connection
     *
     * @param soapService
     */
    public SimpleSOAPClient(SOAPServiceDto soapService) {
        try {
            this.soapService= soapService;
            createSOAPConnection();
        } catch (SOAPException e) {
            LOG.error(e);
        }
    }

    /**
     * 
     * @return soapService
     */
    public SOAPServiceDto getSoapService() {
        return soapService;
    }

    /**
     * 
     * @param soapService 
     */
    public void setSoapService(SOAPServiceDto soapService) {
        this.soapService = soapService;
    }
    
    /**
     * Create a SOAP connection
     *
     * @throws SOAPException
     */
    private void createSOAPConnection() throws SOAPException  {
        SOAPConnectionFactory soapConnectionFactory;
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
    }

    /**
     * Send a SOAP request for a specific operation
     *
     * @param xmlRequestBody the body of the SOAP message
     * @return a response from the server
     * @throws SOAPException
     * @throws IOException
     */
    public String sendMessage(String xmlRequestBody) throws SOAPException, IOException{
        SOAPMessage stringToSOAPMessage = stringToSOAPMessage(xmlRequestBody);
        SOAPMessage soapResponse = soapConnection.call(stringToSOAPMessage, soapService.getEndpoint());
        
        return soatMessageToString(soapResponse);
    }
    
    /**
     * Send a SOAP request for a specific operation
     *
     * @param xmlRequestBody the body of the SOAP message
     * @return a response from the server
     * @throws SOAPException
     * @throws IOException
     */
    public JSONObject sendMessageGetJSON(String xmlRequestBody) throws SOAPException, IOException{
        SOAPMessage stringToSOAPMessage = stringToSOAPMessage(xmlRequestBody);
        SOAPMessage soapResponse = soapConnection.call(stringToSOAPMessage, soapService.getEndpoint());
        String strMsg= soatMessageToString(soapResponse);
        String jsonMsg= XMLMarshaller.convertXMLToJSON(strMsg);
        JSONObject jsonObject= new JSONObject(jsonMsg);
        
        return jsonObject;
    }
    
    /**
     * Transform a String to a SOAP message
     *
     * @param xmlRequestBody the string body representation
     * @return a SOAP element
     * @throws SOAPException
     * @throws IOException
     */
    public SOAPMessage stringToSOAPMessage(String xmlRequestBody) throws SOAPException, IOException{
        InputStream is = new ByteArrayInputStream(xmlRequestBody.getBytes());
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, is);
        
        return soapMessage;
    }
    
    /**
     * 
     * @param soapMessage
     * @return
     * @throws SOAPException
     * @throws IOException 
     */
    public String soatMessageToString(SOAPMessage soapMessage) throws SOAPException, IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        String strMsg = new String(out.toByteArray());

        // This returns the SOAPBodyElement that contains ONLY the Payload
        return strMsg;
    }
    
    /**
     * 
     * @param jsonData
     * @param envelope
     * @return 
     */
    public String mergeDataInEnvelope(JSONObject jsonData, String envelope){
        String mergedEnvelope= envelope;
        Iterator fields = jsonData.keys();
        while (fields.hasNext()) {
            String field = fields.next().toString();
            if (!jsonData.isNull(field)) {
                Object fieldObj = jsonData.get(field);
                if (fieldObj instanceof JSONArray == false && fieldObj instanceof JSONObject ==false) {
                    mergedEnvelope= mergedEnvelope.replace("%%"+field+"%%", fieldObj.toString());
                }
            } else {
                mergedEnvelope= mergedEnvelope.replace("%%"+field+"%%", "");
            }
        }
        return mergedEnvelope;
    }

}
