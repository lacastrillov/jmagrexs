/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.components.ExtViewConfig;
import com.dot.gcpbasedot.components.MenuComponent;
import com.dot.gcpbasedot.components.ServerDomain;
import com.dot.gcpbasedot.dto.MenuItem;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author lacastrillov
 */
public abstract class ExtController {
    
    @Autowired
    public ExtViewConfig extViewConfig;
    
    @Autowired
    public MenuComponent menuComponent;
    
    @Autowired
    public ServerDomain serverDomain;
    
    
    public JSONArray getMenuItems(HttpSession session, MenuComponent globalMenuComponent){
        JSONArray menuJSON= new JSONArray();
        if(session.getAttribute("menuItems")==null){
            List<MenuItem> menuData= globalMenuComponent.getMenuData();
            menuData= configureVisibilityMenu(menuData);
            menuJSON= generateMenuJSON(menuData);
            session.setAttribute("menuItems", menuJSON.toString());
        }else{
            menuJSON= new JSONArray((String)session.getAttribute("menuItems"));
        }
        
        return menuJSON;
    }
    
    private JSONArray generateMenuJSON(List<MenuItem> menuItems){
        JSONArray menuJSON= new JSONArray();
        for (MenuItem menuDataI : menuItems) {
            if(menuDataI.isVisible()){
                JSONObject menuParent= new JSONObject();
                MenuItem itemParent = menuDataI;
                menuParent.put("text", itemParent.getItemTitle());
                if(menuDataI.getType().equals(MenuItem.CHILD)){
                    menuParent.put("href", itemParent.getHref());
                }
                if(itemParent.getSubMenus().size()>0){
                    JSONObject menu= new JSONObject();
                    JSONArray items= generateMenuJSON(itemParent.getSubMenus());
                    menu.put("items", items);
                    menuParent.put("menu", menu);
                }
                menuJSON.put(menuParent);
            }
        }
        return menuJSON;
    }
    
    protected List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        // ABSTRACT CODE HERE
        return menuData;
    }
    
}
