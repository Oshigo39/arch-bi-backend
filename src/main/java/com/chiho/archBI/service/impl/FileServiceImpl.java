package com.chiho.archBI.service.impl;

import com.chiho.archBI.config.MinioConfig;
import com.chiho.archBI.service.FileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    /**
     * 引入官方的Minio客户端API
     */
    @Autowired
    private MinioClient minioClient;

    /**
     * 自动注入minio配置类，读取其中的配置
     */
    @Autowired
    private MinioConfig minioConfig;

    // 定义一个分隔符常量
    private final static String separator = "/";

    /**
     * 路径构造方法
     * @param prefixPath 前缀形成的目录路径
     * @param filename 文件名
     * @return 返回完整文件路径
     */
    public String builderFilePath(String prefixPath,String filename){
        StringBuilder stringBuilder = new StringBuilder(50);
        // 采用 yyyy-MM-dd/dirPath/filename. 的格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 构造当前日期
        String todayStr = sdf.format(new Date());
        // 构造路径
        stringBuilder.append(todayStr).append(separator);
        if (!StringUtils.isEmpty(prefixPath)){
            stringBuilder.append(prefixPath).append(separator);
        }
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    /**
     * 图像文件上传
     * @param prefix 文件前缀
     * @param filename 文件名
     * @param inputStream 文件输入流
     * @return 目标文件存储路径
     */
    @Override
    public String uploadImgFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix,filename);
        try{
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpg")
                    .bucket(minioConfig.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            return minioConfig.getReadPath() + separator + minioConfig.getBucket() +
                    separator + filePath;
        }catch (Exception ex){
            log.error("minio put file error.",ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * html文件上传
     * @param prefix 文件前缀
     * @param filename 文件名
     * @param inputStream 文件输入流
     * @return 目标文件存储路径
     */
    @Override
    public String uploadHtmlFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minioConfig.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            return minioConfig.getReadPath() + separator + minioConfig.getBucket() +
                    separator + filePath;
        }catch (Exception ex){
            log.error("minio put file error.",ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 删除...文件
     * @param pathUrl 文件全路径
     */
    @Override
    public void Delete(String pathUrl) {
        // 去掉URL前缀, 剩下的就是桶和路径的组合
        String key = pathUrl.replace(minioConfig.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);     // 获取桶名称
        String filePath = key.substring(index+1);       // 获取文件路径
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio删除文件错误.  pathUrl:{}",pathUrl);
        }
    }

    /**
     * 下载文件
     * @param pathUrl 文件全路径
     * @return 文件比特流
     */
    @Override
    public byte[] downLoadFile(String pathUrl) {
        String key = pathUrl.replace(minioConfig.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);
        String filePath = key.substring(index+1);
        InputStream inputStream = null;
        try {
            // 从minio中获取文件输入流
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio下载文件失败.  pathUrl:{}",pathUrl);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        // 通过循环读取输入流当中的数据到缓冲区buff
        while (true) {
            try {
                if (inputStream != null && !((rc = inputStream.read(buff, 0, 100)) > 0)) {
                    break;
                }
            } catch (IOException e) {
                log.error("minio下载文件失败",e);
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
