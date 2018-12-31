package org.vincent.devops.system.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty9.InstrumentedConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:SystemConfig.properties")
public class JettyConfig {

    @Bean
    public JettyServletWebServerFactory jettyServletWebServerFactory(
            @Value("${Jetty.Thread.max}") Integer jettyThreadMax,
            @Value("${Jetty.Thread.min}") Integer jettyThreadMin,
            @Value("${server.contextPath}") String contextPath,
            @Value("${server.port}") Integer port) {
        final JettyServletWebServerFactory factory = new JettyServletWebServerFactory(port);

        factory.addServerCustomizers(
                server -> {
                    final QueuedThreadPool threadPool = (QueuedThreadPool)server.getThreadPool();
                    threadPool.setMaxThreads(jettyThreadMax);
                    threadPool.setMinThreads(jettyThreadMin);

                    MetricRegistry metricRegistry = new MetricRegistry();
                    final Connector connector = new ServerConnector(server, new InstrumentedConnectionFactory(new HttpConnectionFactory(), metricRegistry.timer("http.connection")));
                    server.addConnector(connector);
                }
        );

        factory.setContextPath(contextPath);
        factory.setRegisterDefaultServlet(false);
        return factory;
    }
}
