/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.dto;

import com.dot.gcpbasedot.enums.PageType;
import java.util.List;

/**
 *
 * @author desarrollador
 */
public class MenuItem {
    
    private PageType pageType;
    
    private String type;
    
    private String parentMenuTitle;
    
    private String entityRef;
    
    private String itemTitle;
    
    private String reportName;
    
    private String href;
    
    private int parentPosition;
    
    private int itemPosition;
    
    private boolean visible;
    
    private List<MenuItem> subMenus;
    
    
    public MenuItem(String itemTitle){
        this.pageType= PageType.ENTITY;
        this.type= "parent";
        this.parentMenuTitle= "";
        this.entityRef= "";
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.parentPosition= 1000;
        this.itemPosition= 1000;
        this.visible= true;
    }
    
    public MenuItem(String parentMenuTitle, String entityRef, String itemTitle){
        this.pageType= PageType.ENTITY;
        this.type= "child";
        this.parentMenuTitle= parentMenuTitle;
        this.entityRef= entityRef;
        this.itemTitle= itemTitle;
        this.reportName= "";
        this.parentPosition= 1000;
        this.itemPosition= 1000;
        this.visible= true;
    }

    public PageType getPageType() {
        return pageType;
    }

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
     * @return the parentMenuTitle
     */
    public String getParentMenuTitle() {
        return parentMenuTitle;
    }

    /**
     * @param parentMenuTitle the parentMenuTitle to set
     */
    public void setParentMenuTitle(String parentMenuTitle) {
        this.parentMenuTitle = parentMenuTitle;
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
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return the parentPosition
     */
    public int getParentPosition() {
        return parentPosition;
    }

    /**
     * @param parentPosition the parentPosition to set
     */
    public void setParentPosition(int parentPosition) {
        if(parentPosition!=1000){
            this.parentPosition = parentPosition;
        }
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
