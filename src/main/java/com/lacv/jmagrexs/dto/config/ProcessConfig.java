/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto.config;

import com.lacv.jmagrexs.dto.GridTemplate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author desarrollador
 */
public class ProcessConfig {
    
    private String mainProcessRef;
    
    private String mainProcessName;
    
    private String entityRefLogProcess;
    
    private String entityNameLogProcess;
    
    private final Map<String, String> nameProcesses;
    
    private final Map<String, Class> inDtos;

    private final Map<String, Class> outDtos;
    
    private Class logProcessClass;
    
    private String labelField;
    
    private String mainProcessTitle;
    
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
    
    private boolean hideHeadersGrid;
    
    private boolean activeGridTemplate;
    
    private boolean activeGridTemplateAsParent;
    
    private boolean activeGridTemplateAsChild;
    
    private boolean activeNNMulticheckChild;
    
    private String entityRefNNMulticheckChild;
    
    private int gridHeightChildView;
    
    private String defaultOrderBy;
    
    private String defaultOrderDir;
    
    private Long maxResultsPerPage;
    
    private final Set<String> multipartFormProcess;
    
    private final Map<String, String> internalViewButton;
    
    private final Map<String, Class> childExtViews;
    
    private final Map<String, String> typeChildExtViews;
    
    private final Map<String, List<String>> comboboxChildDependent;
    
    private GridTemplate gridTemplate;
    
    
    public ProcessConfig(String mainProcessRef, String entityRefLogProcess, Class logProcessClass) {
        this.mainProcessRef= mainProcessRef;
        this.mainProcessName= mainProcessRef.substring(0, 1).toUpperCase() + mainProcessRef.substring(1);
        this.entityRefLogProcess= entityRefLogProcess;
        this.entityNameLogProcess= logProcessClass.getSimpleName();
        this.logProcessClass= logProcessClass;
        this.labelField= "id";
        this.mainProcessTitle= this.mainProcessRef;
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
        this.hideHeadersGrid= false;
        this.activeGridTemplate= false;
        this.activeGridTemplateAsParent= false;
        this.activeGridTemplateAsChild= false;
        this.activeNNMulticheckChild= false;
        this.entityRefNNMulticheckChild= null;
        this.gridHeightChildView= 0;
        this.defaultOrderBy= "id";
        this.defaultOrderDir= "DESC";
        this.maxResultsPerPage= 50L;
        this.nameProcesses= new LinkedHashMap<>();
        this.inDtos = new HashMap<>();
        this.outDtos = new HashMap<>();
        this.multipartFormProcess= new HashSet<>();
        this.internalViewButton= new HashMap<>();
        this.childExtViews= new HashMap<>();
        this.typeChildExtViews= new HashMap<>();
        this.comboboxChildDependent= new HashMap<>();
        this.gridTemplate= new GridTemplate("");
    }
    
    /**
     * @return the mainProcessRef
     */
    public String getMainProcessRef() {
        return mainProcessRef;
    }

    /**
     * @param mainProcessRef the mainProcessRef to set
     */
    public void setMainProcessRef(String mainProcessRef) {
        this.mainProcessRef = mainProcessRef;
    }

    public String getMainProcessName() {
        return mainProcessName;
    }

    public void setMainProcessName(String mainProcessName) {
        this.mainProcessName = mainProcessName;
    }

    public String getEntityRefLogProcess() {
        return entityRefLogProcess;
    }

    public void setEntityRefLogProcess(String entityRefLogProcess) {
        this.entityRefLogProcess = entityRefLogProcess;
    }

    public String getEntityNameLogProcess() {
        return entityNameLogProcess;
    }

    public void setEntityNameLogProcess(String entityNameLogProcess) {
        this.entityNameLogProcess = entityNameLogProcess;
    }
    
    public void addControlProcessView(String processName, String processTitle, Class inDtoClass, Class outDtoClass){
        nameProcesses.put(processName, processTitle);
        inDtos.put(processName, inDtoClass);
        outDtos.put(processName, outDtoClass);
    }

    public Map<String, String> getNameProcesses() {
        return nameProcesses;
    }

    public Map<String, Class> getInDtos() {
        return inDtos;
    }

    public Map<String, Class> getOutDtos() {
        return outDtos;
    }

    public Class getLogProcessClass() {
        return logProcessClass;
    }

    public void setLogProcessClass(Class logProcessClass) {
        this.logProcessClass = logProcessClass;
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
     * @return the mainProcessTitle
     */
    public String getMainProcessTitle() {
        return mainProcessTitle;
    }

    /**
     * @param mainProcessTitle
     */
    public void setMainProcessTitle(String mainProcessTitle) {
        this.mainProcessTitle = mainProcessTitle;
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
        if(this.activeGridTemplate){
            return false;
        }
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
    public Set getMultipartFormProcess() {
        return multipartFormProcess;
    }

    /**
     * @param processName
     */
    public void addMultipartFormProcess(String processName) {
        this.multipartFormProcess.add(processName);
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
     * @param entityRefNNMulticheckChild
     */
    public void activateNNMulticheckChild(String entityRefNNMulticheckChild) {
        this.activeNNMulticheckChild = true;
        this.entityRefNNMulticheckChild= entityRefNNMulticheckChild;
    }

    /**
     * @return the activeNNMulticheckChild
     */
    public boolean isActiveNNMulticheckChild() {
        return activeNNMulticheckChild;
    }

    /**
     * @param activeNNMulticheckChild
     */
    public void setActiveNNMulticheckChild(boolean activeNNMulticheckChild) {
        this.activeNNMulticheckChild = activeNNMulticheckChild;
    }
    
    /**
     * @return the entityRefNNMulticheckChild
     */
    public String getEntityRefNNMulticheckChild() {
        return entityRefNNMulticheckChild;
    }

    /**
     * @param entityRefNNMulticheckChild
     */
    public void setEntityRefNNMulticheckChild(String entityRefNNMulticheckChild) {
        this.entityRefNNMulticheckChild = entityRefNNMulticheckChild;
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
     * @return the internalViewButton
     */
    public Map<String, String> getInternalViewButton() {
        return internalViewButton;
    }

    /**
     * @param entityRef
     * @param textButton
     */
    public void addInternalViewButton(String entityRef, String textButton) {
        this.internalViewButton.put(entityRef, textButton);
    }

    /**
     * @return the childExtViews
     */
    public Map<String, Class> getChildExtViews() {
        return childExtViews;
    }

    /**
     * @param entityRef
     * @param entityClass
     * @param typeChild
     */
    public void addChildExtView(String entityRef, Class entityClass, String typeChild) {
        this.childExtViews.put(entityRef, entityClass);
        this.typeChildExtViews.put(entityRef, typeChild);
    }
    
    /**
     * @return the typeChildExtViews
     */
    public Map<String, String> getTypeChildExtViews() {
        return typeChildExtViews;
    }

    /**
     * @return the comboboxChildDependent
     */
    public Map<String, List<String>> getComboboxChildDependent() {
        return comboboxChildDependent;
    }

    /**
     * @param parentEntityRef
     * @param childEntityRef
     */
    public void addComboboxChildDependent(String parentEntityRef, String childEntityRef) {
        if(this.getComboboxChildDependent().containsKey(parentEntityRef)){
            this.getComboboxChildDependent().get(parentEntityRef).add(childEntityRef);
        }else{
            this.getComboboxChildDependent().put(parentEntityRef, Arrays.asList(childEntityRef));
        }
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
