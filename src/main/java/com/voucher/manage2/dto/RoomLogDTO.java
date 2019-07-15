package com.voucher.manage2.dto;

import com.voucher.manage2.tkmapper.entity.RoomLog;

import javax.persistence.Table;

/**
 * @author lz
 * @description
 * @date 2019/7/15
 */
@Table(name = "[room_log]")
public class RoomLogDTO extends RoomLog {
    private MenuDTO menuDTO;

    public MenuDTO getMenuDTO() {
        return menuDTO;
    }

    public void setMenuDTO(MenuDTO menuDTO) {
        this.menuDTO = menuDTO;
    }
}
