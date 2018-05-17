package com.lacv.jmagrexs.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {

    private final Map<String, Object[]> equalParameters;

    private final Map<String, Object[]> greaterThanParameters;

    private final Map<String, Object[]> lessThanParameters;

    private final Map<String, Object[]> greaterThanOrEqualParameters;

    private final Map<String, Object[]> lessThanOrEqualParameters;

    private final Map<String, Object[]> differentThanParameters;

    private final Map<String, String> isParameters;

    private final Map<String, String> likeParameters;

    private final Map<String, Object[]> inParameters;
    
    private final Map<String, Object[]> notInParameters;

    private final Map<String, Object[]> betweenParameters;
    
    private final Map<String, String[]> queryParameters;
    
    private final Map<String, Object> valueMapParameters;

    private final Map<String, Object> updateValueParameters;

    private final List<String[]> orderByParameters;

    private Long page;

    private Long maxResults;

    private Long totalResults;

    private Long firstResult;

    private Long lastResult;

    private Long totalPages;

    public Parameters() {
        equalParameters = new HashMap<>();
        greaterThanParameters = new HashMap<>();
        lessThanParameters = new HashMap<>();
        greaterThanOrEqualParameters = new HashMap<>();
        lessThanOrEqualParameters = new HashMap<>();
        differentThanParameters = new HashMap<>();
        isParameters = new HashMap<>();
        likeParameters = new HashMap<>();
        inParameters = new HashMap<>();
        notInParameters = new HashMap<>();
        betweenParameters = new HashMap<>();
        queryParameters = new HashMap<>();
        valueMapParameters = new HashMap<>();
        updateValueParameters = new HashMap<>();
        orderByParameters = new ArrayList<>();
    }

    // *** Where methods

    public void whereEqual(String parameter, Object value) {
        if(value!=null){
            equalParameters.put(parameter, new Object[] { "=", value });
        }else{
            whereIsNull(parameter);
        }
    }

    public void whereGreaterThan(String parameter, Object value) {
        greaterThanParameters.put(parameter, new Object[] { ">", value });
    }

    public void whereLessThan(String parameter, Object value) {
        lessThanParameters.put(parameter, new Object[] { "<", value });
    }

    public void whereGreaterThanOrEqual(String parameter, Object value) {
        greaterThanOrEqualParameters.put(parameter, new Object[] { ">=", value });
    }

    public void whereLessThanOrEqual(String parameter, Object value) {
        lessThanOrEqualParameters.put(parameter, new Object[] { "<=", value });
    }

    public void whereDifferentThan(String parameter, Object value) {
        if(value!=null){
            differentThanParameters.put(parameter, new Object[] { "<>", value });
        }else{
            whereIsNotNull(parameter);
        }
    }

    public void whereIsNull(String parameter) {
        isParameters.put(parameter, "null");
    }

    public void whereIsNotNull(String parameter) {
        isParameters.put(parameter, "not null");
    }

    public void whereLike(String parameter, String value) {
        likeParameters.put(parameter, value);
    }

    public void whereIn(String parameter, Object[] values) {
        inParameters.put(parameter, values);
    }
    
    public void whereNotIn(String parameter, Object[] values) {
        notInParameters.put(parameter, values);
    }

    public void whereBetween(String parameter, Object start, Object end) {
        betweenParameters.put(parameter, new Object[] { start, end });
    }
    
    public void whereQuery(String[] params, String query) {
        queryParameters.put(query, params);
    }
    
    public void addValueMapParameter(String parameter, Object value) {
        valueMapParameters.put(parameter, value);
    }

    // *** Update method

    public void updateValue(String parameter, Object value) {
        updateValueParameters.put(parameter, value);
    }

    // *** Table methods

    public void orderBy(String parameter, String orderDir) {
        if (orderDir.equalsIgnoreCase("DESC")) {
            orderDir = orderDir.toUpperCase();
        } else {
            orderDir = "ASC";
        }
        orderByParameters.add(new String[] { parameter, orderDir });
    }

    public void setMaxResults(Long maxResults) {
        this.maxResults = maxResults;
        if (this.page != null) {
            setFirstResult();
            setLastResult();
        }
    }

    public void setPage(Long page) {
        this.page = page;
        if (this.maxResults != null) {
            setFirstResult();
            setLastResult();
        }
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
        setLastResult();
        setNumPaginas();
    }
    
    public void setFirstResult(Long firstResult) throws Exception {
        this.firstResult = firstResult;
        if(maxResults==null){
            throw new Exception("Error, no se ha establecido la variable maxResults!!!");
        }else{
            setPage();
            setLastResult();
        }
    }

    private void setFirstResult() {
        firstResult = (page * maxResults) - maxResults;
    }

    private void setLastResult() {
        if (page != null && maxResults != null) {
            lastResult = page * maxResults;
            if (totalResults != null && lastResult > totalResults) {
                lastResult = totalResults;
            }
        }
    }

    private void setNumPaginas() {
        if (this.maxResults != null) {
            totalPages = totalResults / this.maxResults;
            if ((totalPages * this.maxResults) < totalResults) {
                totalPages += 1;
            }
        }
    }
    
    private void setPage(){
        page= (firstResult+maxResults)/maxResults;
    }

    // *** Get methods

    public Map<String, Object[]> getEqualParameters() {
        return equalParameters;
    }

    public Map<String, Object[]> getGreaterThanParameters() {
        return greaterThanParameters;
    }

    public Map<String, Object[]> getLessThanParameters() {
        return lessThanParameters;
    }

    public Map<String, Object[]> getGreaterThanOrEqualParameters() {
        return greaterThanOrEqualParameters;
    }

    public Map<String, Object[]> getLessThanOrEqualParameters() {
        return lessThanOrEqualParameters;
    }

    public Map<String, Object[]> getDifferentThanParameters() {
        return differentThanParameters;
    }

    public Map<String, String> getIsParameters() {
        return isParameters;
    }

    public Map<String, String> getLikeParameters() {
        return likeParameters;
    }

    public Map<String, Object[]> getInParameters() {
        return inParameters;
    }
    
    public Map<String, Object[]> getNotInParameters() {
        return notInParameters;
    }

    public Map<String, Object[]> getBetweenParameters() {
        return betweenParameters;
    }
    
    public Map<String, String[]> getQueryParameters() {
        return queryParameters;
    }
    
    public Map<String, Object> getValueMapParameters() {
        return valueMapParameters;
    }

    public Map<String, Object> getUpdateValueParameters() {
        return updateValueParameters;
    }

    public List<String[]> getOrderByParameters() {
        return orderByParameters;
    }

    public Long getPage() {
        return page;
    }

    public Long getMaxResults() {
        return maxResults;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public Long getFirstResult() {
        return firstResult;
    }

    public Long getLastResult() {
        return lastResult;
    }

    public Long getTotalPages() {
        return totalPages;
    }

}
