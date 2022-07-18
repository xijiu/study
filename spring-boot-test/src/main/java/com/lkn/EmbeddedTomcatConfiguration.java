//package com.lkn;
//
//import org.apache.catalina.connector.Connector;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class EmbeddedTomcatConfiguration {
//
//    @Value("${server.additionalPorts}")
//    private String additionalPorts;
//
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        Connector[] additionalConnectors = this.additionalConnector();
//        if (additionalConnectors != null && additionalConnectors.length > 0) {
//            tomcat.addAdditionalTomcatConnectors(additionalConnectors);
//        }
//        return tomcat;
//    }
//
//    private Connector[] additionalConnector() {
//        if (additionalPorts == null || additionalPorts.equalsIgnoreCase("")) {
//            return null;
//        }
//        String[] ports = this.additionalPorts.split(",");
//        List<Connector> result = new ArrayList<>();
//        for (String port : ports) {
//            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//            connector.setScheme("http");
//            connector.setPort(Integer.valueOf(port));
//            result.add(connector);
//        }
//        return result.toArray(new Connector[] {});
//    }
//}