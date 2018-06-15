package com.kylinno.legal.document.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "document")
public class DocumentEntity implements Serializable {

    private static final long serialVersionUID = -3258839839160856613L;

    @Id
    private String id;
    private Float size;
    private String unit;
    private String userId;
    private String filePath;
    private String fileName;
    private String groupName;
    private String remoteFileName;
    private String category;
    private Boolean isDelete;
    private Date createdDate;
    private Date modifiedDate;
    private String description;

    public DocumentEntity() {
    }

    public DocumentEntity(Float size, String unit, String userId, String filePath, String fileName, String groupName,
                          String remoteFileName, String category, String description) {
        this.size = size;
        this.unit = unit;
        this.userId = userId;
        this.filePath = filePath;
        this.fileName = fileName;
        this.groupName = groupName;
        this.remoteFileName = remoteFileName;
        this.category = category;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }
}
