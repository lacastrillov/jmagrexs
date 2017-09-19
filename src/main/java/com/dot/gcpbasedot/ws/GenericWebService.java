/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.ws;

import com.dot.gcpbasedot.domain.BaseEntity;
import com.dot.gcpbasedot.mapper.BasicEntityMapper;
import com.dot.gcpbasedot.reflection.EntityReflection;
import com.dot.gcpbasedot.service.EntityService;
import com.dot.gcpbasedot.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author lacastrillov
 */
public abstract class GenericWebService {

    protected static final Logger LOGGER = Logger.getLogger(GenericWebService.class);

    private final Map<String, String> services = new HashMap<>();

    private final Map<String, String> mappers = new HashMap<>();

    protected void addControlMapping(String entityRef, String entityService, String entityMapper) {
        services.put(entityRef, entityService);
        mappers.put(entityRef, entityMapper);
    }

    /**
     * This is a sample web service operation
     *
     * @param entityRef
     * @param filter
     * @param page
     * @param limit
     * @param dir
     * @param sort
     * @return
     */
    @WebMethod(operationName = "find")
    public String find(@WebParam(name = "entityRef") String entityRef, @WebParam(name = "filter") String filter,
            @WebParam(name = "page") Long page, @WebParam(name = "limit") Long limit, @WebParam(name = "sort") String sort,
            @WebParam(name = "dir") String dir) {

        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        EntityService entityService = (EntityService) ctx.getBean(services.get(entityRef));
        BasicEntityMapper entityMapper = (BasicEntityMapper) ctx.getBean(mappers.get(entityRef));

        try {
            List<? extends BaseEntity> listEntities = entityService.findByJSONFilters(filter, page, limit, sort, dir);
            List<? extends BaseEntity> listDtos = entityMapper.listEntitiesToListDtos(listEntities);
            Long totalCount = entityService.countByJSONFilters(filter);

            return Util.getResultListCallback(listDtos, totalCount, "Busqueda de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("find " + entityRef, e);
            return Util.getResultListCallback(new ArrayList(), "Error buscando " + entityRef + "!!! " + e.getMessage(), false);
        }
    }

    /**
     * 
     * @param data
     * @param entityRef
     * @return 
     */
    @WebMethod(operationName = "create")
    public String create(@WebParam(name = "data") String data, @WebParam(name = "entityRef") String entityRef) {

        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        EntityService entityService = (EntityService) ctx.getBean(services.get(entityRef));
        BasicEntityMapper entityMapper = (BasicEntityMapper) ctx.getBean(mappers.get(entityRef));

        HashMap<String, Object> mapData = Util.jsonToHashMap(data);
        BaseEntity dto = null;

        try {
            BaseEntity entity = (BaseEntity) EntityReflection.getObjectForClass(entityService.getEntityClass());
            EntityReflection.updateEntity(mapData, entity);

            entityService.create(entity);
            dto = entityMapper.entityToDto(entity);
            return Util.getOperationCallback(dto, "Creaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("create " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en creaci&oacute;n de " + entityRef + "!!!", false);
        }
    }

    /**
     * 
     * @param data
     * @param entityRef
     * @return 
     */
    @WebMethod(operationName = "update")
    public String update(@WebParam(name = "data") String data, @WebParam(name = "entityRef") String entityRef) {

        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        EntityService entityService = (EntityService) ctx.getBean(services.get(entityRef));
        BasicEntityMapper entityMapper = (BasicEntityMapper) ctx.getBean(mappers.get(entityRef));

        HashMap<String, Object> mapData = Util.jsonToHashMap(data);
        BaseEntity dto = null;

        try {
            Class entityClass= entityService.getClass();
            Object id= EntityReflection.getParsedFieldValue(entityClass, "id", mapData.get("id").toString());
            BaseEntity entity = (BaseEntity) entityService.loadById(id);
            EntityReflection.updateEntity(mapData, entity);

            entityService.update(entity);
            dto = entityMapper.entityToDto(entity);
            return Util.getOperationCallback(dto, "Actualizaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("update " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en actualizaci&oacute;n de " + entityRef + "!!!", false);
        }
    }

    /**
     * 
     * @param data
     * @param entityRef
     * @return 
     */
    @WebMethod(operationName = "load")
    public String load(@WebParam(name = "data") String data, @WebParam(name = "entityRef") String entityRef) {

        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        EntityService entityService = (EntityService) ctx.getBean(services.get(entityRef));
        BasicEntityMapper entityMapper = (BasicEntityMapper) ctx.getBean(mappers.get(entityRef));

        HashMap<String, Object> mapData = Util.jsonToHashMap(data);
        BaseEntity dto = null;

        try {
            Class entityClass= entityService.getClass();
            Object id= EntityReflection.getParsedFieldValue(entityClass, "id", mapData.get("id").toString());
            BaseEntity entitie = (BaseEntity) entityService.loadById(id);
            dto = entityMapper.entityToDto(entitie);
            return Util.getOperationCallback(dto, "Carga de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("load " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en carga de " + entityRef + "!!!", true);
        }
    }

    /**
     * 
     * @param data
     * @param entityRef
     * @return 
     */
    @WebMethod(operationName = "delete")
    public String delete(@WebParam(name = "data") String data, @WebParam(name = "entityRef") String entityRef) {

        ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        EntityService entityService = (EntityService) ctx.getBean(services.get(entityRef));
        BasicEntityMapper entityMapper = (BasicEntityMapper) ctx.getBean(mappers.get(entityRef));

        HashMap<String, Object> mapData = Util.jsonToHashMap(data);
        BaseEntity dto = null;

        try {
            Class entityClass= entityService.getClass();
            Object id= EntityReflection.getParsedFieldValue(entityClass, "id", mapData.get("id").toString());
            BaseEntity entity = (BaseEntity) entityService.loadById(id);
            dto = entityMapper.entityToDto(entity);
            entityService.remove(entity);
            return Util.getOperationCallback(dto, "Eliminaci&oacute;n de " + entityRef + " realizada...", true);
        } catch (Exception e) {
            LOGGER.error("delete " + entityRef, e);
            return Util.getOperationCallback(dto, "Error en eliminaci&oacute;n de " + entityRef + "!!!", true);
        }
    }

}
