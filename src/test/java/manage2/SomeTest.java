package manage2;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.voucher.manage.daoModel.Room;
import com.voucher.manage2.dto.MenuDTO;
import com.voucher.manage2.dto.SysRouterDTO;
import com.voucher.manage2.dto.SysUserDTO;
import com.voucher.manage2.redis.JedisUtil0;
import com.voucher.manage2.service.UserService;
import com.voucher.manage2.tkmapper.entity.Select;
import com.voucher.manage2.tkmapper.entity.SysUser;
import com.voucher.manage2.tkmapper.mapper.MenuMapper;
import com.voucher.manage2.tkmapper.mapper.SelectMapper;
import com.voucher.manage2.tkmapper.mapper.SysRouterMapper;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.Weekend;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring.xml", "classpath:/spring-mybatis.xml"})
public class SomeTest {
    String REGEX = "item_";
    Pattern pattern = Pattern.compile(REGEX);
    @Autowired
    private SelectMapper selectMapper;
    @Autowired
    private SysRouterMapper sysRouterMapper;
    @Autowired
    private UserService userService;

    @Test
    public void addField() {
        JedisUtil0.setString("1", "1");
    }


    public void insertTable() throws ClassNotFoundException {
        //insert into [item_room] (guid,build_area,room_property,state)  values (?,?,?,?)]; nested exception is java.sql.SQLException: 列名 'build_area' 无效。
        String guid = "111aaaaaaaaa";
        Room room = new Room();
        room.setGuid(guid);
        room.setAddress("住址1");
        room.setBuild_area(22f);

        JSONArray jsonArray = new JSONArray();

        jsonArray.add("guid");
        jsonArray.add(guid);
        jsonArray.add("item_f63037de77771e05145221676250dc11");
        jsonArray.add("测试1的值");


        //currentDao.insertTable(room, jsonArray.toJSONString());
    }


    public void addExcel() throws IOException, BiffException {/*
        List<Object> list = new ArrayList<>();
        String fileName = "C:\\Users\\Administrator\\Desktop\\全市零售户";
        File[] files = new File(fileName).listFiles();
        for (File file : files) {
            getEexcl(file.getAbsolutePath(), list);
        }
        SystemConstant.out.println(list.size());*/
    }

    private <T> void getEexcl(String fileName, List<T> list) throws IOException, BiffException {
        File file = new File(fileName); // 创建文件对象
        System.out.println("???????????????????????????=" + fileName);
        Workbook wb = Workbook.getWorkbook(file); // 从文件流中获取Excel工作区对象（WorkBook）
        Sheet sheet = wb.getSheet(0); // 从工作区中取得页（Sheet）
        for (int i = 0; i < sheet.getRows(); i++) {
            if (i < 1) {
                continue;
            }
            for (int j = 0; j < sheet.getColumns(); j++) {
                if (j == 6) {
                    Cell cell = sheet.getCell(j, i);
                    if (cell.getContents() == null || "".equals(cell.getContents())) {
                        continue;
                    }
                    System.out.println("电话号码" + cell.getContents());
                }
                if (j == 7) {
                    Cell cell = sheet.getCell(j, i);
                    if (cell.getContents() == null || "".equals(cell.getContents())) {
                        continue;
                    }
                    System.out.println("身份证" + cell.getContents());
                }
            }
        }
    }
}
