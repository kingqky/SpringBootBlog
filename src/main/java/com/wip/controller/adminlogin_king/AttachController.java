package com.wip.controller.adminlogin_king;
import io.minio.*;
import io.minio.errors.MinioException;
import com.github.pagehelper.PageInfo;
import com.wip.api.MinioCloudService;
import com.wip.constant.ErrorConstant;
import com.wip.constant.LogActions;
import com.wip.constant.Types;
import com.wip.constant.WebConst;
import com.wip.controller.BaseController;
import com.wip.dto.AttAchDto;
import com.wip.exception.BusinessException;
import com.wip.model.AttAchDomain;
import com.wip.model.UserDomain;
import com.wip.service.attach.AttAchService;
import com.wip.service.log.LogService;
import com.wip.utils.APIResponse;
import com.wip.utils.Commons;
import com.wip.utils.TaleUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.wip.utils.TaleUtils.isImage;

@Api("文件管理")
@Controller
@RequestMapping("adminlogin_king/attach")
public class AttachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

    public static final String CLASSPATH = TaleUtils.getUploadFilePath();

    @Autowired
    private AttAchService attAchService;

    @Autowired
    private LogService logService;



    @ApiOperation("文件管理首页")
    @GetMapping(value = "")
    public String index(
            HttpServletRequest request,
            @ApiParam(name = "page", value = "页数", required = false)
            @RequestParam(name = "page", required = false, defaultValue = "1")
            int page,
            @ApiParam(name = "limit", value = "条数", required = false)
            @RequestParam(name = "limit", required = false, defaultValue = "12")
            int limit
    ) {
        PageInfo<AttAchDto> atts = attAchService.getAtts(page, limit);
        request.setAttribute("attachs", atts);
        request.setAttribute(Types.ATTACH_URL.getType(),Commons.site_option(Types.ATTACH_URL.getType(), Commons.site_url()));
        request.setAttribute("max_file_size", WebConst.MAX_FILE_SIZE / 1024);
        return "adminlogin_king/attach";
    }

    @ApiOperation("markdown文件上传")
    @PostMapping(value = "/uploadfile")
    public void fileUploadToTencentCloud(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(name = "editormcd-image-file", value = "文件数组", required = true)
            @RequestParam(name = "editormd-image-file", required = true)
            MultipartFile file
    ) {
        try {
            // 设置字符编码和响应类型
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");

            // 获取文件名并生成保存路径
            String fileName = file.getOriginalFilename();
            String filePath = "/opt/images/" + fileName;
            // 保存文件到指定路径
            file.transferTo(new File(filePath));

            // 构建图片访问链接
            String imageUrl = "http://121.5.147.152"+"/images/" + fileName; // 修改为你的图片访问路径

            // 构建响应数据，包含图片访问链接
            String responseData = "{\"success\": 1, \"url\": \"" + imageUrl + "\"}";
            response.getWriter().write(responseData);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理异常并返回错误信息
            String errorMessage = "{\"success\": 0, \"message\": \"文件上传失败：" + e.getMessage() + "\"}";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // 设置状态码为500
            try {
                response.getWriter().write(errorMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //    @ApiOperation("多文件上传")
//    @PostMapping(value = "upload")
//    @ResponseBody
//    public APIResponse filesUploadToCloud(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @ApiParam(name = "file", value = "文件数组", required = true)
//            @RequestParam(name = "file", required = true)
//            MultipartFile[] files
//    ) {
//        try {
//            request.setCharacterEncoding("UTF-8");
//            response.setHeader("Content-Type","text/html");
//
//            for (MultipartFile file :files) {
//
//                String fileName = TaleUtils.getFileKey(file.getOriginalFilename().replaceFirst("/", ""));
//
//                QiNiuCloudService.upload(file, fileName);
//                AttAchDomain attAchDomain = new AttAchDomain();
//                HttpSession session = request.getSession();
//                UserDomain sessionUser = (UserDomain) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
//                attAchDomain.setAuthorId(sessionUser.getUid());
//                attAchDomain.setFtype(TaleUtils.isImage(file.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType());
//                attAchDomain.setFname(fileName);
//                attAchDomain.setFkey(QiNiuCloudService.QINIU_UPLOAD_SITE + fileName);
//                attAchService.addAttAch(attAchDomain);
//            }
//            return APIResponse.success();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw BusinessException.withErrorCode(ErrorConstant.Att.ADD_NEW_ATT_FAIL)
//                    .withErrorMessageArguments(e.getMessage());
//        }
//
//    }
    @ApiOperation("多文件上传")
    @PostMapping(value = "upload")
    @ResponseBody
    public APIResponse filesUploadToCloud(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(name = "file", value = "文件数组", required = true)
            @RequestParam(name = "file", required = true)
            MultipartFile[] files
    ) {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Type","text/html");

            for (MultipartFile file :files) {

                String fileName = TaleUtils.getFileKey(file.getOriginalFilename().replaceFirst("/", ""));

                MinioCloudService.upload(file, fileName);
                AttAchDomain attAchDomain = new AttAchDomain();
                HttpSession session = request.getSession();
                UserDomain sessionUser = (UserDomain) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
                attAchDomain.setAuthorId(sessionUser.getUid());
                attAchDomain.setFtype(TaleUtils.isImage(file.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType());
                attAchDomain.setFname(fileName);
                attAchDomain.setFkey(MinioCloudService.upload(file,fileName));
                attAchService.addAttAch(attAchDomain);
            }
            return APIResponse.success();

        } catch (IOException e) {
            e.printStackTrace();
            throw BusinessException.withErrorCode(ErrorConstant.Att.ADD_NEW_ATT_FAIL)
                    .withErrorMessageArguments(e.getMessage());
        }

    }

    @ApiOperation("删除文件")
    @PostMapping(value = "/delete")
    @ResponseBody
    public APIResponse deleteFileInfo(
            HttpServletRequest request,
            @ApiParam(name = "id", value = "文件主键", required = true)
            @RequestParam(name = "id", required = true)
            Integer id
    ) {
        try {
            AttAchDto attach = attAchService.getAttAchById(id);
            if (null == attach)
                throw BusinessException.withErrorCode(ErrorConstant.Att.DELETE_ATT_FAIL + ": 文件不存在");
            attAchService.deleteAttAch(id);
            // 写入日志
            logService.addLog(LogActions.DEL_ATTACH.getAction(),this.user(request).getUsername()+"用户",request.getRemoteAddr(),this.getUid(request));
            return APIResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            throw BusinessException.withErrorCode(e.getMessage());
        }
    }

}
