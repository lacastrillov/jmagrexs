package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.ReportConfig;
import com.lacv.jmagrexs.enums.FieldType;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
import com.lacv.jmagrexs.service.EntityService;
import com.lacv.jmagrexs.dto.ProcessButton;
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import com.google.gson.Gson;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
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
    
    protected final LinkedHashMap<String,JSONObject> fieldGroups= new LinkedHashMap<>();
    
    protected final HashMap<String, Integer> positionColumnForm = new HashMap<>();
    
    
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
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
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
        HashMap<String, String> groupFieldsMap= fcba.getGroupFieldsMap(reportConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(reportConfig.getDtoClass());
        HashSet<String> valueMapFields= fcba.getValueMapFields(reportConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(reportConfig.getDtoClass());
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(reportConfig.getDtoClass());
        positionColumnForm.put("", 0);
        
        if(!reportConfig.isActiveGridTemplate()){
            JSONObject numbererColumn= new JSONObject();
            numbererColumn.put("xtype", "rownumberer");
            numbererColumn.put("width", 40);
            numbererColumn.put("sortable", false);
            numbererColumn.put("renderer", "@Instance.commonExtView.numbererGridRender@");
            jsonGridColumns.put(numbererColumn);
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widthColumn= widhColumnMap.get(fieldName);
                boolean readOnly= true;
                boolean isEditableForm= false;
                
                // ADD TO jsonFormFields
                if(reportConfig.isVisibleForm() && !hideFields.contains(fieldName + HideView.FORM.name())){
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
                        jfef.addJSONField(jsonFormFields, reportName, type, fieldName,
                                fieldTitle, titleGroup, typeFormFields, sizeColumnMap, fieldGroups,
                                positionColumnForm, reportConfig.getNumColumnsForm(), isEditableForm, readOnly, false);
                        
                    }
                }
                
                // ADD TO jsonGridColumns
                if(!hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(!hideFields.contains(fieldName + HideView.GRID.name()) && !reportConfig.isActiveGridTemplate()){
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        jc.addJSONColumn(jsonGridColumns, type, fieldName, fieldTitle, widthColumn, typeFormFields, reportConfig.getLabelField(),
                                    new HashMap<>(), false, true, false);

                    }
                    
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
                        jsonFormMapFields.put("@Instance.commonExtView.getSimpleCombobox('"+fieldName+"','"+fieldTitle+"','valueMap',"+dataArray.toString().replaceAll("\"", "'")+",false)@");
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
        
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonFormMapFields", jsonFormMapFields.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("sortColumns", sortColumns.toString());
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("jsonFormFieldsProcessMap", jsonFormFieldsProcessMap);
    }
    
}
