package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.dto.config.FileExplorerConfig;
import com.dot.gcpbasedot.enums.FieldType;
import com.dot.gcpbasedot.enums.HideView;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.dot.gcpbasedot.components.JSONModels;
import com.dot.gcpbasedot.components.RangeFunctions;
import com.dot.gcpbasedot.enums.PageType;
import com.dot.gcpbasedot.util.Formats;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtFileExplorerController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtFileExplorerController.class);
    
    private FileExplorerConfig viewConfig;
    
    @Autowired
    private FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public RangeFunctions rf;
    
    @Autowired
    public JSONModels jm;
    
    @Autowired
    public JSONFilters jf;
    
    
    protected void addControlMapping(FileExplorerConfig viewConfig) {
        this.viewConfig= viewConfig;
    }

    protected void addControlMapping(String entityRef, EntityService entityService, Class dtoClass) {
        viewConfig= new FileExplorerConfig(entityRef, entityService, dtoClass);
    }

    @RequestMapping(value = "/fileExplorer.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView fileExplorer() {
        ModelAndView mav= new ModelAndView("fileExplorer");
        
        mav.addObject("extViewConfig", extViewConfig);
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtViewport");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        if(globalMenuComponent!=null){
            JSONArray menuItems= getMenuItems(session, globalMenuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        if(viewConfig.isVisibleFilters()){
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(
                    viewConfig.getDtoClass(), viewConfig.getLabelField(), viewConfig.getDateFormat(), PageType.FILE_EXPLORER);
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInit.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInit() {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtInit");
        
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtModel.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extModel() {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtModel");
        
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
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("jsonModel", jsonModel.toString());
        mav.addObject("jsonTemplateModel", jsonTemplateModel.toString());
        mav.addObject("jsonModelValidations", jsonModelValidations.toString());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore() {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtStore");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("restSession", viewConfig.isRestSession());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView(@RequestParam(required = true) String typeView) {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtView");
        
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
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtController");
        
        mav.addObject("typeController", typeController);
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInterfaces.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInterfaces() {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtInterfaces");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("labelField", viewConfig.getLabelField());
        
        return mav;
    }
    
    @RequestMapping(value = "/ajax/plainTextEditor.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView plainTextEditor() {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/PlainTextExtEditor");
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav){
        List<String> modelsEntityRef= new ArrayList<>();
        List<String> interfacesEntityRef= new ArrayList<>();
        
        modelsEntityRef.add(viewConfig.getEntityRef());
        
        List<String> associatedEntityRef= getAssociatedEntityRef(viewConfig.getEntityService().getEntityClass());
        for(String er: associatedEntityRef){
            modelsEntityRef.add(er);
            interfacesEntityRef.add(er);
        }
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("labelField", viewConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
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
        JSONArray jsonGridColumns= new JSONArray();
        JSONArray sortColumns= new JSONArray();
        JSONObject jsonEmptyModel= new JSONObject();
        
        Class entityClass= viewConfig.getEntityService().getEntityClass();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, viewConfig.getDtoClass(), viewConfig.getLabelField());
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String simpleType= propertyDescriptor.getPropertyType().getSimpleName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widhColumn= widhColumnMap.get(fieldName);
                boolean readOnly= fieldsRO.contains(fieldName);
                
                // ADD TO jsonFormFields
                if(viewConfig.isVisibleForm() && !hideFields.contains(fieldName + HideView.FORM.name())){
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
                                jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+")#");
                            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Url File
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.fileRender#");
                                jsonFormFields.put(imageField);
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione una imagen");
                                
                                //Add Image
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.imageRender#");
                                jsonFormFields.put(imageField);
                            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Url Youtube");
                                
                                //Add Video Youtube
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                                jsonFormFields.put(imageField);
                            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un video");
                                
                                //Add Video
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                                jsonFormFields.put(imageField);
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un audio");
                                
                                //Add Video
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                                jsonFormFields.put(imageField);
                            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Video
                                JSONObject imageField= new JSONObject();
                                imageField.put("name", fieldName);
                                imageField.put("fieldLabel", fieldTitle);
                                imageField.put("xtype", "displayfield");
                                imageField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                                jsonFormFields.put(imageField);
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
                            jsonFormFields.put(formField);
                        }
                    }else{
                        jsonFormFields.put("#Instance.formCombobox"+fieldEntity+"#");
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
                if(!hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(!hideFields.contains(fieldName + HideView.GRID.name()) && !viewConfig.isActiveGridTemplate()){
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
                                if(!readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                JSONObject editor= new JSONObject();
                                editor.put("inputType", "password");
                                if(fieldsNN.contains(fieldName)){
                                    editor.put("allowBlank", false);
                                }
                                if(!readOnly){
                                    gridColumn.put("editor", editor);
                                }
                            }else if(typeForm.equals(FieldType.LIST.name())){
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(!readOnly){
                                    gridColumn.put("editor", "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+")#");
                                }
                            }else if(typeForm.equals(FieldType.URL.name()) || typeForm.equals(FieldType.FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_YOUTUBE.name()) || typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                                    typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                
                                gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(!readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(!readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                            }else{
                                JSONObject field= new JSONObject();
                                field.put("type", "textfield");
                                if(!readOnly){
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
                                    if (!readOnly) {
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
                                    if (!readOnly) {
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
                                    if (!readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                case "boolean":
                                case "java.lang.Boolean": {
                                    JSONObject editor = new JSONObject();
                                    editor.put("xtype", "checkbox");
                                    editor.put("cls", "x-grid-checkheader-editor");
                                    if (!readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                default:
                                    JSONObject field = new JSONObject();
                                    field.put("type", "textfield");
                                    if (!readOnly) {
                                        gridColumn.put("field", field);
                                    }
                                    break;
                            }
                        }
                    }else{
                        gridColumn.put("renderer", "#Instance.combobox"+fieldEntity+"Render#");
                        if(!readOnly){
                            gridColumn.put("editor", "#Instance.gridCombobox"+fieldEntity+"#");
                        }
                    }
                    jsonGridColumns.put(gridColumn);
                }
                    
                // ADD TO jsonEmptyModel
                if(fieldName.equals("id")==false){
                    jsonEmptyModel.put(fieldName, "");
                }
            }
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
        
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonRenderReplacements", jsonRenderReplacements.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("sortColumns", sortColumns.toString());
    }
    
}
