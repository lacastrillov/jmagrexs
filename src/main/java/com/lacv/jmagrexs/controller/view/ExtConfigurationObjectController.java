package com.lacv.jmagrexs.controller.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.lacv.jmagrexs.components.JSONForms;
import com.lacv.jmagrexs.components.JSONModels;
import com.lacv.jmagrexs.dto.config.ConfigurationObjectConfig;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public abstract class ExtConfigurationObjectController extends ExtController {

    protected static final Logger LOGGER = Logger.getLogger(ExtConfigurationObjectController.class);
    
    private ConfigurationObjectConfig viewConfig;
    
    @Autowired
    public JSONModels jm;
    
    @Autowired
    public JSONForms jfo;
    
    private final Map<String, String> jsonModelMap= new HashMap();
    
    private final Map<String, String> jsonFormFieldsMap= new HashMap();
    
    private List<String> interfacesEntityRef= new ArrayList<>();
    
    
    protected void addControlMapping(ConfigurationObjectConfig viewConfig) {
        this.viewConfig= viewConfig;
        generateGeneralObjects();
    }

    @RequestMapping(value = "/configurationObject.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView configurationObject() {
        ModelAndView mav= new ModelAndView("configurationObject");
        
        mav.addObject("extViewConfig", extViewConfig);
        mav.addObject("serverDomain", serverDomain);
        mav.addObject("interfacesEntityRef", interfacesEntityRef);
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtViewport.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extViewport(HttpSession session) {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtViewport");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        if(menuComponent!=null){
            JSONArray menuItems= getMenuItems(session, menuComponent);
            mav.addObject("menuItems",menuItems.toString());
        }
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInit.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInit() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtInit");
        
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtModel.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extModel() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtModel");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        mav.addObject("nameConfigurationObjects", viewConfig.getNameConfigurationObjects());
        mav.addObject("jsonModelMap", jsonModelMap);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtStore.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extStore() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtStore");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtView.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extView() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtView");
        
        addGeneralObjects(mav);
        
        mav.addObject("nameConfigurationObjects", viewConfig.getNameConfigurationObjects());
        mav.addObject("jsonFormFieldsMap", jsonFormFieldsMap);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtController.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extController() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtController");
        
        addGeneralObjects(mav);
        
        return mav;
    }
    
    @RequestMapping(value = "/ExtInterfaces.htm", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView extInterfaces() {
        ModelAndView mav= new ModelAndView("scripts/configurationObject/ExtInterfaces");
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        mav.addObject("labelField", viewConfig.getLabelField());
        
        return mav;
    }
    
    private void addGeneralObjects(ModelAndView mav){
        List<String> modelsEntityRef= new ArrayList<>();
        
        modelsEntityRef.add(viewConfig.getMainConfigurationRef());
        
        mav.addObject("viewConfig", viewConfig);
        mav.addObject("entityRef", viewConfig.getMainConfigurationRef());
        mav.addObject("entityName", viewConfig.getMainConfigurationName());
        mav.addObject("labelField", viewConfig.getLabelField());
        mav.addObject("modelsEntityRef", modelsEntityRef);
    }
    
    private void generateGeneralObjects(){
        for (Map.Entry<String, Class> entry : viewConfig.getConfigurationObjects().entrySet()){
            JSONArray jsonModel = jm.getJSONRecursiveModel("", entry.getValue());
            jsonModelMap.put(entry.getKey(), jsonModel.toString());
            
            JSONArray jsonFormFields = jfo.getJSONProcessForm(entry.getKey(), "", entry.getValue());
            jsonFormFieldsMap.put(entry.getKey(), jsonFormFields.toString().replaceAll("\"#", "").replaceAll("#\"", ""));
            
            if(jfo.getInterfacesEntityRefMap().containsKey(entry.getKey())){
                interfacesEntityRef= jfo.getInterfacesEntityRefMap().get(entry.getKey());
            }
        }
    }

}
