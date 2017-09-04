package com.dot.gcpbasedot.service.gcp.impl;

import com.dot.gcpbasedot.dto.AnalyticsResultDto;
import com.dot.gcpbasedot.dto.GrupoDotGoogleCredencial;
import com.dot.gcpbasedot.service.gcp.AnalyticsService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    
    protected static final Logger LOGGER = Logger.getLogger(AnalyticsServiceImpl.class);

    private final String APPLICATION_NAME = "Hello Analytics";
    
    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    
    @Override
    public AnalyticsResultDto findInGoogleAnalytics(String accountId, String keyFile, String metrics, String dimensions, String startDate, String endDate) {
        
        return findInGoogleAnalytics(accountId, keyFile, metrics, dimensions, startDate, endDate, 1, 1000);
    }
    
    @Override
    public AnalyticsResultDto findInGoogleAnalytics(String accountId, String keyFile, String metrics, String dimensions, String startDate, String endDate, Integer startIndex, Integer maxResults) {
        try {
            AnalyticsResultDto analyticsResult= new AnalyticsResultDto();
            
            GrupoDotGoogleCredencial credencial= new GrupoDotGoogleCredencial();
            credencial.setAccountId(accountId);
            credencial.setKeyFile(keyFile);
            
            Analytics analytics = initializeAnalytics(credencial);

            String profile = getFirstProfileId(analytics);
            
            System.out.println("First Profile Id: " + profile);
            List<HashMap> results= getResults(analytics, profile, startDate, endDate, metrics, dimensions, startIndex, maxResults);
            analyticsResult.setResults(results);
            
            if (results != null && !results.isEmpty()) {
                String[] columns= getColumns(metrics, dimensions);
                analyticsResult.setColumns(columns);
            }
            
            //printResults(results, metrics, dimensions);
            return analyticsResult;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public String getFirstProfileId(Analytics analytics) throws IOException {
        // Get the first view (profile) ID for the authorized user.
        String profileId = null;

        // Query for the list of all accounts associated with the service account.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            System.err.println("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query for the list of properties associated with the first account.
            Webproperties properties = analytics.management().webproperties().list(firstAccountId).execute();

            if (properties.getItems().isEmpty()) {
                System.err.println("No Webproperties found");
            } else {
                String firstWebpropertyId = properties.getItems().get(0).getId();

                // Query for the list views (profiles) associated with the property.
                Profiles profiles = analytics.management().profiles().list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    System.err.println("No views (profiles) found");
                } else {
                    // Return the first (view) profile associated with the property.
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }
    
    /**
     *
     * @param analytics
     * @param profileId
     * @param startDate
     * @param endDate
     * @param metrics
     * @param dimensions
     * @param startIndex
     * @param maxResults
     * @return
     * @throws IOException
     */
    @Override
    public List<HashMap> getResults(Analytics analytics, String profileId, String startDate, String endDate,
            String metrics, String dimensions, Integer startIndex, Integer maxResults) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.
        GaData data= analytics.data().ga().get("ga:" + profileId, startDate, endDate, metrics)
                .setDimensions(dimensions).setStartIndex(startIndex).setMaxResults(maxResults).execute();
        
        if (data != null && !data.getRows().isEmpty()) {
            List<HashMap> results= new ArrayList<>();
            
            String[] columns= getColumns(metrics, dimensions);

            List<List<String>> rows= data.getRows();
            for(List<String> row : rows){
                HashMap rowObj= new HashMap();
                for(int i=0; i<columns.length; i++){
                    rowObj.put(columns[i], row.get(i));
                }
                results.add(rowObj);
            }
            return results;
        }
        return null;
    }
    
    private String[] getColumns(String metrics, String dimensions){
        String columns_text="";
        if(dimensions!=null && !dimensions.equals("")){
            columns_text= dimensions;
        }
        if(metrics!=null && !metrics.equals("")){
            if(!columns_text.equals("")){
                columns_text+=",";
            }
            columns_text+= metrics;
        }
        
        return columns_text.replaceAll("ga\\:", "").split(",");
    }

    public void printResults(List<HashMap> results, String metrics, String dimensions) {
        // Parse the response from the Core Reporting API for
        // the profile name and number of sessions.
        
        if (results != null && !results.isEmpty()) {
            String[] columns= getColumns(metrics, dimensions);
            for(String column : columns){
                System.out.print(column+"   ||   ");
            }
            System.out.println();
            System.out.println();
            for(HashMap row : results){
                for(String column : columns){
                    System.out.print(row.get(column)+"   ||   ");
                }
                System.out.println();
            }
        } else {
            System.out.println("No results found");
        }
    }
    
    private Analytics initializeAnalytics(GrupoDotGoogleCredencial credencial) throws Exception {
        // Initializes an authorized analytics service object.
        GoogleCredential credential = connectGoogleCloudPlatformClient(credencial);

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // Construct the Analytics service object.
        return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    }
    
    /**
     * Metodo para conectarse a Google Clowd Platform.
     *
     * @param credencial
     * @return The BigQuery Service
     */
    private GoogleCredential connectGoogleCloudPlatformClient(GrupoDotGoogleCredencial credencial) {
        GoogleCredential objGoogleCredential = null;
        try {
            File objFilePrivateKey = new File(
                    AnalyticsServiceImpl.class.getClassLoader().getResource(credencial.getKeyFile()).getFile());

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleCredential.Builder credBuilder = new GoogleCredential.Builder();
            credBuilder.setJsonFactory(JSON_FACTORY);
            credBuilder.setTransport(httpTransport);
            credBuilder.setServiceAccountId(credencial.getAccountId());
            credBuilder.setServiceAccountPrivateKeyFromP12File(objFilePrivateKey);
            credBuilder.setServiceAccountScopes(Arrays.asList(AnalyticsScopes.ANALYTICS));

            objGoogleCredential = credBuilder.build();

            if (objGoogleCredential.createScopedRequired()) {
                objGoogleCredential = objGoogleCredential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            }

            return objGoogleCredential;
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("\n...Error al conectar a __Google Cloud Platform__ " + e.getMessage());
        }
        return objGoogleCredential;
    }

}
