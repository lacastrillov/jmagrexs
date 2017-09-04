package com.dot.gcpbasedot.dto;

import org.apache.log4j.Logger;

public class ConnectionResponse {

    protected static final Logger LOGGER = Logger.getRootLogger();

///////////////////////////////////////////////////////////////////////////////
//
// PROPERTIES & CONSTRUCTORS
//
///////////////////////////////////////////////////////////////////////////////
    private final boolean success;
    
    private final int code;
    
    private final String rawBody;
    

    public ConnectionResponse(boolean success, int code, String rawBody) {

        this.success = success;
        this.code = code;

        if (rawBody == null) {
            LOGGER.info("rawBody was null; replacing with empty string");
            rawBody = new String();
        }
        this.rawBody = rawBody.trim();
    }

///////////////////////////////////////////////////////////////////////////////
//
// PUBLIC API
//
///////////////////////////////////////////////////////////////////////////////
    /**
     * Returns whether or not the response from the Firebase-client was
     * successful
     *
     * @return true if response from the Firebase-client was successful
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Returns the HTTP status code returned from the Firebase-client
     *
     * @return an integer representing an HTTP status code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Returns the raw data response returned by the Firebase-client
     *
     * @return a String of the JSON-response from the client
     */
    public String getRawBody() {
        return this.rawBody;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        result.append(ConnectionResponse.class.getSimpleName()).append("[ ")
                .append("(Success:").append(this.success).append(") ")
                .append("(Code:").append(this.code).append(") ")
                .append("(Raw-body:").append(this.rawBody).append(") ")
                .append("]");

        return result.toString();
    }
    
}
