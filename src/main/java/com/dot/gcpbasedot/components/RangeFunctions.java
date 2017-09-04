/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

import com.dot.gcpbasedot.enums.PageType;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class RangeFunctions {
    
    public String getListenerFuntionSingleValue(String typeFilter, String fieldName, PageType pageType){
        String container= "Instance.entityExtController";
        if(pageType.equals(PageType.PROCESS)){
            container= "parentExtController";
        }
        String functionOnChange=
                "function(){" +
                "   if(this.getValue()!==null && this.getValue()!==''){" +
                "       "+container+".filter."+typeFilter+"."+fieldName+"= this.getValue();" +
                "   }else{" +
                "       delete "+container+".filter."+typeFilter+"."+fieldName+";" +
                "   }"+
                "}";
        
        return functionOnChange;
    }
    
    public String getListenerFuntionRangeValue(int index, String fieldName, String format, String dateFormat, PageType pageType){
        String getValue= "this.getValue();";
        switch (format) {
            case "date":
                getValue= "Ext.Date.format(this.getValue(), '"+dateFormat+"');";
                break;
            case "time":
                getValue= "Ext.Date.format(this.getValue(), 'H:i:s');";
                break;
        }
        String container= "Instance.entityExtController";
        if(pageType.equals(PageType.PROCESS)){
            container= "parentExtController";
        }
        String functionOnChange=
                "function(){" +
                "   if("+container+".filter.btw."+fieldName+" === undefined){" +
                "           "+container+".filter.btw."+fieldName+"= [null,null];" +
                "   }" +
                "   if(this.getValue()!==null){" +
                "       "+container+".filter.btw."+fieldName+"["+index+"]= " + getValue +
                "   }else{" +
                "       "+container+".filter.btw."+fieldName+"["+index+"]= null;" +
                "   }" +
                "   if("+container+".filter.btw."+fieldName+"[0]===null && "+container+".filter.btw."+fieldName+"[1]===null){" +
                "       delete "+container+".filter.btw."+fieldName+";" +
                "   }" +
                "}";
        
        return functionOnChange;
    }
    
}
