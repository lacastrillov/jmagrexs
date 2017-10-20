/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto.config;

import com.dot.gcpbasedot.dto.GridTemplate;
import com.dot.gcpbasedot.service.EntityService;

/**
 *
 * @author desarrollador
 */
public class FileExplorerConfig {
    
    public static String TCV_STANDARD= "tcv_standard";
    
    public static String TCV_N_N_MULTICHECK= "tcv-n-n-multicheck";
    
    private EntityService entityService;
    
    private Class dtoClass;
    
    private String entityRef;
    
    private String entityName;
    
    private String labelField;
    
    private String pluralEntityTitle;
    
    private String singularEntityTitle;
    
    private String dateFormat;
    
    private boolean visibleFilters;
    
    private boolean visibleForm;
    
    private boolean visibleMenu;
    
    private boolean visibleHeader;
    
    private boolean visibleRemoveButtonInGrid;
    
    private boolean visibleExportButton;
    
    private boolean editableForm;
    
    private boolean collapsedFilters;
    
    private boolean defaultAutoSave;
    
    private boolean multipartFormData;
    
    private boolean hideHeadersGrid;
    
    private boolean activeGridTemplate;
    
    private boolean activeGridTemplateAsParent;
    
    private boolean activeGridTemplateAsChild;
    
    private boolean restSession;
    
    private int gridHeightChildView;
    
    private String defaultOrderBy;
    
    private String defaultOrderDir;
    
    private Long maxResultsPerPage;
    
    private GridTemplate gridTemplate;
    
    
    public FileExplorerConfig(String entityRef, EntityService entityService, Class dtoClass) {
        this.entityRef= entityRef;
        this.entityName= entityService.getEntityClass().getSimpleName();
        this.labelField= "name";
        this.entityService= entityService;
        this.dtoClass= dtoClass;
        this.pluralEntityTitle= this.entityName + "s";
        this.singularEntityTitle= this.entityName;
        this.dateFormat= "d/m/Y";
        this.visibleFilters= true;
        this.visibleForm= true;
        this.visibleMenu= true;
        this.visibleHeader= true;
        this.visibleRemoveButtonInGrid= true;
        this.visibleExportButton= true;
        this.editableForm= true;
        this.collapsedFilters= true;
        this.defaultAutoSave= true;
        this.multipartFormData= true;
        this.hideHeadersGrid= false;
        this.activeGridTemplate= false;
        this.activeGridTemplateAsParent= false;
        this.activeGridTemplateAsChild= false;
        this.restSession= false;
        this.gridHeightChildView= 0;
        this.defaultOrderBy= "id";
        this.defaultOrderDir= "DESC";
        this.maxResultsPerPage= 50L;
        this.gridTemplate= new GridTemplate("");
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
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
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
     * @return the gridHeightChildView
     */
    public int getGridHeightChildView() {
        return gridHeightChildView;
    }

    /**
     * @param gridHeightChildView
     */
    public void setGridHeightChildView(int gridHeightChildView) {
        this.gridHeightChildView = gridHeightChildView;
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
    
    
}
