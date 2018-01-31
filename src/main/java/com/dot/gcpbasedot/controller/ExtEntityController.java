package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.dto.config.EntityConfig;
import com.dot.gcpbasedot.enums.FieldType;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
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
import com.dot.gcpbasedot.dto.ProcessButton;
import com.dot.gcpbasedot.enums.PageType;
import com.dot.gcpbasedot.util.Formats;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtEntityController extends ExtReportController {

    protected static final Logger LOGGER1 = Logger.getLogger(ExtEntityController.class);
    
    private EntityConfig viewConfig;
    
    
    protected void addControlMapping(EntityConfig viewConfig) {
        this.viewConfig= viewConfig;
    }

    protected void addControlMapping(String entityRef, EntityService entityService, Class dtoClass) {
        viewConfig= new EntityConfig(entityRef, entityService, dtoClass);
    }

    @RequestMapping(value = "/entity.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView entity() {
        ModelAndView mav= new ModelAndView("entity");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("basePath", menuComponent.getBasePath());
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtViewport");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        if(viewConfig.isVisibleFilters()){
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(
                    viewConfig.getDtoClass(), viewConfig.getLabelField(), viewConfig.getDateFormat(), PageType.ENTITY);
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
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
        
        JSONArray jsonModel = jm.getJSONModel(viewConfig.getDtoClass(), viewConfig.getDateFormat());
        JSONArray jsonTemplateModel = new JSONArray();
        JSONArray jsonModelValidations= jm.getJSONModelValidations(viewConfig.getDtoClass());
        
        if(viewConfig.isActiveGridTemplateAsParent() || viewConfig.isActiveGridTemplateAsChild()){
            if(viewConfig.getGridTemplate()!=null){
                for(int i=0; i<viewConfig.getGridTemplate().getNumColumns(); i++){
                    JSONObject field= new JSONObject();
                    field.put("name", "column"+i);
                    field.put("type", "string");
                    jsonTemplateModel.put(field);
                }
            }
        }
        
        //Process Models
        if(viewConfig.getProcessButtons().size()>0){
            Map<String, String> jsonProcessModelMap= new HashMap();
            Map<String, String> jsonProcessModelValidationsMap= new HashMap();

            for(ProcessButton processButton: viewConfig.getProcessButtons()){
                JSONArray jsonProcessModel = jm.getJSONRecursiveModel("", processButton.getDtoClass(), processButton.getDateFormat());
                JSONArray jsonProcessModelValidations= jm.getJSONRecursiveModelValidations("",processButton.getDtoClass());
                jsonProcessModelMap.put(processButton.getProcessName(), jsonProcessModel.toString());
                jsonProcessModelValidationsMap.put(processButton.getProcessName(), jsonProcessModelValidations.toString());
            }
        }
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("jsonModel", jsonModel.toString());
        mav.addObject("jsonTemplateModel", jsonTemplateModel.toString());
        mav.addObject("jsonModelValidations", jsonModelValidations.toString());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore(@RequestParam(required = false) Boolean restSession) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtStore");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        if(restSession==null){
            mav.addObject("restSession", viewConfig.isRestSession());
        }else{
            mav.addObject("restSession", restSession);
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView(@RequestParam(required = true) String typeView) {
        ModelAndView mav= new ModelAndView("scripts/entity/ExtView");
        
        if(typeView.equals("Parent")){
            viewConfig.setActiveGridTemplate(viewConfig.isActiveGridTemplateAsParent());
        }
        if(typeView.equals("Child")){
            viewConfig.setActiveGridTemplate(viewConfig.isActiveGridTemplateAsChild());
        }
        mav.addObject("typeView",typeView);
        addGeneralObjects(mav);
        addEntityExtViewConfiguration(mav);
        
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
        List<String> modelsEntityRef= new ArrayList<>();
        List<String> viewsChildEntityRef= new ArrayList<>();
        List<String> interfacesEntityRef= new ArrayList<>();
        List<String> interfacesChildEntityRef= new ArrayList<>();
        
        //modelsEntityRef.add(viewConfig.getEntityRef());
        
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
    
    private List<String> getAssociatedEntityRef(Class entityClass){
        List<String> associatedEntityRef= new ArrayList<>();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String simpleType= StringUtils.uncapitalize(propertyDescriptor.getPropertyType().getSimpleName());
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                //String fieldName= propertyDescriptor.getName();
                if(!Formats.TYPES_LIST.contains(type) && !associatedEntityRef.contains(simpleType)){
                    associatedEntityRef.add(simpleType);
                }
            }
        }
        
        return associatedEntityRef;
    }
    
    private void addEntityExtViewConfiguration(ModelAndView mav){
        JSONArray jsonFormFields= new JSONArray();
        JSONArray jsonRenderReplacements= new JSONArray();
        JSONArray jsonInternalViewButtons= new JSONArray();
        JSONArray jsonGridColumns= new JSONArray();
        JSONArray sortColumns= new JSONArray();
        LinkedHashMap<String,JSONObject> fieldGroups= new LinkedHashMap<>();
        JSONObject jsonEmptyModel= new JSONObject();
        Map<String, String> jsonFormFieldsProcessMap= new HashMap();
        
        Class entityClass= viewConfig.getEntityService().getEntityClass();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, viewConfig.getDtoClass(), viewConfig.getLabelField());
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, String> groupFieldsMap= fcba.getGroupFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widhColumn= widhColumnMap.get(fieldName);
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
                        }
                    }
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        boolean addFormField= true;
                        JSONObject formField= new JSONObject();
                        formField.put("name", fieldName);
                        formField.put("fieldLabel", fieldTitle);
                        if(!viewConfig.isEditableForm() || readOnly){
                            formField.put("readOnly", true);
                        }
                        if(typeFormFields.containsKey(fieldName)){
                            String typeForm= typeFormFields.get(fieldName)[0];
                            if(typeForm.equals(FieldType.EMAIL.name())){
                                formField.put("vtype", "email");
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                formField.put("inputType", "password");
                            }else if(typeForm.equals(FieldType.TEXT_AREA.name())){
                                formField.put("xtype", "textarea");
                                formField.put("height", 200);
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                                formField.put("xtype", "htmleditor");
                                formField.put("enableColors", true);
                                formField.put("enableAlignments", true);
                                formField.put("height", 400);
                            }else if(typeForm.equals(FieldType.LIST.name())){
                                addFormField= false;
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                String field= "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+")#";
                                addFormField(field,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                                formField.put("id", "form" + entityClass.getSimpleName() + "_" +fieldName + "LinkField");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Url Youtube");
                                
                                //Add Video Youtube
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Google Maps Point");
                                
                                //Add GoogleMap
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                                formField.put("name", fieldName + "_File");
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Url File
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.fileRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                formField.put("name", fieldName + "_File");
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione una imagen");
                                
                                //Add Image
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.imageRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                                formField.put("name", fieldName + "_File");
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un video");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                formField.put("name", fieldName + "_File");
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un audio");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                formField.put("name", fieldName + "_File");
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                                addFormField(renderField,jsonFormFields,fieldGroups,titleGroup);
                            }
                            if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                //Add link Field
                                JSONObject linkField= new JSONObject();
                                linkField.put("id", "form"+entityClass.getSimpleName()+"_"+fieldName + "LinkField");
                                linkField.put("name", fieldName);
                                linkField.put("fieldLabel", "&nbsp;");
                                addFormField(linkField,jsonFormFields,fieldGroups,titleGroup);
                            }
                        }else{
                            switch (type) {
                                case "java.util.Date":
                                    formField.put("xtype", "datefield");
                                    formField.put("format", viewConfig.getDateFormat());
                                    formField.put("tooltip", "Seleccione la fecha");
                                    break;
                                case "java.sql.Time":
                                    formField.put("xtype", "timefield");
                                    formField.put("format", "h:i:s A");
                                    formField.put("tooltip", "Seleccione la hora");
                                    break;
                                case "int":
                                case "java.lang.Integer":
                                case "long":
                                case "java.lang.Long":
                                case "java.math.BigInteger":
                                case "double":
                                case "java.lang.Double":
                                case "float":
                                case "java.lang.Float":
                                    formField.put("xtype", "numberfield");
                                    break;
                                case "boolean":
                                case "java.lang.Boolean":
                                    formField.put("xtype", "checkbox");
                                    formField.put("inputValue", "true");
                                    formField.put("uncheckedValue", "false");
                                    break;
                            }
                        }
                        
                        if(fieldsNN.contains(fieldName)){
                            formField.put("allowBlank", false);
                        }
                        if(addFormField){
                            addFormField(formField,jsonFormFields,fieldGroups,titleGroup);
                        }
                    }else{
                        String combobox="(function(){ ";
                        if(!viewConfig.isEditableForm() || readOnly){
                            combobox+="Instance.formCombobox"+fieldEntity+".setDisabled(true); ";
                        }
                        combobox+="return Instance.formCombobox"+fieldEntity+";" +
                                        "})()";
                        addFormField("#"+combobox+"#",jsonFormFields,fieldGroups,titleGroup);
                    }
                }
                
                
                // ADD TO jsonRenderReplacements
                if(viewConfig.isVisibleForm() && !Formats.TYPES_LIST.contains(type)){
                    JSONObject renderReplacement= new JSONObject();
                    renderReplacement.put("component", "#Instance.formCombobox"+fieldEntity+"#");
                    JSONObject replace= new JSONObject();
                    replace.put("field", fieldName);
                    replace.put("attribute", "id");
                    renderReplacement.put("replace", replace);
                    jsonRenderReplacements.put(renderReplacement);
                }
                
                // ADD TO jsonGridColumns
                if(viewConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(viewConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name()) && !viewConfig.isActiveGridTemplate()){
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
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                JSONObject editor= new JSONObject();
                                editor.put("inputType", "password");
                                if(fieldsNN.contains(fieldName)){
                                    editor.put("allowBlank", false);
                                }
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.LIST.name())){
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+")#");
                                }
                            }else if(typeForm.equals(FieldType.URL.name()) || typeForm.equals(FieldType.FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_YOUTUBE.name()) || typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                
                                gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.audioGridRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                            }else{
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("field", field);
                                }
                            }
                        }else{
                            if(fieldName.equals(viewConfig.getLabelField())){
                                gridColumn.put("renderer", "#"+viewConfig.getLabelField()+"EntityRender#");
                            }
                            switch (type) {
                                case "java.util.Date": {
                                    gridColumn.put("xtype", "datecolumn");
                                    gridColumn.put("format", viewConfig.getDateFormat());
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "datefield");
                                    editor.put("format", viewConfig.getDateFormat());
                                    if (fieldsNN.contains(fieldName)) {
                                        editor.put("allowBlank", false);
                                    }
                                    if (viewConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "java.sql.Time": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "timefield");
                                    editor.put("format", "h:i:s A");
                                    if (fieldsNN.contains(fieldName)) {
                                        editor.put("allowBlank", false);
                                    }
                                    if (viewConfig.isEditableGrid() && !readOnly) {
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
                                    if (viewConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "boolean":
                                case "java.lang.Boolean": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "checkbox");
                                    editor.put("cls", "x-grid-checkheader-editor");
                                    if (viewConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                default:
                                    JSONObject field = new JSONObject();
                                    field.put("type", "textfield");
                                    if (viewConfig.isEditableGrid() && !readOnly) {
                                        gridColumn.put("field", field);
                                    }
                                    break;
                            }
                        }
                    }else{
                        gridColumn.put("renderer", "#Instance.combobox"+fieldEntity+"Render#");
                        if(viewConfig.isEditableGrid() && !readOnly){
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
            internalViewButton.put("scope", "#this#");
            internalViewButton.put("scale", "medium");
            internalViewButton.put("handler", "#function(){parentExtController.viewInternalPage('"+menuComponent.getBasePath()+"/"+entry.getKey()+"/entity.htm')}#");
            
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
                gridAction.put("scope", "#this#");
                gridAction.put("icon", processButton.getIconUrl());
                gridAction.put("handler", "#function (grid, rowIndex, colIndex) {" +
                                          "     Instance.showProcessForm('"+processButton.getProcessName()+"', "+sourceByDestinationFields+", rowIndex);" +
                                          "}#");
                
                gridActions.put(gridAction);
                gridActions.put("-");
                
                //ADD Button in Form
                JSONObject internalViewButton= new JSONObject();
                internalViewButton.put("text", processButton.getProcessTitle());
                internalViewButton.put("scope", "#this#");
                internalViewButton.put("scale", "medium");
                internalViewButton.put("style", "background-image: url("+processButton.getIconUrl()+") !important;background-position: left center;background-repeat: no-repeat;background-size: 25px 25px;padding-left: 20px;");
                internalViewButton.put("handler", "#function(){"+
                                                  "     Instance.showProcessForm('"+processButton.getProcessName()+"', "+sourceByDestinationFields+", -1);"+
                                                  "}#");
                
                jsonInternalViewButtons.put(internalViewButton);
                
                //Add Form Fields by Process
                JSONArray jsonFormFieldsProcess = jfo.getJSONProcessForm(processButton.getProcessName(), "", processButton.getDtoClass(), processButton.getDateFormat());
                jsonFormFieldsProcessMap.put(processButton.getProcessName(), jsonFormFieldsProcess.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
            }
            gridColumn.put("items", gridActions);
            jsonGridColumns.put(gridColumn);
        }
        
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonRenderReplacements", jsonRenderReplacements.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("sortColumns", sortColumns.toString());
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(viewConfig.getTypeChildExtViews()));
        mav.addObject("jsonFormFieldsProcessMap", jsonFormFieldsProcessMap);
    }
    
    private void addFormField(Object field, JSONArray jsonFormFields, LinkedHashMap<String,JSONObject> fieldGroups, String titleGroup){
        if(titleGroup.equals("")){
            jsonFormFields.put(field);
        }else{
            fieldGroups.get(titleGroup).getJSONArray("items").put(field);
        }
    }

    public Object getFormRecordId(){
        return null;
    }
    
}
