package com.kylinno.legal.document.domain.service.fastDFS;

import com.kylinno.legal.document.domain.model.FastDFSFile;
import org.apache.commons.lang.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
public class FastDFSClient {

    public static final String POINT = ".";
    public static final String SEPARATOR = "/";
    public static final Map<String, String> EXT_MAPS = new HashMap<>();
    private static final String FASTDFS_CONFIG_PATH = "config.properties";
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    static {
        try {
            ClientGlobal.initByProperties(FASTDFS_CONFIG_PATH);
        } catch (Exception e) {
            logger.error("FastDFS Client Init Fail!", e);
        }
    }

    public FastDFSClient() {
        initExt();
    }

    private void initExt() {
        // image
        EXT_MAPS.put("png", "image/png");
        EXT_MAPS.put("gif", "image/gif");
        EXT_MAPS.put("bmp", "image/bmp");
        EXT_MAPS.put("ico", "image/x-ico");
        EXT_MAPS.put("jpeg", "image/jpeg");
        EXT_MAPS.put("jpg", "image/jpeg");
        // 压缩文件
        EXT_MAPS.put("zip", "application/zip");
        EXT_MAPS.put("rar", "application/x-rar");
        // doc
        EXT_MAPS.put("pdf", "application/pdf");
        EXT_MAPS.put("ppt", "application/vnd.ms-powerpoint");
        EXT_MAPS.put("xls", "application/vnd.ms-excel");
        EXT_MAPS.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        EXT_MAPS.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        EXT_MAPS.put("doc", "application/msword");
        EXT_MAPS.put("doc", "application/wps-office.doc");
        EXT_MAPS.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        EXT_MAPS.put("txt", "text/plain");
        // 音频
        EXT_MAPS.put("mp4", "video/mp4");
        EXT_MAPS.put("flv", "video/x-flv");
        EXT_MAPS.put("avi", "application/x-troff-msvideo");
        EXT_MAPS.put("avi", "video/avi");
        EXT_MAPS.put("avi", "video/msvideo");
        EXT_MAPS.put("avi", "video/x-msvideo");
        EXT_MAPS.put("mp3", "audio/mpeg3");
        EXT_MAPS.put("mp3", "audio/x-mpeg-3");
        EXT_MAPS.put("mp3", "video/mpeg");
        EXT_MAPS.put("mp3", "video/x-mpeg");
    }


    public static String[] upload(FastDFSFile file) {
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", file.getAuthor());

        long startTime = System.currentTimeMillis();
        String[] uploadResults = null;
        TrackerServer trackerServer = null;
        StorageClient storageClient = null;
        try {
            trackerServer = TrackerServerPool.borrowObject();
            storageClient = new StorageClient1(trackerServer, null);
            uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        } catch (IOException e) {
            logger.error("IO Exception when uploadind the file:" + file.getName(), e);
        } catch (Exception e) {
            logger.error("Non IO Exception when uploadind the file:" + file.getName(), e);
        }
        logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

        if (uploadResults == null && storageClient != null) {
            logger.error("upload file fail, error code:" + storageClient.getErrorCode());
        }

        String groupName = uploadResults[0];
        String remoteFileName = uploadResults[1];

        logger.info("upload file successfully!!!" + "group_name:" + groupName + ", remoteFileName:" + " " + remoteFileName);
        // 返还对象
        TrackerServerPool.returnObject(trackerServer);
        return uploadResults;
    }

    public static FileInfo getFile(String groupName, String remoteFileName) {
        TrackerServer trackerServer = null;

        try {
            trackerServer = TrackerServerPool.borrowObject();
            StorageClient1 storageClient = new StorageClient1(trackerServer, null);
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }

        // 返还对象
        TrackerServerPool.returnObject(trackerServer);
        return null;
    }

    public static void downFile(String groupName, String fileName, String remoteFileName, OutputStream outputStream, HttpServletResponse response) {
        TrackerServer trackerServer = null;
        InputStream inputStream = null;

        try {
            trackerServer = TrackerServerPool.borrowObject();
            StorageClient1 storageClient = new StorageClient1(trackerServer, null);

            String contentType = EXT_MAPS.get(getFilenameSuffix(remoteFileName));
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            inputStream = new ByteArrayInputStream(fileByte);
            byte[] buffer = new byte[1024 * 5];
            int len = 0;

            // 设置响应头
            if (StringUtils.isNotBlank(contentType)) {
                // 文件编码 处理文件名中的 '+'、' ' 特殊字符
                String encoderName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20").replace("%2B", "+");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + encoderName + "\"");
                response.setContentType(contentType + ";charset=UTF-8");
                response.setHeader("Accept-Ranges", "bytes");
            }

            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        } finally {
            // 关闭流
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 返还对象
        TrackerServerPool.returnObject(trackerServer);
    }

    public static void deleteFile(String groupName, String remoteFileName)
            throws Exception {
        TrackerServer trackerServer = TrackerServerPool.borrowObject();
        StorageClient1 storageClient = new StorageClient1(trackerServer, null);
        int i = storageClient.delete_file(groupName, remoteFileName);
        logger.info("delete file successfully!!!" + i);
    }

    public static String getTrackerUrl() throws IOException {
        return "http://" + getTrackerServer().getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port() + "/";
    }

    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /**
     * 获取文件名称的后缀
     *
     * @param filename 文件名 或 文件路径
     * @return 文件后缀
     */
    public static String getFilenameSuffix(String filename) {
        String suffix = null;
        String originalFilename = filename;
        if (StringUtils.isNotBlank(filename)) {
            if (filename.contains(SEPARATOR)) {
                filename = filename.substring(filename.lastIndexOf(SEPARATOR) + 1);
            }
            if (filename.contains(POINT)) {
                suffix = filename.substring(filename.lastIndexOf(POINT) + 1);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("filename error without suffix : {}", originalFilename);
                }
            }
        }
        return suffix;
    }
}