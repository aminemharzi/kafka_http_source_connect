# Kafka HTTP Source Connector

A custom **Kafka Connect Source Connector** for ingesting JSON data from an HTTP API into a Kafka topic.

This connector is designed for educational and demo purposes, specifically to pull **student data** from a RESTful API and publish it to Kafka in a structured format.

---

## 📦 Features

- Fetches data from a REST endpoint returning a JSON array of student records.
- Uses `major` as the partition key and `time` field for offset tracking.
- Converts JSON responses into structured Kafka Connect `SourceRecord`s using schemas.
- Built using Kafka Connect's Source Connector API.

---

## 📁 Project Structure

```
src/
├── main/
│   └── java/
│       └── com/eventstreams/connectors/source/
│           ├── MyCustomHttpSourceConnector.java
│           ├── MyCustomConnectorConfig.java
│           ├── MyDataFetcher.java
│           ├── MyRecordFactory.java
│           ├── MySourceTask.java
│           └── data/
│               ├── Student.java
│               └── StudyPath.java
```

---

## ⚙️ Prerequisites

- Java 17+
- Apache Kafka (tested with Kafka 3.x)
- Gradle
- Kafka Connect Standalone or Distributed mode

---

## 💪 Build

Use Gradle to build a fat JAR:

```bash
./gradlew clean shadowJar
```

The output JAR will be located in:

```
build/libs/http-source-connector-all-1.0.0.jar
```

---

## 🔧 Configuration

### `connect.properties`

```properties
bootstrap.servers=localhost:9092
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
value.converter.schemas.enable=true
offset.storage.file.filename=/tmp/connect.offsets
plugin.path=./connector-jars
```

### `source-connector.properties`

```properties
name=http-source-connector
connector.class=com.eventstreams.connectors.source.MyCustomHttpSourceConnector
tasks.max=1

students.url=http://localhost:8080/api/students
students.poll.interval.ms=10000
topic=student-topic
```

---

## 🚀 Run Connector for windows

```bash
%KAFKA_HOME%/bin/windows/connect-standalone.bat connect.properties source-connector.properties
```

Make sure your JAR is placed in the `plugin.path` defined in `connect.properties`.

---

## 📌 Notes

- The HTTP API must return a JSON array of students like:

```json
[
  {
    "id": 1,
    "name": "Alice",
    "age": 21,
    "email": "alice@example.com",
    "major": "Computer Science",
    "gpa": 3.8,
    "study_path": {
      "diploma": "BSc CS",
      "graduation_year": 2024
    },
    "time": "1711213200"
  }
]
```

- Offset tracking is done using the `time` field per `major`.

---


