package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.dto.config.FileExplorerConfig;
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
import com.lacv.jmagrexs.enums.PageType;
import com.lacv.jmagrexs.util.Formats;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public abstract class ExtFileExplorerController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtFileExplorerController.class);
    
    private FileExplorerConfig viewConfig;
    
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
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
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
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
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
    public ModelAndView plainTextEditor(@RequestParam(required = true) String fileUrl, @RequestParam(required = false) Boolean extractButton) {
        ModelAndView mav= new ModelAndView("scripts/fileExplorer/PlainTextExtEditor");
        mav.addObject("serverDomain", serverDomain);
        mav.addObject("restSession", viewConfig.isRestSession());
        try {
            URL url= new URL(fileUrl);
            mav.addObject("fileUrl", fileUrl);
            mav.addObject("fileName", FilenameUtils.getName(url.getPath()));
            mav.addObject("extractButton", ((extractButton!=null)?extractButton:false));
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
        List<String> associatedEntityRef= getAssociatedEntityRef(viewConfig.getEntityService().getEntityClass());
        for(String er: associatedEntityRef){
            if(!er.equals(viewConfig.getEntityRef())){
                modelsEntityRef.add(er);
            }
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
        
        HashMap<String, Integer> widthColumnMap= fcba.getWidthColumnMap(propertyDescriptors, viewConfig.getDtoClass());
        HashSet<String> hideFields= fcba.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fcba.getNotNullFields(viewConfig.getDtoClass());
        HashSet<String> fieldsRO= fcba.getReadOnlyFields(viewConfig.getDtoClass());
        HashMap<String,String[]> typeFormFields= fcba.getTypeFormFields(viewConfig.getDtoClass());
        HashMap<String, Integer[]> sizeColumnMap= fcba.getSizeColumnMap(viewConfig.getDtoClass());
        titledFieldsMap= fcba.getTitledFieldsMap(propertyDescriptors, viewConfig.getDtoClass());
        LinkedHashMap<String,JSONObject> fieldGroups= new LinkedHashMap<>();
        HashMap<String, Integer> positionColumnForm = new HashMap<>();
        
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String type = propertyDescriptor.getPropertyType().getName();
            
            if(type.equals("java.util.List")==false && type.equals("java.lang.Class")==false){
                String fieldName= propertyDescriptor.getName();
                String fieldEntity= StringUtils.capitalize(fieldName);
                String fieldTitle= titledFieldsMap.get(fieldName);
                Integer widthColumn= widthColumnMap.get(fieldName);
                boolean readOnly= fieldsRO.contains(fieldName);
                
                // ADD TO jsonFormFields
                if(viewConfig.isVisibleForm() && !hideFields.contains(fieldName + HideView.FORM.name())){
                    if(Formats.TYPES_LIST.contains(type)){
                        jfef.addJSONField(jsonFormFields, entityClass.getSimpleName(), type, fieldName,
                                fieldTitle, "", typeFormFields, sizeColumnMap, fieldGroups,
                                positionColumnForm, 1, viewConfig.isEditableForm(),
                                readOnly, fieldsNN.contains(fieldName));
                        
                    }else{
                        jfef.addEntityCombobox(jsonFormFields, fieldEntity, viewConfig.isEditableForm(),
                                1, "", fieldGroups, positionColumnForm,
                                readOnly, fieldsNN.contains(fieldName));
                    }
                }
                
                // ADD TO jsonGridColumns
                if(!hideFields.contains(fieldName + HideView.GRID.name())){
                    sortColumns.put(fieldName+":"+fieldTitle);
                }
                if(!hideFields.contains(fieldName + HideView.GRID.name()) && !viewConfig.isActiveGridTemplate()){
                    
                    if(Formats.TYPES_LIST.contains(type)){
                        jc.addJSONColumn(jsonGridColumns, type, fieldName, fieldTitle, widthColumn, typeFormFields, viewConfig.getLabelField(),
                                sizeColumnMap, viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }else{
                        jc.addEntityCombobox(jsonGridColumns, fieldName, fieldTitle, fieldEntity, widthColumn,
                                viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                        
                    }
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
