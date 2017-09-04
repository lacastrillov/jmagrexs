/**
 *
 */
package com.dot.gcpbasedot.dto;

/**
 * @author lcastrillo
 *
 */
public class GrupoDotGoogleCredencial {

    private String accountId = "";
    
    private String keyFile = "";
    
    private String projectId = "";
    
    private String datasetId = "";
    
    private String tableId = "";
    
    private String data = "";
    

    public GrupoDotGoogleCredencial() {
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GrupoDotGoogleCredencial [sACCOUNT_ID=" + accountId
                + ", sKEY_FILE=" + keyFile + ", sPROJECT_ID=" + projectId
                + ", sDATASET_ID=" + datasetId + ", sTABLE_ID=" + tableId
                + ", sData=" + data + "]";
    }

    /**
     * @return the accountId
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the keyFile
     */
    public String getKeyFile() {
        return keyFile;
    }

    /**
     * @param keyFile the keyFile to set
     */
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the datasetId
     */
    public String getDatasetId() {
        return datasetId;
    }

    /**
     * @param datasetId the datasetId to set
     */
    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    /**
     * @return the tableId
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * @param tableId the tableId to set
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

}
