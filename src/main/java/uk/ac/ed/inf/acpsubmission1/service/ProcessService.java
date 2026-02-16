package uk.ac.ed.inf.acpsubmission1.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class ProcessService {
    private final RestClient rest = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();

    private double getDouble(Map<String, Object> map, String key){
        if(map==null)
            return 0;
        Object v=map.get(key);
        if(v==null)
            return 0;
        if(v instanceof Number n)
            return n.doubleValue();
        try {
            return Double.parseDouble(v.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private double sanitize(double x){
        if(Double.isNaN(x) || Double.isInfinite(x))
            return 0;
        return x;
    }
    public List<Map<String, Object>> processDrones(String urlPath) throws Exception{
        String body = rest.get().uri(urlPath).retrieve().body(String.class);
        List<Map<String, Object>> drones = mapper.readValue(body, new TypeReference<>() {});

        for(Map<String, Object> drone : drones){
            Map<String, Object> capability = (Map<String, Object>) drone.get("capability");
            double costInitial = getDouble(capability, "costInitial");
            double costFinal = getDouble(capability, "costFinal");
            double costPerMove = getDouble(capability, "costPerMove");
            double costPer100Moves = sanitize(costInitial) + sanitize(costFinal) + sanitize(costPerMove)*100;

            drone.put("costPer100Moves", costPer100Moves);
        }
        return drones;
    }

}
