package uk.ac.ed.inf.acpsubmission1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DynamoService {

    private final DynamoDbClient dynamo;
    private final ObjectMapper mapper = new ObjectMapper();

    // Configurable attribute names (no hardcoding)
    @Value("${acp.dynamo.key:id}")
    private String keyAttr;

    @Value("${acp.dynamo.data:data}")
    private String dataAttr;

    public DynamoService(DynamoDbClient dynamo) {
        this.dynamo = dynamo;
    }

    public List<Object> readAllObjectsVerbatim(String table) throws Exception {

        ScanRequest request = ScanRequest.builder()
                .tableName(table)
                .build();
        ScanResponse response = dynamo.scan(request);
        List<Object> result = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            AttributeValue dataValue = item.get(dataAttr);
            if (dataValue != null && dataValue.s() != null) {
                String json = dataValue.s();
                Object parsed = mapper.readValue(json, Object.class);
                result.add(parsed);
            }
        }

        return result;
    }

    public Object readSingleObjectVerbatim(String table, String key) throws Exception {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(table)
                .key(Map.of(
                        keyAttr, AttributeValue.builder().s(key).build()
                ))
                .build();
        GetItemResponse response = dynamo.getItem(request);
        Map<String, AttributeValue> item = response.item();
        if (item == null || item.isEmpty()) {
            throw new RuntimeException("Item not found");
        }
        AttributeValue dataValue = item.get(dataAttr);

        if (dataValue == null || dataValue.s() == null) {
            throw new RuntimeException("No data attribute");
        }

        String json = dataValue.s();
        return mapper.readValue(json, Object.class);
    }

    public void writeObject(String table, String key, String content) {
        PutItemRequest request = PutItemRequest.builder().tableName(table).item(Map.of(keyAttr, AttributeValue.builder().s(key).build(), dataAttr, AttributeValue.builder().s(content).build())).build();
        dynamo.putItem(request);
    }
}
