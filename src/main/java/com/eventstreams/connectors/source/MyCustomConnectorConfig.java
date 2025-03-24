// ========================
// File: MyCustomConnectorConfig.java
// ========================
package com.eventstreams.connectors.source;

import org.apache.kafka.common.config.ConfigDef;

public class MyCustomConnectorConfig {

    public static final String TOPIC_NAME_STRING = "topic";
    public static final String MY_POLL_INTERVAL_MS = "students.poll.interval.ms";
    public static final String MY_URL = "students.url";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
        .define(TOPIC_NAME_STRING, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Topic to write to")
        .define(MY_POLL_INTERVAL_MS, ConfigDef.Type.LONG, ConfigDef.Importance.HIGH, "Poll interval in milliseconds")
        .define(MY_URL, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "URL to fetch data from");
}