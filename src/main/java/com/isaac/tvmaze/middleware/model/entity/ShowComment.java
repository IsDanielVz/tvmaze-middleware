package com.isaac.tvmaze.middleware.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "show_comments")
public class ShowComment {
    @Id
    private String id;
    private Long showId;
    private String comment;
    private Integer rating;
}
