package com.liyang.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private ServerConfig server;
    private DatasourceConfig datasource;

    // Getter 和 Setter
    public ServerConfig getServer() { return server; }
    public void setServer(ServerConfig server) { this.server = server; }
    public DatasourceConfig getDatasource() { return datasource; }
    public void setDatasource(DatasourceConfig datasource) { this.datasource = this.datasource; }

    public static class ServerConfig {
        private int port;
        // Getter 和 Setter
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }

    public static class DatasourceConfig {
        private String url;
        private String username;
        private String password;
        private String driver;

        // Getter 和 Setter
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getDriver() { return driver; }
        public void setDriver(String driver) { this.driver = driver; }
    }
}