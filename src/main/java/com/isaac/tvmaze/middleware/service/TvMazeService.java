package com.isaac.tvmaze.middleware.service;

import com.isaac.tvmaze.middleware.model.dto.ShowSearchResponse;
import com.isaac.tvmaze.middleware.model.dto.ShowSearchResponse.CommentDTO;
import com.isaac.tvmaze.middleware.model.entity.CachedShow;
import com.isaac.tvmaze.middleware.model.entity.ShowComment;
import com.isaac.tvmaze.middleware.repository.CachedShowRepository;
import com.isaac.tvmaze.middleware.repository.ShowCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TvMazeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CachedShowRepository cachedShowRepository;

    @Autowired
    private ShowCommentRepository showCommentRepository;

    @Value("${tvmaze.api.base-url}")
    private String baseUrl;

    public List<ShowSearchResponse> searchShows(String query) {
        List<ShowSearchResponse> resultList = new ArrayList<>();
        try {
            String sanitizedQuery = query != null ? query.trim() : "";
            String finalUrl = baseUrl + "/search/shows?q=" + sanitizedQuery;
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

                        List<CommentDTO> savedCommentsDTO = new ArrayList<>();
                        if (id != null) {
                            List<ShowComment> dbComments = showCommentRepository.findByShowId(id);
                            savedCommentsDTO = dbComments.stream()
                                    .map(c -> CommentDTO.builder()
                                            .comment(c.getComment())
                                            .rating(c.getRating())
                                            .build())
                                    .collect(Collectors.toList());
                        }

                        ShowSearchResponse dto = ShowSearchResponse.builder()
                                .id(id)
                                .name(show.get("name") != null ? show.get("name").toString() : "No Name")
                                .channel(channelName)
                                .summary(show.get("summary") != null ? show.get("summary").toString() : "")
                                .genres((List<String>) show.get("genres"))
                                .comments(savedCommentsDTO)
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

    public Map<String, Object> getShowById(Long showId) {
        Map<String, Object> showDataResult = null;

        Optional<CachedShow> dbResult = cachedShowRepository.findById(showId);
        if (dbResult.isPresent()) {
            System.out.println("--- Retornando Show ID: " + showId + " desde CACHÉ (MongoDB Atlas) ---");
            showDataResult = new java.util.HashMap<>(dbResult.get().getShowData());
        } else {
            try {
                System.out.println("--- Consumiendo API externa de TV Maze para ID: " + showId + " ---");
                String url = baseUrl + "/shows/" + showId; 
                
                Map<String, Object> externalShow = restTemplate.getForObject(url, Map.class);

                if (externalShow != null) {
                    CachedShow showToCache = CachedShow.builder()
                            .id(showId)
                            .showData(externalShow)
                            .build();
                    cachedShowRepository.save(showToCache);
                    
                    showDataResult = new java.util.HashMap<>(externalShow);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (showDataResult != null) {
            List<ShowComment> dbComments = showCommentRepository.findByShowId(showId);
            
            List<Map<String, Object>> commentsList = dbComments.stream()
                    .map(c -> {
                        Map<String, Object> map = new java.util.HashMap<>();
                        map.put("comment", c.getComment());
                        map.put("rating", c.getRating());
                        return map;
                    })
                    .collect(Collectors.toList());

            showDataResult.put("comments", commentsList);
        }

        return showDataResult;
    }

    public ShowComment addComment(Long showId, String commentText, Integer rating) {
        if (rating == null || rating < 0 || rating > 5) {
            throw new IllegalArgumentException("La calificación debe ser un entero entre 0 y 5.");
        }

        ShowComment commentEntity = ShowComment.builder()
                .showId(showId)
                .comment(commentText != null ? commentText.trim() : "")
                .rating(rating)
                .build();

        return showCommentRepository.save(commentEntity);
    }
}
