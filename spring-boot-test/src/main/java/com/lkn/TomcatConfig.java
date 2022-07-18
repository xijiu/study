package com.lkn;

import org.apache.catalina.connector.Connector;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 增加端口监听
 * @Class: TomcatConfig
 * @Author: Yiang37
 * @Date: 2021/9/3 15:56
 * @Version: 1.0
 */
@Configuration
public class TomcatConfig {

    @Value("${server.additionalPorts}")
    private String additionalPorts;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector[] additionalConnectors = this.additionalConnector();
        if (additionalConnectors != null && additionalConnectors.length > 0) {
            tomcat.addAdditionalTomcatConnectors(additionalConnectors);
        }
        return tomcat;
    }

    private Connector[] additionalConnector() {
        if (StringUtils.isBlank(this.additionalPorts)) {
            return null;
        }
        // 端口按,分割
        String[] ports = this.additionalPorts.split(",");
        List<Connector> result = new ArrayList<>();
        for (String port : ports) {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(Integer.parseInt(port));
            result.add(connector);
        }
        return result.toArray(new Connector[] {});
    }

}