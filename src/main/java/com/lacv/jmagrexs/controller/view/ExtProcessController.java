package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.ProcessConfig;
import com.lacv.jmagrexs.enums.HideView;
import com.lacv.jmagrexs.reflection.EntityReflection;
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
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtProcessController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtProcessController.class);
    
    private ProcessConfig processConfig;
    
    private final JSONArray jsonInternalViewButtons= new JSONArray();
    
    private final JSONArray jsonGridColumns= new JSONArray();
    
    private final JSONObject jsonEmptyModel= new JSONObject();
    
    private JSONArray jsonModelLogProcess;
    
    private JSONArray jsonModelValidationsLogProcess;
    
    private JSONArray jsonFieldsFilters;
    
    private HashMap<String, String> titledFieldsMap;
    
    private final Map<String, String> jsonFormFieldsMap= new HashMap();;
    
    private final Map<String, String> jsonModelMap= new HashMap();;
    
    private final List<String> interfacesEntityRef= new ArrayList<>();
    
    
    protected void addControlMapping(ProcessConfig processConfig) {
        this.processConfig= processConfig;
        generateGeneralObjects();
        generateEntityExtProcessConfiguration();
    }

    @RequestMapping(value = "/process.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView process() {
        ModelAndView mav= new ModelAndView("process");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("serverDomain", serverDomain);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/process/ExtViewport");
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
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
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("nameProcesses", processConfig.getNameProcesses());
        mav.addObject("jsonModelMap", jsonModelMap);
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
        
        mav.addObject("nameProcesses", processConfig.getNameProcesses());
        mav.addObject("jsonFormFieldsMap", jsonFormFieldsMap);
        
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonInternalViewButtons", jsonInternalViewButtons.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("jsonTypeChildExtViews", new Gson().toJson(processConfig.getTypeChildExtViews()));
        
        if(processConfig.isVisibleFilters()){
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
        
        modelsEntityRef.add(processConfig.getMainProcessRef());
        
        mav.addObject("viewConfig", processConfig);
        mav.addObject("entityRef", processConfig.getMainProcessRef());
        mav.addObject("entityName", processConfig.getMainProcessName());
        mav.addObject("labelField", processConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
    }
    
    private void generateGeneralObjects(){
        for (Map.Entry<String, Class> entry : processConfig.getInDtos().entrySet()){
            JSONArray jsonModel = jm.getJSONRecursiveModel("", entry.getValue());
            jsonModelMap.put(entry.getKey(), jsonModel.toString());
            
            JSONArray jsonFormFields = jfo.getJSONProcessForm(entry.getKey(), "", entry.getValue());
            jsonFormFieldsMap.put(entry.getKey(), jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
            
            if(jfo.getInterfacesEntityRefMap().containsKey(entry.getKey())){
                interfacesEntityRef.addAll(jfo.getInterfacesEntityRefMap().get(entry.getKey()));
            }
        }
        
        jsonModelLogProcess = jm.getJSONModel(processConfig.getLogProcessClass());
        jsonModelValidationsLogProcess= jm.getJSONModelValidations(processConfig.getLogProcessClass());
        jsonFieldsFilters= jf.getFieldsFilters(processConfig.getLogProcessClass(), processConfig.getLabelField(), PageType.PROCESS);
    }
    
    private void generateEntityExtProcessConfiguration(){
        Class entityClass= processConfig.getLogProcessClass();
        
        PropertyDescriptor[] propertyDescriptors = EntityReflection.getPropertyDescriptors(entityClass);
        fcba.orderPropertyDescriptor(propertyDescriptors, entityClass, processConfig.getLabelField());
        
        HashMap<String, Integer> widhColumnMap= fcba.getWidthColumnMap(propertyDescriptors, entityClass);
        HashMap<String, String> defaultValueMap= fcba.getDefaultValueMap(entityClass);
        HashSet<String> hideFields= fcba.getHideFields(entityClass);
        HashSet<String> fieldsNN= fcba.getNotNullFields(entityClass);
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(entityClass);
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(entityClass);
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(entityClass);
        titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, entityClass);
        
        JSONObject numbererColumn= new JSONObject();
        numbererColumn.put("xtype", "rownumberer");
        numbererColumn.put("width", 40);
        numbererColumn.put("sortable", false);
        numbererColumn.put("renderer", "#Instance.commonExtView.numbererGridRender#");
        jsonGridColumns.put(numbererColumn);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widthColumn= widhColumnMap.get(fieldName);
                boolean readOnly= fieldsRO.contains(fieldName);
                
                // ADD TO jsonGridColumns
                if(processConfig.isVisibleGrid() && !hideFields.contains(fieldName + HideView.GRID.name()) && !processConfig.isActiveGridTemplate()){
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        jc.addJSONColumn(jsonGridColumns, type, fieldName, fieldTitle, widthColumn, typeFormFields, processConfig.getLabelField(),
                                sizeColumnMap, processConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }else{
                        jc.addEntityCombobox(jsonGridColumns, fieldName, fieldTitle, fieldEntity, widthColumn,
                                processConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }
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
    }

}
