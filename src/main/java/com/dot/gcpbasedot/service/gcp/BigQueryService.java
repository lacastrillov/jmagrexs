/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.service.gcp;

import com.dot.gcpbasedot.dto.GrupoDotGoogleCredencial;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.TableDataInsertAllResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author desarrollador
 */
public interface BigQueryService {
    
    Iterator<GetQueryResultsResponse> runQuery(GrupoDotGoogleCredencial credencial, final String sQueryString) throws Exception;
    
    TableDataInsertAllResponse insertQuery(GrupoDotGoogleCredencial credencial, List<Map> records) throws Exception;
    
}
