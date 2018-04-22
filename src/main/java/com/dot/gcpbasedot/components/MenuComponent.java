/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.components;

import com.dot.gcpbasedot.dto.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author lacastrillov
 */
@Component
public class MenuComponent {

    private final List<MenuItem> menuData = new ArrayList<>();

    private boolean ordered= false;
    
    
    public void addItemMenu(MenuItem menuItem){
        addItem(menuData, menuItem);
    }
    
    private void addItem(List<MenuItem> menu, MenuItem menuItem){
        MenuItem existingMenuItem= getMenuItem(menu, menuItem.getItemTitle());
        if(existingMenuItem==null){
            menu.add(menuItem);
        }else{
            if(menuItem.getItemPosition()!=1000){
                existingMenuItem.setItemPosition(menuItem.getItemPosition());
            }
            for(MenuItem subMenuItem: menuItem.getSubMenus()){
                addItem(existingMenuItem.getSubMenus(), subMenuItem);
            }
        }
    }
    
    private MenuItem getMenuItem(List<MenuItem> menuItems, String itemTitle){
        for(MenuItem menuItem: menuItems){
            if(menuItem.getItemTitle().equals(itemTitle)){
                return menuItem;
            }
        }
        return null;
    }

    public List<MenuItem> getMenuData() {
        if (!ordered) {
            sortMenuItems(menuData);
            ordered= true;
        }
        return menuData;
    }
    
    private void sortMenuItems(List<MenuItem> menuItems){
        Collections.sort(menuItems, new MenuItemComparator());
        for(MenuItem subMenuItem: menuItems){
            if(subMenuItem.getSubMenus().size()>0){
                sortMenuItems(subMenuItem.getSubMenus());
            }
        }
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
