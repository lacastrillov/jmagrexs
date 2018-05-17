/*
 * © 2015 All rights reserved. GrupoDot.
 *
 * Licecia establecida por los derechos de implicitos en GrupoDot.
 * 
 */
package com.lacv.jmagrexs.service.gcp.impl;

import com.lacv.jmagrexs.dto.GrupoDotGoogleCredencial;
import com.lacv.jmagrexs.service.gcp.BigQueryService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Jobs.GetQueryResults;
import com.google.api.services.bigquery.Bigquery.Jobs.Query;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.api.services.bigquery.model.TableDataInsertAllRequest.Rows;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import java.io.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Implementación de la clase BigQueryServiceImpl.java
 *
 * Objecto que se espera ejecute una consulta asincrona con BitTable.
 *
 * @author Luis Castrillo (lcastrillo)
 * @Since Sep 26, 2015 1:05:07 AM
 * @Version 1.1
 *
 */
@Service
public class BigQueryServiceImpl implements BigQueryService {
    
    private final String APPLICATION_NAME = "Google Cloud Platform BigQuery";

    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Protected because this is a collection of static methods.
     */
    protected BigQueryServiceImpl() {

    }

    /**
     * @param credencial
     * @param sQueryString
     * @return Objecto tipo <code>Iterator</code> que se espera tenga la
     * coleccion de datos consultados.
     * @throws IOException ioexception
     * @throws GeneralSecurityException
     */
    @Override
    public Iterator<GetQueryResultsResponse> runQuery(GrupoDotGoogleCredencial credencial, final String sQueryString) throws Exception {
        //Realizamos la conexion con la plataforma GCP e intanciamos el objeto de la consulta.	 
        Bigquery objBigquery = connectBigquery(credencial);

        //Creamos una varible para el timepo de espera entre las consultas.
        final long lWaitTime = 10;
        //Espere hasta que la consulta se realiza con 10 segundos de tiempo de espera, en la mayoría de 5 reintentos de error.
        QueryRequest queryRequest = new QueryRequest().setTimeoutMs(lWaitTime).setQuery(sQueryString);
        Query query = objBigquery.jobs().query(credencial.getProjectId(), queryRequest);
        QueryResponse queryResponse = query.execute();

        //Haz una solicitud para obtener los resultados de la consulta
        //(tiempo de espera es de cero, ya que el trabajo debe ser completa)
        System.out.println("objGetQueryResults");
        GetQueryResults objGetQueryResults = objBigquery.jobs().getQueryResults(queryResponse.getJobReference().getProjectId(), queryResponse.getJobReference().getJobId());

        //Obtenemos la coleccion filan como resultado de la consulta
        System.out.println("objIteratorGetQueryResultsResponse");
        Iterator<GetQueryResultsResponse> objIteratorGetQueryResultsResponse = GDGACBigqueryUtils.getPages(objGetQueryResults);

        return objIteratorGetQueryResultsResponse;
    }

    /**
     * Creates a Query Job for a particular query on a dataset
     *
     * @param credencial
     * @param records
     * @return a reference to the inserted query job
     * @throws IOException
     * @throws java.security.GeneralSecurityException
     */
    @Override
    public TableDataInsertAllResponse insertQuery(GrupoDotGoogleCredencial credencial, List<Map> records) throws Exception {
        System.out.println("\nInserting Query List: " + records.size());

        Bigquery bigquery = connectBigquery(credencial);

        List<Rows> rows = new ArrayList<>();
        for (Map<String, Object> record : records) {
            Rows r = new Rows();
            r.setJson(record);
            rows.add(r);
            System.out.print(record);
        }

        TableDataInsertAllRequest content = new TableDataInsertAllRequest().setRows(rows);

        System.out.println("Rows SIZE: " + content.getRows().size());

        TableDataInsertAllResponse response = bigquery.tabledata().insertAll(credencial.getProjectId(),
                credencial.getDatasetId(),
                credencial.getTableId(),
                content).execute();

        System.out.println("\nNum Rows inserted:\n" + response.toPrettyString());

        return response;
    }

    /**
     * Metodo para crear un cliente autorizado para Google BigQuery.
     *
     * @param credencial
     * @return The BigQuery Service
     * @throws IOException Thrown if there is an error connecting
     */
    private Bigquery connectBigquery(GrupoDotGoogleCredencial credencial) throws Exception {
        GoogleCredential credential = connectGoogleCloudPlatformClient(credencial);

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Bigquery.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    /**
     * Metodo para conectarse a Google Clowd Platform BigQuery.
     *
     * @param <code>objGrupoDotGoogleCredencial></code> Objeto que se espera
     * contenga las credenciales de GCP.
     * @return The BigQuery Service
     * @throws IOException Thrown if there is an error connecting
     */
    private GoogleCredential connectGoogleCloudPlatformClient(GrupoDotGoogleCredencial credencial) {
        GoogleCredential objGoogleCredential = null;
        try {
            File objFilePrivateKey = new File(
                    BigQueryServiceImpl.class.getClassLoader().getResource(credencial.getKeyFile()).getFile());

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            // JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            GoogleCredential.Builder credBuilder = new GoogleCredential.Builder();
            credBuilder.setJsonFactory(JSON_FACTORY);
            credBuilder.setTransport(httpTransport);
            credBuilder.setServiceAccountId(credencial.getAccountId());
            credBuilder.setServiceAccountPrivateKeyFromP12File(objFilePrivateKey);
            credBuilder.setServiceAccountScopes(Arrays.asList(BigqueryScopes.BIGQUERY));

            objGoogleCredential = credBuilder.build();

            if (objGoogleCredential.createScopedRequired()) {
                objGoogleCredential = objGoogleCredential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
            }

            return objGoogleCredential;
        } catch (GeneralSecurityException e) {
            System.out.println("\n...Error al conectar a __Google Cloud Platform__ " + e.getMessage());
        } catch (IOException e) {
            System.out.println("\n...Error al conectar a __Google Cloud Platform__ " + e.getMessage());
        }
        return objGoogleCredential;
    }
    
}