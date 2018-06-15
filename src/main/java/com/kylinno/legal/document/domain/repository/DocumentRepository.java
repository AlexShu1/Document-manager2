package com.kylinno.legal.document.domain.repository;

import com.kylinno.legal.document.domain.entity.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, Serializable> {

    List<DocumentEntity> findByUserId(String userId);

    DocumentEntity findById(String id);
}
