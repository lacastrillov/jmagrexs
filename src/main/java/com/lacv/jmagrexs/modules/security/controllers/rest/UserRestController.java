/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.security.controllers.rest;


import com.lacv.jmagrexs.controller.rest.RestSessionController;
import com.lacv.jmagrexs.domain.BaseEntity;
import com.lacv.jmagrexs.modules.security.entities.User;
import com.lacv.jmagrexs.modules.security.mappers.UserMapper;
import com.lacv.jmagrexs.modules.security.services.UserService;
import com.lacv.jmagrexs.modules.fileexplorer.entities.WebFile;
import com.lacv.jmagrexs.modules.fileexplorer.services.WebFileService;
import com.lacv.jmagrexs.modules.security.services.SecurityService;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lcastrillo
 */
@Controller
@RequestMapping(value="/rest/user")
public class UserRestController extends RestSessionController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    UserMapper userMapper;
    
    @Autowired
    SecurityService securityService;
    
    @Autowired
    WebFileService webFileService;
    
    
    @PostConstruct
    public void init(){
        super.addControlMapping("user", userService, userMapper);
    }
    
    @Override
    public String saveFilePart(int slice, String fieldName, String fileName, String fileType, int fileSize, InputStream is, Object idParent) {
        String path= "imagenes/usuario/";
        WebFile parentWebFile= webFileService.createDirectoriesIfMissing(path);
        
        try {
            String imageName= idParent + "_" +fileName.replaceAll(" ", "_");
            WebFile webFile= webFileService.createByFileData(parentWebFile, slice, imageName, fileType, fileSize, is);
            
            User user = userService.loadById(idParent);
            user.setUrlPhoto(webFile.getLocation());
            userService.update(user);
            
            return "Archivo " + imageName + " almacenado correctamente";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    
    @Override
    public String saveResizedImage(String fieldName, String fileName, String fileType, int width, int height, int fileSize, InputStream is, Object idParent){
        String path= "imagenes/usuario/";
        WebFile parentWebFile= webFileService.createDirectoriesIfMissing(path);
        
        try {
            String imageName= idParent + "_" + width + "x" + height + "_" +fileName.replaceAll(" ", "_");
            webFileService.createByFileData(parentWebFile, 0, imageName, fileType, fileSize, is);
            
            return "Archivo " + imageName + " almacenado correctamente";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    
    @Override
    public JSONObject addSessionSearchFilter(JSONObject jsonFilters) {
        jsonFilters.getJSONObject("eq").put("id", securityService.getCurrentUser().getId());
        return jsonFilters;
    }

    @Override
    public JSONObject addSessionReportFilter(String reportName, JSONObject jsonFilters) {
        return jsonFilters;
    }
    
    @Override
    public boolean canLoad(BaseEntity entity){
        User user= (User) entity;
        return securityService.getCurrentUser().getId().equals(user.getId());
    }
    
    @Override
    public boolean canCreate(BaseEntity entity){
        return false;
    }
    
    @Override
    public boolean canUpdate(BaseEntity entity){
        User user= (User) entity;
        return securityService.getCurrentUser().getId().equals(user.getId());
    }

    @Override
    public boolean canDelete(BaseEntity entity) {
        return false;
    }

    @Override
    public boolean canUpdateByFilters(JSONObject jsonFilters) {
        return false;
    }

    @Override
    public boolean canDeleteByFilters(JSONObject jsonFilters) {
        return false;
    }
    
}
