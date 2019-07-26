package com.voucher.manage2.service.impl;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.voucher.manage2.constant.MenuConstant;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.exception.FileUploadException;
import com.voucher.manage2.constant.FileConstant;
import com.voucher.manage2.service.FileService;
import com.voucher.manage2.tkmapper.entity.RoomFile;
import com.voucher.manage2.tkmapper.entity.UploadFile;
import com.voucher.manage2.tkmapper.mapper.RoomFileMapper;
import com.voucher.manage2.tkmapper.mapper.UploadFileMapper;
import com.voucher.manage2.utils.FileUtils;
import com.voucher.manage2.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilterReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.voucher.manage2.tkmapper.entity.Menu;
import com.voucher.manage2.tkmapper.mapper.MenuMapper;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author lz
 * @description
 * @date 2019/5/20
 */
@SuppressWarnings("ALL")
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private RoomFileMapper roomFileMapper;
    @Autowired
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String roomFileUpload(MultipartFile file, List<String> roomGuids, String menuGuid) {
        UploadFile uploadFile = fileService.fileUpload(file, menuGuid);
        if (ObjectUtils.isNotEmpty(uploadFile)) {
            List<RoomFile> roomFiles = roomGuids.stream().map(roomGuid -> {
                RoomFile roomFile = new RoomFile();
                roomFile.setFileGuid(uploadFile.getGuid());
                roomFile.setRoomGuid(roomGuid);
                roomFile.setMenuGuid(menuGuid);
                return roomFile;
            }).collect(Collectors.toList());
            roomFileMapper.insertList(roomFiles);
            //返回文件名
            return uploadFile.getGuid();
        }
        return null;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadFile fileUpload(MultipartFile file, String menuGuid) {
        String fileGuid = IdUtil.simpleUUID() + "_" + file.getOriginalFilename();
        File tarFile = tarFile = FileUtils.getFileByFileName(fileGuid);
        UploadFile uploadFile = new UploadFile();
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

            uploadFile.setGuid(fileGuid);
            uploadFile.setType(fileType);
            uploadFile.setUploadTime(System.currentTimeMillis());
            uploadFile.setUrl(FileUtils.getFileUrlPath(fileGuid));
            uploadFile.setMenuGuid(menuGuid);
            uploadFileMapper.insert(uploadFile);
        } catch (Exception e) {
            log.warn("文件入库异常!", e);
            if (tarFile.exists()) {
                tarFile.delete();
            }
            throw new FileUploadException(file.getOriginalFilename(), e);
        }
        return uploadFile;
    }
}
