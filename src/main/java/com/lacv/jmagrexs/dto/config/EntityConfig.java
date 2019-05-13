/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto.config;

import com.lacv.jmagrexs.annotation.LabelField;
import com.lacv.jmagrexs.dto.GridTemplate;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.dto.ProcessForm;
import com.lacv.jmagrexs.dto.ProcessGlobalAction;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author desarrollador
 */
public class EntityConfig {
    
    public static String TCV_1_TO_N= "tcv_1_to_n";
    
    public static String TCV_1_TO_1= "tcv_1_to_1";
    
    public static String TCV_N_TO_N= "tcv_n_to_n";
    
    private EntityService entityService;
    
    private Class dtoClass;
    
    private String entityRef;
    
    private String pathRef;
    
    private String entityName;
    
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
    
    private boolean activeGridTemplate;
    
    private boolean activeGridTemplateAsParent;
    
    private boolean activeGridTemplateAsChild;
    
    private boolean activeNNMulticheckChild;
    
    private boolean restSession;
    
    private boolean preloadedForm;
    
    private boolean labelPlusId;
    
    private String entityRefNNMulticheckChild;
    
    private int gridHeightChildView;
    
    private String defaultOrderBy;
    
    private String defaultOrderDir;
    
    private Long maxResultsPerPage;
    
    private final Map<String, String> internalViewButton;
    
    private final Map<String, Class> childExtViews;
    
    private final Map<String, String> typeChildExtViews;
    
    private final Map<String, List<String>> comboboxChildDependent;
    
    private final List<ProcessButton> processButtons;
    
    private final List<ProcessGlobalAction> processGlobalActions;
    
    private final List<ProcessForm> processForms;
    
    private GridTemplate gridTemplate;
    
    
    public EntityConfig(String entityRef, EntityService entityService, Class dtoClass) {
        this.entityRef= entityRef;
        this.pathRef= entityRef;
        this.entityName= entityService.getEntityClass().getSimpleName();
        this.labelField= "id";
        this.entityService= entityService;
        this.dtoClass= dtoClass;
        this.pluralEntityTitle= this.entityName + "s";
        this.singularEntityTitle= this.entityName;
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
        this.activeGridTemplate= false;
        this.activeGridTemplateAsParent= false;
        this.activeGridTemplateAsChild= false;
        this.activeNNMulticheckChild= false;
        this.restSession= false;
        this.preloadedForm= false;
        this.labelPlusId= false;
        this.entityRefNNMulticheckChild= null;
        this.gridHeightChildView= 0;
        this.defaultOrderBy= "id";
        this.defaultOrderDir= "DESC";
        this.maxResultsPerPage= 50L;
        this.internalViewButton= new LinkedHashMap<>();
        this.childExtViews= new LinkedHashMap<>();
        this.typeChildExtViews= new HashMap<>();
        this.comboboxChildDependent= new HashMap<>();
        this.processButtons= new ArrayList<>();
        this.processGlobalActions= new ArrayList<>();
        this.processForms= new ArrayList<>();
        this.gridTemplate= new GridTemplate("");
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
     * 
     * @return pathRef
     */
    public String getPathRef() {
        return pathRef;
    }

    /**
     * 
     * @param pathRef 
     */
    public void setPathRef(String pathRef) {
        this.pathRef = pathRef;
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
     * 
     * @return preloadedForm
     */
    public boolean isPreloadedForm() {
        return preloadedForm;
    }

    /**
     * 
     * @param preloadedForm 
     */
    public void setPreloadedForm(boolean preloadedForm) {
        this.preloadedForm = preloadedForm;
        if(this.preloadedForm){
            visibleFilters= false;
            visibleGrid= false;
        }
    }
    
    /**
     * 
     * @return labelPlusId
     */
    public boolean isLabelPlusId() {
        return labelPlusId;
    }

    /**
     * 
     * @param labelPlusId 
     */
    public void setLabelPlusId(boolean labelPlusId) {
        this.labelPlusId = labelPlusId;
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
     * @return processButtons
     */
    public List<ProcessButton> getProcessButtons() {
        return processButtons;
    }
    
    /**
     * 
     * @param processButton 
     */
    public void addProcessButton(ProcessButton processButton){
        processButtons.add(processButton);
        ProcessForm processForm= new ProcessForm(processButton.getMainProcessRef(), processButton.getProcessName(), processButton.getProcessTitle());
        getProcessForms().add(processForm);
    }

    /**
     * @return the processGlobalActions
     */
    public List<ProcessGlobalAction> getProcessGlobalActions() {
        return processGlobalActions;
    }
    
    /**
     * @param processGlobalAction
     */
    public void addProcessGlobalActions(ProcessGlobalAction processGlobalAction) {
        this.processGlobalActions.add(processGlobalAction);
        ProcessForm processForm= new ProcessForm(processGlobalAction.getMainProcessRef(), processGlobalAction.getProcessName(), processGlobalAction.getProcessTitle());
        getProcessForms().add(processForm);
    }

    /**
     * @return the processForms
     */
    public List<ProcessForm> getProcessForms() {
        return processForms;
    }
    
}
