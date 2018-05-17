/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author desarrollador
 */
public class AnalyticsResultDto {
    
    private List<HashMap> results;
    
    private String[] columns;

    /**
     * @return the results
     */
    public List<HashMap> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<HashMap> results) {
        this.results = results;
    }

    /**
     * @return the columns
     */
    public String[] getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }
    
    
    
}
