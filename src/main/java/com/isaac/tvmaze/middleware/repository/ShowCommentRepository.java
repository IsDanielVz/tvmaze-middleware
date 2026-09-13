package com.isaac.tvmaze.middleware.repository;

import com.isaac.tvmaze.middleware.model.entity.ShowComment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowCommentRepository extends MongoRepository<ShowComment, String> {
}
