package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.EntityConfig;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import com.google.gson.Gson;
import com.lacv.jmagrexs.dto.ProcessGlobalAction;
import javax.persistence.Embeddable;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtEntityController extends ExtReportController {
    
    protected static final Logger LOGGER1 = Logger.getLogger(ExtEntityController.class);
    
    private final List<String> modelsEntityRef= new ArrayList<>();
    
    private final List<String> viewsChildEntityRef= new ArrayList<>();
    
    private final List<String> interfacesEntityRef= new ArrayList<>();
    
    private final List<String> interfacesChildEntityRef= new ArrayList<>();
    
    private JSONArray jsonModel;
    
    private final JSONArray jsonTemplateModel = new JSONArray();;
    
    private JSONArray jsonModelValidations;
    
    private JSONArray jsonFieldsFilters;
    
    private final JSONArray jsonFormFields= new JSONArray();
    
    private final JSONArray jsonInternalViewButtons= new JSONArray();
    
    private final JSONArray jsonGridColumns= new JSONArray();
    
    private final JSONArray sortColumns= new JSONArray();
    
    private final JSONArray jsonGlobalActions= new JSONArray();
    
    private final JSONObject jsonEmptyModel= new JSONObject();
    
    private final JSONObject jsonDefaultModel= new JSONObject();
    
    private final Map<String, String> jsonFormFieldsProcessMap= new HashMap();
    
    private HashMap<String, String> titledFieldsMap;
    
    private EntityConfig viewConfig;
    
    
    protected void addControlMapping(EntityConfig viewConfig) {
        this.viewConfig= viewConfig;
        generateGeneralObjects();
        generateEntityExtViewConfiguration();
    }

    protected void addControlMapping(String entityRef, EntityService entityService, Class dtoClass) {
        viewConfig= new EntityConfig(entityRef, entityService, dtoClass);
        generateGeneralObjects();
        generateEntityExtViewConfiguration();
    }

    @RequestMapping(value = "/entity.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView entity(@RequestParam(required = false) Boolean onlyForm) {
        ModelAndView mav= new ModelAndView("entity");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("serverDomain", serverDomain);
        if(onlyForm!=null){
            mav.addObject("onlyForm", onlyForm);
        }else{
            mav.addObject("onlyForm", false);
        }
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(@RequestParam(required = false) Boolean onlyForm, HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtViewport");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("onlyForm", onlyForm);
        JSONArray menuItems= getMenuItems(session, menuComponent);
        mav.addObject("menuItems",menuItems.toString());
        if(viewConfig.isVisibleFilters()){
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInit.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInit() {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtInit");
        
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtModel.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extModel() {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtModel");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("jsonModel", jsonModel.toString());
        mav.addObject("jsonTemplateModel", jsonTemplateModel.toString());
        mav.addObject("jsonModelValidations", jsonModelValidations.toString());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore(@RequestParam(required = false) Boolean restSession, @RequestParam(required = false) Boolean jsLib) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtStore");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        if(restSession==null){
            mav.addObject("restSession", viewConfig.isRestSession());
        }else{
            mav.addObject("restSession", restSession);
        }
        mav.addObject("jsLib", jsLib);
        if(jsLib!=null){
            mav.addObject("serverDomain", serverDomain);
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView(@RequestParam(required = true) String typeView, @RequestParam(required = false) Boolean onlyForm) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtView");
        mav.addObject("serverDomain", serverDomain);
        if(typeView.equals("Parent")){
            viewConfig.setActiveGridTemplate(viewConfig.isActiveGridTemplateAsParent());
        }
        if(typeView.equals("Child")){
            viewConfig.setActiveGridTemplate(viewConfig.isActiveGridTemplateAsChild());
        }
        mav.addObject("typeView",typeView);
        mav.addObject("onlyForm",onlyForm);
        addGeneralObjects(mav);
        
        //addEntityExtViewConfiguration(mav);
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonGlobalActions", jsonGlobalActions.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("jsonDefaultModel", jsonDefaultModel.toString());
        mav.addObject("sortColumns", sortColumns.toString());
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(viewConfig.getTypeChildExtViews()));
        mav.addObject("jsonFormFieldsProcessMap", jsonFormFieldsProcessMap);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtController.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extController(@RequestParam(required = true) String typeController) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtController");
        
        mav.addObject("typeController", typeController);
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(viewConfig.getTypeChildExtViews()));
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInterfaces.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInterfaces() {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtInterfaces");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("labelField", viewConfig.getLabelField());
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav){
        if(viewConfig.isPreloadedForm()){
            mav.addObject("formRecordId", getFormRecordId());
        }
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("labelField", viewConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
        mav.addObject("viewsChildEntityRef", viewsChildEntityRef);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
        mav.addObject("interfacesChildEntityRef", interfacesChildEntityRef);
    }
    
    private void generateGeneralObjects(){
        List<String> associatedEntityRef= getAssociatedEntityRef(viewConfig.getEntityService().getEntityClass());
        for(String er: associatedEntityRef){
            modelsEntityRef.add(er);
            interfacesEntityRef.add(er);
        }
        
        for(Map.Entry<String,Class> entry: viewConfig.getChildExtViews().entrySet()){
            if(!modelsEntityRef.contains(entry.getKey()) && !entry.getKey().equals(viewConfig.getEntityRef())){
                modelsEntityRef.add(entry.getKey());
            }
            viewsChildEntityRef.add(entry.getKey());
            List<String> childExtViews= getAssociatedEntityRef(entry.getValue());
            for(String er: childExtViews){
                if(!modelsEntityRef.contains(er) && !er.equals(viewConfig.getEntityRef())){
                    modelsEntityRef.add(er);
                }
                if(!interfacesEntityRef.contains(er)){
                    interfacesChildEntityRef.add(er);
                }
            }
        }
        
        jsonModel = jm.getJSONModel(viewConfig.getDtoClass());
        jsonModelValidations= jm.getJSONModelValidations(viewConfig.getDtoClass());
        
        if(viewConfig.getGridTemplate()!=null){
            for(int i=0; i<viewConfig.getGridTemplate().getNumColumns(); i++){
                JSONObject field= new JSONObject();
                field.put("name", "column"+i);
                field.put("type", "string");
                jsonTemplateModel.put(field);
            }
        }
        
        jsonFieldsFilters= jf.getFieldsFilters(viewConfig.getDtoClass(), viewConfig.getLabelField(), PageType.ENTITY);
        
    }
    
    private List<String> getAssociatedEntityRef(Class entityClass){
        List<String> associatedEntityRef= new ArrayList<>();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String simpleType= StringUtils.uncapitalize(propertyDescriptor.getPropertyType().getSimpleName());
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false && propertyDescriptor.getPropertyType().getAnnotation(Embeddable.class)==null){
                //String fieldName= propertyDescriptor.getName();
                if(!Formats.TYPES_LIST.contains(type) && !associatedEntityRef.contains(simpleType)){
                    associatedEntityRef.add(simpleType);
                }
            }
        }
        
        return associatedEntityRef;
    }
    
    private void generateEntityExtViewConfiguration(){
        Class entityClass= viewConfig.getEntityService().getEntityClass();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, viewConfig.getDtoClass(), viewConfig.getLabelField());
        
        HashMap<String, Integer> widthColumnMap= fcba.getWidthColumnMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(viewConfig.getDtoClass());
        HashMap<String, String> groupFieldsMap= fcba.getGroupFieldsMap(viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(viewConfig.getDtoClass());
        titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        positionColumnForm.put("", 0);
        
        if(!viewConfig.isActiveGridTemplate()){
            JSONObject numbererColumn= new JSONObject();
            numbererColumn.put("xtype", "rownumberer");
            numbererColumn.put("width", 40);
            numbererColumn.put("sortable", false);
            numbererColumn.put("renderer", "@Instance.commonExtView.numbererGridRender@");
            jsonGridColumns.put(numbererColumn);
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            if(propertyDescriptor.getPropertyType().getAnnotation(Embeddable.class)!=null){
                type= "java.lang.String";
            }
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widthColumn= widthColumnMap.get(fieldName);
                boolean readOnly= fieldsRO.contains(fieldName);
                
                // ADD TO jsonFormFields
                if(viewConfig.isVisibleForm() && !hideFields.contains(fieldName + HideView.FORM.name())){
                    String titleGroup="";
                    if(groupFieldsMap.containsKey(fieldName)){
                        titleGroup= groupFieldsMap.get(fieldName);
                        if(!fieldGroups.containsKey(titleGroup)){
                            JSONObject fieldDefaults= new JSONObject();
                            fieldDefaults.put("anchor", "49%");
                            fieldDefaults.put("minWidth", 280);
                            fieldDefaults.put("labelAlign", "right");
                            
                            JSONObject objectField= new JSONObject();
                            objectField.put("xtype", "fieldset");
                            objectField.put("title", titleGroup);
                            objectField.put("collapsible", true);
                            objectField.put("layout", "anchor");
                            objectField.put("defaultType", "textfield");
                            objectField.put("cls", "my-fieldset");
                            objectField.put("fieldDefaults", fieldDefaults);
                            objectField.put("items", new JSONArray());
                            
                            fieldGroups.put(titleGroup, objectField);
                            positionColumnForm.put(titleGroup, 0);
                        }
                    }
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        jfef.addJSONField(jsonFormFields, entityClass.getSimpleName(), type, fieldName,
                                fieldTitle, titleGroup, typeFormFields, sizeColumnMap, fieldGroups,
                                positionColumnForm, viewConfig.getNumColumnsForm(), viewConfig.isEditableForm(),
                                readOnly, fieldsNN.contains(fieldName));
                        
                    }else{
                        jfef.addEntityCombobox(jsonFormFields, fieldEntity, viewConfig.isEditableForm(),
                                viewConfig.getNumColumnsForm(), titleGroup, fieldGroups, positionColumnForm,
                                readOnly, fieldsNN.contains(fieldName));
                        
                    }
                }
                
                // ADD TO jsonGridColumns
                if(viewConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(viewConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name()) && !viewConfig.isActiveGridTemplate()){
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        jc.addJSONColumn(jsonGridColumns, type, fieldName, fieldTitle, widthColumn, typeFormFields, viewConfig.getLabelField(),
                                sizeColumnMap, viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }else{
                        jc.addEntityCombobox(jsonGridColumns, fieldName, fieldTitle, fieldEntity, widthColumn,
                                viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }
                }
                    
                // ADD TO jsonEmptyModel
                if(fieldName.equals("id")==false){
                    jsonEmptyModel.put(fieldName, (defaultValueMap.containsKey(fieldName))?defaultValueMap.get(fieldName):"");
                }
                
                // ADD TO jsonDefaultModel
                if(fieldName.equals("id")){
                    jsonDefaultModel.put(fieldName, -1);
                }else{
                    String typeField="";
                    if(typeFormFields.containsKey(fieldName)){
                        typeField= typeFormFields.get(fieldName)[0];
                    }
                    jsonDefaultModel.put(fieldName, Formats.getDefaultValueByType(type, typeField));
                }
            }
        }
        //ADD fieldGroups in FORM
        for (Map.Entry<String, JSONObject> group : fieldGroups.entrySet()) {
            jsonFormFields.put(group.getValue());
        }
        
        if(viewConfig.isActiveGridTemplate()){
            if(viewConfig.getGridTemplate()!=null){
                for(int i=0; i<viewConfig.getGridTemplate().getNumColumns(); i++){
                    JSONObject gridColumn= new JSONObject();
                    gridColumn.put("dataIndex", "column"+i);
                    gridColumn.put("header", "Column"+i);
                    gridColumn.put("flex", 1);
                    jsonGridColumns.put(gridColumn);
                }
            }
        }
        
        for (Map.Entry<String, String> entry : viewConfig.getInternalViewButton().entrySet()) {
            JSONObject internalViewButton= new JSONObject();
            internalViewButton.put("text", entry.getValue());
            internalViewButton.put("scope", "@this@");
            internalViewButton.put("scale", "medium");
            internalViewButton.put("handler", "@function(){parentExtController.viewInternalPage('"+serverDomain.getApplicationContext() + serverDomain.getAdminContext() + serverDomain.getAdminPath()+"/"+entry.getKey()+"/entity.htm')}@");
            
            jsonInternalViewButtons.put(internalViewButton);
        }
        
        if(viewConfig.getProcessButtons().size()>0){
            Gson gs= new Gson();
            JSONObject gridColumn= new JSONObject();
            gridColumn.put("xtype", "actioncolumn");
            gridColumn.put("width", (viewConfig.getProcessButtons().size()*33));
            gridColumn.put("sortable", false);
            gridColumn.put("menuDisabled", true);
            JSONArray gridActions= new JSONArray();
            for(ProcessButton processButton: viewConfig.getProcessButtons()){
                String sourceByDestinationFields= gs.toJson(processButton.getSourceByDestinationFields()).replaceAll("\"", "'");
                //ADD Button in Grid
                JSONObject gridAction= new JSONObject();
                gridAction.put("tooltip", processButton.getProcessTitle());
                gridAction.put("scope", "@this@");
                gridAction.put("icon", processButton.getIconUrl());
                gridAction.put("handler", "@function (grid, rowIndex, colIndex) {" +
                                          "     Instance.showProcessForm('"+processButton.getProcessName()+"', "+sourceByDestinationFields+", rowIndex);" +
                                          "}@");
                
                gridActions.put(gridAction);
                gridActions.put("-");
                
                //ADD Button in Form
                JSONObject internalViewButton= new JSONObject();
                internalViewButton.put("text", processButton.getProcessTitle());
                internalViewButton.put("scope", "@this@");
                internalViewButton.put("scale", "medium");
                internalViewButton.put("style", "background-image: url("+processButton.getIconUrl()+") !important;background-position: left center;background-repeat: no-repeat;background-size: 25px 25px;padding-left: 20px;");
                internalViewButton.put("handler", "@function(){"+
                                                  "     Instance.showProcessForm('"+processButton.getProcessName()+"', "+sourceByDestinationFields+", -1);"+
                                                  "}@");
                
                jsonInternalViewButtons.put(internalViewButton);
                
                //Add Form Fields by Process
                JSONArray jsonFormFieldsProcess = jfo.getJSONProcessForm(processButton.getProcessName(), "", processButton.getDtoClass());
                jsonFormFieldsProcessMap.put(processButton.getProcessName(), jsonFormFieldsProcess.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
            }
            gridColumn.put("items", gridActions);
            jsonGridColumns.put(gridColumn);
        }
        
        if(viewConfig.getProcessGlobalActions().size()>0){
            for(ProcessGlobalAction processGlobalActions: viewConfig.getProcessGlobalActions()){
                //ADD Global Action
                JSONObject globalAction= new JSONObject();
                globalAction.put("text", processGlobalActions.getProcessTitle());
                globalAction.put("scope", "@this@");
                globalAction.put("icon", processGlobalActions.getIconUrl());
                globalAction.put("handler", "@function () {" +
                                          "     Instance.showGlobalProcessForm('"+processGlobalActions.getProcessName()+"', '"+processGlobalActions.getIdsField()+"');" +
                                          "}@");
                
                jsonGlobalActions.put(globalAction);
                
                //Add Form Fields by Process
                JSONArray jsonFormFieldsProcess = jfo.getJSONProcessForm(processGlobalActions.getProcessName(), "", processGlobalActions.getDtoClass());
                jsonFormFieldsProcessMap.put(processGlobalActions.getProcessName(), jsonFormFieldsProcess.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
            }
        }
        
    }
    
    public Object getFormRecordId(){
        return null;
    }
    
}
