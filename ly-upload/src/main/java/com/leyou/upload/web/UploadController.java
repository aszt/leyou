package com.leyou.upload.web;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 默认情况下，所有的请求经过Zuul网关的代理，默认会通过SpringMVC预先对请求进行处理，缓存。
 * 普通请求并不会有什么影响，但是对于文件上传，就会造成不必要的网络负担，
 * 在高并发时，可能导致网络堵塞，Zuul网关不可用，这样我们的整个系统就瘫痪了。
 * 所以，我们上传文件的请求需要绕过请求的缓存，直接通过路由到达目标微服务
 * 官方推荐在地址前面加上/zuul,在不改变请求地址的情况下，去修改nginx对地址进行重写
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传图片
     *
     * @param file 文件
     * @return
     */
    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadService.upload(file));
    }
}
