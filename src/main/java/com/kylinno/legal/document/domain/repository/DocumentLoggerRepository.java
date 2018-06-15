package com.kylinno.legal.document.domain.repository;

import com.kylinno.legal.document.domain.entity.DocumentLoggerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface DocumentLoggerRepository extends MongoRepository<DocumentLoggerEntity, Serializable> {
}
