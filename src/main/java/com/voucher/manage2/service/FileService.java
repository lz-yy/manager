package com.voucher.manage2.service;


import com.voucher.manage2.tkmapper.entity.UploadFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author lz
 * @Description: 文件操作
 * @Date: 2019/5/22 15:32
 **/

public interface FileService {

    @Transactional(rollbackFor = Exception.class)
    <T> UploadFile fileUpload(MultipartFile file, String menuGuid, T param);

    void delFile(String fileGuid);

}

