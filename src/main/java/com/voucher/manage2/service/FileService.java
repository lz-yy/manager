package com.voucher.manage2.service;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author lz
 * @Description: 文件操作
 * @Date: 2019/5/22 15:32
 **/

public interface FileService {

    String fileUpload(MultipartFile file, List<String> roomGuids, String menuGuid);

    void delFile(String fileGuid);

}

