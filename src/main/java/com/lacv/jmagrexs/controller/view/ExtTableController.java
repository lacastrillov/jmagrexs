package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.FieldConfigurationByTableColumns;
import com.lacv.jmagrexs.components.JSONFilters;
import com.lacv.jmagrexs.components.JSONForms;
import com.lacv.jmagrexs.components.JSONModels;
import com.lacv.jmagrexs.components.RangeFunctions;
import com.lacv.jmagrexs.components.TableColumnsConfig;
import com.lacv.jmagrexs.dto.GenericTableColumn;
import com.lacv.jmagrexs.enums.FieldType;
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
import com.lacv.jmagrexs.dto.config.TableConfig;
import com.lacv.jmagrexs.util.Formats;
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
        mav.addObject("serverDomain", serverDomain);
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
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(columns);
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
        JSONArray jsonModel = jm.getJSONModel(columns);
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
        HashMap<String, Integer> sizeColumnMap= fctc.getSizeColumnMap(columns);
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
                            jsonFormFields.put("#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','form',"+dataArray.toString().replaceAll("\"", "'")+","+(!fieldsNN.contains(fieldName))+")#");
                        }else if(typeForm.equals(FieldType.RADIOS.name())){
                            addFormField= false;
                            String[] data= typeFormFields.get(fieldName);
                            JSONArray dataArray = new JSONArray();
                            for(int i=1; i<data.length; i++){
                                dataArray.put(data[i]);
                            }
                            jsonFormFields.put("#Instance.commonExtView.getRadioGroup('"+fieldName+"','"+fieldTitle+"',"+dataArray.toString().replaceAll("\"", "'")+")#");
                        }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                            formField.put("id", "form" + viewConfig.getTableName() + "_" +fieldName + "LinkField");
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
                            formField.put("id", "form" + viewConfig.getTableName() + "_" +fieldName + "LinkField");
                            formField.put("fieldLabel", "&nbsp;");
                            formField.put("emptyText", "Url Youtube");

                            //Add Video Youtube
                            JSONObject renderField= new JSONObject();
                            renderField.put("name", fieldName);
                            renderField.put("fieldLabel", fieldTitle);
                            renderField.put("xtype", "displayfield");
                            renderField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                            jsonFormFields.put(renderField);
                        }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                            formField.put("fieldLabel", "&nbsp;");
                            formField.put("emptyText", "Google Maps Point");

                            //Add GoogleMap
                            JSONObject renderField= new JSONObject();
                            renderField.put("name", fieldName);
                            renderField.put("fieldLabel", fieldTitle);
                            renderField.put("xtype", "displayfield");
                            renderField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                            jsonFormFields.put(renderField);
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
                            jsonFormFields.put(renderField);
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
                            jsonFormFields.put(renderField);
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
                            jsonFormFields.put(renderField);
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
                            jsonFormFields.put(renderField);
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
                            jsonFormFields.put(renderField);
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
                        //formField.put("minLength", 0);
                        formField.put("maxLength", sizeColumnMap.get(fieldName));
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
                        if(fieldName.equals("id")){
                            gridColumn.put("renderer", "#idEntityRender#");
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
                            //field.put("minLength", 0);
                            field.put("maxLength", sizeColumnMap.get(fieldName));
                        }
                        if(viewConfig.isEditableGrid() && !readOnly){
                            gridColumn.put("field", field);
                        }
                    }else if(editor!=null){
                        if(fieldsNN.contains(fieldName)){
                            editor.put("allowBlank", false);
                        }
                        if(sizeColumnMap.containsKey(fieldName)){
                            //editor.put("minLength", 0);
                            editor.put("maxLength", sizeColumnMap.get(fieldName));
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
