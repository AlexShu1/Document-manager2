package com.kylinno.legal.document.domain.model;

public class DocumentResponseModel {

    private String fileId;
    private float fileSize;
    private String message;
    private String category;
    private String fileName;
    private String modifiedDate;
    private String fileSizeUnit;
    private String description;
    private Boolean success = true;

    public DocumentResponseModel() {
    }

    public DocumentResponseModel(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeUnit() {
        return fileSizeUnit;
    }

    public void setFileSizeUnit(String fileSizeUnit) {
        this.fileSizeUnit = fileSizeUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
