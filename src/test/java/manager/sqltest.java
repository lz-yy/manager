package manager;

import com.alibaba.fastjson.JSONArray;
import com.voucher.manage.dao.AssetsDAO;
import com.voucher.manage.dao.CurrentDao;
import com.voucher.manage.daoModel.RoomInfo;
import com.voucher.manage.daoModel.Test;
import com.voucher.manage.tools.MyTestUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class sqltest {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ClassPathXmlApplicationContext applicationContext=new ClassPathXmlApplicationContext("spring-sqlservers.xml");
		
		AssetsDAO assetsDAO=(AssetsDAO) applicationContext.getBean("assetsdao");
		
		CurrentDao currentDao=(CurrentDao) applicationContext.getBean("currentDao");
		
		//assetsDAO.createTable("test");
		
		//assetsDAO.alterTable(false,"test", "bbb","item_c6394475c5e3e7de6bbd51ef337f33c0");
		
		//SystemConstant.out.println("i="+i);
		
		RoomInfo roomInfo=new RoomInfo();
		
		roomInfo.setLimit(10);
		roomInfo.setOffset(0);
		roomInfo.setNotIn("guid");
		
		Test test=new Test();
		
		test.setGuid("aaa");
		//test.setAddress("nnn");
		
		test.setLimit(10);
		test.setOffset(0);
		test.setNotIn("id");
		
		String[] where={"address=","nnn"};
		
		test.setWhere(where);
		/*
		try {
			Map map=NewSelectSqlJoin.get(roomInfo);
			MyTestUtil.print(map);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		//currentDao.alterTable(false,"test", "bbb","item_64a463f9c06363093b92e6a79dc28877");
		
		JSONArray jsonArray=new JSONArray();
		
		jsonArray.add("guid");
		jsonArray.add("aaa");
		jsonArray.add("item_cf59eb00a4b18cedbb430884494028e2");
		jsonArray.add("nnn");
		
		//currentDao.insertTable(test, jsonArray.toJSONString());
		
		Map map=currentDao.selectTable(test,"guid");
		
		List rows=(List) map.get("rows");
		
		MyTestUtil.print(rows);
		
	}
	
}
