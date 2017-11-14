package jiaGeXieYeProject;
import java.util.*;
import java.text.*;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import org.json.*;

import sun.misc.*;


public class mysqlManager {
    Connection con;
    ResultSet resultSet;
    Statement statement;
    PreparedStatement preparedStatement;
    String diverClass = "com.mysql.jdbc.Driver";
    String userName = "root";
    String password = "h809166696";
//    &zeroDateTimeBehavior=convertToNull
    String sqlUrl = "jdbc:mysql://45.114.11.49:3306/shoosedb?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	//开启数据库
	public boolean SqlOpen() {
		try {
			Class.forName(diverClass);
			con = DriverManager.getConnection(sqlUrl, userName, password);
			if (!con.isClosed()) {
				return true;
			}else
			{
				return false;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	//关闭数据库
	public void closeSql() {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//在一个表中插入一个数据
	public boolean addData(String tableName, String[] keyArray,String[] valueArray) {
	if (SqlOpen()) {
		try {
			String keyString = "";
			String valueString = "";
			for (int i = 0; i < keyArray.length; i++) {
				if (i == keyArray.length-1) {
					keyString = keyString+keyArray[i];
					valueString = valueString+"?";
				}else
				{
					keyString = keyString+keyArray[i]+",";
					valueString = valueString+"?,";
				}
				
				
			}
			
//			try {
//				name = new String(
//						name.getBytes("ISO-8859-1"), 
//				        "gb2312");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//name = getFromBase64(name);
			preparedStatement = con.prepareStatement("insert into "+tableName+" ("+keyString+")"+"values("+valueString+")");
			for (int i = 0; i < valueArray.length; i++) {
				String string = valueArray[i];
				preparedStatement.setString(i+1, string);
			}
//			preparedStatement.setString(1, name);
//			//System.out.println("在内部的name为"+name);
//			preparedStatement.setInt(2, sex);
//			preparedStatement.setString(3, account);
//			preparedStatement.setString(4, password);
//			preparedStatement.setString(5, uuid);
//			preparedStatement.setString(6, kouLing);
			preparedStatement.executeUpdate();
			closeSql();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}else {
		return false;
	} 
	}
	//添加用户
	public boolean addUser(String name,String account,String password,int sex,String kouLing) {
	if (SqlOpen()) {
		try {
			String uuid = getRandowUUID();
//			try {
//				name = new String(
//						name.getBytes("ISO-8859-1"), 
//				        "gb2312");
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			//name = getFromBase64(name);
			preparedStatement = con.prepareStatement("insert into usertable (NAME,SEX,ACCOUNT,PASSWORD,UID,KOULING)"+"values(?,?,?,?,?,?)");
			preparedStatement.setString(1, name);
			//System.out.println("在内部的name为"+name);
			preparedStatement.setInt(2, sex);
			preparedStatement.setString(3, account);
			preparedStatement.setString(4, password);
			preparedStatement.setString(5, uuid);
			preparedStatement.setString(6, kouLing);
			preparedStatement.executeUpdate();
			closeSql();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}else {
		return false;
	} 
	}
	//获取某个表中的所有数据并且转化为json数组格式
	public String getTableAllData(String tableName,int pageIndex,int pageSize,String privateKeyName) throws JSONException, SQLException {

    
   	 
		if (SqlOpen()) {
		
			statement = con.createStatement();
			//Ҫִ�е���ݿ����
			int fromInt = (pageIndex-1)*pageSize;
			//int toInt = pageIndex*pageSize;
//			SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
			String sql = "select * from "+tableName+" where "+privateKeyName+" >= (select indexKey from "+tableName+" limit "+fromInt+", 1) limit "+pageSize;
			//3.ResultSet�� ������Ż�ȡ�Ľ��
			resultSet = statement.executeQuery(sql);

			JSONArray array = new JSONArray();	
			List<Map<String, Object>> data = resultTurnRoList(resultSet);	
			 for (Map<String, Object> rowItem : data) {
					JSONObject jsonObject = new JSONObject();
					for (Map.Entry<String, Object> entry : rowItem.entrySet()) {
						jsonObject.put(entry.getKey(), entry.getValue());
					}
					array.put(jsonObject);
				}
				//  System.out.println(array.toString());
			     closeSql();
			     return array.toString();
				
				
//			}
//			  String string = resultSetToJson(rs);
		
//			  rs.close();
//			  con.close();
//			  return string;
			  
		}else
		{
			closeSql();
			return "";
		}
		
	

	}
 //根据一个值扫描表，得到这个值对应的另一个值
	public  List<Map<String, Object>> getOtherText(String tableName,String searchName,String searchValue,String[] returnStringName) {
		String allText = "";
		for (int i = 0; i < returnStringName.length; i++) {
			if (i == returnStringName.length-1) {
				allText = allText+returnStringName[i];
			}else
			{
				allText = allText+returnStringName[i]+",";
			}
		
			
		}
		String sql = "select "+allText+" from "+tableName+" where "+searchName+"=?";
		
		if (SqlOpen()) {
			  try
		         { 
		             
		        	 PreparedStatement ps=con.prepareStatement(sql);
		        	 ps.setString(1, searchValue);
		        	
		             resultSet=ps.executeQuery();
		             List<Map<String, Object>> data = resultTurnRoList(resultSet);
		            // System.out.println(rwo);
		           //  System.out.println(rwo.getMetaData().getColumnCount());
		            // String passWord = (String)data.get(0).get("PASSWORD");
		            closeSql();
		           // System.out.println(passWord);
		             	return data; 
		            
		            
		         }	
		         catch (Exception e)
		         {
		        	 closeSql();
		        	 return null;
		         }
			
		}else
		{
			//System.out.println("����ʧ��");
			  closeSql();
			return null;
		}
	}
   //获取随机的UUID
	public String getRandowUUID() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		return uuid;
	}
	//检查某个表中的多个参数
	public boolean checkZiDuanIsExist(String tableName,String[] valueArray,String[] keyArray){
		String keyString = "";
		String valueString = "";
	for (int i = 0; i < keyArray.length; i++) {
		if (i == keyArray.length-1) {
			keyString = keyString+keyArray[i];
			valueString = valueString+keyArray[i]+"=?";
		}else
		{
			keyString = keyString+keyArray[i]+",";
			valueString = valueString+keyArray[i]+"=? AND ";
		}
		
	}
	
	String sql="select "+keyString+" from "+tableName+" where "+valueString+"";
	if (SqlOpen()) {
		  try
	         { 
	             
	        	 PreparedStatement ps=con.prepareStatement(sql);
	        	for (int i = 0; i < valueArray.length; i++) {
	        		ps.setString(i+1, valueArray[i]);
				}
	        	 
	        
	             ResultSet rwo=ps.executeQuery();
	            // System.out.println(rwo);
	           //  System.out.println(rwo.getMetaData().getColumnCount());
	             while(rwo.next()){
	            	 closeSql();
	             	return true; 
	             }
	             closeSql();
	         }	
	         catch (Exception e)
	         {
	        	 closeSql();
	        	 return false;
	         }
		  closeSql();
	         return false;
	}else
	{
		System.out.println("����ʧ��");
		return false;
	}
	
	}
	//检查某个表中的某个字段的某个值是否存在了
	public boolean checkUserIsExist(String tableName,String value,String name){
		String sql="select "+name+" from "+tableName+" where "+name+"=?";
		//System.out.println(sql);
		if (SqlOpen()) {
			  try
		         { 
		             
		        	 PreparedStatement ps=con.prepareStatement(sql);
		        	 ps.setString(1, value);
		        	// System.out.println(value);
		             ResultSet rwo=ps.executeQuery();
		            // System.out.println(rwo);
		           //  System.out.println(rwo.getMetaData().getColumnCount());
		             while(rwo.next()){
		            	 closeSql();
		             	return true; 
		             }
		             closeSql();
		         }	
		         catch (Exception e)
		         {
		        	 closeSql();
		        	 return false;
		         }
			  closeSql();
		         return false;
		}else
		{
			System.out.println("����ʧ��");
			return false;
		}
	       
		}
	
	 // ����  
    public static String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);  
        }  
        return s;  
    }  
  
    // ����  
    public static String getFromBase64(String s) {  
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
                result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    } 
	
    //将得到的resultset 转化为数组嵌套字典的格式弄出来
	public List<Map<String, Object>> resultTurnRoList(ResultSet resultSet) throws SQLException {
		
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		java.sql.ResultSetMetaData metaData;
			metaData = resultSet.getMetaData();
	    // System.out.println(metaData);
	     int columnCount = metaData.getColumnCount();
//	     System.out.println("����Ϊ====="+columnCount);
//	     System.out.println("����Ϊ====="+resultSet.getRow());
	     while (resultSet.next()) {
//	    	 job = rs.getString("job");
//				id = rs.getString("ename");
//				System.out.println(id+"\t"+job);
	    	// System.out.println("nextnextyneyx");
			Map<String, Object> map = new HashMap<String,Object>();
			for (int i = 1; i <= columnCount; i++) {
				
			//System.out.println(metaData.getColumnName(i));
				map.put(metaData.getColumnName(i), resultSet.getObject(i));
			}
			data.add(map);
		}
	     return data;
	     
	    
	}
	//根据转换成的数组转换成jsonArray
	public JSONArray listTurnToJson(List<Map<String, Object>> data) throws JSONException {
		JSONArray array = new JSONArray();
		 for (Map<String, Object> rowItem : data) {
				JSONObject jsonObject = new JSONObject();
				for (Map.Entry<String, Object> entry : rowItem.entrySet()) {
					jsonObject.put(entry.getKey(), entry.getValue());
				}
				array.put(jsonObject);
			}
		return array;
	}
	 public boolean checkStrArray(String[] strArray) {
			for (String str: strArray) {
				if (str == null) {
					return false;
				}
			}
			return true;
		}
}
