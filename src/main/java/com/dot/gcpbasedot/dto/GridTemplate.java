/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desarrollador
 */
public class GridTemplate {
    
    private String templateName;
    
    private List<String> styles;
    
    private int numColumns;
    
    
    public GridTemplate(String templateName){
        this.templateName= templateName;
        this.styles= new ArrayList<>();
        this.numColumns= 4;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return the styles
     */
    public List<String> getStyles() {
        return styles;
    }

    /**
     * @param styles the styles to set
     */
    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    /**
     * @return the numColumns
     */
    public int getNumColumns() {
        return numColumns;
    }

    /**
     * @param numColumns the numColumns to set
     */
    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }
    
}
