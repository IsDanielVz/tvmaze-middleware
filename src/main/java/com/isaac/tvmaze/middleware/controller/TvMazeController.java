package com.isaac.tvmaze.middleware.controller;

import com.isaac.tvmaze.middleware.model.dto.ShowSearchResponse;
import com.isaac.tvmaze.middleware.model.entity.ShowComment;
import com.isaac.tvmaze.middleware.service.TvMazeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shows")
public class TvMazeController {

    @Autowired
    private TvMazeService tvMazeService;

    @GetMapping("/search")
    public List<ShowSearchResponse> search(@RequestParam("search_query") String query) {
        return tvMazeService.searchShows(query);
    }

    @GetMapping("/{show_id}")
    public ResponseEntity<Map<String, Object>> getShowById(@PathVariable("show_id") Long showId) {
        Map<String, Object> show = tvMazeService.getShowById(showId);
        if (show != null) {
            return ResponseEntity.ok(show);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/comment")
    public ResponseEntity<?> addComment(
            @RequestParam("show_id") Long showId,
            @RequestParam("comment") String comment,
            @RequestParam("rating") Integer rating) {
        try {
            ShowComment savedComment = tvMazeService.addComment(showId, comment, rating);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Comentario y calificación guardados con éxito",
                "data", savedComment
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "error",
                "message", "Error interno: " + e.getMessage()
            ));
        }
    }
}