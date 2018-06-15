package com.kylinno.legal.document.domain.service;

import com.kylinno.legal.document.domain.exception.DocumentException;
import com.kylinno.legal.document.domain.model.DocumentResponseModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DocumentService {

    /**
     * Save files to server.
     *
     * @param multipartFile
     * @param userId
     * @param category
     * @return Save document.
     * @throws IOException
     * @throws DocumentException
     */
    DocumentResponseModel saveDocument(MultipartFile multipartFile, String userId,
                                       String category, String description) throws IOException, DocumentException;

    /**
     * According to userId, find user's documents.
     *
     * @param userId
     * @return Find documents
     */
    List<DocumentResponseModel> fetchAllDocumentsByUserId(String userId);

    /**
     * According to document id, find document.
     *
     * @param documentId
     * @return Find document.
     */
    DocumentResponseModel fetchDocumentByDocumentId(String documentId);

    /**
     * According to document id, delete document.
     *
     * @param documentId
     * @return Deleted status.
     */
    DocumentResponseModel deleteDocumentByDocumentId(String documentId);

    /**
     * According to document id, changed document name.
     *
     * @param documentId
     * @param newFileName
     * @return Updated status.
     */
    DocumentResponseModel updateDocument(String documentId, String newFileName);

    /**
     * According to file id, download file.
     *
     * @param fileId
     */
    DocumentResponseModel downloadFile(String fileId, HttpServletResponse response) throws IOException;
}
