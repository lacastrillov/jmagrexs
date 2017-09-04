/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

import com.dot.gcpbasedot.dto.MenuItem;
import com.dot.gcpbasedot.enums.PageType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author lacastrillov
 */
//@Component
public class MenuComponent {
    
    @Autowired
    private ServletContext context;

    private String basePath;

    private final Set parentMenuKey = new HashSet();

    private final List<MenuItem> menuData = new ArrayList<>();

    private boolean ordered= false;
    

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void addItemMenu(String parentMenuTitle, String entityRef, String itemTitle) {
        MenuItem menuItem = new MenuItem(parentMenuTitle, entityRef, itemTitle);
        addItemMenu(menuItem);
    }

    public void addItemMenu(MenuItem menuItem) {
        String href= context.getContextPath() + basePath + "/" + menuItem.getEntityRef() + "/" + menuItem.getPageType().getPageRef();
        if (menuItem.getPageType()==PageType.REPORT) {
            href+= "/" + menuItem.getReportName() + ".htm";
        } else {
            href+= ".htm";
        }
        menuItem.setHref(href);

        if (parentMenuKey.contains(menuItem.getParentMenuTitle()) == false) {
            MenuItem parentMenuItem = new MenuItem(menuItem.getParentMenuTitle());
            parentMenuItem.setItemPosition(menuItem.getParentPosition());

            List<MenuItem> itemsSubMenu = new ArrayList<>();
            itemsSubMenu.add(menuItem);
            parentMenuItem.setSubMenus(itemsSubMenu);
            menuData.add(parentMenuItem);
            parentMenuKey.add(menuItem.getParentMenuTitle());
        } else {
            for (int i = 0; i < menuData.size(); i++) {
                MenuItem parentMenuItem = menuData.get(i);
                if (parentMenuItem.getItemTitle().equals(menuItem.getParentMenuTitle())) {
                    menuData.get(i).setItemPosition(menuItem.getParentPosition());
                    menuData.get(i).getSubMenus().add(menuItem);
                }
            }
        }
    }

    public List<MenuItem> getMenuData() {
        if (!ordered) {
            Collections.sort(menuData, new MenuItemComparator());
            for(MenuItem parentMenuItem: menuData){
                List<MenuItem> subMenus= parentMenuItem.getSubMenus();
                Collections.sort(subMenus, new MenuItemComparator());
                parentMenuItem.setSubMenus(subMenus);
            }
            ordered= true;
        }
        return menuData;
    }
    
    class MenuItemComparator implements Comparator<MenuItem> {

        @Override
        public int compare(MenuItem o1, MenuItem o2) {
            if(o1.getItemPosition() < o2.getItemPosition()){
                return -1;
            }else if(o1.getItemPosition() == o2.getItemPosition()){
                return 0;
            }else{
                return 1;
            }
        }
    }

}
