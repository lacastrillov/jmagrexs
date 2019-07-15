/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

import com.lacv.jmagrexs.dto.OperationCallback;
import com.lacv.jmagrexs.dto.ResultListCallback;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 *
 * @author lacastrillov
 */
public class Util {
    
    private static final AESEncrypt MY_INSTANCE= AESEncrypt.getDefault("#@JM4GR3X5@#");
    
    /**
     * 
     * @param listDto
     * @param messagge
     * @param success
     * @return 
     */
    public static String getResultListCallback(List listDto, String messagge, boolean success) {
        return getResultListCallback(listDto, (long) listDto.size(), messagge, success);
    }

    /**
     * 
     * @param listDto
     * @param totalCount
     * @param messagge
     * @param success
     * @return 
     */
    public static String getResultListCallback(List listDto, Long totalCount, String messagge, boolean success) {
        ResultListCallback resultListCallback = getResultList(listDto, totalCount, messagge, success);
        
        return JSONService.objectToJson(resultListCallback);
    }

    /**
     * 
     * @param listDto
     * @param messagge
     * @param success
     * @return 
     */
    public static ResultListCallback getResultList(List listDto, String messagge, boolean success) {
        return getResultList(listDto, (long) listDto.size(), messagge, success);
    }

    /**
     * 
     * @param listDto
     * @param totalCount
     * @param messagge
     * @param success
     * @return 
     */
    public static ResultListCallback getResultList(List listDto, Long totalCount, String messagge, boolean success) {
        ResultListCallback resultListCallback = new ResultListCallback();

        resultListCallback.setData(listDto);
        resultListCallback.setMessage(messagge);
        resultListCallback.setSuccess(success);
        resultListCallback.setTotalCount(totalCount);

        return resultListCallback;
    }

    /**
     * 
     * @param dto
     * @param messagge
     * @param success
     * @return 
     */
    public static String getOperationCallback(Object dto, String messagge, boolean success) {
        OperationCallback operationCallback = getOperation(dto, messagge, success);

        return JSONService.objectToJson(operationCallback);
    }

    /**
     * 
     * @param dto
     * @param messagge
     * @param success
     * @return 
     */
    public static OperationCallback getOperation(Object dto, String messagge, boolean success) {
        OperationCallback operationCallback = new OperationCallback();

        operationCallback.setData(dto);
        operationCallback.setMessage(messagge);
        operationCallback.setSuccess(success);

        return operationCallback;
    }
    
    /**
     * 
     * @param diffHour
     * @return 
     */
    public static Time getCurrentTime(int diffHour){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - diffHour);
        Date currentDate = cal.getTime();
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        sdfTime.setTimeZone(TimeZone.getTimeZone("EST"));
        
        return Time.valueOf(sdfTime.format(currentDate));
    }
    
    /**
     * 
     * @param data
     * @return 
     */
    public static byte[] getStringBytes(String data){
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public static String encodeObject(Object object){
        String json= JSONService.objectToJson(object);
        return Base64.encodeBase64String(getStringBytes(json));
    }
    
    /**
     * 
     * @param code
     * @param objectClass
     * @return 
     */
    public static Object decodeObject(String code, Class objectClass){
        try{
            String json= new String(Base64.decodeBase64(code), StandardCharsets.UTF_8);
            return JSONService.jsonToObject(json, objectClass);
        }catch(Exception e){
            return null;
        }
    }
    
    /**
     * 
     * @param data
     * @param type
     * @return 
     */
    public static HttpEntity<byte[]> getHttpEntityBytes(String data, String type){
        byte[] byteResult= getStringBytes(data);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", type));
        header.setContentLength(byteResult.length);
        return new HttpEntity<>(byteResult, header);
    }
    
    /**
     * 
     * @return 
     */
    public static String getTodayCode(){
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return MY_INSTANCE.encrypt(date, "25091986");
    }
    
}
