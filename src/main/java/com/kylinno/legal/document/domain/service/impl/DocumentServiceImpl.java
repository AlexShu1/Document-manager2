package com.kylinno.legal.document.domain.service.impl;

import com.kylinno.legal.document.domain.entity.DocumentEntity;
import com.kylinno.legal.document.domain.entity.DocumentLoggerEntity;
import com.kylinno.legal.document.domain.exception.DocumentException;
import com.kylinno.legal.document.domain.model.DocumentResponseModel;
import com.kylinno.legal.document.domain.model.FastDFSFile;
import com.kylinno.legal.document.domain.repository.DocumentRepository;
import com.kylinno.legal.document.domain.service.DocumentService;
import com.kylinno.legal.document.domain.service.fastDFS.FastDFSClient;
import com.kylinno.legal.document.domain.untils.DocumentOperatorLoggerUtil;
import com.kylinno.legal.document.domain.untils.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentOperatorLoggerUtil documentOperatorLoggerUtil;

    private DocumentEntity saveFileInfo(DocumentEntity document) {
        DocumentEntity file = new DocumentEntity();
        file.setDelete(false);
        file.setCreatedDate(new Date());
        file.setModifiedDate(new Date());
        file.setUnit(document.getUnit());
        file.setSize(document.getSize());
        file.setUserId(document.getUserId());
        file.setFilePath(document.getFilePath());
        file.setFileName(document.getFileName());
        file.setCategory(document.getCategory());
        file.setGroupName(document.getGroupName());
        file.setRemoteFileName(document.getRemoteFileName());
        file.setDescription(document.getDescription());

        return documentRepository.insert(file);
    }

    @Override
    public DocumentResponseModel saveDocument(MultipartFile multipartFile, String userId,
                                              String category, String description) throws IOException, DocumentException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        int status = 200;
        InputStream inputStream = multipartFile.getInputStream();
        DocumentResponseModel model = new DocumentResponseModel();

        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }

        inputStream.close();

        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        String chineseFileName = fileName;

        fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs

        if (fileName.contains("&#")) {
            chineseFileName = DocumentUtil.fetchShowFilePath(fileName);
        }

        if (fileAbsolutePath == null) {
            logger.error("upload file failed, Server don't response group id and save path.");
            status = 400;
            throw new DocumentException("upload file failed!");
        }

        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        String groupName = fileAbsolutePath[0];
        String remoteFileName = fileAbsolutePath[1];
        BigDecimal fileLength = new BigDecimal(multipartFile.getSize());
        String fileSizeUnit = DocumentUtil.changedFileSize(fileLength);
        String[] fileSizeString = fileSizeUnit.split("\\|");
        float fileSize = Float.parseFloat(fileSizeString[0]);

        DocumentEntity document = new DocumentEntity(fileSize, fileSizeString[1], userId, path, chineseFileName,
                groupName, remoteFileName, category, description);
        DocumentEntity documentEntity = saveFileInfo(document);
        documentOperatorLoggerUtil
                .setDocumentLoggerData(documentEntity.getUserId(), documentEntity.getId(), "Save file!");
        model = DocumentUtil.setResponseData(documentEntity, status);
        return model;
    }

    @Override
    public List<DocumentResponseModel> fetchAllDocumentsByUserId(String userId) {
        List<DocumentEntity> resultDocuments = documentRepository.findByUserId(userId);
        List<DocumentResponseModel> responseModels = new ArrayList<>();
        int resultSize = resultDocuments.size();

        for (int i = 0; i < resultSize; i++) {
            DocumentEntity document = resultDocuments.get(i);

            if(document.getDelete() == false) {
                DocumentResponseModel model = DocumentUtil.setResponseData(document, 200);
                documentOperatorLoggerUtil
                        .setDocumentLoggerData(document.getUserId(), document.getId(), "Find file!");
                responseModels.add(model);
            }
        }

        return DocumentUtil.fetchSortByModifiedDateDocument(responseModels);
    }

    @Override
    public DocumentResponseModel fetchDocumentByDocumentId(String documentId) {
        DocumentEntity document = documentRepository.findById(documentId);
        documentOperatorLoggerUtil
                .setDocumentLoggerData(document.getUserId(), document.getId(), "Find file!");
        return DocumentUtil.setResponseData(document, 200);
    }

    @Override
    public String deleteDocumentByDocumentId(String documentId) {
        Optional<DocumentEntity> documentOptional = Optional.of(documentRepository.findById(documentId));
        DocumentEntity document = documentOptional.orElse(null);
        document.setDelete(true);
        documentRepository.save(document);
        documentOperatorLoggerUtil
                .setDocumentLoggerData(document.getUserId(), document.getId(), "Delete file!");
        return "200";

    }

    @Override
    public String updateDocument(String documentId, String newFileName) {
        Optional<DocumentEntity> documentOptional = Optional.of(documentRepository.findById(documentId));
        DocumentEntity document = documentOptional.orElse(null);

        String originalFileName = document.getFileName();
        int index = originalFileName.lastIndexOf(".");
        String suffix = originalFileName.substring(index, originalFileName.length());
        newFileName += suffix;
        document.setFileName(newFileName);
        documentRepository.save(document);
        documentOperatorLoggerUtil
                .setDocumentLoggerData(document.getUserId(), document.getId(), "Update file name!");
        return "200";

    }

    @Override
    public void downloadFile(String fileId, HttpServletResponse response) throws IOException {
        Optional<DocumentEntity> documentOptional = Optional.of(documentRepository.findById(fileId));
        DocumentEntity document = documentOptional.orElse(null);

        FastDFSClient.downFile(document.getGroupName(), document.getFileName(),document.getRemoteFileName(), response.getOutputStream(), response);
    }
}
