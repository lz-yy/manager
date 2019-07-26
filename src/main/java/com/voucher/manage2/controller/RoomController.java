package com.voucher.manage2.controller;

import cn.hutool.core.util.IdUtil;
import com.voucher.manage.dao.CurrentDao;
import com.voucher.manage.daoModel.Room;
import com.voucher.manage.tools.MyTestUtil;
import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.service.RoomService;
import com.voucher.manage2.service.SysService;
import com.voucher.manage2.tkmapper.entity.RoomIn;
import com.voucher.manage2.tkmapper.entity.RoomOut;
import com.voucher.manage2.tkmapper.entity.SysUserCondition;
import com.voucher.manage2.utils.CommonUtils;
import com.voucher.manage2.utils.ObjectUtils;
import com.voucher.manage2.constant.ResultConstant;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.tkmapper.entity.Select;
import com.voucher.manage2.utils.MapUtils;

import com.voucher.manage2.utils.RoomUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/room")
public class RoomController {


    @Autowired
    private CurrentDao currentDao;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SysService sysService;

    @RequestMapping("getList")
    public Object getList(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, SQLException {

        Room room = new Room();
        Integer limit = MapUtils.getInteger("limit", jsonMap);
        room.setLimit(limit);
        room.setOffset(((MapUtils.getInteger("page", jsonMap)) - 1) * limit);
        room.setNotIn("id");
        Map<String, Object> query = MapUtils.getStrMap("query", jsonMap);
        Object searchContent = query.get("searchContent");
        Object state = query.get("state");
        Object neaten_flow = query.get("neaten_flow");
        String roomGuid = MapUtils.getString("roomGuid", jsonMap);

        List<String> searchList = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(searchContent)) {
            searchList.add("address like");
            searchList.add("%" + searchContent + "%");
        }
        if (ObjectUtils.isNotEmpty(state)) {
            searchList.add("state =");
            searchList.add(state.toString());
        }
        if (ObjectUtils.isNotEmpty(neaten_flow)) {
            searchList.add("neaten_flow =");
            searchList.add(neaten_flow.toString());
        }
        if (ObjectUtils.isNotEmpty(roomGuid)) {
            searchList.add("room.guid =");
            searchList.add(roomGuid);
        }

        SysUserDTO currentUser = CommonUtils.getCurrentUser();
       
            searchList.add("company_guid =");
            searchList.add(currentUser.getCompanyGuid());
  

        List<String> userConditonList = new ArrayList<>();
        List<SysUserCondition> sysUserConditionList = sysService.getUserConditionsByUserGuid(currentUser.getGuid());
        if (ObjectUtils.isNotEmpty(sysUserConditionList)) {
            for (SysUserCondition sysUserCondition : sysUserConditionList) {
                userConditonList.add(sysUserCondition.getLineUuid() + "=");
                userConditonList.add(sysUserCondition.getLineValue() + "");
            }
        }
        searchList.addAll(userConditonList);
        searchList.add("del=");
        searchList.add("0");
        MyTestUtil.print(searchList);
        String[] where = new String[searchList.size()];
        room.setWhere(searchList.toArray(where));
        room.setWhereTerm("or");
        return currentDao.selectTable(room, "guid");
    }

    @RequestMapping("updateFieldName")
    public Integer updateTable(String line_uuid, String value) {
        return currentDao.updateTable("item_room", line_uuid, value);
    }

    @RequestMapping("updateRoom")
    public Integer updateItmeTable(@RequestBody Map<String, Object> jsonMap) throws InvocationTargetException, IllegalAccessException {
        return currentDao.updateItmeTable(jsonMap);
    }

    @RequestMapping("delRoom")
    public Integer delItmeTable(String guid) {
        return currentDao.delItmeTable(guid);
    }


    @RequestMapping("recycleRoom")
    public Integer recycleRoom(@RequestBody List<String> guidList) {
        //回收逻辑删除
        return currentDao.recycleRoom(guidList);
    }

    @RequestMapping("addField")
    public Integer addField(@RequestBody Map<String, Object> jsonMap) {
        Map<Integer, String> selects = null;
        String fieldName = MapUtils.getString("title", jsonMap);
        Integer roomType = MapUtils.getInteger("roomType", jsonMap);
        Integer filedType = MapUtils.getInteger("rowType", jsonMap);
        List<LinkedHashMap<String, Object>> domains = (List<LinkedHashMap<String, Object>>) jsonMap.get("domains");
        if (ObjectUtils.isNotEmpty(domains)) {
            selects = new HashMap<>();
            int i = 1;
            for (LinkedHashMap<String, Object> domain : domains) {
                selects.put(i++, MapUtils.getString("value", domain));
            }
        }
        //fieldName = "ccc";
        String company="";
        
        SysUserDTO currentUser = CommonUtils.getCurrentUser();
        
        if(currentUser!=null){
        	company=currentUser.getCompanyGuid();
        }
        return currentDao.addField("item_room"+company, fieldName, filedType, selects, roomType);
    }

    @RequestMapping("delField")
    public Integer delField(String line_uuid) {
        return currentDao.alterTable(false, "item_room", "guid", null, line_uuid);
    }

    @RequestMapping("recycleField")
    public Integer recycleField(String line_uuid) throws BaseException {
        //王靖代码不支持逻辑删除,废弃
        return currentDao.recycleField(line_uuid);
    }

    @RequestMapping("insertResource")
    public Integer insertResource(@RequestBody Map<String, Object> jsonMap) throws InvocationTargetException, IllegalAccessException {
        return currentDao.insertResource(jsonMap);
    }

    @RequestMapping("updateSelect")
    public Integer updateSelect(@RequestBody Map<String, Object> jsonMap) {
        System.out.println(jsonMap);
        Map<String, String> domains = (Map<String, String>) jsonMap.get("domains");
        String line_uuid = MapUtils.getString("line_uuid", jsonMap);
        if (ObjectUtils.isEmpty(domains, line_uuid)) {
            return ResultConstant.FAILED;
        }
        List<Select> selects = new ArrayList<>();
        for (Map.Entry<String, String> entry : domains.entrySet()) {
            Select select = new Select();
            select.setLineUuid(line_uuid);
            select.setValue(Integer.valueOf(entry.getKey()));
            select.setName(entry.getValue());
            selects.add(select);
        }
        return currentDao.updateSelect(selects);
    }

    @RequestMapping("updateTextLength")
    public Integer updateTextLength(@RequestBody Map<String, Object> jsonMap) {
        String line_uuid = MapUtils.getString("line_uuid", jsonMap);
        Integer text_length = MapUtils.getInteger("text_length", jsonMap);
        if (ObjectUtils.isEmpty(line_uuid, text_length)) {
            return ResultConstant.FAILED;
        }
        return currentDao.updateTextLength("item_room", line_uuid, text_length);
    }

    @RequestMapping("roomIn")
    public Integer RoomIn(@RequestBody Map<String, Object> jsonMap) throws InvocationTargetException, IllegalAccessException {
        List<String> roomGuids = MapUtils.getStrList("roomGuids", jsonMap);
        if (ObjectUtils.isEmpty(roomGuids)) {
            throw BaseException.getDefault("roomGuids为空");
        }
        List<RoomIn> roomIns = new ArrayList<>();
        for (String roomGuid : roomGuids) {
            RoomIn roomIn = new RoomIn();
            BeanUtils.populate(roomIn, jsonMap);
            roomIn.setGuid(IdUtil.simpleUUID());
            roomIn.setRoomGuid(roomGuid);
            roomIns.add(roomIn);
        }
        return roomService.roomIn(roomIns);
    }

    @RequestMapping("roomOut")
    public Object roomOut(@RequestBody Map<String, Object> jsonMap) throws InvocationTargetException, IllegalAccessException {
        //不可出租,空置的才可以出账
        List<String> roomGuids = new ArrayList<>();
        List<String> faildRoomGuids = new ArrayList<>();
        List<Map<String, Object>> rooms = (List<Map<String, Object>>) jsonMap.get("rooms");
        for (Map<String, Object> room : rooms) {
            String guid = MapUtils.getString("guid", room);
            if (RoomUtils.checkRoomOutByState(MapUtils.getInteger("state", room))) {
                roomGuids.add(guid);
            } else {
                faildRoomGuids.add(guid);
            }
        }

        if (ObjectUtils.isNotEmpty(roomGuids)) {
            List<RoomOut> roomOuts = new ArrayList<>();
            for (String roomGuid : roomGuids) {
                RoomOut roomOut = new RoomOut();
                BeanUtils.populate(roomOut, jsonMap);
                roomOut.setGuid(IdUtil.simpleUUID());
                roomOut.setRoomGuid(roomGuid);
                roomOuts.add(roomOut);
            }
            roomService.roomOut(roomOuts);
        }
        return faildRoomGuids;
    }

    @RequestMapping("test")
    public void test() {

        //currentDao.addField("item_room", "aa" + DateUtil.now(), 1, null);
    }
}
