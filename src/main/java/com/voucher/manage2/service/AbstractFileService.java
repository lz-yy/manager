package com.voucher.manage2.service;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.exception.FileUploadException;
import com.voucher.manage2.tkmapper.entity.RoomFile;
import com.voucher.manage2.tkmapper.entity.UploadFile;
import com.voucher.manage2.tkmapper.mapper.RoomFileMapper;
import com.voucher.manage2.tkmapper.mapper.UploadFileMapper;
import com.voucher.manage2.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import tk.mybatis.mapper.entity.Example;

/**
 * @author lz
 * @description
 * @date 2019/5/20
 */
@SuppressWarnings("ALL")
@Slf4j
public abstract class AbstractFileService implements FileService {
    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private RoomFileMapper roomFileMapper;

    protected abstract <T> void doAdditional(String menuGuid, String fileGuid, T param);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> UploadFile fileUpload(MultipartFile file, String menuGuid, T param) {
        String fileName = IdUtil.simpleUUID() + "_" + file.getOriginalFilename();
        File tarFile = tarFile = FileUtils.getFileByFileName(fileName);
        UploadFile uploadFile = null;
        try {
            //文件后缀名
            String suffixName = FileTypeUtil.getType(file.getInputStream());
            if (suffixName == null) {
                BaseException.getDefault("不支持此类型文件");
            }
            //文件类型
            Integer fileType = FileUtils.getFileType(suffixName);
            //保存
            //将保存的文件对象
            //SystemConstant.out.println("+++++++++" + tarPath);
            file.transferTo(tarFile);
            //TODO 类型是图片就压缩
            //if (FileUtils.isImage(type)) {
            //
            //}
            //文件入库
            uploadFile = new UploadFile();
            uploadFile.setGuid(fileName);
            uploadFile.setType(fileType);
            uploadFile.setUploadTime(System.currentTimeMillis());
            uploadFile.setUrl(FileUtils.getFileUrlPath(fileName));
            uploadFile.setMenuGuid(menuGuid);
            uploadFileMapper.insert(uploadFile);
            doAdditional(menuGuid, fileName, param);
        } catch (Exception e) {
            //log.warn("文件入库异常!", e);
            if (tarFile.exists()) {
                tarFile.delete();
            }
            throw new FileUploadException(file.getOriginalFilename(), e);
        }
        //返回文件名
        return uploadFile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFile(String fileGuid) {
        Example example1 = new Example(UploadFile.class);
        example1.createCriteria().andEqualTo("guid", fileGuid);
        uploadFileMapper.deleteByExample(example1);

        Example example = new Example(RoomFile.class);
        example.createCriteria().andEqualTo("fileGuid", fileGuid);
        roomFileMapper.deleteByExample(example);

        File file = FileUtils.getFileByFileName(fileGuid);
        if (file.exists()) {
            file.delete();
        }
    }
}
