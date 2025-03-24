package com.eventstreams.connectors.source;

import com.eventstreams.connectors.source.data.Student;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Responsible for transforming Student objects into Kafka Connect SourceRecords.
 */
public class MyRecordFactory {

    private static final Logger log = LoggerFactory.getLogger(MyRecordFactory.class);

    private final String topic;
    private static final String SOURCE_OFFSET = "student_id";

    public MyRecordFactory(AbstractConfig config) {
        this.topic = config.getString(MyCustomConnectorConfig.TOPIC_NAME_STRING);
    }

    // Schema for nested study_path object
    private static final Schema STUDY_PATH_SCHEMA = SchemaBuilder.struct()
            .name("study_path")
            .field("diploma", Schema.STRING_SCHEMA)
            .field("graduation_year", Schema.INT32_SCHEMA)
            .build();

    // Main schema for Student record
    private static final Schema STUDENT_SCHEMA = SchemaBuilder.struct()
            .name("student")
            .field("id", Schema.INT32_SCHEMA)
            .field("name", Schema.STRING_SCHEMA)
            .field("age", Schema.INT32_SCHEMA)
            .field("email", Schema.STRING_SCHEMA)
            .field("major", Schema.STRING_SCHEMA)
            .field("gpa", Schema.FLOAT64_SCHEMA)
            .field("study_path", STUDY_PATH_SCHEMA)
            .field("time", Schema.STRING_SCHEMA)
            .build();

    public SourceRecord createSourceRecord(Student student) {
        return new SourceRecord(
                createSourcePartition(student),
                createSourceOffset(student),
                topic,
                STUDENT_SCHEMA,
                createStruct(student)
        );
    }

    private Map<String, Object> createSourcePartition(Student student) {
        return Collections.singletonMap("major", student.getMajor());
    }
    

    private Map<String, Object> createSourceOffset(Student student) {
        return Collections.singletonMap(SOURCE_OFFSET, student.getId());
    }

    private Struct createStruct(Student student) {
        Struct studyPath = new Struct(STUDY_PATH_SCHEMA)
                .put("diploma", student.getStudyPath().getDiploma())
                .put("graduation_year", student.getStudyPath().getGraduationYear());

        return new Struct(STUDENT_SCHEMA)
                .put("id", student.getId())
                .put("name", student.getName())
                .put("age", student.getAge())
                .put("email", student.getEmail())
                .put("major", student.getMajor())
                .put("gpa", student.getGpa())
                .put("study_path", studyPath)
                .put("time", student.getTime());
    }

    public Long getPersistedOffset(OffsetStorageReader offsetReader, Student student) {
        if (offsetReader == null) {
            log.debug("No offset reader available");
            return 0L;
        }
    
        // Now based on "major"
        Map<String, Object> partition = createSourcePartition(student);
        Map<String, Object> offsetMap = offsetReader.offset(partition);
    
        if (offsetMap == null) {
            log.debug("No persisted offset for major " + student.getMajor());
            return 0L;
        }
    
        Object offsetValue = offsetMap.get(SOURCE_OFFSET);
        return (offsetValue instanceof Long) ? (Long) offsetValue : 0L;
    }
    
}
