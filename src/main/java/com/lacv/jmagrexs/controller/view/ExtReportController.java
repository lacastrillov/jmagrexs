package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.ReportConfig;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import com.lacv.jmagrexs.components.FieldConfigurationByAnnotations;
import com.lacv.jmagrexs.components.JSONFilters;
import com.lacv.jmagrexs.components.JSONForms;
import com.lacv.jmagrexs.components.JSONModels;
import com.lacv.jmagrexs.components.RangeFunctions;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import com.google.gson.Gson;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class ExtReportController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtReportController.class);
    
    private final Map<String,ReportConfig> reportsConfig= new HashMap<>();
    
    @Autowired
    public FieldConfigurationByAnnotations fcba;
    
    @Autowired
    public RangeFunctions rf;
    
    @Autowired
    public JSONModels jm;
    
    @Autowired
    public JSONFilters jf;
    
    @Autowired
    public JSONForms jfo;
    
    
    protected void addReportMapping(ReportConfig reportConfig) {
        reportsConfig.put(reportConfig.getReportName(), reportConfig);
    }
    
    protected void addReportMapping(String entityRef, String reportName, EntityService entityService, Class dtoClass) {
        ReportConfig reportConfig= new ReportConfig(entityRef, reportName, entityService, dtoClass);
        reportsConfig.put(reportName, reportConfig);
    }

    @RequestMapping(value = "/report/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView report(@PathVariable String reportName) {
        ModelAndView mav= new ModelAndView("report");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("reportConfig", reportsConfig.get(reportName));
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName", reportName);
        mav.addObject("serverDomain", serverDomain);
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtViewport/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportViewportExtView(@PathVariable String reportName, HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtViewport");
        
        mav.addObject("reportConfig", reportsConfig.get(reportName));
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        if(reportsConfig.get(reportName).isVisibleFilters()){
            JSONArray jsonFieldsFilters= jf.getFieldsFilters(
                    reportsConfig.get(reportName).getDtoClass(), "name", PageType.REPORT);
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtInit/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportExtInit(@PathVariable String reportName) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtInit");
        
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtModel/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportExtModel(@PathVariable String reportName) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtModel");
        
        JSONArray jsonModel = jm.getJSONModel(reportsConfig.get(reportName).getDtoClass());
        JSONArray jsonTemplateModel = new JSONArray();
        
        if(reportsConfig.get(reportName).isActiveGridTemplate()){
            if(reportsConfig.get(reportName).getGridTemplate()!=null){
                for(int i=0; i<reportsConfig.get(reportName).getGridTemplate().getNumColumns(); i++){
                    JSONObject field= new JSONObject();
                    field.put("name", "column"+i);
                    field.put("type", "string");
                    jsonTemplateModel.put(field);
                }
            }
        }
        
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        mav.addObject("jsonModel", jsonModel.toString());
        mav.addObject("jsonTemplateModel", jsonTemplateModel.toString());
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtStore/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportExtStore(@PathVariable String reportName, @RequestParam(required = false) Boolean restSession) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtStore");
        
        mav.addObject("reportConfig", reportsConfig.get(reportName));
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        if(restSession==null){
            mav.addObject("restSession", reportsConfig.get(reportName).isRestSession());
        }else{
            mav.addObject("restSession", restSession);
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtView/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportExtView(@PathVariable String reportName, @RequestParam(required = true) String typeView) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtView");
        
        if(typeView.equals("Parent")){
            reportsConfig.get(reportName).setActiveGridTemplate(reportsConfig.get(reportName).isActiveGridTemplateAsParent());
        }
        if(typeView.equals("Child")){
            reportsConfig.get(reportName).setActiveGridTemplate(reportsConfig.get(reportName).isActiveGridTemplateAsChild());
        }
        mav.addObject("typeView",typeView);
        mav.addObject("reportConfig", reportsConfig.get(reportName));
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        addReportExtViewConfiguration(mav, reportName);
        
        return mav;
    }
    
    @RequestMapping(value = "/reportExtController/{reportName}.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView reportExtController(@PathVariable String reportName, @RequestParam(required = true) String typeController) {
        ModelAndView mav= new ModelAndView("scripts/report/ExtController");
        
        mav.addObject("typeController", typeController);
        mav.addObject("reportConfig", reportsConfig.get(reportName));
        mav.addObject("entityRef", reportsConfig.get(reportName).getEntityRef());
        mav.addObject("reportName",reportName);
        mav.addObject("jsonChildRefColumnNames", new Gson().toJson(reportsConfig.get(reportName).getChildRefColumnNames()));
        
        return mav;
    }
    
    private void addReportExtViewConfiguration(ModelAndView mav, String reportName){
        JSONArray jsonGridColumns= new JSONArray();
        JSONArray jsonFormFields= new JSONArray();
        JSONArray jsonFormMapFields= new JSONArray();
        JSONArray jsonInternalViewButtons= new JSONArray();
        JSONArray sortColumns= new JSONArray();
        JSONObject jsonEmptyModel= new JSONObject();
        Map<String, String> jsonFormFieldsProcessMap= new HashMap();
        
        ReportConfig reportConfig= reportsConfig.get(reportName);
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(reportConfig.getDtoClass());
        fcba.orderPropertyDescriptor(propertyDescriptors, reportConfig.getDtoClass(), "name");
        
        HashMap<String, String> titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, reportConfig.getDtoClass());
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, reportConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(reportConfig.getDtoClass());
        HashSet<String> valueMapFields= fcba.getValueMapFields(reportConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(reportConfig.getDtoClass());
        
        if(!reportConfig.isActiveGridTemplate()){
            JSONObject numbererColumn= new JSONObject();
            numbererColumn.put("xtype", "rownumberer");
            numbererColumn.put("width", 40);
            numbererColumn.put("sortable", false);
            numbererColumn.put("renderer", "#Instance.commonExtView.numbererGridRender#");
            jsonGridColumns.put(numbererColumn);
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            String simpleType= propertyDescriptor.getPropertyType().getSimpleName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widhColumn= widhColumnMap.get(fieldName);
                
                // ADD TO jsonFormFields
                if(reportConfig.isVisibleForm() && !hideFields.contains(fieldName + HideView.FORM.name())){
                    if(Formats.TYPES_LIST.contains(type)){
                        JSONObject formField= new JSONObject();
                        formField.put("id", reportName + "_" +fieldName);
                        formField.put("name", fieldName);
                        formField.put("fieldLabel", fieldTitle);
                        formField.put("readOnly", true);
                        if(typeFormFields.containsKey(fieldName)){
                            String typeForm= typeFormFields.get(fieldName)[0];
                            if(typeForm.equals(FieldType.EMAIL.name())){
                                formField.put("vtype", "email");
                            }else if(typeForm.equals(FieldType.PASSWORD.name())){
                                formField.put("inputType", "password");
                            }else if(typeForm.equals(FieldType.DATETIME.name())){
                                formField.put("xtype", "datefield");
                                formField.put("format", extViewConfig.getDatetimeFormat());
                            }else if(typeForm.equals(FieldType.TEXT_AREA.name())){
                                formField.put("xtype", "textarea");
                                formField.put("height", 200);
                            }else if(typeForm.equals(FieldType.HTML_EDITOR.name())){
                                formField.put("xtype", "htmleditor");
                                formField.put("enableColors", true);
                                formField.put("enableAlignments", true);
                                formField.put("height", 400);
                            }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                                //Add file Size Text
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.fileSizeRender#");
                            }else if(typeForm.equals(FieldType.PERCENTAJE.name())){
                                formField.put("xtype", "numberfield");
                                formField.put("fieldLabel", fieldTitle+" (%)");
                            }else if(typeForm.equals(FieldType.COLOR.name())){
                                formField.put("xtype", "customcolorpicker");
                            }else if(typeForm.equals(FieldType.FILE_UPLOAD.name())){
                                //Add Url File
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.fileRender#");
                            }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                                //Add Image
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.imageRender#");
                            }else if(typeForm.equals(FieldType.VIDEO_YOUTUBE.name())){
                                formField.put("fieldLabel", "&nbsp;");
                                formField.put("emptyText", "Url Youtube");
                                
                                //Add Video Youtube
                                JSONObject rendererField= new JSONObject();
                                rendererField.put("id", reportName + "_" + fieldName + "Renderer");
                                rendererField.put("name", fieldName);
                                rendererField.put("fieldLabel", fieldTitle);
                                rendererField.put("xtype", "displayfield");
                                rendererField.put("renderer", "#Instance.commonExtView.videoYoutubeRender#");
                                jsonFormFields.put(rendererField);
                            }else if(typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name())){
                                //Add Video
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.videoFileUploadRender#");
                            }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                                //Add Audio
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.audioFileUploadRender#");
                            }else if(typeForm.equals(FieldType.GOOGLE_MAP.name())){
                                formField.put("fieldLabel", "Coordenadas "+fieldTitle);
                                formField.put("emptyText", "Google Maps Point");
                                
                                //Add GoogleMap
                                JSONObject rendererField= new JSONObject();
                                rendererField.put("id", reportName + "_" + fieldName + "Renderer");
                                rendererField.put("name", fieldName);
                                rendererField.put("fieldLabel", fieldTitle);
                                rendererField.put("xtype", "displayfield");
                                rendererField.put("renderer", "#Instance.commonExtView.googleMapsRender#");
                                jsonFormFields.put(rendererField);
                            }else if(typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                //Add File
                                formField.put("xtype", "displayfield");
                                formField.put("renderer", "#Instance.commonExtView.multiFileRender#");
                            }
                            jsonFormFields.put(formField);
                            if(typeForm.equals(FieldType.FILE_UPLOAD.name()) || typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) ||
                                    typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                                //Add link Field
                                JSONObject linkField= new JSONObject();
                                linkField.put("id", reportName + "_" +fieldName + "Link");
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
                            jsonFormFields.put(formField);
                        }
                    }
                }
                
                // ADD TO jsonGridColumns
                if(!hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(!hideFields.contains(fieldName + HideView.GRID.name()) && !reportConfig.isActiveGridTemplate()){
                    JSONObject gridColumn= new JSONObject();
                    gridColumn.put("dataIndex", fieldName);
                    gridColumn.put("header", fieldTitle);
                    gridColumn.put("width", widhColumn);
                    gridColumn.put("sortable:", true);
                    if(typeFormFields.containsKey(fieldName)){
                        String typeForm= typeFormFields.get(fieldName)[0];
                        if(typeForm.equals(FieldType.URL.name()) || typeForm.equals(FieldType.FILE_UPLOAD.name()) ||
                            typeForm.equals(FieldType.VIDEO_YOUTUBE.name()) || typeForm.equals(FieldType.VIDEO_FILE_UPLOAD.name()) || 
                            typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name()) || typeForm.equals(FieldType.MULTI_FILE_TYPE.name())){
                            
                            gridColumn.put("renderer", "#Instance.commonExtView.urlRender#");
                        }else if(typeForm.equals(FieldType.DURATION.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.durationGridRender#");
                        }else if(typeForm.equals(FieldType.PRICE.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.priceGridRender#");
                        }else if(typeForm.equals(FieldType.FILE_SIZE.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.fileSizeGridRender#");
                        }else if(typeForm.equals(FieldType.PERCENTAJE.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.percentageGridRender#");
                        }else if(typeForm.equals(FieldType.COLOR.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.colorGridRender#");
                        }else if(typeForm.equals(FieldType.DATETIME.name())){
                            gridColumn.put("xtype", "datecolumn");
                            gridColumn.put("format", extViewConfig.getDatetimeFormat());
                        }else if(typeForm.equals(FieldType.IMAGE_FILE_UPLOAD.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.imageGridRender#");
                        }else if(typeForm.equals(FieldType.AUDIO_FILE_UPLOAD.name())){
                            gridColumn.put("renderer", "#Instance.commonExtView.audioGridRender#");
                        }
                    }else{
                        if(fieldName.equals(reportConfig.getLabelField())){
                            gridColumn.put("renderer", "#"+reportConfig.getLabelField()+"EntityRender#");
                        }
                        if(type.equals("java.util.Date")){
                            gridColumn.put("xtype", "datecolumn");
                            gridColumn.put("format", extViewConfig.getDateFormat());
                        }else if(type.equals("java.sql.Time")){
                            gridColumn.put("xtype", "timefield");
                            gridColumn.put("format", extViewConfig.getTimeFormat());
                        }
                    }
                   
                    jsonGridColumns.put(gridColumn);
                }
                
                //ADD to jsonFormFields valueMapFields
                if(reportConfig.isVisibleValueMapForm() && valueMapFields.contains(fieldName)){
                    boolean addFormField= true;
                    JSONObject formField= new JSONObject();
                    formField.put("name", fieldName);
                    formField.put("fieldLabel", fieldTitle);
                    if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.LIST.name())){
                        addFormField= false;
                        String[] data= typeFormFields.get(fieldName);
                        JSONArray dataArray = new JSONArray();
                        for(int i=1; i<data.length; i++){
                            dataArray.put(data[i]);
                        }
                        jsonFormMapFields.put("#Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','valueMap',"+dataArray.toString().replaceAll("\"", "'")+",false)#");
                    }else if (type.equals("java.util.Date")) {
                        String format=extViewConfig.getDateFormat();
                        if(typeFormFields.containsKey(fieldName) && typeFormFields.get(fieldName)[0].equals(FieldType.DATETIME.name())){
                            format=extViewConfig.getDatetimeFormat();
                        }
                        formField.put("xtype", "datefield");
                        formField.put("format", format);
                        formField.put("tooltip", "Seleccione la fecha");
                    }else if (type.equals("java.sql.Time")) {
                        formField.put("xtype", "timefield");
                        formField.put("format", extViewConfig.getTimeFormat());
                        formField.put("tooltip", "Seleccione la hora");
                    }else if(type.equals("short") || type.equals("java.lang.Short") || type.equals("int") || type.equals("java.lang.Integer") || type.equals("java.lang.Long") ||
                            type.equals("java.math.BigInteger") || type.equals("double") || type.equals("java.lang.Double") || type.equals("float") || type.equals("java.lang.Float")){
                        formField.put("xtype", "numberfield");
                    }else if(type.equals("boolean") || type.equals("java.lang.Boolean")){
                        formField.put("xtype", "checkbox");
                    }
                    if(addFormField){
                        jsonFormMapFields.put(formField);
                    }
                }
                    
                // ADD TO jsonEmptyModel
                jsonEmptyModel.put(fieldName, "");
            }
        }
        
        if(reportConfig.isActiveGridTemplate()){
            if(reportConfig.getGridTemplate()!=null){
                for(int i=0; i<reportConfig.getGridTemplate().getNumColumns(); i++){
                    JSONObject gridColumn= new JSONObject();
                    gridColumn.put("dataIndex", "column"+i);
                    gridColumn.put("header", "Column"+i);
                    gridColumn.put("flex", 1);
                    jsonGridColumns.put(gridColumn);
                }
            }
        }
        
        if(reportConfig.getProcessButtons().size()>0){
            Gson gs= new Gson();
            JSONObject gridColumn= new JSONObject();
            gridColumn.put("xtype", "actioncolumn");
            gridColumn.put("width", (reportConfig.getProcessButtons().size()*33));
            gridColumn.put("sortable", false);
            gridColumn.put("menuDisabled", true);
            JSONArray gridActions= new JSONArray();
            for(ProcessButton processButton: reportConfig.getProcessButtons()){
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
        
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonFormMapFields", jsonFormMapFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("sortColumns", sortColumns.toString());
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("jsonFormFieldsProcessMap", jsonFormFieldsProcessMap);
    }
    
}
