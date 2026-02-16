package uk.ac.ed.inf.acpsubmission1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PostgresService {

    @Value("${acp.sid}")
    private String sid;
    private final JdbcTemplate jdbc;



    public PostgresService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> readAll(String table) {

        if (!table.matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name");
        }
        String fullTable = sid + "." + table;
        return jdbc.queryForList("SELECT * FROM " + fullTable);
    }
    public void insertDrones(String table, List<Map<String, Object>> drones){
        if (!table.matches("^[A-Za-z0-9_]+$"))
            throw new IllegalArgumentException("Invalid table name");

        String query = "INSERT INTO s2735972." + table +
                " (name,id,cooling,heating,capacity,maxMoves,costPerMove,costInitial,costFinal,costPer100Moves) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?) " +
                "ON CONFLICT (name) DO NOTHING";

        for(Map<String, Object> drone : drones){
            String name = (String) drone.get("name");
            String id = (String) drone.get("id");
            Number cost100 = (Number) drone.get("costPer100Moves");

            Map<String, Object> capability = (Map<String, Object>) drone.get("capability");
            Boolean cooling = (Boolean) capability.get("cooling");
            Boolean heating = (Boolean) capability.get("heating");
            Number capacity = (Number) capability.get("capacity");
            Number maxMoves = (Number) capability.get("maxMoves");
            Number costPerMove = (Number) capability.get("costPerMove");
            Number costInitial = (Number) capability.get("costInitial");
            Number costFinal = (Number) capability.get("costFinal");

            jdbc.update(query, name, id, cooling, heating, capacity, maxMoves, costPerMove, costInitial, costFinal, cost100);


        }

    }
}