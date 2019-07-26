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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String fileUpload(MultipartFile file, List<String> roomGuids, String menuGuid) {

        String fileName = IdUtil.simpleUUID() + "_" + file.getOriginalFilename();
        File tarFile = tarFile = FileUtils.getFileByFileName(fileName);
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
            UploadFile uploadFile = new UploadFile();
            uploadFile.setGuid(fileName);
            uploadFile.setType(fileType);
            uploadFile.setUploadTime(System.currentTimeMillis());
            uploadFile.setUrl(FileUtils.getFileUrlPath(fileName));
            uploadFile.setMenuGuid(menuGuid);
            uploadFileMapper.insert(uploadFile);
            //文件资产关系入库
            List<RoomFile> roomFiles = roomGuids.stream().map(roomGuid -> {
                RoomFile roomFile = new RoomFile();
                roomFile.setFileGuid(fileName);
                roomFile.setRoomGuid(roomGuid);
                roomFile.setMenuGuid(menuGuid);
                return roomFile;
            }).collect(Collectors.toList());
            roomFileMapper.insertList(roomFiles);
        } catch (Exception e) {
            //log.warn("文件入库异常!", e);
            if (tarFile.exists()) {
                tarFile.delete();
            }
            throw new FileUploadException(file.getOriginalFilename(), e);
        }
        //返回文件名
        return fileName;
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
