package uk.ac.ed.inf.acpsubmission1.controller;


import uk.ac.ed.inf.acpsubmission1.request.ProcessRequest;
import uk.ac.ed.inf.acpsubmission1.service.PostgresService;
import uk.ac.ed.inf.acpsubmission1.service.S3Service;
import uk.ac.ed.inf.acpsubmission1.service.DynamoService;
import uk.ac.ed.inf.acpsubmission1.service.ProcessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/acp")

public class AcpController {

    private final PostgresService postgresService;
    private final S3Service s3Service;
    private final DynamoService dynamoService;
    private final ProcessService processService;

    @Value("${acp.sid}")
    private String sid;


    public AcpController(PostgresService postgresService, S3Service s3Service, DynamoService dynamoService, ProcessService processService) {
        this.postgresService = postgresService;
        this.s3Service = s3Service;
        this.dynamoService = dynamoService;
        this.processService = processService;
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

    @PostMapping("/process/dump")
    public ResponseEntity<?> processDump(@RequestBody ProcessRequest request){
        try{
            return ResponseEntity.ok(
                    processService.processDrones(request.getUrlPath())
            );
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/process/s3")
    public ResponseEntity<?> processS3(@RequestBody ProcessRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> drones = processService.processDrones(request.getUrlPath());
            for (Map<String, Object> drone : drones) {
                String name = (String) drone.get("name");
                String json = mapper.writeValueAsString(drone);
                s3Service.writeObject(sid, name, json);
            }
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
    @GetMapping("/process/dynamo")
    public ResponseEntity<?> processDynamo(@RequestBody ProcessRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> drones = processService.processDrones(request.getUrlPath());
            for (Map<String, Object> drone : drones) {
                String name = (String) drone.get("name");
                String json = mapper.writeValueAsString(drone);
                dynamoService.writeObject(sid, name, json);
            }
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/process/postgres/{table}")
    public ResponseEntity<?> processPostgres (@PathVariable String table, @RequestBody ProcessRequest request){
        try{
            List<Map<String, Object>> drones = processService.processDrones(request.getUrlPath());
            postgresService.insertDrones(table, drones);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/copy-content/dynamo/{table}")
    public ResponseEntity<?> copyContentToDynamo(@PathVariable String table){
        ObjectMapper mapper = new ObjectMapper();
        try{
            List<Map<String, Object>> rows = postgresService.readAll(table);
            for (Map<String, Object> row : rows){
                String uuid = java.util.UUID.randomUUID().toString();
                String json = mapper.writeValueAsString(row);
                dynamoService.writeObject(sid, uuid, json);
            }
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/copy-content/S3/{table}")
    public ResponseEntity<?> copyContentToS3(@PathVariable String table){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Object>> rows = postgresService.readAll(table);
            for (Map<String, Object> row : rows) {
                String uuid = java.util.UUID.randomUUID().toString();
                String json = mapper.writeValueAsString(row);
                s3Service.writeObject(sid, uuid, json);
            }
            return ResponseEntity.ok().build();
        }
        catch (Exception e){
            return ResponseEntity.status(404).build();
        }
    }
}




