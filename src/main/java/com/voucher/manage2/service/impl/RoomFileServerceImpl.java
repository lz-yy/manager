package com.voucher.manage2.service.impl;

import com.voucher.manage2.service.AbstractFileService;
import com.voucher.manage2.tkmapper.entity.RoomFile;
import com.voucher.manage2.tkmapper.mapper.RoomFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lz
 * @description
 * @date 2019/7/25
 */
@Service
public class RoomFileServerceImpl extends AbstractFileService {
    @Autowired
    private RoomFileMapper roomFileMapper;

    @Override
    protected <T> void doAdditional(String menuGuid, String fileGuid, T param) {
        List<String> roomGuids;
        if (param instanceof List) {
            roomGuids = (List<String>) param;
            List<RoomFile> roomFiles = roomGuids.stream().map(roomGuid -> {
                RoomFile roomFile = new RoomFile();
                roomFile.setFileGuid(fileGuid);
                roomFile.setRoomGuid(roomGuid);
                roomFile.setMenuGuid(menuGuid);
                return roomFile;
            }).collect(Collectors.toList());
            roomFileMapper.insertList(roomFiles);
        }
    }
}
