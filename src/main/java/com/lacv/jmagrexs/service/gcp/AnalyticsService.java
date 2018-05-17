package com.lacv.jmagrexs.service.gcp;


import com.lacv.jmagrexs.dto.AnalyticsResultDto;
import com.google.api.services.analytics.Analytics;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A simple example of how to access the Google Analytics API using a service
 * account.
 */
public interface AnalyticsService {
    
    AnalyticsResultDto findInGoogleAnalytics(String accountId, String keyFile, String metrics, String dimensions, String startDate, String endDate);
    
    AnalyticsResultDto findInGoogleAnalytics(String accountId, String keyFile, String metrics, String dimensions, String startDate, String endDate, Integer startIndex, Integer maxResults);

    String getFirstProfileId(Analytics analytics) throws IOException;
    
    List<HashMap> getResults(Analytics analytics, String profileId, String startDate, String endDate, String metrics, String dimensions, Integer startIndex, Integer maxResults) throws IOException;
    
}