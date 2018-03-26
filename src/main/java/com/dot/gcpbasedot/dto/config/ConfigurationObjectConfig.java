/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author desarrollador
 */
public class ConfigurationObjectConfig {
    
    private String mainConfigurationRef;
    
    private String mainConfigurationName;
    
    private String mainConfigurationTitle;
    
    private final Map<String, String> nameConfigurationObjects;
    
    private final Map<String, Class> configurationObjects;
    
    private String labelField;
    
    private String dateFormat;
    
    private boolean visibleForm;
    
    private boolean visibleMenu;
    
    private boolean visibleHeader;
    
    private boolean visibleSeeAllButton;
    
    private boolean editableForm;
    
    private final Set<String> multipartFormConfig;
    
    
    public ConfigurationObjectConfig(String mainConfigurationRef) {
        this.mainConfigurationRef= mainConfigurationRef;
        this.mainConfigurationName= mainConfigurationRef.substring(0, 1).toUpperCase() + mainConfigurationRef.substring(1);
        this.mainConfigurationTitle= this.mainConfigurationRef;
        this.labelField= "id";
        this.dateFormat= "d/m/Y";
        this.visibleForm= true;
        this.visibleMenu= true;
        this.visibleHeader= true;
        this.visibleSeeAllButton= false;
        this.editableForm= true;
        this.multipartFormConfig= new HashSet<>();
        this.nameConfigurationObjects= new LinkedHashMap<>();
        this.configurationObjects = new HashMap<>();
    }
    
    /**
     * @return the mainConfigurationRef
     */
    public String getMainConfigurationRef() {
        return mainConfigurationRef;
    }

    /**
     * @param mainConfigurationRef the mainConfigurationRef to set
     */
    public void setMainConfigurationRef(String mainConfigurationRef) {
        this.mainConfigurationRef = mainConfigurationRef;
    }

    public String getMainConfigurationName() {
        return mainConfigurationName;
    }

    public void setMainConfigurationName(String mainConfigurationName) {
        this.mainConfigurationName = mainConfigurationName;
    }
    
    public void addControlConfigurationObjectView(String configurationObjectRef, String processTitle, Class configurationObject){
        nameConfigurationObjects.put(configurationObjectRef, processTitle);
        configurationObjects.put(configurationObjectRef, configurationObject);
    }

    public Map<String, String> getNameConfigurationObjects() {
        return nameConfigurationObjects;
    }

    public Map<String, Class> getConfigurationObjects() {
        return configurationObjects;
    }

    /**
     * @return the labelField
     */
    public String getLabelField() {
        return labelField;
    }

    /**
     * @param labelField the labelField to set
     */
    public void setLabelField(String labelField) {
        this.labelField = labelField;
    }

    /**
     * @return the mainConfigurationTitle
     */
    public String getMainConfigurationTitle() {
        return mainConfigurationTitle;
    }

    /**
     * @param mainConfigurationTitle
     */
    public void setMainConfigurationTitle(String mainConfigurationTitle) {
        this.mainConfigurationTitle = mainConfigurationTitle;
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * @return the visibleForm
     */
    public boolean isVisibleForm() {
        return visibleForm;
    }

    /**
     * @param visibleForm the visibleForm to set
     */
    public void setVisibleForm(boolean visibleForm) {
        this.visibleForm = visibleForm;
    }

    /**
     * @return the visibleMenu
     */
    public boolean isVisibleMenu() {
        return visibleMenu;
    }

    /**
     * @param visibleMenu the visibleMenu to set
     */
    public void setVisibleMenu(boolean visibleMenu) {
        this.visibleMenu = visibleMenu;
    }
    
    /**
     * @return the visibleHeader
     */
    public boolean isVisibleHeader() {
        return visibleHeader;
    }

    /**
     * @param visibleHeader the visibleHeader to set
     */
    public void setVisibleHeader(boolean visibleHeader) {
        this.visibleHeader = visibleHeader;
    }
    
    /**
     * 
     * @return the visibleSeeAllButton
     */
    public boolean isVisibleSeeAllButton() {
        return visibleSeeAllButton;
    }

    /**
     * 
     * @param visibleSeeAllButton 
     */
    public void setVisibleSeeAllButton(boolean visibleSeeAllButton) {
        this.visibleSeeAllButton = visibleSeeAllButton;
    }

    /**
     * @return the editableForm
     */
    public boolean isEditableForm() {
        return editableForm;
    }

    /**
     * @param editableForm the editableForm to set
     */
    public void setEditableForm(boolean editableForm) {
        this.editableForm = editableForm;
    }
    
    /**
     * @return the multipartFormData
     */
    public Set getMultipartFormConfig() {
        return multipartFormConfig;
    }

    /**
     * @param configurationObjectRef
     */
    public void addMultipartFormConfig(String configurationObjectRef) {
        this.multipartFormConfig.add(configurationObjectRef);
    }
    
}
