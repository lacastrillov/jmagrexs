package com.lacv.jmagrexs.controller.view;

import com.lacv.jmagrexs.components.FieldConfigurationByTableColumns;
import com.lacv.jmagrexs.components.TableColumnsConfig;
import com.lacv.jmagrexs.dto.GenericTableColumn;
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
import java.util.LinkedHashMap;
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
    public FieldConfigurationByTableColumns fctc;
    
    
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
            mav.addObject("jsonFieldsFilters", jsonFieldsFilters.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
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
        JSONArray jsonGridColumns= new JSONArray();
        JSONArray sortColumns= new JSONArray();
        JSONObject jsonEmptyModel= new JSONObject();
        
        List<GenericTableColumn> columns= tableColumnsConfig.getColumnsFromTableName(tableName);
        fctc.orderTableColumns(columns);
        
        HashMap<String, String> titledFieldsMap= fctc.getTitledFieldsMap(columns);
        HashMap<String, Integer> widthColumnMap= fctc.getWidthColumnMap(columns);
        HashMap<String, String> defaultValueMap= fctc.getDefaultValueMap(columns);
        //HashSet<String> hideFields= fctc.getHideFields(viewConfig.getDtoClass());
        HashSet<String> fieldsNN= fctc.getNotNullFields(columns);
        HashMap<String,String[]> typeFormFields= fctc.getTypeFormFields(columns);
        HashMap<String, Integer[]> sizeColumnMap= fctc.getSizeColumnMap(columns);
        LinkedHashMap<String,JSONObject> fieldGroups= new LinkedHashMap<>();
        HashMap<String, Integer> positionColumnForm = new HashMap<>();
        HashSet<String> fieldsRO= new HashSet<>();//fctc.getReadOnlyFields(viewConfig.getDtoClass());
        fieldsRO.add("id");
        
        JSONObject numbererColumn= new JSONObject();
        numbererColumn.put("xtype", "rownumberer");
        numbererColumn.put("width", 40);
        numbererColumn.put("sortable", false);
        numbererColumn.put("renderer", "@Instance.commonExtView.numbererGridRender@");
        jsonGridColumns.put(numbererColumn);
        for (GenericTableColumn column : columns) {
            String type = column.getDataType();
            
            String fieldName= column.getColumnAlias();
            String fieldEntity= StringUtils.capitalize(fieldName);
            String fieldTitle= titledFieldsMap.get(fieldName);
            Integer widthColumn= widthColumnMap.get(fieldName);
            boolean readOnly= fieldsRO.contains(fieldName);

            // ADD TO jsonFormFields
            if(viewConfig.isVisibleForm()){
                if(Formats.TYPES_LIST.contains(type)){
                    jfef.addJSONField(jsonFormFields, viewConfig.getTableName(), type, fieldName,
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
            if(viewConfig.isVisibleGrid()){
                sortColumns.put(fieldName+":"+fieldTitle);
            }
            if(viewConfig.isVisibleGrid()){
                
                if(Formats.TYPES_LIST.contains(type)){
                    jc.addJSONColumn(jsonGridColumns, type, fieldName, fieldTitle, widthColumn, typeFormFields, viewConfig.getLabelField(),
                            sizeColumnMap, viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                    
                }else{
                    jc.addEntityCombobox(jsonGridColumns, fieldName, fieldTitle, fieldEntity, widthColumn,
                                viewConfig.isEditableGrid(), readOnly, fieldsNN.contains(fieldName));
                    
                }
            }

            // ADD TO jsonEmptyModel
            if(!fieldName.equals("id")){
                jsonEmptyModel.put(fieldName, (defaultValueMap.containsKey(fieldName))?defaultValueMap.get(fieldName):"");
            }
        }
        
        mav.addObject("titledFieldsMap", titledFieldsMap);
        mav.addObject("jsonFormFields", jsonFormFields.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonGridColumns", jsonGridColumns.toString().replaceAll("\"@", "").replaceAll("@\"", ""));
        mav.addObject("jsonEmptyModel", jsonEmptyModel.toString());
        mav.addObject("sortColumns", sortColumns.toString());
    }

}
