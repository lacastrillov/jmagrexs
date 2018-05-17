/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.dto;

import com.lacv.jmagrexs.components.ServerDomain;
import com.lacv.jmagrexs.enums.PageType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author desarrollador
 */
public class MenuItem {
    
    public static final String PARENT= "parent";
    
    public static final String CHILD= "child";
    
    private PageType pageType;
    
    private String type;
    
    private String entityRef;
    
    private String itemTitle;
    
    private String reportName;
    
    private int itemPosition;
    
    private boolean visible;
    
    private List<MenuItem> subMenus= new ArrayList<>();
    
    
    public MenuItem(String itemTitle){
        this.type= "parent";
        this.entityRef= "";
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.itemPosition= 1000;
        this.visible= true;
    }
    
    public MenuItem(String itemTitle, int itemPosition){
        this.type= "parent";
        this.entityRef= "";
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.itemPosition= itemPosition;
        this.visible= true;
    }
    
    public MenuItem(String entityRef, String itemTitle){
        this.pageType= PageType.ENTITY;
        this.type= "child";
        this.entityRef= entityRef;
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.itemPosition= 1000;
        this.visible= true;
    }
    
    public MenuItem(String entityRef, String itemTitle, int itemPosition){
        this.pageType= PageType.ENTITY;
        this.type= "child";
        this.entityRef= entityRef;
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.itemPosition= itemPosition;
        this.visible= true;
    }
    
    /**
     * @return the href
     */
    public String getHref(){
        if(this.type.equals(CHILD)){
            ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
            ServerDomain serverDomain= (ServerDomain) ctx.getBean("serverDomain");
            String href= serverDomain.getContextPath() + serverDomain.getAdminPath() + "/" 
                    + this.entityRef + "/" + this.pageType.getPageRef();
            if (this.pageType==PageType.REPORT) {
                href+= "/" + this.reportName + ".htm";
            } else {
                href+= ".htm";
            }
            return href;
        }
        return "";
    }

    /**
     * 
     * @return pageType
     */
    public PageType getPageType() {
        return pageType;
    }

    /**
     * 
     * @param pageType 
     */
    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the entityRef
     */
    public String getEntityRef() {
        return entityRef;
    }

    /**
     * @param entityRef the entityRef to set
     */
    public void setEntityRef(String entityRef) {
        this.entityRef = entityRef;
    }

    /**
     * @return the itemTitle
     */
    public String getItemTitle() {
        return itemTitle;
    }

    /**
     * @param itemTitle the itemTitle to set
     */
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    /**
     * @return the reportName
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * @param reportName the reportName to set
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * @return the itemPosition
     */
    public int getItemPosition() {
        return itemPosition;
    }

    /**
     * @param itemPosition the itemPosition to set
     */
    public void setItemPosition(int itemPosition) {
        if(itemPosition!=1000){
            this.itemPosition = itemPosition;
        }
    }

    /**
     * @return the subMenus
     */
    public List<MenuItem> getSubMenus() {
        return subMenus;
    }

    /**
     * @param subMenus the subMenus to set
     */
    public void setSubMenus(List<MenuItem> subMenus) {
        this.subMenus = subMenus;
    }
    
    /**
     * @param subMenu
     */
    public void addSubMenu(MenuItem subMenu) {
        this.subMenus.add(subMenu);
    }

    /**
     * 
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * 
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
