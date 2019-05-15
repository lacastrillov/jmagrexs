package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.FileExplorerConfig;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
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
import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.JSONFilters;
import com.lacv.jmagrexs.components.JSONModels;
import com.lacv.jmagrexs.components.RangeFunctions;
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
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
    
    private final JSONArray jsonFormFields= new JSONArray();
    
    private final JSONArray jsonGridColumns= new JSONArray();
    
    private final JSONArray sortColumns= new JSONArray();
    
    private final JSONObject jsonEmptyModel= new JSONObject();
    
    private JSONArray jsonModel;
    
    private final JSONArray jsonTemplateModel = new JSONArray();
    
    private JSONArray jsonModelValidations;
    
    private JSONArray jsonFieldsFilters;
    
    private final List<String> modelsEntityRef= new ArrayList<>();
    
    private final List<String> interfacesEntityRef= new ArrayList<>();
    
    private HashMap<String, String> titledFieldsMap;
    
    
    
    protected void addControlMapping(FileExplorerConfig viewConfig) {
        this.viewConfig= viewConfig;
        generateGeneralObjects();
        generateEntityExtViewConfiguration();
    }

    protected void addControlMapping(String entityRef, EntityService entityService, Class dtoClass) {
        viewConfig= new FileExplorerConfig(entityRef, entityService, dtoClass);
        generateGeneralObjects();
        generateEntityExtViewConfiguration();
    }

    @RequestMapping(value = "/fileExplorer.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView fileExplorer() {
        ModelAndView mav= new ModelAndView("fileExplorer");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("serverDomain", serverDomain);
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/ExtViewport");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        if(viewConfig.isVisibleFilters()){
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
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("sortColumns", sortColumns.toString());
        
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
    public ModelAndView plainTextEditor(@RequestParam(required = true) String fileUrl) {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/PlainTextExtEditor");
        mav.addObject("serverDomain", serverDomain);
        try {
            URL url= new URL(fileUrl);
            mav.addObject("fileUrl", fileUrl);
            mav.addObject("fileName", FilenameUtils.getName(url.getPath()));
        } catch (MalformedURLException ex) {
            LOGGER.error("ERROR plainTextEditor", ex);
        }
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav){
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getEntityRef());
        mav.addObject("entityName", viewConfig.getEntityName());
        mav.addObject("labelField", viewConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
    }
    
    private void generateGeneralObjects(){
        modelsEntityRef.add(viewConfig.getEntityRef());
        
        List<String> associatedEntityRef= getAssociatedEntityRef(viewConfig.getEntityService().getEntityClass());
        for(String er: associatedEntityRef){
            interfacesEntityRef.add(er);
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
        
        jsonFieldsFilters= jf.getFieldsFilters(viewConfig.getDtoClass(), viewConfig.getLabelField(), PageType.FILE_EXPLORER);
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
    
    private void generateEntityExtViewConfiguration(){
        Class entityClass= viewConfig.getEntityService().getEntityClass();
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, viewConfig.getDtoClass(), viewConfig.getLabelField());
        
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(viewConfig.getDtoClass());
        titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        
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
                        formField.put("allowBlank", !fieldsNN.contains(fieldName));
                        if(!viewConfig.isEditableForm() || readOnly){
                            formField.put("readOnly", true);
                        }
                        if(typeFormFields.containsKey(fieldName)){
                            String typeForm= typeFormFields.get(fieldName)[0];
                            if(typeForm.equals(FieldType.EMAIL.name())){
                                formField.put("vtype", "email");
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                formField.put("inputType", "password");
                            }else if(typeForm.equals(FieldType.DATETIME.name())){
                                formField.put("xtype", "datefield");
                                formField.put("format", extViewConfig.getDatetimeFormat());
                                formField.put("tooltip", "Seleccione la fecha");
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
                                jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#");
                            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                                formField.put("id", "form" + entityClass.getSimpleName() + "_" +fieldName + "LinkField");
                                formField.put("xtype", "numberfield");
                                formField.put("fieldLabel", "&nbsp;");
                                
                                //Add file Size Text
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.fileSizeRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Url File
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.fileRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione una imagen");
                                
                                //Add Image
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.imageRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Url Youtube");
                                
                                //Add Video Youtube
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un video");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un audio");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                                jsonFormFields.put(renderField);
                            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                formField.put("xtype", "filefield");
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Seleccione un archivo");
                                
                                //Add Video
                                JSONObject renderField= new JSONObject();
                                renderField.put("name", fieldName);
                                renderField.put("fieldLabel", fieldTitle);
                                renderField.put("xtype", "displayfield");
                                renderField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                                jsonFormFields.put(renderField);
                            }
                        }else{
                            switch (type) {
                                case "java.util.Date":
                                    formField.put("xtype", "datefield");
                                    formField.put("format", extViewConfig.getDateFormat());
                                    formField.put("tooltip", "Seleccione la fecha");
                                    break;
                                case "java.sql.Time":
                                    formField.put("xtype", "timefield");
                                    formField.put("format", extViewConfig.getTimeFormat());
                                    formField.put("tooltip", "Seleccione la hora");
                                    break;
                                case "short":
                                case "java.lang.Short":
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
                        if(sizeColumnMap.containsKey(fieldName)){
                            formField.put("minLength", sizeColumnMap.get(fieldName)[0]);
                            formField.put("maxLength", sizeColumnMap.get(fieldName)[1]);
                        }
                        if(addFormField){
                            jsonFormFields.put(formField);
                        }
                    }else{
                        jsonFormFields.put("#Instance.formCombobox"+fieldEntity+"#");
                    }
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
                    JSONObject field= null;
                    JSONObject editor= null;
                    if(Formats.TYPES_LIST.contains(type)){
                        gridColumn.put("sortable", true);
                        if(typeFormFields.containsKey(fieldName)){
                            String typeForm= typeFormFields.get(fieldName)[0];
                            if(typeForm.equals(FieldType.EMAIL.name())){
                                editor= new JSONObject();
                                editor.put("vtype", "email");
                                
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                editor= new JSONObject();
                                editor.put("inputType", "password");
                            }else if(typeForm.equals(FieldType.DATETIME.name())){
                                gridColumn.put("xtype", "datecolumn");
                                gridColumn.put("format", extViewConfig.getDatetimeFormat());
                                editor = new JSONObject();
                                editor.put("xtype", "datefield");
                                editor.put("format", extViewConfig.getDatetimeFormat());
                            }else if(typeForm.equals(FieldType.DURATION.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.durationGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                                
                            }else if(typeForm.equals(FieldType.PRICE.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.priceGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                                if(!readOnly){
                                    gridColumn.put("field", field);
                                }
                            }else if(typeForm.equals(FieldType.LIST.name())){
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(!readOnly){
                                    gridColumn.put("editor", "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#");
                                }
                            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.fileSizeGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.URL.name()) || typeForm.equals(FieldType.FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_YOUTUBE.name()) || typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                                    typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                
                                gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                            }else{
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }
                        }else{
                            if(fieldName.equals(viewConfig.getLabelField())){
                                gridColumn.put("renderer", "#"+viewConfig.getLabelField()+"EntityRender#");
                            }
                            switch (type) {
                                case "java.util.Date": {
                                    gridColumn.put("xtype", "datecolumn");
                                    gridColumn.put("format", extViewConfig.getDateFormat());
                                    editor = new JSONObject();
                                    editor.put("xtype", "datefield");
                                    editor.put("format", extViewConfig.getDateFormat());
                                    break;
                                }
                                case "java.sql.Time": {
                                    editor = new JSONObject();
                                    editor.put("xtype", "timefield");
                                    editor.put("format", extViewConfig.getTimeFormat());
                                    break;
                                }
                                case "short":
                                case "java.lang.Short":
                                case "int":
                                case "java.lang.Integer":
                                case "long":
                                case "java.lang.Long":
                                case "java.math.BigInteger":
                                case "double":
                                case "java.lang.Double":
                                case "float":
                                case "java.lang.Float": {
                                    editor = new JSONObject();
                                    editor.put("xtype", "numberfield");
                                    break;
                                }
                                case "boolean":
                                case "java.lang.Boolean": {
                                    editor = new JSONObject();
                                    editor.put("xtype", "checkbox");
                                    editor.put("cls", "x-grid-checkheader-editor");
                                    if (!readOnly) {
                                        gridColumn.put("editor", editor);
                                    }
                                    break;
                                }
                                default:
                                    field = new JSONObject();
                                    field.put("type", "textfield");
                                    break;
                            }
                        }
                        if(field!=null){
                            if(fieldsNN.contains(fieldName)){
                                field.put("allowBlank", false);
                            }
                            if(sizeColumnMap.containsKey(fieldName)){
                                field.put("minLength", sizeColumnMap.get(fieldName)[0]);
                                field.put("maxLength", sizeColumnMap.get(fieldName)[1]);
                            }
                            if(!readOnly){
                                gridColumn.put("field", field);
                            }
                        }else if(editor!=null){
                            if(fieldsNN.contains(fieldName)){
                                editor.put("allowBlank", false);
                            }
                            if(sizeColumnMap.containsKey(fieldName)){
                                editor.put("minLength", sizeColumnMap.get(fieldName)[0]);
                                editor.put("maxLength", sizeColumnMap.get(fieldName)[1]);
                            }
                            if(!readOnly){
                                gridColumn.put("editor", editor);
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
    }
    
}
