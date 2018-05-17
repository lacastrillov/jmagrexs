/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lacv.jmagrexs.generator;

/**
 *
 * @author grupot
 */
public class CodeGenerator {
    
    private final DtoGenerator dtoGenerator;
    
    private final MapperGenerator mapperGenerator;
    
    private final DaoGenerator daoGenerator;
    
    private final DaoImplGenerator daoImplGenerator;
    
    private final ServiceGenerator serviceGenerator;
    
    private final ServiceImplGenerator serviceImplGenerator;
    
    private final RestControllerGenerator restControllerGenerator;
    
    private final ViewControllerGenerator viewControllerGenerator;
    
    
    public CodeGenerator(Class originClass, String groupId){
        this.dtoGenerator= new DtoGenerator(originClass, groupId);
        this.mapperGenerator= new MapperGenerator(originClass, groupId);
        this.daoGenerator= new DaoGenerator(originClass, groupId);
        this.daoImplGenerator= new DaoImplGenerator(originClass, groupId);
        this.serviceGenerator= new ServiceGenerator(originClass, groupId);
        this.serviceImplGenerator= new ServiceImplGenerator(originClass, groupId);
        this.restControllerGenerator= new RestControllerGenerator(originClass, groupId);
        this.viewControllerGenerator= new ViewControllerGenerator(originClass, groupId);
    }
    
    /**
     * 
     * @param entitiesPackage 
     */
    public void generate(String entitiesPackage){ 
        System.out.println("--------- Generate Dtos ---------");
        this.dtoGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Mappers ---------");
        this.mapperGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Daos ---------");
        this.daoGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Daos Impl ---------");
        this.daoImplGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Services ---------");
        this.serviceGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Services Impl ---------");
        this.serviceImplGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate Rest Controllers ---------");
        this.restControllerGenerator.generate(entitiesPackage);
        System.out.println("--------- Generate View Controllers ---------");
        this.viewControllerGenerator.generate(entitiesPackage);
    }
    
    /**
     * 
     * @param entityClass 
     */
    public void generate(Class entityClass){
        this.dtoGenerator.generate(entityClass);
        this.mapperGenerator.generate(entityClass);
        this.daoGenerator.generate(entityClass);
        this.daoImplGenerator.generate(entityClass);
        this.serviceGenerator.generate(entityClass);
        this.serviceImplGenerator.generate(entityClass);
        this.restControllerGenerator.generate(entityClass);
        this.viewControllerGenerator.generate(entityClass);
    }

    /**
     * @return the dtoGenerator
     */
    public DtoGenerator getDtoGenerator() {
        return dtoGenerator;
    }

    /**
     * @return the mapperGenerator
     */
    public MapperGenerator getMapperGenerator() {
        return mapperGenerator;
    }

    /**
     * @return the daoGenerator
     */
    public DaoGenerator getDaoGenerator() {
        return daoGenerator;
    }

    /**
     * @return the daoImplGenerator
     */
    public DaoImplGenerator getDaoImplGenerator() {
        return daoImplGenerator;
    }

    /**
     * @return the serviceGenerator
     */
    public ServiceGenerator getServiceGenerator() {
        return serviceGenerator;
    }

    /**
     * @return the serviceImplGenerator
     */
    public ServiceImplGenerator getServiceImplGenerator() {
        return serviceImplGenerator;
    }
    
    /**
     * @return the restControllerGenerator
     */
    public RestControllerGenerator getRestControllerGenerator() {
        return restControllerGenerator;
    }
    
    /**
     * @return the viewControllerGenerator
     */
    public ViewControllerGenerator getViewControllerGenerator() {
        return viewControllerGenerator;
    }

    /**
     * @param indexDataSource the indexDataSource to set
     */
    public void setIndexDataSource(String indexDataSource) {
        this.serviceImplGenerator.setIndexDataSource(indexDataSource);
        this.daoImplGenerator.setIndexDataSource(indexDataSource);
    }

    
}
