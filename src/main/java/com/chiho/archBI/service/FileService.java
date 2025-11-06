package com.chiho.archBI.service;

import java.io.InputStream;

/**
 * 文件存储服务 - Minio实现
 *
 */
public interface FileService {
    /**
     * 上传图片文件
     * @param prefix 文件前缀
     * @param filename 文件名
     * @param inputStream 文件输入流
     * @return 文件路径
     */
    String uploadImgFile(String prefix, String filename, InputStream inputStream);

    /**
     * 上传Html文件
     * @param prefix 文件前缀
     * @param filename 文件名
     * @param inputStream 文件输入流
     * @return 文件路径
     */
    String uploadHtmlFile(String prefix,String filename,InputStream inputStream);

    /**
     * 根据文件路径删除文件
     * @param pathUrl 文件全路径
     */
    void Delete(String pathUrl);

    /**
     * 下载文件
     * @param pathUrl 文件全路径
     * @return 文件字节流数组
     */
    byte[] downLoadFile(String pathUrl);
}
