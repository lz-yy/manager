package com.voucher.manage.controller;

import com.voucher.manage.dao.CurrentDao;
import com.voucher.manage.dao.HireDAO;
import com.voucher.manage.daoModel.ChartInfo;
import com.voucher.manage.daoModel.ChartRoom;
import com.voucher.manage.daoModel.HireList;
import com.voucher.manage.daoModel.HirePay;
import com.voucher.manage.tools.MonthDiff;
import com.voucher.manage.tools.MyTestUtil;
import com.voucher.manage2.utils.MapUtils;
import com.voucher.manage2.utils.ObjectUtils;
import com.voucher.manage2.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/hire")
public class HireController {

    @Autowired
    private CurrentDao currentDao;
    @Autowired
    private HireDAO hireDao;

    @RequestMapping("/action")
    public Object action(@RequestBody Map<String, Object> jsonMap) throws ClassNotFoundException, ParseException {

        String contractNo = MapUtils.getString("contractNo", jsonMap);


        String Charter = MapUtils.getString("Charter", jsonMap);

        int sex = 1;

        try {
            sex = MapUtils.getString("sex", jsonMap).equals("男") ? 1 : 0;
        } catch (Exception e) {

        }
        String CardType = MapUtils.getString("CardType", jsonMap);

        String IDNo = MapUtils.getString("IDNo", jsonMap);

        String Phone = MapUtils.getString("Phone", jsonMap);


        int month = 0;

        Date chartBeginDate = null;

        Date chartEndDate = null;

        try {
            month = MapUtils.getInteger("month", jsonMap);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        try {
            List<Long> chartDate = (List<Long>) jsonMap.get("chartDate");
            String sDate = TimeUtils.formatTime(chartDate.get(0), TimeUtils.exp1);
            String eDate = TimeUtils.formatTime(chartDate.get(1), TimeUtils.exp1);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //
            chartBeginDate = format.parse(sDate);
            chartEndDate = format.parse(eDate);

            month = MonthDiff.get(chartBeginDate, chartEndDate);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        Integer augment = null;

        Float increment = null;

        Integer augmentGenre = null;

        String augmentDate = null;

        Integer relet = null;

        Float margin = null;

        String oldChartGUID = null;

        try {

            augment = MapUtils.getInteger("augment", jsonMap);
            increment = Float.valueOf(MapUtils.getString("increment", jsonMap));
            augmentGenre = MapUtils.getInteger("augmentGenre", jsonMap);
            augmentDate = MapUtils.getString("augmentDate", jsonMap);
            margin = MapUtils.getFloat("margin", jsonMap);
            relet = MapUtils.getInteger("augmentGenre", jsonMap);
            oldChartGUID = MapUtils.getString("augmentDate", jsonMap);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        Date date = new Date();

        String ChartGUID = UUID.randomUUID().toString();

        ChartInfo chartInfo = new ChartInfo();

        chartInfo.setChartGUID(ChartGUID);
        chartInfo.setContractNo(contractNo);
        if (chartBeginDate != null) {
            chartInfo.setConcludeDate(chartBeginDate);
            chartInfo.setChartBeginDate(chartBeginDate);
        } else {
            chartInfo.setConcludeDate(date);
            chartInfo.setChartBeginDate(date);
        }
        if (chartEndDate != null) {
            chartInfo.setChartEndDate(chartEndDate);
        }


        if (margin != null) {
            chartInfo.setMargin(margin);
        }

        if (oldChartGUID != null) {
            chartInfo.setRelet(1);
            chartInfo.setOldChartGUID(oldChartGUID);
        }
        chartInfo.setChartMothon(month);
        chartInfo.setIsHistory(0);

        if (augment != null) {
            chartInfo.setAugment(augment);
            chartInfo.setIncrement(increment);
            if (augmentGenre != null) {
                chartInfo.setAugmentGenre(augmentGenre);
                DateFormat fmt = new SimpleDateFormat("yyyy-MM");
                Date augmentTime = fmt.parse(augmentDate);
                chartInfo.setAugmentDate(augmentTime);
            } else {
                chartInfo.setAugmentGenre(0);
            }
        }


        float allChartAreas = 0;

        float allHires = 0;
        List chartRooms = new ArrayList<>();
        List<Map<String, Object>> assetData = (List<Map<String, Object>>) jsonMap.get("assetData");
        if (ObjectUtils.isNotEmpty(assetData)) {
            for (Map<String, Object> asset : assetData) {
                ChartRoom chartRoom = new ChartRoom();
                chartRoom.setChartArea(MapUtils.getFloat("build_area", asset));
                chartRoom.setChartGUID(ChartGUID);
                chartRoom.setGuid(MapUtils.getString("guid", asset));
                chartRoom.setCharter(Charter);
                chartRoom.setSex(sex);
                chartRoom.setCardType(CardType);
                chartRoom.setIDNo(IDNo);
                chartRoom.setPhone(Phone);
                chartRooms.add(chartRoom);
                Float hire = MapUtils.getFloat("hire", asset);
                allHires = allHires + hire;
                chartRoom.setHire(hire);
                allChartAreas = allChartAreas + chartRoom.getChartArea();
            }
        }


        chartInfo.setAllHire(allHires);
        chartInfo.setAllChartArea(allChartAreas);
        chartInfo.setDel(0);
        
        return hireDao.insertHire(chartInfo, chartRooms);

    }

    @RequestMapping("/insertHirePay")
    public Integer insertHirePay(@RequestBody List<String> guid) {
        List<HireList> hireLists = new ArrayList<>();
        for (String hireGuid : guid) {
            System.out.println("hireGuidhireGuid" + hireGuid);
            HireList hireList = new HireList();
            hireList.setHireGUID(hireGuid);
            hireLists.add(hireList);
        }

        MyTestUtil.print(hireLists);
        return hireDao.insertHirePay(hireLists);
    }


    @RequestMapping("/refundHirePay")
    public void refundHirePay(@RequestBody List<String> hirePayGUIDs) {
        List<HirePay> HirePays = new ArrayList<>();
        List<HireList> HireLists = new ArrayList<>();
        for (String hirePayGUID : hirePayGUIDs) {
            HirePay hirePay = new HirePay();
            HireList hireList = new HireList();
            hireList.setState(0);
            hireList.setHirePayGUID(hirePayGUID);
            String[] where = {"hirePayGUID=", hirePayGUID};
            hireList.setWhere(where);
            HireLists.add(hireList);

            hirePay.setDel(1);
            hirePay.setWhere(where);
            HirePays.add(hirePay);
        }
        MyTestUtil.print(HirePays);
        hireDao.refundHirePay(HirePays, HireLists);
    }

}