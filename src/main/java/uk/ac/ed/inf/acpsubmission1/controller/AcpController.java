package uk.ac.ed.inf.acpsubmission1.controller;

import uk.ac.ed.inf.acpsubmission1.service.PostgresService;
import uk.ac.ed.inf.acpsubmission1.service.S3Service;
import uk.ac.ed.inf.acpsubmission1.service.DynamoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class AcpController {

    private final PostgresService postgresService;
    private final S3Service s3Service;
    private final DynamoService dynamoService;


    public AcpController(PostgresService postgresService, S3Service s3Service, DynamoService dynamoService) {
        this.postgresService = postgresService;
        this.s3Service = s3Service;
        this.dynamoService = dynamoService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/all/postgres/{table}")
    public ResponseEntity<?> getAllPostgres(@PathVariable String table) {

        try {
            List<Map<String, Object>> rows =
                    postgresService.readAll(table);
            return ResponseEntity.ok(rows);

        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/all/s3/{bucket}")
    public ResponseEntity<?> getAllS3(@PathVariable String bucket) {
        try {
            return ResponseEntity.ok(s3Service.readAllObjectsVerbatim(bucket));
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/single/s3/{bucket}/{key}")
    public ResponseEntity<?> getSingleS3(@PathVariable String bucket, @PathVariable String key){
       try{
           return ResponseEntity.ok(s3Service.readSingleObjectVerbatim(bucket, key));
       }
       catch (Exception e){
           return ResponseEntity.status(404).build();
       }
    }

    @GetMapping("/all/dynamo/{table}")
    public ResponseEntity<?> getAllDynamo(@PathVariable String table){
        try{
            return ResponseEntity.ok(dynamoService.readAllObjectsVerbatim(table));
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }
    }
    @GetMapping("/single/dynamo/{table}/{key}")
    public ResponseEntity<?> getSingleDynamo(@PathVariable String table, @PathVariable String key){
        try{
            return ResponseEntity.ok(dynamoService.readSingleObjectVerbatim(table, key));
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }
    }

}
