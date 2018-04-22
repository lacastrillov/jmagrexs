/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto.config;

import com.dot.gcpbasedot.util.Formats;


/**
 *
 * @author desarrollador
 */
public class TableConfig {
    
    private String tableRef;
    
    private String tableName;
    
    private String labelField;
    
    private String pluralEntityTitle;
    
    private String singularEntityTitle;
    
    private boolean visibleFilters;
    
    private boolean visibleForm;
    
    private boolean visibleGrid;
    
    private boolean visibleMenu;
    
    private boolean visibleHeader;
    
    private boolean visibleAddButtonInGrid;
    
    private boolean visibleRemoveButtonInGrid;
    
    private boolean visibleExportButton;
    
    private boolean visibleSeeAllButton;
    
    private boolean editableGrid;
    
    private boolean editableForm;
    
    private boolean collapsedFilters;
    
    private boolean defaultAutoSave;
    
    private boolean multipartFormData;
    
    private boolean hideHeadersGrid;
    
    private String defaultOrderBy;
    
    private String defaultOrderDir;
    
    private Long maxResultsPerPage;
    
    
    public TableConfig(String tableRef) {
        this.tableRef= tableRef;
        this.tableName= Formats.capitalize(tableRef.replaceFirst("lt_", ""));
        this.labelField= "id";
        this.pluralEntityTitle= this.tableName + "s";
        this.singularEntityTitle= this.tableName;
        this.visibleFilters= true;
        this.visibleForm= true;
        this.visibleGrid= true;
        this.visibleMenu= true;
        this.visibleHeader= true;
        this.visibleAddButtonInGrid= true;
        this.visibleRemoveButtonInGrid= true;
        this.visibleExportButton= true;
        this.visibleSeeAllButton= false;
        this.editableGrid= true;
        this.editableForm= true;
        this.collapsedFilters= true;
        this.defaultAutoSave= true;
        this.multipartFormData= false;
        this.hideHeadersGrid= false;
        this.defaultOrderBy= "id";
        this.defaultOrderDir= "DESC";
        this.maxResultsPerPage= 50L;
    }

    /**
     * @return the tableRef
     */
    public String getTableRef() {
        return tableRef;
    }

    /**
     * @param tableRef the tableRef to set
     */
    public void setTableRef(String tableRef) {
        this.tableRef = tableRef;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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
     * @return the pluralEntityTitle
     */
    public String getPluralEntityTitle() {
        return pluralEntityTitle;
    }

    /**
     * @param pluralEntityTitle the pluralEntityTitle to set
     */
    public void setPluralEntityTitle(String pluralEntityTitle) {
        this.pluralEntityTitle = pluralEntityTitle;
    }

    /**
     * @return the singularEntityTitle
     */
    public String getSingularEntityTitle() {
        return singularEntityTitle;
    }

    /**
     * @param singularEntityTitle the singularEntityTitle to set
     */
    public void setSingularEntityTitle(String singularEntityTitle) {
        this.singularEntityTitle = singularEntityTitle;
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
     * @return the visibleGrid
     */
    public boolean isVisibleGrid() {
        return visibleGrid;
    }

    /**
     * @param visibleGrid the visibleGrid to set
     */
    public void setVisibleGrid(boolean visibleGrid) {
        this.visibleGrid = visibleGrid;
        if(visibleGrid==false){
            this.visibleFilters= false;
        }
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
     * @return the editableGrid
     */
    public boolean isEditableGrid() {
        return editableGrid;
    }

    /**
     * @param editableGrid the editableGrid to set
     */
    public void setEditableGrid(boolean editableGrid) {
        this.editableGrid = editableGrid;
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
     * @return the visibleAddButtonInGrid
     */
    public boolean isVisibleAddButtonInGrid() {
        return visibleAddButtonInGrid;
    }

    /**
     * @param visibleAddButtonInGrid the visibleAddButtonInGrid to set
     */
    public void setVisibleAddButtonInGrid(boolean visibleAddButtonInGrid) {
        this.visibleAddButtonInGrid = visibleAddButtonInGrid;
    }

    /**
     * @return the visibleRemoveButtonInGrid
     */
    public boolean isVisibleRemoveButtonInGrid() {
        return visibleRemoveButtonInGrid;
    }

    /**
     * @param visibleRemoveButtonInGrid the visibleRemoveButtonInGrid to set
     */
    public void setVisibleRemoveButtonInGrid(boolean visibleRemoveButtonInGrid) {
        this.visibleRemoveButtonInGrid = visibleRemoveButtonInGrid;
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
     * @return the defaultAutoSave
     */
    public boolean isDefaultAutoSave() {
        return defaultAutoSave;
    }

    /**
     * @param defaultAutoSave the defaultAutoSave to set
     */
    public void setDefaultAutoSave(boolean defaultAutoSave) {
        this.defaultAutoSave = defaultAutoSave;
    }
    
    /**
     * @return the multipartFormData
     */
    public boolean isMultipartFormData() {
        return multipartFormData;
    }

    /**
     * @param multipartFormData the multipartFormData to set
     */
    public void setMultipartFormData(boolean multipartFormData) {
        this.multipartFormData = multipartFormData;
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
    
    
}
