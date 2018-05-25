package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.EntityConfig;
import com.lacv.jmagrexs.enums.FieldType;
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
import java.util.LinkedHashMap;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtEntityController extends ExtReportController {
    
    private final List<String> modelsEntityRef= new ArrayList<>();
    
    private final List<String> viewsChildEntityRef= new ArrayList<>();
    
    private final List<String> interfacesEntityRef= new ArrayList<>();
    
    private final List<String> interfacesChildEntityRef= new ArrayList<>();
    
    private JSONArray jsonModel;
    
    private JSONArray jsonTemplateModel;
    
    private JSONArray jsonModelValidations;
    
    private JSONArray jsonFieldsFilters;
    
    private final JSONArray jsonFormFields= new JSONArray();
    
    private final JSONArray jsonRenderReplacements= new JSONArray();
    
    private final JSONArray jsonInternalViewButtons= new JSONArray();
    
    private final JSONArray jsonGridColumns= new JSONArray();
    
    private final JSONArray sortColumns= new JSONArray();
    
    private final LinkedHashMap<String,JSONObject> fieldGroups= new LinkedHashMap<>();
    
    private final JSONObject jsonEmptyModel= new JSONObject();
    
    private final Map<String, String> jsonFormFieldsProcessMap= new HashMap();
    
    private HashMap<String, String> titledFieldsMap;

    protected static final Logger LOGGER1 = Logger.getLogger(ExtEntityController.class);
    
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
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonRenderReplacements", jsonRenderReplacements.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
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
        jsonTemplateModel = new JSONArray();
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
        HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(propertyDescriptors, viewConfig.getDtoClass());
        HashMap<String, String> groupFieldsMap= fcba.getGroupFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(viewConfig.getDtoClass());
        titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        
        if(!viewConfig.isActiveGridTemplate()){
            JSONObject numbererColumn= new JSONObject();
            numbererColumn.put("xtype", "rownumberer");
            numbererColumn.put("width", 40);
            numbererColumn.put("sortable", false);
            numbererColumn.put("renderer", "#Instance.commonExtView.numbererGridRender#");
            jsonGridColumns.put(numbererColumn);
        }
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
                            }else if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name())){
                                addFormField= false;
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(!readOnly){
                                    String field= "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#";
                                    addFormField(field,jsonFormFields,fieldGroups,titleGroup);
                                }else{
                                    addFormField=true;
                                }
                            }else if(typeForm.equals(FieldType.RADIOS.name())){
                                addFormField= false;
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                String field= "#Instance.commonExtView.getRadioGroup('"+fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+")#";
                                addFormField(field,jsonFormFields,fieldGroups,titleGroup);
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
                                    formField.put("format", extViewConfig.getDateFormat());
                                    formField.put("tooltip", "Seleccione la fecha");
                                    break;
                                case "java.sql.Time":
                                    formField.put("xtype", "timefield");
                                    formField.put("format", extViewConfig.getTimeFormat());
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
                        if(sizeColumnMap.containsKey(fieldName)){
                            formField.put("minLength", sizeColumnMap.get(fieldName)[0]);
                            formField.put("maxLength", sizeColumnMap.get(fieldName)[1]);
                        }
                        if(addFormField){
                            addFormField(formField,jsonFormFields,fieldGroups,titleGroup);
                        }
                    }else{
                        String combobox="(function(){ ";
                        if(!viewConfig.isEditableForm() || readOnly){
                            combobox+="Instance.formCombobox"+fieldEntity+".setDisabled(true); ";
                        }
                        if(fieldsNN.contains(fieldName)){
                            combobox+="Instance.formCombobox"+fieldEntity+".allowBlank=false; ";
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
                            }else if(typeForm.equals(FieldType.DURATION.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.durationGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.PRICE.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.priceGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.DATETIME.name())){
                                gridColumn.put("xtype", "datecolumn");
                                gridColumn.put("format", extViewConfig.getDatetimeFormat());
                                editor = new JSONObject();
                                editor.put("xtype", "datefield");
                                editor.put("format", extViewConfig.getDatetimeFormat());
                            }else if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name()) ||
                                    typeForm.equals(FieldType.RADIOS.name())){
                                String[] data= typeFormFields.get(fieldName);
                                JSONArray dataArray = new JSONArray();
                                for(int i=1; i<data.length; i++){
                                    dataArray.put(data[i]);
                                }
                                if(viewConfig.isEditableGrid() && !readOnly){
                                    gridColumn.put("editor", "#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','grid',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#");
                                }
                            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.fileSizeGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.URL.name()) || typeForm.equals(FieldType.FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_YOUTUBE.name()) || typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                
                                gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                                field= new JSONObject();
                                field.put("type", "textfield");
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                gridColumn.put("renderer", "#Instance.commonExtView.audioGridRender#");
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
                            if(viewConfig.isEditableGrid() && !readOnly){
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
                            if(viewConfig.isEditableGrid() && !readOnly){
                                gridColumn.put("editor", editor);
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
            internalViewButton.put("handler", "#function(){parentExtController.viewInternalPage('"+serverDomain.getApplicationContext() + serverDomain.getAdminContext() + serverDomain.getAdminPath()+"/"+entry.getKey()+"/entity.htm')}#");
            
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
                JSONArray jsonFormFieldsProcess = jfo.getJSONProcessForm(processButton.getProcessName(), "", processButton.getDtoClass());
                jsonFormFieldsProcessMap.put(processButton.getProcessName(), jsonFormFieldsProcess.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
            }
            gridColumn.put("items", gridActions);
            jsonGridColumns.put(gridColumn);
        }
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
