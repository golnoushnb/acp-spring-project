package uk.ac.ed.inf.acpsubmission1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3;
    private final ObjectMapper mapper = new ObjectMapper();

    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    public List<Object> readAllObjectsVerbatim(String bucket) throws Exception {
        ListObjectsV2Response list = s3.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucket).build()
        );

        List<Object> out = new ArrayList<>();

        for (S3Object obj : list.contents()) {
            String raw = readObjectAsString(bucket, obj.key());
            Object parsed = mapper.readValue(raw, Object.class);
            out.add(parsed);
        }

        return out;
    }
    public Object readSingleObjectVerbatim(String bucket, String key) throws Exception {
        String raw = readObjectAsString(bucket, key);
        Object parsed = mapper.readValue(raw, Object.class);
        return parsed;

    }

    public void writeObject(String bucket, String key, String content){
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucket).key(key).build();
        RequestBody body = RequestBody.fromString(content);
        s3.putObject(request, body);
    }


    private String readObjectAsString(String bucket, String key) {
        ResponseBytes<GetObjectResponse> bytes = s3.getObject(
                GetObjectRequest.builder().bucket(bucket).key(key).build(),
                ResponseTransformer.toBytes()
        );

        return bytes.asString(StandardCharsets.UTF_8);
    }
}
