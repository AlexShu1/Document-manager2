package com.kylinno.legal.document.resource;

import com.kylinno.legal.document.domain.constants.ErrorCode;
import com.kylinno.legal.document.domain.exception.DocumentException;
import com.kylinno.legal.document.domain.model.DocumentResponseModel;
import com.kylinno.legal.document.domain.service.DocumentService;
import com.kylinno.legal.document.domain.untils.DocumentCheck;
import com.kylinno.legal.document.domain.untils.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    private List<DocumentResponseModel> responseModels;

    private static Logger logger = LoggerFactory.getLogger(DocumentController.class);

    /**
     * Upload files: single file or more files.
     *
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("")
    public List<DocumentResponseModel> uploadDocument(@RequestParam("file") MultipartFile[] files,
                                                      @RequestParam("userId") String userId,
                                                      @RequestParam("category") String category,
                                                      @RequestParam("description") String description) {

        responseModels = new ArrayList<>();
        int fileLength = files.length;
        final CountDownLatch countDownLatch = new CountDownLatch(fileLength);

        for (int i = 0; i < fileLength; i++) {
            MultipartFile file = files[i];
            DocumentResponseModel model = new DocumentResponseModel();

            // 检查文件类型
            if (!DocumentCheck.checkImage(file.getOriginalFilename())) {
                model.setSuccess(false);
                model.setMessage(ErrorCode.FILE_TYPE_ERROR.MESSAGE);
                responseModels.add(model);
                continue;
            }

            Thread thread = new Thread(() -> {
                try {
                    DocumentResponseModel resultModel = documentService.saveDocument(file, userId, category, description);
                    model.setSuccess(true);
                    model.setFileId(resultModel.getFileId());
                    model.setFileSize(resultModel.getFileSize());
                    model.setCategory(resultModel.getCategory());
                    model.setFileName(resultModel.getFileName());
                    model.setModifiedDate(resultModel.getModifiedDate());
                    model.setFileSizeUnit(resultModel.getFileSizeUnit());
                    model.setDescription(resultModel.getDescription());
                } catch (IOException | DocumentException e) {
                    model.setSuccess(false);
                    model.setMessage(e.getMessage());
                    logger.error("upload file failed", e);
                } finally {
                    countDownLatch.countDown();
                    responseModels.add(model);
                }
            });

            thread.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("upload file failed", e);
        }

        return DocumentUtil.fetchSortByModifiedDateDocument(responseModels);
    }

    /**
     * 上传图片，会对文件的格式，进行检查，是否为图片格式。
     * 默认的格式有: ['png', 'gif', 'jpeg', 'jpg', 'bmp'].
     *
     * @param file
     * @param userId
     * @param category
     * @param description
     * @return
     */
    @PostMapping("/image")
    public DocumentResponseModel uploadImage(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userId") String userId,
                                             @RequestParam("category") String category,
                                             @RequestParam("description") String description) {

        DocumentResponseModel responseData = new DocumentResponseModel();

        // 检查文件类型
        if (!DocumentCheck.checkImage(file.getOriginalFilename())) {
            responseData.setSuccess(false);
            responseData.setMessage(ErrorCode.FILE_TYPE_ERROR_IMAGE.MESSAGE);
            return responseData;
        }

        try {
            responseData = documentService.saveDocument(file, userId, category, description);
            responseData.setSuccess(true);
        } catch (IOException | DocumentException e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
        }

        return responseData;
    }


    @GetMapping("/user/{userId}")
    public List<DocumentResponseModel> fetchAllDocuments(@PathVariable("userId") String userId) {
        return documentService.fetchAllDocumentsByUserId(userId);
    }

    @GetMapping("/{documentId}")
    public DocumentResponseModel fetchDocumentById(@PathVariable("documentId") String documentId) {
        return documentService.fetchDocumentByDocumentId(documentId);
    }

    @DeleteMapping("/{documentId}")
    public String deleteDocumentById(@PathVariable("documentId") String documentId) {
        return documentService.deleteDocumentByDocumentId(documentId);
    }

    @PutMapping("")
    public String putDocumentById(@RequestParam("documentId") String documentId,
                                  @RequestParam("fileName") String fileName) {
        return documentService.updateDocument(documentId, fileName);
    }

    @GetMapping("/file/{fileId}")
    public void downloadDocument(@PathVariable("fileId") String fileId, HttpServletResponse response) throws IOException {
        documentService.downloadFile(fileId, response);
    }
}