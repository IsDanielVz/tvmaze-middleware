package com.isaac.tvmaze.middleware.controller;

import com.isaac.tvmaze.middleware.model.dto.ShowSearchResponse;
import com.isaac.tvmaze.middleware.service.TvMazeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class TvMazeController {

    @Autowired
    private TvMazeService tvMazeService;

    @GetMapping("/search")
    public List<ShowSearchResponse> search(@RequestParam("search_query") String query) {
        return tvMazeService.searchShows(query);
    }
}
