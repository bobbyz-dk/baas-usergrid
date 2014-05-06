package io.cloudsoft.usergrid.app;

import io.cloudsoft.usergrid.AbstractUsergridApplication;
import io.cloudsoft.usergrid.UsergridTomcatServer;

import java.util.Collection;

import brooklyn.catalog.Catalog;
import brooklyn.catalog.CatalogConfig;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.database.DatastoreMixins;
import brooklyn.entity.nosql.cassandra.CassandraNode;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.event.basic.DependentConfiguration;
import brooklyn.location.Location;

import com.google.common.base.Preconditions;

@Catalog(
    name = "Usergrid basic (2-host) app",
    description = "Deploys usergrid 2-host configuration",
    iconUrl = "classpath://usergrid.png"
)
public class UsergridBasicApp extends AbstractUsergridApplication {
    
    @CatalogConfig(label="Properties template URL")
    public static final ConfigKey<String> PROPERTIES_TEMPLATE_URL = ConfigKeys.newStringConfigKey("usergrid.app.basic.properties.url", 
        "Properties file freemarker template URL", 
        "classpath://usergrid-basic.properties");
    
    @Override
    public void init() {
        setDisplayName("Usergrid basic (2-host) deployment");
        
        CassandraNode cassandraNode = addChild(EntitySpec.create(CassandraNode.class));
        addChild(EntitySpec.create(UsergridTomcatServer.class)
            .configure(UsergridTomcatServer.CASSANDRA_URL, 
                DependentConfiguration.attributeWhenReady(cassandraNode, DatastoreMixins.DATASTORE_URL))
            .configure(SoftwareProcess.SUGGESTED_VERSION, "7.0.53")
            .configure(UsergridTomcatServer.USERGRID_PROPERTIES_TEMPLATE_URL, getConfig(PROPERTIES_TEMPLATE_URL))
            .configure(UsergridTomcatServer.ROOT_WAR, "classpath://ROOT.war"));
    }
    
    @Override
    protected void doStart(Collection<? extends Location> locations) {
        Preconditions.checkArgument(locations.size() == 1, "expected 1 location but got %s", locations.size());
        super.doStart(locations);
    }
}