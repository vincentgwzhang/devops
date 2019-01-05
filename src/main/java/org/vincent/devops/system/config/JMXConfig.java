package org.vincent.devops.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    private Properties initialJMXServerProperties() throws Exception {
        Properties properties = new Properties();

        final File passwordFile = File.createTempFile(passwordFileName, "temp");
        final File accessFile = File.createTempFile(accessFileName, "temp");

        InputStream pfInputStream = JMXConfig.class.getClassLoader().getResourceAsStream(passwordFileName);
        InputStream afInputStream = JMXConfig.class.getClassLoader().getResourceAsStream(accessFileName);

        try (
                FileOutputStream foPasswordFile = new FileOutputStream(passwordFile);
                FileOutputStream foAccessFile = new FileOutputStream(accessFile)
        ) {
            Optional.ofNullable(pfInputStream).orElseThrow(() -> new RuntimeException("JMX password file not found"));
            Optional.ofNullable(afInputStream).orElseThrow(() -> new RuntimeException("JMX access file not found"));
            pfInputStream.transferTo(foPasswordFile);
            afInputStream.transferTo(foAccessFile);
            properties.setProperty(PASSWORD_FILE_PROP, passwordFile.getAbsolutePath());
            properties.setProperty(ACCESS_FILE_PROP, accessFile.getAbsolutePath());
            passwordFile.deleteOnExit();
            accessFile.deleteOnExit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return properties;
    }

}
