package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.components.FieldConfigurationByAnnotations;
import com.dot.gcpbasedot.components.FieldConfigurationByTableColumns;
import com.dot.gcpbasedot.components.JSONFilters;
import com.dot.gcpbasedot.components.JSONForms;
import com.dot.gcpbasedot.components.JSONModels;
import com.dot.gcpbasedot.components.RangeFunctions;
import com.dot.gcpbasedot.components.TableColumnsConfig;
import com.dot.gcpbasedot.dto.GenericTableColumn;
import com.dot.gcpbasedot.enums.FieldType;
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
import com.dot.gcpbasedot.dto.config.TableConfig;
import com.dot.gcpbasedot.util.Formats;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public abstract class ExtTableController extends ExtController {

    protected static final Logger LOGGER1 = Logger.getLogger(ExtTableController.class);
    
    private TableConfig viewConfig;
    
    @Autowired
    private TableColumnsConfig tableColumnsConfig;
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public FieldConfigurationByTableColumns fctc;
    
    @Autowired
    public RangeFunctions rf;
    
    @Autowired
    public JSONModels jm;
    
    @Autowired
    public JSONFilters jf;
    
    @Autowired
    public JSONForms jfo;
    
    
    protected void addControlMapping(TableConfig viewConfig) {
        this.viewConfig= viewConfig;
    }

    protected void addControlMapping(String tableRef) {
        viewConfig= new TableConfig(tableRef);
    }

    @RequestMapping(value = "/{tableName}/table.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView table(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("table");
        
        tableColumnsConfig.updateColumnsConfig(tableName);
        List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
        if(columns!=null){
            viewConfig.setPluralEntityTitle(columns.get(0).getTableName());
            viewConfig.setSingularEntityTitle(columns.get(0).getTableName());
            viewConfig.setMultipartFormData(columns.get(0).getFileUpload());
        }
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("basePath", menuComponent.getBasePath());
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView homeViewportExtView(HttpSession session, @PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtViewport");
        
        List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
        
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        if(viewConfig.isVisibleFilters()){
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(columns, viewConfig.getDateFormat());
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        }
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtInit.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInit(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtInit");
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtModel.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extModel(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtModel");
        
        List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
        JSONArray jsonModel = jm.getJSONModel(columns, viewConfig.getDateFormat());
        JSONArray jsonModelValidations= jm.getJSONModelValidations(columns);
        
        mav.addObject("jsonModel", jsonModel.toString());
        mav.addObject("jsonModelValidations", jsonModelValidations.toString());
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtStore");
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtView");
        
        addEntityExtViewConfiguration(mav, tableName);
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    @RequestMapping(value = "/{tableName}/ExtController.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extController(@PathVariable String tableName) {
        ModelAndView mav= new ModelAndView("scripts/table/ExtController");
        
        addGeneralObjects(mav, tableName);
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav, String tableName){
        mav.addObject("tableName", tableName);
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getTableRef());
        mav.addObject("entityName", viewConfig.getTableName());
    }
    
    private void addEntityExtViewConfiguration(ModelAndView mav, String tableName){
        JSONArray jsonFormFields= new JSONArray();
        JSONArray jsonRenderReplacements= new JSONArray();
        JSONArray jsonGridColumns= new JSONArray();
        JSONArray sortColumns= new JSONArray();
        JSONObject jsonEmptyModel= new JSONObject();
        
        List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
        fctc.orderTableColumns(columns);
        
        HashMap<String, String> titledFieldsMap= fctc.getTitledFieldsMap(columns);
        HashMap<String, Integer> widhColumnMap= fctc.getWidthColumnMap(columns);
        HashMap<String, String> defaultValueMap= fctc.getDefaultValueMap(columns);
        //HashSet<String> hideFields= fctc.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fctc.getNotNullFields(columns);
        HashMap<String,String[]> typeFormFields= fctc.getTypeFormFields(columns);
        HashSet<String> fieldsRO= new HashSet<>();//fctc.getReadOnlyFields(viewConfig.getDtoClass());
        fieldsRO.add("id");
        
        JSONObject numbererColumn= new JSONObject();
        numbererColumn.put("xtype", "rownumberer");
        numbererColumn.put("width", 40);
        numbererColumn.put("sortable", false);
        numbererColumn.put("renderer", "#Instance.commonExtView.numbererGridRender#");
        jsonGridColumns.put(numbererColumn);
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            
            String fieldName= column.getColumnAlias();
            String fieldEntity= StringUtils.capitalize(fieldName);
            String fieldTitle= titledFieldsMap.get(fieldName);
            Integer widhColumn= widhColumnMap.get(fieldName);
            boolean readOnly= fieldsRO.contains(fieldName);

            // ADD TO jsonFormFields
            if(viewConfig.isVisibleForm()){
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
                        }else if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name())){
                            addFormField= false;
                            String[] data= typeFormFields.get(fieldName);
                            JSONArray dataArray = new JSONArray();
                            for(int i=1; i<data.length; i++){
                                dataArray.put(data[i]);
                            }
                            jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+")#");
                        }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                            formField.put("id", "form" + viewConfig.getTableName() + "_" +fieldName + "LinkField");
                            formField.put("fieldLabel", "&nbsp;");
                            formField.put("emptyText", "Url Youtube");

                            //Add Video Youtube
                            JSONObject imageField= new JSONObject();
                            imageField.put("name", fieldName);
                            imageField.put("fieldLabel", fieldTitle);
                            imageField.put("xtype", "displayfield");
                            imageField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                            jsonFormFields.put(imageField);
                        }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                            formField.put("fieldLabel", "&nbsp;");
                            formField.put("emptyText", "Google Maps Point");

                            //Add GoogleMap
                            JSONObject imageField= new JSONObject();
                            imageField.put("name", fieldName);
                            imageField.put("fieldLabel", fieldTitle);
                            imageField.put("xtype", "displayfield");
                            imageField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                            jsonFormFields.put(imageField);
                        }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                            formField.put("name", fieldName + "_File");
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
                            formField.put("name", fieldName + "_File");
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
                        }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                            formField.put("name", fieldName + "_File");
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
                            formField.put("name", fieldName + "_File");
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
                            formField.put("name", fieldName + "_File");
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
                        if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                            //Add link Field
                            JSONObject linkField= new JSONObject();
                            linkField.put("id", "form" + viewConfig.getTableName() + "_" +fieldName + "LinkField");
                            linkField.put("name", fieldName);
                            linkField.put("fieldLabel", "&nbsp;");
                            jsonFormFields.put(linkField);
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
            if(viewConfig.isVisibleGrid()){
                sortColumns.put(fieldName+":"+fieldTitle);
            }
            if(viewConfig.isVisibleGrid()){
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
                        }else if(typeForm.equals(FieldType.DURATION.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.durationGridRender#");
                            JSONObject field= new JSONObject();
                            field.put("type", "textfield");
                            if(viewConfig.isEditableGrid() && !readOnly){
                                gridColumn.put("field", field);
                            }
                        }else if(typeForm.equals(FieldType.PRICE.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.priceGridRender#");
                            JSONObject field= new JSONObject();
                            field.put("type", "textfield");
                            if(viewConfig.isEditableGrid() && !readOnly){
                                gridColumn.put("field", field);
                            }
                        }else if(typeForm.equals(FieldType.LIST.name()) || typeForm.equals(FieldType.MULTI_SELECT.name())){
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
                        if(fieldName.equals("id")){
                            gridColumn.put("renderer", "#idEntityRender#");
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
            if(!fieldName.equals("id")){
                jsonEmptyModel.put(fieldName, (defaultValueMap.containsKey(fieldName))?defaultValueMap.get(fieldName):"");
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
