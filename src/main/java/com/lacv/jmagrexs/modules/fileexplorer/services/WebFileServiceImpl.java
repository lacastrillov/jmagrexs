/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.fileexplorer.services;


import com.lacv.jmagrexs.modules.fileexplorer.daos.WebFileJpa;
import com.lacv.jmagrexs.modules.fileexplorer.entities.WebFile;
import com.lacv.jmagrexs.modules.fileexplorer.mappers.WebFileMapper;
import com.lacv.jmagrexs.dao.GenericDao;
import com.lacv.jmagrexs.dao.Parameters;
import com.lacv.jmagrexs.service.EntityServiceImpl;
import com.lacv.jmagrexs.util.FileService;
import com.lacv.jmagrexs.util.Formats;
import com.google.api.services.storage.model.StorageObject;
import com.lacv.jmagrexs.modules.common.constants.SystemConstants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lcastrillo
 */
@Service("webFileService")
public class WebFileServiceImpl extends EntityServiceImpl<WebFile> implements WebFileService {
    
    @Autowired
    public WebFileJpa webFileJpa;
    
    @Autowired
    public WebFileMapper webFileMapper;
    
    @Autowired
    SystemConstants systemConstants;
    
    
    @Override
    public GenericDao getGenericDao(){
        return webFileJpa;
    }
    
    
    @Override
    @Transactional(readOnly= true)
    public WebFile findByPath(String path){
        WebFile parentWebFile=null;
        String[] folders= path.split("/");
        
        if(folders.length>0){
            for(String folder: folders){
                if(!folder.equals("")){
                    Parameters p= new Parameters();
                    p.whereEqual("webFile", parentWebFile);
                    p.whereEqual("name", folder);
                    parentWebFile= super.loadByParameters(p);
                    if(parentWebFile==null){
                        return null;
                    }
                }
            }
        }
        return parentWebFile;
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public WebFile createByFileData(WebFile parentWebFile, int slice, String fileName, String fileType, int fileSize, InputStream is) throws IOException {
        WebFile webFile= new WebFile();
        webFile.setWebFile(parentWebFile);
        String path= webFile.getPath();
        String location= systemConstants.LOCAL_DIR + SystemConstants.ROOT_FOLDER + path;

        if(slice==0){
            webFile.setName(fileName);
            webFile.setType(fileType);
            webFile.setSize(fileSize);
            webFile.setIcon(Formats.getSimpleContentType(fileType));
            webFile.setCreationDate(new Date());
            webFile.setModificationDate(new Date());

            deleteIfExist(parentWebFile, fileName);
            create(webFile);

            FileService.deleteFile(location + fileName);
        }

        FileService.addPartToFile(fileName, location, fileSize, is);

        return webFile;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public WebFile createByStorageObject(StorageObject object, WebFile parent, String location) {
        WebFile webFile= new WebFile();
        
        webFile.setName(object.getName());
        webFile.setSize(object.getSize().intValue());
        webFile.setType(object.getContentType());
        webFile.setIcon(Formats.getSimpleContentType(object.getContentType()));
        webFile.setCreationDate(new Date());
        webFile.setModificationDate(new Date());
        
        super.create(webFile);
        return webFile;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public WebFile createFolder(WebFile parentWebFile, String folderName) {
        if(!folderName.equals("")){
            WebFile webFile= new WebFile();
            webFile.setName(folderName);
            webFile.setCreationDate(new Date());
            webFile.setType("folder");
            webFile.setIcon("folder");
            webFile.setModificationDate(new Date());
            webFile.setSize(1);
            webFile.setWebFile(parentWebFile);
            super.createForce(webFile);

            String path= webFile.getPath();
            String location= systemConstants.LOCAL_DIR + SystemConstants.ROOT_FOLDER + path;
            FileService.createFolder(location + webFile.getName());
            return webFile;
        }
        return null;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public WebFile createEmptyFile(WebFile parentWebFile, String fileName) {
        if(!fileName.equals("")){
            String extension= FilenameUtils.getExtension(fileName);
            WebFile webFile= new WebFile();
            webFile.setName(fileName);
            webFile.setCreationDate(new Date());
            webFile.setType(extension);
            webFile.setIcon(Formats.getSimpleContentType(extension));
            webFile.setModificationDate(new Date());
            webFile.setSize(1);
            webFile.setWebFile(parentWebFile);
            super.create(webFile);

            String path= webFile.getPath();
            String location= systemConstants.LOCAL_DIR + SystemConstants.ROOT_FOLDER + path;
            FileService.createFile(location + webFile.getName());

            return webFile;
        }else{
            return null;
        }
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public WebFile createDirectoriesIfMissing(String path){
        WebFile parentWebFile=null, webFile=null;
        String[] folders= path.split("/");
        
        if(folders.length>0){
            for(String folder: folders){
                if(!folder.equals("")){
                    Parameters p= new Parameters();
                    p.whereEqual("webFile", parentWebFile);
                    p.whereEqual("name", folder);
                    webFile= super.loadByParameters(p);
                    if(webFile==null){
                        webFile= createFolder(parentWebFile, folder);
                    }
                    parentWebFile= webFile;
                }
            }
        }
        return webFile;
    }
    
    @Override
    @Transactional(value = TRANSACTION_MANAGER, propagation = Propagation.REQUIRED)
    public boolean deleteIfExist(WebFile parentWebFile, String fileName){
        Parameters p= new Parameters();
        p.whereEqual("webFile", parentWebFile);
        p.whereEqual("name", fileName);
        List<WebFile> webFileInFolder= super.findByParameters(p);
        if(webFileInFolder.size()>0){
            for(WebFile WebFile: webFileInFolder){
                super.remove(WebFile);
            }
            return true;
        }
        return false;
    }
    
    
}
