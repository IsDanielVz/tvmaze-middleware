package com.isaac.tvmaze.middleware.service;

import com.isaac.tvmaze.middleware.model.dto.ShowSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TvMazeService {

    @Autowired
    private RestTemplate restTemplate;

    public List<ShowSearchResponse> searchShows(String query) {
        List<ShowSearchResponse> resultList = new ArrayList<>();
        
        try {
            String finalUrl = "https://api.tvmaze.com/search/shows?q=" + query;
            
            List<Map<String, Object>> response = restTemplate.getForObject(finalUrl, List.class);

            if (response != null) {
                for (Map<String, Object> item : response) {
                    Map<String, Object> show = (Map<String, Object>) item.get("show");
                    if (show != null) {
                        
                        Long id = null;
                        if (show.get("id") != null) {
                            id = Long.valueOf(show.get("id").toString());
                        }

                        String channelName = "Unknown";
                        if (show.get("network") != null) {
                            Map<String, Object> network = (Map<String, Object>) show.get("network");
                            if (network.get("name") != null) {
                                channelName = network.get("name").toString();
                            }
                        } else if (show.get("webChannel") != null) {
                            Map<String, Object> webChannel = (Map<String, Object>) show.get("webChannel");
                            if (webChannel.get("name") != null) {
                                channelName = webChannel.get("name").toString();
                            }
                        }

                        ShowSearchResponse dto = ShowSearchResponse.builder()
                                .id(id)
                                .name(show.get("name") != null ? show.get("name").toString() : "No Name")
                                .channel(channelName)
                                .summary(show.get("summary") != null ? show.get("summary").toString() : "")
                                .genres((List<String>) show.get("genres"))
                                .build();
                        
                        resultList.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
}
