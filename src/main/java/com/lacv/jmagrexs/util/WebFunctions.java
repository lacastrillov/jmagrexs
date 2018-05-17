/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.util;

/**
 *
 * @author lacastrillov
 */
public class WebFunctions {
    
    public String addParameterToQueryString(String queryString, String nameParameter, String valueParameter){
        if(queryString.contains(nameParameter+"=")){
            return queryString.replaceAll(nameParameter+"=[^&]+", nameParameter+"=" + valueParameter);
        }else{
            return queryString + ((queryString.length()>0)?"&":"") + nameParameter+"="+valueParameter;
        }
    }
    
    public String removeParameterFromQueryString(String queryString, String nameParameter){
        return queryString.replaceAll("[?&]"+nameParameter+"=[^&]+", "");
    }
    
    public String getImageLinkByDimensions(String urlImage, String dimensions){
        try{
            return urlImage.substring(0, urlImage.indexOf("_"))+"_"+dimensions+"_"+urlImage.substring(urlImage.indexOf("_")+1, urlImage.length());
        }catch(Exception e){
            return urlImage;
        }
    }
    
}
