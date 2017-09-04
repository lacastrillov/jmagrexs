/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.controller;

import com.dot.gcpbasedot.components.ExtViewConfig;
import com.dot.gcpbasedot.components.MenuComponent;
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
    
    protected MenuComponent globalMenuComponent;
    
    
    protected void addMenuComponent(MenuComponent menuComponent){
        this.globalMenuComponent= menuComponent;
    }
    
    public JSONArray getMenuItems(HttpSession session, MenuComponent globalMenuComponent){
        JSONArray menuItems= new JSONArray();
        if(session.getAttribute("menuItems")==null){
            List<MenuItem> menuData= globalMenuComponent.getMenuData();
            menuData= configureVisibilityMenu(menuData);

            for (MenuItem menuDataI : menuData) {
                if(menuDataI.isVisible()){
                    JSONObject menuParent= new JSONObject();
                    MenuItem itemParent = menuDataI;
                    menuParent.put("text", itemParent.getItemTitle());
                    JSONObject menu= new JSONObject();
                    JSONArray items= new JSONArray();
                    for(int j=0; j<itemParent.getSubMenus().size(); j++){
                        MenuItem itemChild= itemParent.getSubMenus().get(j);
                        if(itemChild.isVisible()){
                            JSONObject item= new JSONObject();
                            item.put("text", itemChild.getItemTitle());
                            item.put("href", itemChild.getHref());
                            items.put(item);
                        }
                    }
                    menu.put("items", items);
                    menuParent.put("menu", menu);
                    menuItems.put(menuParent);
                }
            }
            session.setAttribute("menuItems", menuItems.toString());
        }else{
            menuItems= new JSONArray((String)session.getAttribute("menuItems"));
        }
        
        return menuItems;
    }
    
    protected List<MenuItem> configureVisibilityMenu(List<MenuItem> menuData){
        // ABSTRACT CODE HERE
        return menuData;
    }
    
}
