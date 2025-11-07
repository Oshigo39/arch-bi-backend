package com.chiho.archBI.controller;

import com.chiho.archBI.common.BaseResponse;
import com.chiho.archBI.common.ErrorCode;
import com.chiho.archBI.common.ResultUtils;
import com.chiho.archBI.exception.BusinessException;
import com.chiho.archBI.model.dto.file.UploadFileRequest;
import com.chiho.archBI.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
@Slf4j
@Api(tags = "文件相关接口")
public class FileController {
    @Autowired
    private FileService fileService;

    /**
     *
     * @param file http传输的多部分文件流
     *             其中@RequestPart("file") 用于处理multipart/form-data类型的请求，
     *             核心作用是从多部份的请求中提取指定名称的部分，然后绑定到方法参数上
     * @param uploadFileRequest 文件上传请求DTO
     * @param httpServletRequest 封装HTTP请求信息的核心接口，提供一系列工具获取http请求中的数据
     * @return 返回上传成功的文件路径
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile file,
                                       UploadFileRequest uploadFileRequest,
                                       HttpServletRequest httpServletRequest) {
        log.info("文件上传：{}", file);
        try {
            //原始文件名
            String originalFileName = file.getOriginalFilename();
            // 截取原始文件名的后缀（得到文件类型）
            String fileExtensionName = "";
            if (originalFileName != null) {
                fileExtensionName = originalFileName
                        .substring(originalFileName.lastIndexOf("."));
            }
            // 构造新文件名称
            String prefix = "";
            String objectName = UUID.randomUUID() + fileExtensionName;
            if (originalFileName != null) {
                prefix = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            }
            String filePath = fileService.uploadImgFile(prefix, objectName, file.getInputStream());
            // 上传成功
            return ResultUtils.success(filePath);

        } catch (IOException e) {
            log.error("文件上传失败：", e);
        }
        // todo 这个其实不太准确，可以考虑封装好文件相关的常量、公共类等
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
    }

    @DeleteMapping("/delete")
    @ApiOperation("根据url删除文件")
    public BaseResponse<Boolean> deleteFileByUrl(@RequestParam("pathUrl") String pathUrl) {
        if (pathUrl == null || pathUrl.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL不能为空");
        }
        try {
            fileService.Delete(pathUrl);
            log.info("文件删除成功，URL：{}", pathUrl);
            return ResultUtils.success(true);
        } catch (Exception ex) {
            log.error("文件删除失败：{}", pathUrl, ex);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件删除失败" + ex.getMessage());
        }
    }

    @GetMapping("/download")
    @ApiOperation("文件下载")
    public void downloadFile() {
        // todo 文件下载接口
    }

}
