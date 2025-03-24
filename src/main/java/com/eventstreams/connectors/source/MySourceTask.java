package com.eventstreams.connectors.source;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.stream.Collectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eventstreams.connectors.source.data.Student;

/**
 * Kafka Connect Source Task for fetching and transforming Student data.
 */
public class MySourceTask extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(MySourceTask.class);

    private Timer fetchTimer;
    private MyDataFetcher dataFetcher;
    private MyRecordFactory recordFactory;

    @Override
    public void start(Map<String, String> properties) {
        log.info("Starting Student Source Task with properties: {}", properties);
    
        AbstractConfig config = new AbstractConfig(MyCustomConnectorConfig.CONFIG_DEF, properties);
        recordFactory = new MyRecordFactory(config);
    
        OffsetStorageReader offsetReader = getOffsetStorageReader();
    
        //  Dummy student just to call getPersistedOffset per major
        Student dummy = new Student();
        dummy.setMajor("Computer Science");  // Or the default expected major for offset lookup
    
        Long offset = recordFactory.getPersistedOffset(offsetReader, dummy);
    
        dataFetcher = new MyDataFetcher(config, offset);
    
        Long pollInterval = config.getLong(MyCustomConnectorConfig.MY_POLL_INTERVAL_MS);
        fetchTimer = new Timer();
        fetchTimer.scheduleAtFixedRate(dataFetcher, 0, pollInterval);
    }
    

    @Override
    public void stop() {
        log.info("Stopping MySourceTask");
        if (fetchTimer != null) {
            fetchTimer.cancel();
        }
        fetchTimer = null;
        dataFetcher = null;
        recordFactory = null;
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return dataFetcher.getResponses()
            .stream()
            .map(recordFactory::createSourceRecord)
            .collect(Collectors.toList());
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    private OffsetStorageReader getOffsetStorageReader() {
        if (context == null || context.offsetStorageReader() == null) {
            log.debug("No offset storage reader found. Assuming first run.");
            return null;
        }
        return context.offsetStorageReader();
    }
}