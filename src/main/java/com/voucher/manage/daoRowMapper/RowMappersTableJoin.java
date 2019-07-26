package com.voucher.manage.daoRowMapper;

import com.voucher.manage.daoSQL.annotations.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RowMappersTableJoin implements RowMapper<Map> {

	List<String> columnList;
    Class<?> className;
    String tableName;

    private String[] colNames;//列名数组
    private String[] colType;//列名类型数组  
    private int[] colSize;//列名大小数组  
    private boolean f_util_date = false;//是否需要导入java.util.*  
    private boolean f_Clob = false;//是否需要导入java.sql.*  
    private boolean f_Blob = false;//是否需要导入java.sql.* 

    public RowMappersTableJoin(List<String> columnList,Class<?> className, String tableName) {
        // TODO Auto-generated constructor stub
        this.columnList=columnList;
        this.className = className;
        this.tableName = tableName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map mapRow(ResultSet rs, int rowNum) throws SQLException {
        // TODO Auto-generated method stub

        Map map = new HashMap<>();

        String name = className.getName();
        Class<?> cl = null;
        try {
            cl = Class.forName(name);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Field field : cl.getDeclaredFields())                  //获取声明的属性
        {
            String columnName = null;
            Annotation[] anns = field.getDeclaredAnnotations();//获取注解，一个属性可以有多个注解，所以是数组类型
            if (anns.length < 1) {
                continue;
            } else if (anns[0] instanceof SQLInteger)                //判断注解类型
            {
                SQLInteger sInt = (SQLInteger) anns[0];
                columnName = (sInt.name().length() < 1) ? field.getName() : sInt.name();//获取列名称与获取表名一样
                setIntMethods(rs, map, cl, field, columnName);

            } else if (anns[0] instanceof SQLString) {
                SQLString sStr = (SQLString) anns[0];
                columnName = (sStr.name().length() < 1) ? field.getName().toUpperCase() : sStr.name();
                setStringMethods(rs, map, cl, field, columnName);
            } else if (anns[0] instanceof SQLFloat) {
                SQLFloat sStr = (SQLFloat) anns[0];
                columnName = (sStr.name().length() < 1) ? field.getName().toUpperCase() : sStr.name();
                setFloatMethods(rs, map, cl, field, columnName);
            } else if (anns[0] instanceof SQLDouble) {
                SQLDouble sStr = (SQLDouble) anns[0];
                columnName = (sStr.name().length() < 1) ? field.getName().toUpperCase() : sStr.name();
                setDoubleMethods(rs, map, cl, field, columnName);
            } else if (anns[0] instanceof SQLLong) {
                SQLLong sStr = (SQLLong) anns[0];
                columnName = (sStr.name().length() < 1) ? field.getName().toUpperCase() : sStr.name();
                setLongMethods(rs, map, cl, field, columnName);
            } else if (anns[0] instanceof SQLDateTime) {
                SQLDateTime sStr = (SQLDateTime) anns[0];
                columnName = (sStr.name().length() < 1) ? field.getName().toUpperCase() : sStr.name();
                setDateTimeMethods(rs, map, cl, field, columnName);
            }
        }
        /*
        int size = rsmd.getColumnCount();//共有多少列  
        colNames = new String[size];
        colType = new String[size];
        colSize = new int[size];

        for (int i = 1; i < rsmd.getColumnCount(); i++) {
            colNames[i] = rsmd.getColumnName(i + 1);
            colType[i] = rsmd.getColumnTypeName(i + 1);
            if (colType[i].equalsIgnoreCase("datetime")) {
                f_util_date = true;
            }
            if (colType[i].equalsIgnoreCase("text")) {
                f_Clob = true;
            }
            if (colType[i].equalsIgnoreCase("image")) {
                f_Blob = true;
            }
            colSize[i] = rsmd.getColumnDisplaySize(i + 1);

            String setMethodName = "set" + rsmd.getColumnName(i + 1);

            try {

                String aa = rs.getString(rsmd.getColumnName(i + 1));
                //  	 SystemConstant.out.println("aa="+aa);
                map.put(rsmd.getColumnName(i + 1), aa);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {  // ResultSet的异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 

        }
         */
        
        Iterator<String> iterator=columnList.iterator();
        
        while (iterator.hasNext()) {
        	
        	String setMethodName=iterator.next();
        	
        	try {

                String aa = rs.getString(setMethodName);
                //  	 SystemConstant.out.println("aa="+aa);
                map.put(setMethodName, aa);
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {  // ResultSet的异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
			
		}
        
        return map;
    }

    public void setStringMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setIntMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //   SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setFloatMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //   SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setDoubleMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //   SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setLongMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //   SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setDateTimeMethods(ResultSet rs, Map map, Class className, Field field, String columnName) {
        String filedName = field.getName();
        //获取相应字段的getXXX()方法  
        String setMethodName = "set" + filedName.substring(0, 1).toUpperCase()
                + filedName.substring(1);
        //   SystemConstant.out.println("setMethodName="+setMethodName);
        try {
            //  	 SystemConstant.out.println("setmethod="+setMethod);
            String aa = rs.getString(columnName);
            //  	 SystemConstant.out.println("aa="+aa);
            map.put(columnName, aa);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {  // ResultSet的异常
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
