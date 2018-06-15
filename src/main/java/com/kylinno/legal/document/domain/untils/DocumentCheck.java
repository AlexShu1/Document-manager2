package com.kylinno.legal.document.domain.untils;

import com.kylinno.legal.document.domain.service.fastDFS.FastDFSClient;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Check document type.
 */
public class DocumentCheck {

    /**
     * Image type.
     */
    private static final List<String> TYPE_IMAGE = new ArrayList<>();

    /**
     * Document type.
     */
    private static final List<String> TYPE_DOC = new ArrayList<>();

    /**
     * Music type.
     */
    private static final List<String> TYPE_VIDEO = new ArrayList<>();

    /**
     * Compressed file type.
     */
    private static final List<String> TYPE_COMPRESS = new ArrayList<>();

    /**
     * All file type.
     */
    private static final List<String> TYPE_ALL_FILES = new ArrayList<>();

    static {
        TYPE_IMAGE.add("png");
        TYPE_IMAGE.add("gif");
        TYPE_IMAGE.add("jpeg");
        TYPE_IMAGE.add("jpg");
        TYPE_IMAGE.add("ico");
        TYPE_IMAGE.add("bmp");

        TYPE_DOC.add("ppt");
        TYPE_DOC.add("pptx");
        TYPE_DOC.add("xls");
        TYPE_DOC.add("xlsx");
        TYPE_DOC.add("doc");
        TYPE_DOC.add("docx");
        TYPE_DOC.add("pdf");
        TYPE_DOC.add("txt");

        TYPE_VIDEO.add("mp3");
        TYPE_VIDEO.add("mp4");
        TYPE_VIDEO.add("flv");
        TYPE_VIDEO.add("avi");

        TYPE_COMPRESS.add("zip");
        TYPE_COMPRESS.add("rar");

        TYPE_ALL_FILES.addAll(TYPE_IMAGE);
        TYPE_ALL_FILES.addAll(TYPE_DOC);
        TYPE_ALL_FILES.addAll(TYPE_VIDEO);
        TYPE_ALL_FILES.addAll(TYPE_COMPRESS);
    }

    /**
     * Checked image type.
     * Default image type: ['png', 'gif', 'jpeg', 'jpg', 'bmp', 'ico']
     * @param filename
     * @return
     */
    public static boolean checkImage(String filename){
        return checkImage(null, filename);
    }

    /**
     * Checked file type.
     * @param filename
     * @return
     */
    public static boolean checkFiles(String filename){
        return checkFile(null, filename);
    }

    /**
     * Checked image type.
     * @param types Default image type: ['png', 'gif', 'jpeg', 'jpg', 'bmp', 'ico']
     * @param filename
     * @return
     */
    public static boolean checkImage(List<String> types, String filename){
        List<String> checkTypes = types;
        if(types == null || types.size() == 0){
            checkTypes = TYPE_IMAGE;
        }

        return checkType(checkTypes, filename);
    }

    /**
     * Checked file type.
     * @param filename
     * @return
     */
    public static boolean checkFile(List<String> types, String filename){
        List<String> checkTypes = types;
        if(types == null || types.size() == 0){
            checkTypes = TYPE_ALL_FILES;
        }

        return checkType(checkTypes, filename);
    }

    /**
     * Checked document type.
     * Default document type:  ['pdf', 'ppt', 'xls', 'xlsx', 'pptx', 'doc', 'docx', 'txt']
     * @param filename
     * @return
     */
    public static boolean checkDoc(String filename){
        return checkDoc(null, filename);
    }

    /**
     * Checked document type.
     * @param types Default document type:  ['pdf', 'ppt', 'xls', 'xlsx', 'pptx', 'doc', 'docx', 'txt']
     * @param filename
     * @return
     */
    public static boolean checkDoc(List<String> types, String filename){
        List<String> checkTypes = types;
        if(types == null || types.size() == 0){
            checkTypes = TYPE_DOC;
        }

        return checkType(checkTypes, filename);
    }

    /**
     * Checked music type.
     * Default music type:  ['mp3', 'mp4', 'flv', 'avi']
     * @param filename 文件名称
     * @return
     */
    public static boolean checkVideo(String filename){
        return checkVideo(null, filename);
    }

    /**
     * Checked music type.
     * @param types Default music type:  ['mp3', 'mp4', 'flv', 'avi']
     * @param filename
     * @return
     */
    public static boolean checkVideo(List<String> types, String filename){
        List<String> checkTypes = types;
        if(types == null || types.size() == 0){
            checkTypes = TYPE_VIDEO;
        }

        return checkType(checkTypes, filename);
    }

    /**
     * Checked compressed file type.
     * Default compressed file type:  ['zip', 'rar']
     * @param filename
     * @return
     */
    public static boolean checkCompress(String filename){
        return checkCompress(null, filename);
    }

    /**
     * Checked compressed file type.
     *
     * @param types Default compressed file type:  ['zip', 'rar']
     * @param filename 文件名称
     * @return
     */
    public static boolean checkCompress(List<String> types, String filename){
        List<String> checkTypes = types;
        if(types == null || types.size() == 0){
            checkTypes = TYPE_COMPRESS;
        }

        return checkType(checkTypes, filename);
    }

    /**
     * Checked file type.
     */
    private static boolean checkType(List<String> checkTypes, String filename){
        return checkTypes.contains(getFilenameSuffix(filename));
    }

    /**
     * Fetch file Suffix.
     *
     * @param filename
     * @return file Suffix.
     */
    public static String getFilenameSuffix(String filename) {
        String suffix = null;
        if (StringUtils.isNotBlank(filename) && filename.contains(FastDFSClient.POINT)) {
            suffix = filename.substring(filename.lastIndexOf(FastDFSClient.POINT) + 1).toLowerCase();
        }
        return suffix;
    }
}
