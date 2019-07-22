/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto.config;

import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.dto.GridTemplate;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author desarrollador
 */
public class ReportConfig {
    
    private EntityService entityService;
    
    private Class dtoClass;
    
    private final String dtoName;
    
    private String entityRef;
    
    private String reportName;
    
    private String idColumnName;
    
    private String labelField;
    
    private String pluralReportTitle;
    
    private boolean visibleForm;
    
    private boolean visibleValueMapForm;
    
    private boolean visibleFilters;
    
    private boolean visibleMenu;
    
    private boolean visibleHeader;
    
    private boolean visibleExportButton;
    
    private boolean visibleOrderByDirButtons;
    
    private boolean visibleSeeAllButton;
    
    private boolean collapsedFilters;
    
    private boolean hideHeadersGrid;
    
    private boolean activeGridTemplate;
    
    private boolean activeGridTemplateAsParent;
    
    private boolean activeGridTemplateAsChild;
    
    private boolean restSession;
    
    private String defaultOrderBy;
    
    private String defaultOrderDir;
    
    private int numColumnsForm;
    
    private Long maxResultsPerPage;
    
    private GridTemplate gridTemplate;
    
    private final Map<String, String> childExtReports;
    
    private final Map<String, String> childRefColumnNames;
    
    private final List<ProcessButton> processButtons;
    
    
    public ReportConfig(String entityRef, String reportName, EntityService entityService, Class dtoClass) {
        this.entityService= entityService;
        this.dtoClass= dtoClass;
        this.dtoName= dtoClass.getCanonicalName();
        this.entityRef= entityRef;
        this.reportName= reportName;
        this.idColumnName= "id";
        this.labelField= "id";
        this.pluralReportTitle= this.reportName + "s";
        this.visibleForm= true;
        this.visibleValueMapForm= false;
        this.visibleFilters= true;
        this.visibleMenu= true;
        this.visibleHeader= true;
        this.visibleExportButton= true;
        this.visibleOrderByDirButtons= true;
        this.visibleSeeAllButton= false;
        this.collapsedFilters= true;
        this.hideHeadersGrid= false;
        this.activeGridTemplate= false;
        this.activeGridTemplateAsParent= false;
        this.activeGridTemplateAsChild= false;
        this.restSession= false;
        this.defaultOrderBy= "";
        this.defaultOrderDir= "";
        this.numColumnsForm= 1;
        this.maxResultsPerPage= 50L;
        this.gridTemplate= new GridTemplate("");
        this.childExtReports= new LinkedHashMap<>();
        this.childRefColumnNames= new HashMap<>();
        this.processButtons= new ArrayList<>();
        LabelField ann= (LabelField) EntityReflection.getClassAnnotation(dtoClass, LabelField.class);
        if(ann!=null){
            this.labelField= ann.value();
        }
    }

    /**
     * @return the entityService
     */
    public EntityService getEntityService() {
        return entityService;
    }

    /**
     * @param entityService the entityService to set
     */
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    /**
     * @return the dtoClass
     */
    public Class getDtoClass() {
        return dtoClass;
    }

    /**
     * @param dtoClass the dtoClass to set
     */
    public void setDtoClass(Class dtoClass) {
        this.dtoClass = dtoClass;
    }
    
    /**
     * @return the dtoName
     */
    public String getDtoName() {
        return dtoName;
    }

    /**
     * @return the entityRef
     */
    public String getEntityRef() {
        return entityRef;
    }

    /**
     * @param entityRef the entityRef to set
     */
    public void setEntityRef(String entityRef) {
        this.entityRef = entityRef;
    }

    /**
     * @return the reportName
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * @param reportName the reportName to set
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * @return the idColumnName
     */
    public String getIdColumnName() {
        return idColumnName;
    }

    /**
     * @param idColumnName
     */
    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
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
     * @return the pluralReportTitle
     */
    public String getPluralReportTitle() {
        return pluralReportTitle;
    }

    /**
     * @param pluralReportTitle the pluralReportTitle to set
     */
    public void setPluralReportTitle(String pluralReportTitle) {
        this.pluralReportTitle = pluralReportTitle;
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
     * @return the visibleValueMapForm
     */
    public boolean isVisibleValueMapForm() {
        return visibleValueMapForm;
    }

    /**
     * @param visibleValueMapForm the visibleValueMapForm to set
     */
    public void setVisibleValueMapForm(boolean visibleValueMapForm) {
        this.visibleValueMapForm = visibleValueMapForm;
    }

    /**
     * @return the visibleFilters
     */
    public boolean isVisibleFilters() {
        return visibleFilters;
    }

    /**
     * @param visibleFilters the visibleFilters to set
     */
    public void setVisibleFilters(boolean visibleFilters) {
        this.visibleFilters = visibleFilters;
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
     * @return the visibleExportButton
     */
    public boolean isVisibleExportButton() {
        return visibleExportButton;
    }

    /**
     * @param visibleExportButton the visibleExportButton to set
     */
    public void setVisibleExportButton(boolean visibleExportButton) {
        this.visibleExportButton = visibleExportButton;
    }

    /**
     * 
     * @return visibleOrderByDirButtons
     */
    public boolean isVisibleOrderByDirButtons() {
        return visibleOrderByDirButtons;
    }

    /**
     * 
     * @param visibleOrderByDirButtons 
     */
    public void setVisibleOrderByDirButtons(boolean visibleOrderByDirButtons) {
        this.visibleOrderByDirButtons = visibleOrderByDirButtons;
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
     * @return the collapsedFilters
     */
    public boolean isCollapsedFilters() {
        return collapsedFilters;
    }

    /**
     * @param collapsedFilters the collapsedFilters to set
     */
    public void setCollapsedFilters(boolean collapsedFilters) {
        this.collapsedFilters = collapsedFilters;
    }
    
    /**
     * @return the hideHeadersGrid
     */
    public boolean isHideHeadersGrid() {
        return hideHeadersGrid;
    }

    /**
     * @param hideHeadersGrid the hideHeadersGrid to set
     */
    public void setHideHeadersGrid(boolean hideHeadersGrid) {
        this.hideHeadersGrid = hideHeadersGrid;
    }
    
    /**
     * @return the activeGridTemplate
     */
    public boolean isActiveGridTemplate() {
        return activeGridTemplate;
    }

    /**
     * @param activeGridTemplate the activeGridTemplate to set
     */
    public void setActiveGridTemplate(boolean activeGridTemplate) {
        this.activeGridTemplate = activeGridTemplate;
        this.hideHeadersGrid = this.activeGridTemplate;
    }

    /**
     * @return the activeGridTemplateAsParent
     */
    public boolean isActiveGridTemplateAsParent() {
        return activeGridTemplateAsParent;
    }

    /**
     * @param activeGridTemplateAsParent the activeGridTemplateAsParent to set
     */
    public void setActiveGridTemplateAsParent(boolean activeGridTemplateAsParent) {
        this.activeGridTemplateAsParent = activeGridTemplateAsParent;
    }
    
    /**
     * @return the activeGridTemplateAsChild
     */
    public boolean isActiveGridTemplateAsChild() {
        return activeGridTemplateAsChild;
    }

    /**
     * @param activeGridTemplateAsChild the activeGridTemplateAsChild to set
     */
    public void setActiveGridTemplateAsChild(boolean activeGridTemplateAsChild) {
        this.activeGridTemplateAsChild = activeGridTemplateAsChild;
    }
    
    /**
     * 
     * @return restSession
     */
    public boolean isRestSession() {
        return restSession;
    }

    /**
     * 
     * @param restSession 
     */
    public void setRestSession(boolean restSession) {
        this.restSession = restSession;
    }

    /**
     * @return the defaultOrderBy
     */
    public String getDefaultOrderBy() {
        return defaultOrderBy;
    }

    /**
     * @param defaultOrderBy the defaultOrderBy to set
     */
    public void setDefaultOrderBy(String defaultOrderBy) {
        this.defaultOrderBy = defaultOrderBy;
    }

    /**
     * @return the defaultOrderDir
     */
    public String getDefaultOrderDir() {
        return defaultOrderDir;
    }

    /**
     * @param defaultOrderDir the defaultOrderDir to set
     */
    public void setDefaultOrderDir(String defaultOrderDir) {
        this.defaultOrderDir = defaultOrderDir;
    }
    
    /**
     * @param defaultOrderBy the defaultOrderBy to set
     * @param defaultOrderDir
     */
    public void setDefaultOrder(String defaultOrderBy, String defaultOrderDir) {
        this.defaultOrderBy = defaultOrderBy;
        this.defaultOrderDir = defaultOrderDir;
    }
    
    /**
     * 
     * @return numColumnsForm
     */
    public int getNumColumnsForm() {
        return numColumnsForm;
    }

    /**
     * 
     * @param numColumnsForm 
     */
    public void setNumColumnsForm(int numColumnsForm) {
        this.numColumnsForm = numColumnsForm;
    }

    /**
     * @return the maxResultsPerPage
     */
    public Long getMaxResultsPerPage() {
        return maxResultsPerPage;
    }

    /**
     * @param maxResultsPerPage the maxResultsPerPage to set
     */
    public void setMaxResultsPerPage(Long maxResultsPerPage) {
        this.maxResultsPerPage = maxResultsPerPage;
    }

    /**
     * @return the gridTemplate
     */
    public GridTemplate getGridTemplate() {
        return gridTemplate;
    }

    /**
     * @param gridTemplate the gridTemplate to set
     */
    public void setGridTemplate(GridTemplate gridTemplate) {
        this.gridTemplate = gridTemplate;
    }
    
    /**
     * @param entityRef
     * @param reportName
     * @param refColumnName
     */
    public void addChildExtReport(String entityRef, String reportName, String refColumnName) {
        this.childExtReports.put(entityRef, reportName);
        this.childRefColumnNames.put(reportName, refColumnName);
    }
    
    /**
     * @return the typeChildExtViews
     */
    public Map<String, String> getChildExtReports() {
        return childExtReports;
    }

    /**
     * @return the childRefColumnNames
     */
    public Map<String, String> getChildRefColumnNames() {
        return childRefColumnNames;
    }
    
    /**
     * 
     * @param mainProcessRef
     * @param processName
     * @param processTitle
     * @param sourceField
     * @param destinationField
     * @param dtoClass 
     */
    public void addProcessButton(String mainProcessRef, String processName, String processTitle, String sourceField, String destinationField, Class dtoClass) {
        ProcessButton processButton= new ProcessButton();
        processButton.setDtoClass(dtoClass);
        processButton.addSourceByDestinationField(sourceField, destinationField);
        processButton.setMainProcessRef(mainProcessRef);
        processButton.setProcessName(processName);
        processButton.setProcessTitle(processTitle);
        
        processButtons.add(processButton);
    }
    
    /**
     * 
     * @param processButton 
     */
    public void addProcessButton(ProcessButton processButton){
        processButtons.add(processButton);
    }

    /**
     * 
     * @return processButtons
     */
    public List<ProcessButton> getProcessButtons() {
        return processButtons;
    }
    
}
