package com.eventstreams.connectors.source;

import com.eventstreams.connectors.source.data.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.common.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Periodically fetches student data from a REST API.
 */
public class MyDataFetcher extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(MyDataFetcher.class);

    private final String url;
    private final Gson gson = new Gson();
    private final SortedSet<Student> fetchedRecords;
    private Long offset;

    public MyDataFetcher(AbstractConfig config, Long initialOffset) {
        this.url = config.getString(MyCustomConnectorConfig.MY_URL);
        this.offset = initialOffset;
        this.fetchedRecords = new TreeSet<>((a, b) -> Integer.compare(a.getId(), b.getId()));
    }

    @Override
    public void run() {
        try {
            log.info("Fetching student data from {}", url);
            String json = fetchData();
            List<Student> students = parseData(json);

            synchronized (this) {
                for (Student student : students) {
                    if (student.getId() > offset) {
                        fetchedRecords.add(student);
                        offset = (long) student.getId(); // update offset to highest seen ID
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error while fetching or parsing student data", e);
        }
    }

    private String fetchData() throws Exception {
        URL endpoint = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    private List<Student> parseData(String json) {
        return gson.fromJson(json, new TypeToken<List<Student>>() {}.getType());
    }

    public synchronized List<Student> getResponses() {
        List<Student> list = fetchedRecords.stream().collect(Collectors.toList());
        fetchedRecords.clear();
        return list;
    }
}
