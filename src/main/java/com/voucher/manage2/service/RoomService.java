package com.voucher.manage2.service;

import com.voucher.manage2.dto.RoomLogDTO;
import com.voucher.manage2.tkmapper.entity.RoomIn;
import com.voucher.manage2.tkmapper.entity.RoomOut;

import java.util.List;

/**
 * @author lz
 * @description 资产操作
 * @date 2019/6/3
 */
public interface RoomService {
    /**
     * @Author lz
     * @Description: 资产入账
     * @param: []
     * @return: {java.lang.Integer}
     * @Date: 2019/6/3 15:36
     **/
    Integer roomIn(List<RoomIn> roomIns);

    /**
     * @Author lz
     * @Description: 出账
     * @param: [roomOuts]
     * @return: {java.lang.Integer}
     * @Date: 2019/6/5 15:19
     **/
    Integer roomOut(List<RoomOut> roomOuts);

    List<RoomLogDTO> getLogByRoomGuid(String roomGuid);
}
