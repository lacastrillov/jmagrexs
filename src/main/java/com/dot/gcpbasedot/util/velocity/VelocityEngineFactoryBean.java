/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util.velocity;

import java.io.IOException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;

@Deprecated
public class VelocityEngineFactoryBean
        extends VelocityEngineFactory
        implements FactoryBean<VelocityEngine>, InitializingBean, ResourceLoaderAware {

    private VelocityEngine velocityEngine;

    @Override
    public void afterPropertiesSet()
            throws IOException, VelocityException {
        this.velocityEngine = createVelocityEngine();
    }

    @Override
    public VelocityEngine getObject() {
        return this.velocityEngine;
    }

    @Override
    public Class<? extends VelocityEngine> getObjectType() {
        return VelocityEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
