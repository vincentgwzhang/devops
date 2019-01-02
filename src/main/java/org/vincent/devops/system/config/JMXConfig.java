package org.vincent.devops.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import java.net.URL;
import java.util.Optional;
import java.util.Properties;

@Configuration
public class JMXConfig {

    @Value("${jmx.rmi.host:localhost}")
    private String rmiHost;

    @Value("${jmx.rmi.port:1099}")
    private Integer rmiPort;

    @Value("${jmx.rmi.password:jmxremote.password}")
    private String passwordFileName;

    @Value("${jmx.rmi.access:jmxremote.access}")
    private String accessFileName;

    private static final String PASSWORD_FILE_PROP = "jmx.remote.x.password.file";
    private static final String ACCESS_FILE_PROP = "jmx.remote.x.access.file";

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public RmiRegistryFactoryBean rmiRegistry() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(rmiPort);
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistry")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean = new ConnectorServerFactoryBean();
        connectorServerFactoryBean.setObjectName("connector:name=DevOpsConnector");
        connectorServerFactoryBean.setServiceUrl(String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", rmiHost, rmiPort, rmiHost, rmiPort));

        try {
            connectorServerFactoryBean.setEnvironment(initialJMXServerProperties());
        } catch (RuntimeException e) {
            logger.warn(e.getMessage(), e);
        }

        return connectorServerFactoryBean;
    }

    private Properties initialJMXServerProperties() throws RuntimeException {
        URL passwordURL = JMXConfig.class.getClassLoader().getResource(passwordFileName);
        URL accessURL   = JMXConfig.class.getClassLoader().getResource(accessFileName);

        String passFile     = Optional.ofNullable(passwordURL).map(URL::getPath).orElseThrow(() -> new RuntimeException("JMX password file not exist"));
        String accessFile   = Optional.ofNullable(accessURL).map(URL::getPath).orElseThrow(() -> new RuntimeException("JMX access file not exist"));

        Properties properties = new Properties();
        properties.setProperty(PASSWORD_FILE_PROP, passFile);
        properties.setProperty(ACCESS_FILE_PROP, accessFile);
        return properties;
    }

}
