package com.voucher.manage.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.voucher.manage.daoModel.*;
import com.voucher.manage.tools.MyTestUtil;
import com.voucher.manage2.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import com.voucher.manage.dao.CurrentDao;
import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.exception.BaseException;
import com.voucher.manage2.service.SysService;
import com.voucher.manage2.tkmapper.entity.SysUserCondition;
import com.voucher.manage2.utils.CommonUtils;
import com.voucher.manage2.utils.MapUtils;
import com.voucher.sqlserver.context.Connect;

@CrossOrigin
@RestController
@RequestMapping("/hireList")
public class HireListController {

    //ApplicationContext applicationContext = new Connect().get();
    //
    //CurrentDao currentDao = (CurrentDao) applicationContext.getBean("currentDao");
    @Autowired
    private CurrentDao currentDao;

    @Autowired
    private SysService sysService;
    
    @RequestMapping("getChartInfoList")
    public Object getChartInfoList(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, SQLException {
        ChartInfo chartInfo = new ChartInfo();
        System.out.println("jsonMap=" + jsonMap);
        Integer limit = MapUtils.getInteger("limit", jsonMap);
        Integer offset = MapUtils.getInteger("offset", jsonMap);
        chartInfo.setLimit(limit);
        chartInfo.setOffset(offset);
        chartInfo.setNotIn("id");

        List<String> searchList = new ArrayList<>();

        searchList.add("del=");
        searchList.add("0");

        String chartGUID = MapUtils.getString("chartGUID", jsonMap);

        if (chartGUID != null) {
            searchList.add("[ChartInfo].ChartGUID=");
            searchList.add(chartGUID);
        }

        String[] where = new String[searchList.size()];
        if (where.length > 0)
            chartInfo.setWhere(searchList.toArray(where));

        Map map = null;
        try {
            map = currentDao.selectTable(chartInfo, "ChartGUID");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;

    }

    @RequestMapping("getChartInfoRoom")
    public Object getChartInfo(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, SQLException {

    	long startTime = System.currentTimeMillis();
    	
		Integer limit = MapUtils.getInteger("limit", jsonMap);
		Integer offset = MapUtils.getInteger("offset", jsonMap);

        ChartInfo chartInfo = new ChartInfo();
        chartInfo.setLimit(limit);
        chartInfo.setOffset(offset);
        chartInfo.setNotIn("id");

        ChartRoom chartRoom = new ChartRoom();
        chartRoom.setLimit(limit);
        chartRoom.setOffset(offset);
        chartRoom.setNotIn("id");

        Room room = new Room();
        room.setLimit(limit);
        room.setOffset(offset);
        room.setNotIn("id");

        List<String> searchList = new ArrayList<>();

        searchList.add("[ChartInfo].del=");
        searchList.add("0");

        SysUserDTO currentUser = CommonUtils.getCurrentUser();
        
        searchList.add("company_guid =");
        searchList.add("1");
        
        searchList.add("address like");
        searchList.add("%大北街%");
        
        String chartGUID = MapUtils.getString("chartGUID", jsonMap);

        if (chartGUID != null) {
            searchList.add("[ChartInfo].ChartGUID=");
            searchList.add(chartGUID);
        }
        String ContractNo = MapUtils.getString("ContractNo", jsonMap);

        if (ContractNo != null) {
            searchList.add("[ChartInfo].ContractNo like");
            searchList.add("%" + ContractNo + "%");
        }

        String guid = MapUtils.getString("guid", jsonMap);

        if (guid != null) {
            searchList.add("[ChartRoom].guid=");
            searchList.add(guid);
        }

        String Charter = MapUtils.getString("Charter", jsonMap);

        if (Charter != null) {
            searchList.add("[ChartRoom].Charter like");
            searchList.add("%" + Charter + "%");
        }

        String Phone = MapUtils.getString("Phone", jsonMap);

        if (Phone != null) {
            searchList.add("[ChartRoom].Phone like");
            searchList.add("%" + Phone + "%");
        }
        String IDNo = MapUtils.getString("IDNo", jsonMap);

        if (IDNo != null) {
            searchList.add("[ChartRoom].IDNo like");
            searchList.add("%" + IDNo + "%");
        }


        String[] where = new String[searchList.size()];
        if (where.length > 0) {
            chartInfo.setWhere(searchList.toArray(where));
            chartRoom.setWhere(searchList.toArray(where));
            room.setWhere(searchList.toArray(where));
        }

        Object[] objects = {chartRoom, chartInfo, room};

        String[][] joinParameters = {{"ChartGUID", "ChartGUID"}, {"guid", "guid"}, {"guid", "guid"}};

        String[] itemjoinParameters = {"ChartGUID", "guid", "guid"};

        Map map = null;
        try {
            map = currentDao.selectTable(objects, joinParameters, itemjoinParameters);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long excTime = endTime - startTime;
        System.out.println("当前调用方法为getChartInfoRoom");
        System.out.println("执行时间:" + excTime);
        
        return map;

    }

    @RequestMapping("getHireList")
    public Object getHireList(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, SQLException {
        Integer limit = MapUtils.getInteger("limit", jsonMap);
        Integer offset = (MapUtils.getInteger("offset", jsonMap) - 1) * limit;
        String state = MapUtils.getString("state", jsonMap);
        HireList hireList = new HireList();
        hireList.setLimit(limit);
        hireList.setOffset(offset);
        hireList.setNotIn("id");

        List<String> searchList = new ArrayList<>();

		if (state != null) {
			searchList.add("state=");
			searchList.add(state);
		}
        searchList.add("del=");
        searchList.add("0");

        String chartGUID = MapUtils.getString("ChartGUID", jsonMap);

        if (chartGUID != null) {
            searchList.add("ChartGUID=");
            searchList.add(chartGUID);
        }

        String[] where = new String[searchList.size()];
        if (where.length > 0) {
            hireList.setWhere(searchList.toArray(where));
        }

        Map map = null;
        try {
            map = currentDao.selectTable(hireList, "HireGUID");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;

    }

    @RequestMapping("getHirePay")
    public Object getHirePay(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, SQLException {
        Integer limit = MapUtils.getInteger("limit", jsonMap);
        Integer offset = (MapUtils.getInteger("offset", jsonMap) - 1) * limit;
        String chartGUID = MapUtils.getString("ChartGUID", jsonMap);
        HirePay hirePay = new HirePay();
        hirePay.setLimit(limit);
        hirePay.setOffset(offset);
        hirePay.setNotIn("id");

        List<String> searchList = new ArrayList<>();

        searchList.add("ChartGUID=");
        searchList.add(chartGUID);
        searchList.add("del=");
        searchList.add("0");


        String[] where = new String[searchList.size()];
        if (where.length > 0)
            hirePay.setWhere(searchList.toArray(where));

        Map map = null;
        try {
            map = currentDao.selectTable(hirePay, "HirePayGUID");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;

    }

    @RequestMapping("addField")
    public Integer addField(@RequestParam String tableName, @RequestBody Map<String, Object> jsonMap) {
        Map<Integer, String> selects = null;
        String fieldName = MapUtils.getString("title", jsonMap);
        Integer type = MapUtils.getInteger("type", jsonMap);
        List<LinkedHashMap<String, Object>> domains = (List<LinkedHashMap<String, Object>>) jsonMap.get("domains");
        if (ObjectUtils.isNotEmpty(domains)) {
            selects = new HashMap<>();
            int i = 1;
            for (LinkedHashMap<String, Object> domain : domains) {
                selects.put(i++, MapUtils.getString("value", domain));
            }
        }
        return currentDao.addField(tableName, fieldName, type, selects, null);
    }

    @RequestMapping("delField")
    public Integer delField(@RequestParam String tableName, @RequestParam String lineName, @RequestParam String line_uuid) throws BaseException {
        return currentDao.alterTable(false, tableName, lineName, null, line_uuid);
    }

    @RequestMapping("recycleField")
    public Integer recycleField(String line_uuid) throws BaseException {
        return currentDao.recycleField(line_uuid);
    }

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


      

            searchList.add("state =");
            searchList.add("7");
 
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
            searchList.add("1");
  
            searchList.add("address like");
            searchList.add("%大北街%");

        searchList.add("del=");
        searchList.add("0");
        MyTestUtil.print(searchList);
        String[] where = new String[searchList.size()];
        room.setWhere(searchList.toArray(where));
        room.setWhereTerm("or");
        return currentDao.selectTable(room, "guid");
    }
}
