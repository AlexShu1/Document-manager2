package com.kylinno.legal.document.domain.untils;

import com.kylinno.legal.document.domain.entity.DocumentLoggerEntity;
import com.kylinno.legal.document.domain.repository.DocumentLoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DocumentOperatorLoggerUtil {

    @Autowired
    private DocumentLoggerRepository documentLoggerRepository;

    /**
     * Save user operator information.
     *
     * @param userId
     * @param fileId
     * @param description
     */
    public void setDocumentLoggerData(String userId, String fileId, String description) {
        DocumentLoggerEntity loggerEntity = new DocumentLoggerEntity();
        loggerEntity.setUserId(userId);
        loggerEntity.setFileId(fileId);
        loggerEntity.setDescription(description);
        loggerEntity.setOperationTime(new Date());

        documentLoggerRepository.save(loggerEntity);
    }
}
