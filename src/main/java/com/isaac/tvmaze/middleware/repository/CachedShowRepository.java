package com.isaac.tvmaze.middleware.repository;

import com.isaac.tvmaze.middleware.model.entity.CachedShow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedShowRepository extends MongoRepository<CachedShow, Long> {
}
