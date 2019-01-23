package de.viadee.anchorj.server.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
public class AppConfiguration {

    private Map<String, String> h2oServer = new HashMap<>();

    public Map<String, String> getH2oServer() {
        return h2oServer;
    }

    public void setH2oServer(Map<String, String> h2oServer) {
        this.h2oServer = h2oServer;
    }

    public Set<String> getH2oConnectionNames() {
        return this.h2oServer.keySet();
    }

    public String getH2oConnectionName(String connectionName) {
        return this.h2oServer.get(connectionName);
    }

}
