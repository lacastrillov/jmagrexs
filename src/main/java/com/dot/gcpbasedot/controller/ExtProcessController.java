package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.dto.config.ProcessConfig;
import com.dot.gcpbasedot.enums.FieldType;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.reflection.EntityReflection;
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
import com.dot.gcpbasedot.components.FieldConfigurationByAnnotations;
import com.dot.gcpbasedot.components.JSONFilters;
import com.dot.gcpbasedot.components.JSONForms;
import com.dot.gcpbasedot.components.JSONModels;
import com.dot.gcpbasedot.components.RangeFunctions;
import com.dot.gcpbasedot.enums.PageType;
import com.dot.gcpbasedot.util.Formats;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtProcessController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtProcessController.class);
    
    private ProcessConfig processConfig;
    
    @Autowired
    private FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public RangeFunctions rf;
    
    @Autowired
    public JSONModels jm;
    
    @Autowired
    public JSONFilters jf;
    
    @Autowired
    public JSONForms jfo;
    
    
    protected void addControlMapping(ProcessConfig processConfig) {
        this.processConfig= processConfig;
    }

    @RequestMapping(value = "/process.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView process() {
        ModelAndView mav= new ModelAndView("process");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("basePath", globalMenuComponent.getBasePath());
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/process/ExtViewport");
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        if(globalMenuComponent!=null){
            JSONArray menuItems= getMenuItems(session, globalMenuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInit.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInit() {
        ModelAndView mav= new ModelAndView("scripts/process/ExtInit");
        
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtModel.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extModel() {
        ModelAndView mav= new ModelAndView("scripts/process/ExtModel");
        
        Map<String, String> nameProcesses= processConfig.getNameProcesses();
        Map<String, Class> inDtos= processConfig.getInDtos();
        
        Map<String, String> jsonModelMap= new HashMap();
        Map<String, String> jsonModelValidationsMap= new HashMap();
        
        for (Map.Entry<String, Class> entry : inDtos.entrySet()){
            JSONArray jsonModel = jm.getJSONRecursiveModel("", entry.getValue(), processConfig.getDateFormat());
            JSONArray jsonModelValidations= jm.getJSONRecursiveModelValidations("",entry.getValue());
            jsonModelMap.put(entry.getKey(), jsonModel.toString());
            jsonModelValidationsMap.put(entry.getKey(), jsonModelValidations.toString());
        }
        
        //
        JSONArray jsonModelLogProcess = jm.getJSONModel(processConfig.getLogProcessClass(), processConfig.getDateFormat());
        JSONArray jsonModelValidationsLogProcess= jm.getJSONModelValidations(processConfig.getLogProcessClass());
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("nameProcesses", nameProcesses);
        mav.addObject("jsonModelMap", jsonModelMap);
        mav.addObject("jsonModelValidationsMap", jsonModelValidationsMap);
        mav.addObject("jsonModelLogProcess", jsonModelLogProcess);
        mav.addObject("jsonModelValidationsLogProcess", jsonModelValidationsLogProcess);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore() {
        ModelAndView mav= new ModelAndView("scripts/process/ExtStore");
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("entityRefLogProcess", processConfig.getEntityRefLogProcess());
        mav.addObject("entityNameLogProcess", processConfig.getEntityNameLogProcess());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView(@RequestParam(required = true) String typeView) {
        ModelAndView mav= new ModelAndView("scripts/process/ExtView");
        
        if(typeView.equals("Parent")){
            processConfig.setActiveGridTemplate(processConfig.isActiveGridTemplateAsParent());
        }
        if(typeView.equals("Child")){
            processConfig.setActiveGridTemplate(processConfig.isActiveGridTemplateAsChild());
        }
        mav.addObject("typeView",typeView);
        addGeneralObjects(mav);
        
        Map<String, String> nameProcesses= processConfig.getNameProcesses();
        Map<String, Class> inDtos= processConfig.getInDtos();
        
        Map<String, String> jsonFormFieldsMap= new HashMap();
        
        for (Map.Entry<String, Class> entry : inDtos.entrySet()){
            JSONArray jsonFormFields = jfo.getJSONProcessForm(entry.getKey(), "", entry.getValue(), processConfig.getDateFormat());
            jsonFormFieldsMap.put(entry.getKey(), jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        }
        mav.addObject("nameProcesses", nameProcesses);
        mav.addObject("jsonFormFieldsMap", jsonFormFieldsMap);
        
        addEntityExtProcessConfiguration(mav);
        if(processConfig.isVisibleFilters()){
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(
                    processConfig.getLogProcessClass(), processConfig.getLabelField(), processConfig.getDateFormat(), PageType.PROCESS);
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtController.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extController(@RequestParam(required = true) String typeController) {
        ModelAndView mav= new ModelAndView("scripts/process/ExtController");
        
        mav.addObject("typeController", typeController);
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(processConfig.getTypeChildExtViews()));
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInterfaces.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInterfaces() {
        ModelAndView mav= new ModelAndView("scripts/process/ExtInterfaces");
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("labelField", processConfig.getLabelField());
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav){
        List<String> modelsEntityRef= new ArrayList<>();
        List<String> viewsChildEntityRef= new ArrayList<>();
        List<String> interfacesEntityRef= new ArrayList<>();
        List<String> interfacesChildEntityRef= new ArrayList<>();
        
        modelsEntityRef.add(processConfig.getMainProcessRef());
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("labelField", processConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
        mav.addObject("viewsChildEntityRef", viewsChildEntityRef);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
        mav.addObject("interfacesChildEntityRef", interfacesChildEntityRef);
    }
    
    private void addEntityExtProcessConfiguration(ModelAndView mav){
        JSONArray jsonInternalViewButtons= new JSONArray();
        JSONArray jsonGridColumns= new JSONArray();
        JSONObject jsonEmptyModel= new JSONObject();
        
        Class entityClass= processConfig.getLogProcessClass();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, entityClass, processConfig.getLabelField());
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, entityClass);
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, entityClass);
        HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(propertyDescriptors, entityClass);
        HashSet<String> hideFields= fcba.getHideFields(entityClass);
        HashSet<String> fieldsNN= fcba.getNotNullFields(entityClass);
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(entityClass);
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(entityClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widhColumn= widhColumnMap.get(fieldName);
                boolean readOnly= fieldsRO.contains(fieldName);
                
                // ADD TO jsonGridColumns
                if(processConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name()) && !processConfig.isActiveGridTemplate()){
                    JSONObject gridColumn= new JSONObject();
                    gridColumn.put("dataIndex", fieldName);
                    gridColumn.put("header", fieldTitle);
                    gridColumn.put("width", widhColumn);
                    if(Formats.TYPES_LIST.contains(type)){
                        gridColumn.put("sortable", true);
                        if(typeFormFields.containsKey(fieldName)){
                            String typeForm= typeFormFields.get(fieldName)[0];
                            if(typeForm.equals(FieldType.EMAIL.name())){
                                JSONObject editor= new JSONObject();
                                editor.put("vtype", "email");
                                if(fieldsNN.contains(fieldName)){
                                    editor.put("allowBlank", false);
                                }
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                JSONObject editor= new JSONObject();
                                editor.put("inputType", "password");
                                if(fieldsNN.contains(fieldName)){
                                    editor.put("allowBlank", false);
                                }
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.LIST.name())){
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+")#");
                                }
                            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.URL.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                            }else{
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(processConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }
                        }else{
                            if(fieldName.equals(processConfig.getLabelField())){
                                gridColumn.put("renderer", "#"+processConfig.getLabelField()+"EntityRender#");
                            }
                            switch (type) {
                                case "java.util.Date": {
                                    gridColumn.put("xtype", "datecolumn");
                                    gridColumn.put("format", processConfig.getDateFormat());
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "datefield");
                                    editor.put("format", processConfig.getDateFormat());
                                    if (fieldsNN.contains(fieldName)) {
                                        editor.put("allowBlank", false);
                                    }
                                    if (processConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "java.sql.Time": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "timefield");
                                    if (fieldsNN.contains(fieldName)) {
                                        editor.put("allowBlank", false);
                                    }
                                    if (processConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "int":
                                case "java.lang.Integer":
                                case "long":
                                case "java.lang.Long":
                                case "java.math.BigInteger":
                                case "double":
                                case "java.lang.Double":
                                case "float":
                                case "java.lang.Float": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "numberfield");
                                    if (fieldsNN.contains(fieldName)) {
                                        editor.put("allowBlank", false);
                                    }
                                    if (processConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "boolean":
                                case "java.lang.Boolean": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "checkbox");
                                    editor.put("cls", "x-grid-checkheader-editor");
                                    if (processConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                default:
                                    JSONObject field = new JSONObject();
                                    field.put("type", "textfield");
                                    if (processConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("field", field);
                                    }
                                    break;
                            }
                        }
                    }else{
                        gridColumn.put("renderer", "#Instance.combobox"+fieldEntity+"Render#");
                        if(processConfig.isEditableGrid() && !readOnly){
                            gridColumn.put("editor", "#Instance.gridCombobox"+fieldEntity+"#");
                        }
                    }
                    jsonGridColumns.put(gridColumn);
                }
                    
                // ADD TO jsonEmptyModel
                if(fieldName.equals("id")==false){
                    jsonEmptyModel.put(fieldName, (defaultValueMap.containsKey(fieldName))?defaultValueMap.get(fieldName):"");
                }
            }
        }
        
        if(processConfig.isActiveGridTemplate()){
            if(processConfig.getGridTemplate()!=null){
                for(int i=0; i<processConfig.getGridTemplate().getNumColumns(); i++){
                    JSONObject gridColumn= new JSONObject();
                    gridColumn.put("dataIndex", "column"+i);
                    gridColumn.put("header", "Column"+i);
                    gridColumn.put("flex", 1);
                    jsonGridColumns.put(gridColumn);
                }
            }
        }
        
        for (Map.Entry<String, String> entry : processConfig.getInternalViewButton().entrySet()) {
            JSONObject internalViewButton= new JSONObject();
            internalViewButton.put("text", entry.getValue());
            internalViewButton.put("scope", "#this#");
            internalViewButton.put("scale", "medium");
            internalViewButton.put("handler", "#function(){parentExtController.viewInternalPage('/vista/"+entry.getKey()+"/table.htm')}#");
            
            jsonInternalViewButtons.put(internalViewButton);
        }
        
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(processConfig.getTypeChildExtViews()));
    }

}
