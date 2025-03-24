// ========================
// File: MyCustomHttpSourceConnector.java
// ========================
package com.eventstreams.connectors.source;

import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.common.config.ConfigDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class MyCustomHttpSourceConnector extends SourceConnector {

    private static final Logger log = LoggerFactory.getLogger(MyCustomHttpSourceConnector.class);
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting connector with properties: {}", props);
        this.configProps = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>();
        configs.add(configProps);
        return configs;
    }

    @Override
    public void stop() {
        log.info("Stopping connector");
    }

    @Override
    public ConfigDef config() {
        return MyCustomConnectorConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    public class VersionUtil {
        public static String getVersion() {
            return "1.0.0";
        }
    }
    
}
