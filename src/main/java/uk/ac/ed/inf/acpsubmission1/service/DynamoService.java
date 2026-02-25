package uk.ac.ed.inf.acpsubmission1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamoService {

    private final DynamoDbClient dynamo;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${acp.dynamo.data:data}")
    private String dataAttr;
    private final Map<String, String> pkCache = new ConcurrentHashMap<>();

    public DynamoService(DynamoDbClient dynamo) {
        this.dynamo = dynamo;
    }
    private String getPartitionKeyAttr(String table) {
        return pkCache.computeIfAbsent(table, t -> {
            DescribeTableResponse resp = dynamo.describeTable(
                    DescribeTableRequest.builder().tableName(t).build()
            );

            return resp.table().keySchema().stream()
                    .filter(e -> e.keyType() == KeyType.HASH)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No HASH key found for table: " + t))
                    .attributeName();
        });
    }

    public List<Object> readAllObjectsVerbatim(String table) throws Exception {
        ScanResponse response = dynamo.scan(
                ScanRequest.builder().tableName(table).build()
        );

        List<Object> result = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            AttributeValue dataValue = item.get(dataAttr);
            if (dataValue != null && dataValue.s() != null) {
                Object parsed = mapper.readValue(dataValue.s(), Object.class);
                result.add(parsed);
            }
        }
        return result;
    }

    public Object readSingleObjectVerbatim(String table, String key) throws Exception {
        String pkAttr = getPartitionKeyAttr(table);

        GetItemResponse response = dynamo.getItem(
                GetItemRequest.builder()
                        .tableName(table)
                        .key(Map.of(pkAttr, AttributeValue.builder().s(key).build()))
                        .build()
        );

        Map<String, AttributeValue> item = response.item();
        if (item == null || item.isEmpty()) {
            throw new RuntimeException("Item not found");
        }

        AttributeValue dataValue = item.get(dataAttr);
        if (dataValue == null || dataValue.s() == null) {
            throw new RuntimeException("No data attribute: " + dataAttr);
        }

        return mapper.readValue(dataValue.s(), Object.class);
    }


    public void writeObject(String table, String key, String content) {
        String pkAttr = getPartitionKeyAttr(table);

        PutItemRequest request = PutItemRequest.builder()
                .tableName(table)
                .item(Map.of(
                        pkAttr, AttributeValue.builder().s(key).build(),
                        dataAttr, AttributeValue.builder().s(content).build()
                ))
                .build();

        dynamo.putItem(request);
    }
}
