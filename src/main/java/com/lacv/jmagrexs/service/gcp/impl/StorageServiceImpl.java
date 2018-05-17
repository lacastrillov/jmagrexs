/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.service.gcp.impl;

import com.lacv.jmagrexs.dto.GrupoDotGoogleCredencial;
import com.lacv.jmagrexs.service.gcp.StorageService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author desarrollador
 */
@Service
public class StorageServiceImpl implements StorageService {
    
    private static final Logger LOGGER = Logger.getLogger(StorageServiceImpl.class);

    private final String APPLICATION_NAME = "Google Cloud Platform Storage";
    
    
    /**
     * Fetch a list of the objects within the given bucket.
     *
     * @param bucketName the name of the bucket to list.
     * @param fileName
     * @param credencial
     * @return a list of the contents of the specified bucket.
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public StorageObject getStorageObject(String bucketName, String fileName, GrupoDotGoogleCredencial credencial) throws Exception {
        Storage client = buildService(credencial);
        Storage.Objects.List listRequest = client.objects().list(bucketName);

        List<StorageObject> results = new ArrayList<>();
        Objects objects;

        // Iterate through each page of results, and add them to our results list.
        String nextPageToken;
        do {
            objects = listRequest.execute();
            // Add the items in this page of results to the list we'll return.
            if(objects.getItems()!=null){
                results.addAll(objects.getItems());
            }
            // Get the next page, in the next iteration of this loop.
            nextPageToken= objects.getNextPageToken();
            listRequest.setPageToken(nextPageToken);
        } while (nextPageToken!=null);
        
        for(StorageObject obj: results){
            if(obj.getName().equals(fileName)){
                return obj;
            }
        }

        return null;
    }

    /**
     * Fetch a list of the objects within the given bucket.
     *
     * @param bucketName the name of the bucket to list.
     * @param credencial
     * @return a list of the contents of the specified bucket.
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public List<StorageObject> listBucket(String bucketName, GrupoDotGoogleCredencial credencial) throws Exception {
        Storage client = buildService(credencial);
        Storage.Objects.List listRequest = client.objects().list(bucketName);

        List<StorageObject> results = new ArrayList<>();
        Objects objects;

        // Iterate through each page of results, and add them to our results list.
        String nextPageToken;
        do {
            objects = listRequest.execute();
            // Add the items in this page of results to the list we'll return.
            if(objects.getItems()!=null){
                results.addAll(objects.getItems());
            }
            // Get the next page, in the next iteration of this loop.
            nextPageToken= objects.getNextPageToken();
            listRequest.setPageToken(nextPageToken);
        } while (nextPageToken!=null);

        return results;
    }

    /**
     * Fetches the metadata for the given bucket.
     *
     * @param bucketName the name of the bucket to get metadata about.
     * @param credencial
     * @return a Bucket containing the bucket's metadata.
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public Bucket getBucket(String bucketName, GrupoDotGoogleCredencial credencial) throws Exception {
        Storage client = buildService(credencial);

        Storage.Buckets.Get bucketRequest = client.buckets().get(bucketName);
        // Fetch the full set of the bucket's properties (e.g. include the ACLs in
        // the response)
        bucketRequest.setProjection("full");
        return bucketRequest.execute();
    }

    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param file the file to upload.
     * @param bucketName the name of the bucket to create the object in.
     * @param credencial
     * @return 
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public StorageObject uploadFile(String name, String contentType, File file, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception {
        InputStreamContent contentStream = new InputStreamContent(contentType, new FileInputStream(file));
        
        // Setting the length improves upload performance
        contentStream.setLength(file.length());
        StorageObject objectMetadata = new StorageObject()
                .setContentDisposition("interna1")
                // Set the destination object name
                .setName(name)
                
                .setCacheControl("public, max-age=600000")
                // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage client = buildService(credencial);
        Storage.Objects.Insert insertRequest = client.objects().insert(bucketName, objectMetadata, contentStream);
        
        

        StorageObject obj= insertRequest.execute();
        
        return obj;
    }
    
    /**
     * Uploads data to an object in a bucket.
     *
     * @param name the name of the destination object.
     * @param contentType the MIME type of the data.
     * @param inputStream
     * @param bucketName the name of the bucket to create the object in.
     * @param credencial
     * @return 
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public StorageObject uploadFile(String name, String contentType, InputStream inputStream, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception {
        InputStreamContent contentStream = new InputStreamContent(contentType, inputStream);
        
        // Setting the length improves upload performance
        //contentStream.setLength(file.length());
        StorageObject objectMetadata = new StorageObject()
                
                .setContentDisposition("attachment")
                // Set the destination object name
                .setName(name)
                
                .setCacheControl("public, max-age=600000")
                // Set the access control list to publicly read-only
                .setAcl(Arrays.asList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")));

        // Do the insert
        Storage client = buildService(credencial);
        Storage.Objects.Insert insertRequest = client.objects().insert(bucketName, objectMetadata, contentStream);

        StorageObject obj= insertRequest.execute();
        
        return obj;
    }

    /**
     * Deletes an object in a bucket.
     *
     * @param path the path to the object to delete.
     * @param bucketName the bucket the object is contained in.
     * @param credencial
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public void deleteObject(String path, String bucketName, GrupoDotGoogleCredencial credencial) throws Exception {
        Storage client = buildService(credencial);
        client.objects().delete(bucketName, path).execute();
    }

    /**
     * Metodo para crear un cliente autorizado para Google Storage.
     *
     * @param credencial
     * @return The Storage Service
     * @throws IOException Thrown if there is an error connecting
     */
    private Storage buildService(GrupoDotGoogleCredencial credencial) throws IOException, GeneralSecurityException {
        GoogleCredential credential = connectGoogleCloudPlatformClient(credencial);

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        return new Storage.Builder(transport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Metodo para conectarse a Google Clowd Platform BigQuery.
     *
     * @param <code>credencial></code> Objeto que se espera
     * contenga las credenciales de GCP.
     * @return The BigQuery Service
     * @throws IOException Thrown if there is an error connecting
     */
    private static GoogleCredential connectGoogleCloudPlatformClient(GrupoDotGoogleCredencial credencial) {
        GoogleCredential objGoogleCredential = null;
        try {
            //File objFilePrivateKey = new File("/home/desarrollador/NetBeansProjects/UploadImageStorage/src/main/resources/novaventa-co-7652830868f6.p12");
            //File objFilePrivateKey = new File("/home/desarrollador/NetBeansProjects/UploadImageStorage/src/main/resources/grupodot-101-gcp-22697afcc28d.p12");
            File objFilePrivateKey = new File(StorageServiceImpl.class.getClassLoader().getResource(credencial.getKeyFile()).getFile());
            
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = new JacksonFactory();

            GoogleCredential.Builder credBuilder = new GoogleCredential.Builder();
            credBuilder.setJsonFactory(jsonFactory);
            credBuilder.setTransport(transport);
            credBuilder.setServiceAccountId(credencial.getAccountId());
            credBuilder.setServiceAccountPrivateKeyFromP12File(objFilePrivateKey);
            credBuilder.setServiceAccountScopes(Arrays.asList(StorageScopes.CLOUD_PLATFORM));

            objGoogleCredential = credBuilder.build();

            if (objGoogleCredential.createScopedRequired()) {
                objGoogleCredential = objGoogleCredential.createScoped(StorageScopes.all());
            }

            return objGoogleCredential;
        } catch (GeneralSecurityException e) {
            LOGGER.error(e);
            System.out.println("\n...Error al conectar a __Google Cloud Platform__ " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e);
            System.out.println("\n...Error al conectar a __Google Cloud Platform__ " + e.getMessage());
        }
        return objGoogleCredential;
    }
    
}
