/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lacv.jmagrexs.modules.fileexplorer.services;

import com.lacv.jmagrexs.modules.fileexplorer.entities.WebFile;
import com.lacv.jmagrexs.service.EntityService;
import com.google.api.services.storage.model.StorageObject;
import java.io.IOException;
import java.io.InputStream;



/**
 *
 * @author lacastrillov
 */
public interface WebFileService extends EntityService<WebFile> {
    
    WebFile findByPath(String path);
    
    WebFile createByFileData(WebFile parentWebFile, int slice, String fileName, String fileType, int fileSize, InputStream is) throws IOException;
    
    WebFile createByStorageObject(StorageObject object, WebFile parent, String location);
    
    WebFile createFolder(WebFile parentWebFile, String folderName);
    
    WebFile createEmptyFile(WebFile parentWebFile, String fileName);
    
    WebFile createDirectoriesIfMissing(String path);
    
    boolean deleteIfExist(WebFile parentWebFile, String fileName);
    
}
