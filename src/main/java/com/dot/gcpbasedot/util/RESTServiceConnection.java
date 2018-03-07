/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import com.dot.gcpbasedot.dto.ConnectionResponse;
import com.dot.gcpbasedot.dto.RESTServiceDto;
import com.dot.gcpbasedot.reflection.EntityReflection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author grupot
 */
public class RESTServiceConnection {

    private static final Logger LOGGER = Logger.getLogger(RESTServiceConnection.class);

    private final HttpClient client;
    
    private final RestTemplate restTemplate;

    private final RESTServiceDto externalService;
    
    

    public RESTServiceConnection(RESTServiceDto externalService) {
        this.externalService = externalService;
        //HttpClient
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(60000).build();
        client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        //RestTemplate
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
        SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        rf.setReadTimeout(60000);
        rf.setConnectTimeout(60000);
    }

    /**
     * @return the externalService
     */
    public RESTServiceDto getRESTService() {
        return externalService;
    }

    /**
     * 
     * @param headers
     * @param pathVars
     * @param parameters
     * @param body
     * @return
     * @throws IOException 
     */
    public Object getObjectResult(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters, Object body) throws IOException {
        Object result = null;
        String stringResult;
        if (body != null) {
            stringResult = getStringResult(headers, pathVars, body);
        } else {
            stringResult = getStringResult(headers, pathVars, parameters);
        }
        try {
            if(getRESTService().getOutClass().equals(String.class)){
                return stringResult;
            }
            switch (externalService.getResponseDataFormat()) {
                case RESTServiceDto.JSON:
                    result = EntityReflection.jsonToObject(stringResult, getRESTService().getOutClass());
                    break;
                case RESTServiceDto.XML:
                    result = XMLMarshaller.convertXMLToObject(stringResult, getRESTService().getOutClass());
                    break;
                default:
                    result = stringResult;
                    break;
            }
        } catch (Exception ex) {
            System.err.print("Invaild json Format");
        }

        return result;
    }

    /**
     * 
     * @param headers
     * @param pathVars
     * @param parameters
     * @param body
     * @return
     * @throws IOException 
     */
    public List<Object> getListResult(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters, Object body) throws IOException {
        List<Object> result = new ArrayList<>();
        String stringResult;
        if (body != null) {
            stringResult = getStringResult(headers, pathVars, body);
        } else {
            stringResult = getStringResult(headers, pathVars, parameters);
        }
        try {
            switch (externalService.getResponseDataFormat()) {
                case RESTServiceDto.JSON:
                    JSONArray array = new JSONArray(stringResult);
                    for (int i = 0; i < array.length(); i++) {
                        result.add(EntityReflection.jsonToObject(array.getJSONObject(i).toString(), getRESTService().getOutClass()));
                    }
                    break;
                case RESTServiceDto.XML:
                    result= XMLMarshaller.convertXMLToList(stringResult, getRESTService().getOutClass());
                    break;
                default:
                    result.add(stringResult);
                    break;
            }
        } catch (JSONException ex) {
            System.err.print("Invaild json Format");
        }

        return result;
    }

    /**
     * 
     * @param headers
     * @param pathVars
     * @param parameters
     * @return
     * @throws IOException 
     */
    public String getStringResult(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws IOException {
        ConnectionResponse response = null;
        switch (getRESTService().getMethod()) {
            case GET:
                response = get(headers, pathVars, parameters);
                break;
            case POST:
                response = post(headers, pathVars, parameters);
                break;
            case PATCH:
                response = patch(headers, pathVars, parameters);
                break;
            case PUT:
                response = put(headers, pathVars, parameters);
                break;
            case DELETE:
                response = delete(headers, pathVars);
                break;
        }
        if (response != null) {
            return response.getRawBody();
        }
        return null;
    }

    /**
     * 
     * @param headers
     * @param pathVars
     * @param body
     * @return
     * @throws IOException 
     */
    public String getStringResult(Map<String, String> headers, Map<String, String> pathVars, Object body) throws IOException {
        ConnectionResponse response = null;
        String stringData = "";
        if(headers==null){
            headers= new HashMap<>();
        }
        if (body != null) {
            switch (getRESTService().getInputDataFormat()) {
                case RESTServiceDto.JSON:
                    stringData = Util.objectToJson(body);
                    headers.put("Content-Type", "application/json");
                    break;
                case RESTServiceDto.XML:
                    stringData = XMLMarshaller.convertObjectToXML(body);
                    headers.put("Content-Type", "application/xml");
                    break;
                default:
                    stringData = (String) body;
                    break;
            }
        }
        switch (getRESTService().getMethod()) {
            case POST:
                response = post(headers, pathVars, stringData);
                break;
            case PATCH:
                response = patch(headers, pathVars, stringData);
                break;
            case PUT:
                response = put(headers, pathVars, stringData);
                break;
        }
        if (response != null) {
            return response.getRawBody();
        }
        return null;
    }
    
    /**
     * 
     * @param pathVars
     * @return
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    public String readUrl(Map<String, String> pathVars) throws MalformedURLException, UnsupportedEncodingException, IOException{
        String url = this.buildFullUrl(pathVars);
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
        }
        return response.toString();
    }
    
    /**
     * 
     * @param pathVars
     * @param parameters
     * @param data
     * @return
     * @throws UnsupportedEncodingException 
     */
    public String restTemplate(Map<String, String> pathVars, Map<String, String> parameters, String data) throws UnsupportedEncodingException{
        String result;
        String url = this.buildFullUrl(pathVars);        
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> accepts = new ArrayList<>();
        accepts.add(MediaType.APPLICATION_JSON);
        accepts.add(MediaType.APPLICATION_XML);
        headers.setAccept(accepts);
        org.springframework.http.HttpEntity<String> entity;
        
        if(getRESTService().getMethod().equals(HttpMethod.POST)){
            if(parameters!=null){
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    map.add(parameter.getKey(), parameter.getValue());
                }
                org.springframework.http.HttpEntity<MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(map, headers);
                ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
                result= response.getBody();
            }else{
                entity= new org.springframework.http.HttpEntity<>(data, headers);
                result= restTemplate.postForObject(url, entity, String.class);
            }
        }else{
            entity= new org.springframework.http.HttpEntity<>(headers);
            ResponseEntity res= restTemplate.exchange(url, getRESTService().getMethod(), entity, String.class);
            result= res.getBody().toString();
        }
        return result;
    }

    /**
     * GETs data from the provided-path relative to the base-url.
     *
     * @param headers
     * @param pathVars
     * @param parameters
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse get(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (parameters != null) {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                map.add(parameter.getKey(), parameter.getValue());
            }
        }
        url = UriComponentsBuilder.fromHttpUrl(url).queryParams(map).build().toUriString();
        HttpGet request = new HttpGet(url);
        addHeaders(request, headers);

        HttpResponse httpResponse = client.execute(request);
        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.GET, httpResponse);

        return response;
    }

    /**
     * POSTs data to the provided-path relative to the base-url (ie: creates).
     *
     * NOTE: the Firebase API does not treat this method in the conventional
     * way, but instead defines it as 'PUSH'; the API will insert this data
     * under the provided path but associated with a Firebase- generated key;
     * thus, every use of this method will result in a new insert even if the
     * provided path and data already exist.
     *
     * @param headers
     * @param pathVars
     * @param parameters
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse post(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        System.out.println("Url " + url);
        HttpPost request = new HttpPost(url);
        addHeaders(request, headers);
        if (parameters != null) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            request.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.POST, httpResponse);

        return response;
    }
    
    /**
     * POSTs data to the provided-path relative to the base-url (ie: creates).
     *
     * NOTE: the Firebase API does not treat this method in the conventional
     * way, but instead defines it as 'PUSH'; the API will insert this data
     * under the provided path but associated with a Firebase- generated key;
     * thus, every use of this method will result in a new insert even if the
     * provided path and data already exist.
     *
     * @param headers
     * @param pathVars
     * @param parameters
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse postUrlGet(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (parameters != null) {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                map.add(parameter.getKey(), parameter.getValue());
            }
        }
        url = UriComponentsBuilder.fromHttpUrl(url).queryParams(map).build().toUriString();
        HttpPost request = new HttpPost(url);
        addHeaders(request, headers);
        if (parameters != null) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            request.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.POST, httpResponse);

        return response;
    }

    /**
     * POSTs data to the provided-path relative to the base-url (ie: creates).
     *
     * NOTE: the Firebase API does not treat this method in the conventional
     * way, but instead defines it as 'PUSH'; the API will insert this data
     * under the provided path but associated with a Firebase- generated key;
     * thus, every use of this method will result in a new insert even if the
     * provided path and data already exist.
     *
     * @param headers
     * @param pathVars
     * @param stringData -- can be null/empty but will result in no data being
     * POSTed
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse post(Map<String, String> headers, Map<String, String> pathVars, String stringData) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpPost request = new HttpPost(url);
        addHeaders(request, headers);
        request.setEntity(new StringEntity(stringData));
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.POST, httpResponse);

        return response;
    }

    /**
     * 
     * @param server
     * @param headers
     * @param pathVars
     * @param parameters
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    public ConnectionResponse postHttps(String server, Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        CloseableHttpClient httpclient;
        HttpHost target = new HttpHost(server, 443, "https");

        //SSLContext sslcontext = SSLContexts.createSystemDefault();
        SSLContext sslcontext;
        sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(new KeyManager[0], new TrustManager[]{ new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                            System.out.println("getAcceptedIssuers =============");
                            return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs,
                                    String authType) {
                            System.out.println("checkClientTrusted =============");
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs,
                                    String authType) {
                            System.out.println("checkServerTrusted =============");
                    }
        } }, new SecureRandom());
    
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1", "SSLv3"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https", sslConnectionSocketFactory)
        .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setConnectionManager(cm)
                .build();

        String url = this.buildFullUrl(pathVars);
        HttpPost httpPost = new HttpPost(url);

        if (parameters != null) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        HttpResponse httpResponse = httpclient.execute(target, httpPost);
        ConnectionResponse response = this.processResponse(HttpMethod.POST, httpResponse);

        return response;
    }

    /**
     * PATCHs data on the provided-path relative to the base-url.
     *
     * @param headers
     * @param pathVars
     * @param parameters
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse patch(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpPatch request = new HttpPatch(url);

        if (parameters != null) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            request.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        addHeaders(request, headers);

        HttpResponse httpResponse = client.execute(request);
        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.PATCH, httpResponse);

        return response;
    }

    /**
     *
     * @param headers
     * @param pathVars
     * @param stringData
     * @return
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse patch(Map<String, String> headers, Map<String, String> pathVars, String stringData) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpPatch request = new HttpPatch(url);

        if (stringData != null) {
            request.setEntity(new StringEntity(stringData));
        }
        addHeaders(request, headers);

        HttpResponse httpResponse = client.execute(request);
        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.PATCH, httpResponse);

        return response;
    }

    /**
     * PUTs data to the provided-path relative to the base-url (ie: creates or
     * overwrites). If there is already data at the path, this data overwrites
     * it. If data is null/empty, any data existing at the path is deleted.
     *
     * @param headers
     * @param pathVars
     * @param parameters
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse put(Map<String, String> headers, Map<String, String> pathVars, Map<String, String> parameters) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpPut request = new HttpPut(url);
        addHeaders(request, headers);
        if (parameters != null) {
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                urlParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            request.setEntity(new UrlEncodedFormEntity(urlParameters));
        }
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.PUT, httpResponse);

        return response;
    }

    /**
     * PUTs data to the provided-path relative to the base-url (ie: creates or
     * overwrites). If there is already data at the path, this data overwrites
     * it. If data is null/empty, any data existing at the path is deleted.
     *
     * @param headers
     * @param pathVars
     * @param stringData -- can be null/empty
     * @return {@link HttpResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse put(Map<String, String> headers, Map<String, String> pathVars, String stringData) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpPut request = new HttpPut(url);
        addHeaders(request, headers);
        request.setEntity(new StringEntity(stringData));
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.PUT, httpResponse);

        return response;
    }

    /**
     * DELETEs data from the provided-path relative to the base-url.
     *
     * @param headers
     * @param pathVars
     * @return {@link ConnectionResponse}
     * @throws UnsupportedEncodingException
     */
    public ConnectionResponse delete(Map<String, String> headers, Map<String, String> pathVars) throws UnsupportedEncodingException, IOException {
        // make the request
        String url = this.buildFullUrl(pathVars);
        HttpDelete request = new HttpDelete(url);
        addHeaders(request, headers);
        HttpResponse httpResponse = client.execute(request);

        // process the response
        ConnectionResponse response = this.processResponse(HttpMethod.DELETE, httpResponse);

        return response;
    }

    private String buildFullUrl(Map<String, String> pathVars) throws UnsupportedEncodingException {
        String endpoint = getRESTService().getEndpoint();
        if (pathVars != null) {
            for (Map.Entry<String, String> pathVar : pathVars.entrySet()) {
                endpoint = endpoint.replaceAll("\\{" + pathVar.getKey() + "}", pathVar.getValue());
            }
        }

        return endpoint;
    }

    private void addHeaders(HttpRequestBase httpRequestBase, Map<String, String> headers) throws UnsupportedEncodingException {
        httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8");
        httpRequestBase.setHeader("Accept-Language", "es-CO,en-US;q=0.7,en;q=0.3");
        httpRequestBase.setHeader("Connection", "keep-alive");
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpRequestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    private ConnectionResponse processResponse(HttpMethod method, HttpResponse httpResponse) throws IOException {
        ConnectionResponse response;

        // sanity-checks
        if (method == null) {
            String msg = "method cannot be null";
            LOGGER.error(msg);
            throw new NullPointerException(msg);
        }
        if (httpResponse == null) {
            String msg = "httpResponse cannot be null";
            LOGGER.error(msg);
            throw new NullPointerException(msg);
        }

        // get the response-entity
        HttpEntity entity = httpResponse.getEntity();

        // get the response-code
        int code = httpResponse.getStatusLine().getStatusCode();

        // set the response-success
        boolean success = false;
        switch (method) {
            case DELETE:
                if (httpResponse.getStatusLine().getStatusCode() == 204
                        && "No Content".equalsIgnoreCase(httpResponse.getStatusLine().getReasonPhrase())) {
                    success = true;
                }
                break;
            case PATCH:
            case PUT:
            case POST:
            case GET:
                if (httpResponse.getStatusLine().getStatusCode() == 200
                        && "OK".equalsIgnoreCase(httpResponse.getStatusLine().getReasonPhrase())) {
                    success = true;
                }
                break;
            default:
                break;
        }

        // get the response-body
        Writer writer = new StringWriter();
        if (entity != null) {

            try {
                InputStream is = entity.getContent();
                char[] buffer = new char[1024];
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }

            } catch (IOException t) {
                String msg = "unable to read response-content; read up to this point: '" + writer.toString() + "'";
                writer = new StringWriter(); // don't want to later give jackson partial JSON it might choke on
                LOGGER.error(msg);
                throw new IOException(msg, t);

            } catch (IllegalStateException t) {
                String msg = "unable to read response-content; read up to this point: '" + writer.toString() + "'";
                writer = new StringWriter(); // don't want to later give jackson partial JSON it might choke on
                LOGGER.error(msg);
                throw new IllegalStateException(msg, t);
            }
        }

        // build the response
        response = new ConnectionResponse(success, code, writer.toString());

        return response;
    }

}
