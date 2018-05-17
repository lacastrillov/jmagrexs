/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.service.gcp;

import com.lacv.jmagrexs.dto.GrupoDotGoogleCredencial;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author desarrollador
 */
public interface StorageService {
    
    StorageObject getStorageObject(String bucketName, String fileName, GrupoDotGoogleCredencial credencial) throws Exception;
    
    List<StorageObject> listBucket(String bucketName, GrupoDotGoogleCredencial credencial) throws Exception;
    
    Bucket getBucket(String bucketName, GrupoDotGoogleCredencial credencial) throws Exception;
    
    StorageObject uploadFile(String name, String contentType, File file, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception;
    
    StorageObject uploadFile(String name, String contentType, InputStream inputStream, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception;
    
    void deleteObject(String path, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception;
            
}
