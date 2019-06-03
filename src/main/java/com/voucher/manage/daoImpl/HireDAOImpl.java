package com.voucher.manage.daoImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.voucher.manage.dao.HireDAO;
import com.voucher.manage.daoModel.ChartInfo;
import com.voucher.manage.daoModel.ChartRoom;
import com.voucher.manage.daoModel.HireList;
import com.voucher.manage.daoModel.Room;
import com.voucher.manage.daoModelJoin.ChartInfo_ChartRoom;
import com.voucher.manage.daoSQL.InsertExe;
import com.voucher.manage.daoSQL.SelectExe;
import com.voucher.manage.daoSQL.SelectJoinExe;
import com.voucher.manage.daoSQL.SelectSqlJoinExe;
import com.voucher.manage.daoSQL.UpdateExe;
import com.voucher.manage.tools.MyTestUtil;

public class HireDAOImpl extends JdbcDaoSupport implements HireDAO{

	@Override
	public Room getRoomByGUID(String guid) {
		// TODO Auto-generated method stub
		
		Room room=new Room();
		
		room.setLimit(1);
		room.setOffset(0);
		room.setNotIn("id");
		
		String[] where={"guid=",guid};
		
		room.setWhere(where);
		
		try{
			List list=SelectExe.get(this.getJdbcTemplate(), room);
			room=(Room) list.get(0);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return room;
	}
	
	@Override
	public Integer insertHire(ChartInfo chartInfo, List<ChartRoom> chartRooms) {
		// TODO Auto-generated method stub
		System.out.println("chartInfo=");
		MyTestUtil.print(chartInfo);
		
		ChartInfo chartInfo2=new ChartInfo();
		
		chartInfo2.setLimit(1000);
		chartInfo2.setOffset(0);
		chartInfo2.setNotIn("id");
		
		ChartRoom chartRoom2=new ChartRoom();
		
		chartRoom2.setLimit(1000);
		chartRoom2.setOffset(0);
		chartRoom2.setNotIn("id");
		
		int i=0;
		
		Iterator<ChartRoom> iteratorCrooms=chartRooms.iterator();
		
		while (iteratorCrooms.hasNext()) {

			ChartRoom chartRoom = iteratorCrooms.next();

			String[] where0 = { "IsHistory=", "0", "guid=", chartRoom.getGuid() };

			chartInfo2.setWhere(where0);

			chartRoom2.setWhere(where0);

			Object[] objects = { chartInfo2, chartRoom2 };

			ChartInfo_ChartRoom chartInfo_ChartRoom = new ChartInfo_ChartRoom();

			String[][] joinParames = { { "ChartGUID", "ChartGUID" } };

			List list0 = SelectJoinExe.get(this.getJdbcTemplate(), objects, chartInfo_ChartRoom, joinParames);

			Iterator iterator = list0.iterator();

			float already = 0;

			while (iterator.hasNext()) {

				ChartInfo_ChartRoom chartInfo_ChartRoom2 = (ChartInfo_ChartRoom) iterator.next();

				already = already + chartInfo_ChartRoom2.getChartArea();

				MyTestUtil.print(chartInfo_ChartRoom2);

				System.out.println("already=" + already);
			}

			Room room = new Room();

			room.setLimit(1);
			room.setOffset(0);
			room.setNotIn("id");

			String[] where = { "guid=", chartRoom.getGuid(),"del=","0"};

			room.setWhere(where);
			
			List list = SelectExe.get(this.getJdbcTemplate(), room);

			Room room2 = (Room) list.get(0);

			System.out.println("getBuild_area="+room2.getBuild_area() +" ready= "+already +" chart= "+ chartRoom.getChartArea());
			System.out.println(chartRoom.getChartArea());
			if ((room2.getBuild_area() - already - chartRoom.getChartArea()) >= 0&&chartRoom.getChartArea()>0) {

				i=InsertExe.get(this.getJdbcTemplate(), chartRoom);
				
				if (i < 1) {
		            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		        }
				
				if(room2.getBuild_area()-chartRoom.getChartArea()>=0){
				
					room.setState(0);
					
					i=UpdateExe.get(this.getJdbcTemplate(), room);
					
					if (i < 1) {
			            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			        }
					
				}else if(room2.getBuild_area()-chartRoom.getChartArea()<=0){
					
					room.setState(1);
					
					i=UpdateExe.get(this.getJdbcTemplate(), room);
					
					if (i < 1) {
			            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			        }
				}
				
			} else {

				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				
				return -1;

			}

		}
		
		i=InsertExe.get(this.getJdbcTemplate(), chartInfo);
		
		if (i < 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
		
		
		Date date=new Date();
		
		boolean augment=false;
		
		if(chartInfo.getAugment()!=null&&chartInfo.getAugment()>0){
			augment=true;
		}
		
		int augmentGenre=0;
		
		Date augmentDate=null;
		
		if(chartInfo.getAugmentGenre()!=null){
			augmentGenre=chartInfo.getAugmentGenre();
			augmentDate=chartInfo.getAugmentDate();
		}
		
		float allHire=chartInfo.getAllHire();
		
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		
		int month = cal.get(Calendar.MONTH )+1;
		
		int end=0;
		
		if(chartInfo.getChartMothon()>0){
			end=chartInfo.getChartMothon();
		}
		
		for(int n=0;n<chartInfo.getChartMothon();n++){
			
			HireList hireList=new HireList();
		
			hireList.setHireGUID(UUID.randomUUID().toString());
			hireList.setChartGUID(chartInfo.getChartGUID());
			hireList.setHire(allHire);
			hireList.setHireDate(date);
			hireList.setState(false);
						
			if(month<=12){
				cal.set(year, month, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
			}else{
				cal.set(year, 1, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);  
			}
			
			hireList.setRepayDate(cal.getTime());
			
			i=InsertExe.get(this.getJdbcTemplate(), hireList);
			
			if (i < 1) {
	            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	        }
			
			month++;
			
			if (augment) {
				if (augmentGenre == 0) {
					if (month == 12) {
						allHire = allHire + allHire * chartInfo.getIncrement();
					}
				}else{
					
					cal.set(year, month, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
					Date currentDate=cal.getTime();
					
					SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM");
					
					String currnetTime=sdf.format(currentDate);
					
					String augmentTime=sdf.format(augmentDate);
					
					if(currnetTime.equals(augmentTime)){
						
						allHire = allHire + allHire * chartInfo.getIncrement();
						
					}
					
				}
			}
			
			if(month>12){
				month=1;
				year++;
			}
		
		}
		
		if(chartInfo.getChartEndDate()==null&&chartInfo.getChartMothon()>0){
			cal.set(year, month-1, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0); 
			Date chartEndDate=cal.getTime();
			ChartInfo chartInfo3=new ChartInfo();
			chartInfo3.setChartEndDate(chartEndDate);
			String[] where={"ChartGUID=",chartInfo.getChartGUID()};
			chartInfo3.setWhere(where);
			i=UpdateExe.get(this.getJdbcTemplate(), chartInfo3);
			if (i < 1) {
	            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	        }
		}
		 
		
		return i;
	}



}
