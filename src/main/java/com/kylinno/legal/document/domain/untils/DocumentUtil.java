package com.kylinno.legal.document.domain.untils;

import com.kylinno.legal.document.domain.entity.DocumentEntity;
import com.kylinno.legal.document.domain.model.DocumentResponseModel;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentUtil {

    /**
     * Let Hex string to chinese.
     */
    public static String hexStringToChinese(String hexString) {
        StringBuilder stringBuilder = new StringBuilder();
        hexString = hexString.replace("&#", "");
        String[] codes = hexString.split(";");
        for (int i = 0; i < codes.length; i++) {
            stringBuilder.append((char) Integer.parseInt(codes[i]));
        }

        return stringBuilder.toString();
    }

    /**
     * Changed file size to B/KB/MB/GB/TB/PB.
     *
     * @param fileSize
     * @return
     */
    public static String changedFileSize(BigDecimal fileSize) {
        BigDecimal multiple = new BigDecimal(1024L);

        String[] units = {"KB", "MB", "GB", "TB", "PB"};
        String unit = "B";
        int unitsLength = units.length;

        if (fileSize.compareTo(multiple) > -1) {
            for (int i = 0; i < unitsLength; i++) {
                fileSize = fileSize.divide(multiple);

                if (fileSize.compareTo(multiple) == -1) {
                    unit = units[i];
                    break;
                }
            }
        }

        fileSize = fileSize.setScale(2, BigDecimal.ROUND_HALF_UP);
        return fileSize + "|" + unit;
    }

    /**
     * Return: Customer upload file name. Because uploaded file name is error code.
     *
     * @param filePath: Original File name.
     * @return
     */
    public static String fetchShowFilePath(String filePath) {
        String[] filePaths = filePath.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        int filePathLength = filePaths.length;

        for (int i = 0; i < filePathLength; i++) {
            if (filePaths[i].contains("&#") && filePaths[i].contains(".")) { // Chinese file
                String[] chineseFilePaths = filePaths[i].split("\\.");
                stringBuilder.append(DocumentUtil.hexStringToChinese(chineseFilePaths[0]));
                stringBuilder.append(".");
                stringBuilder.append(chineseFilePaths[1]);
            } else if (filePaths[i].contains("&#")) { // Chinese directory
                stringBuilder.append(DocumentUtil.hexStringToChinese(filePaths[i]));
                stringBuilder.append("/");
            } else if (filePaths[i].contains(".")) { // English file
                stringBuilder.append(filePaths[i]);
            } else { // English directory
                stringBuilder.append(filePaths[i]);
                stringBuilder.append("/");
            }
        }

        return stringBuilder.toString();
    }

    public static List<DocumentResponseModel> fetchSortByModifiedDateDocument(List<DocumentResponseModel> responseModels) {

        return responseModels.stream()
                .sorted(Comparator.comparing(DocumentResponseModel::getModifiedDate).reversed())
                .collect(Collectors.toList());
    }

    public static DocumentResponseModel setResponseData(DocumentEntity document) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String modifyDate = dateFormat.format(document.getModifiedDate());

        DocumentResponseModel model = new DocumentResponseModel();
        model.setSuccess(true);
        model.setFileId(document.getId());
        model.setModifiedDate(modifyDate);
        model.setFileSize(document.getSize());
        model.setFileName(document.getFileName());
        model.setCategory(document.getCategory());
        model.setFileSizeUnit(document.getUnit());
        model.setDescription(document.getDescription());
        return model;
    }
}
