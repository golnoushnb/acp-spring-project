package uk.ac.ed.inf.acpsubmission1.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostgresService {

    private final JdbcTemplate jdbc;

    public PostgresService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> readAll(String table) {

        if (!table.matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name");
        }

        return jdbc.queryForList("SELECT * FROM " + table);
    }
}