package de.viadee.anchorj.server.configuration.yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import de.viadee.anchorj.server.configuration.AppConfiguration;

/**
 *
 */
@Component
@ConfigurationProperties("app")
public class YamlAppConfiguration implements AppConfiguration {

    private Map<String, String> serverNameMapping = new HashMap<>();

    public Map<String, String> getServerNameMapping() {
        return this.serverNameMapping;
    }

    public void setServer(Map<String, String> serverNameMapping) {
        this.serverNameMapping = serverNameMapping;
    }

    public Set<String> getConnectionNames() {
        return this.serverNameMapping.keySet();
    }

    public String getConnectionName(String connectionName) {
        return this.serverNameMapping.get(connectionName);
    }

}
