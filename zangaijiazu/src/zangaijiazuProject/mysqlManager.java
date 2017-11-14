package zangaijiazuProject;
import java.util.*;
import java.text.*;
import java.io.UnsupportedEncodingException;
import java.sql.*;

import org.apache.naming.java.javaURLContextFactory;
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
    String sqlUrl = "jdbc:mysql://45.114.11.49:3306/zangaijiazu?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull";
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
	public boolean addData(String tableName, Map<String, Object> map) {
	if (SqlOpen()) {
		try {
			String keyString = "";
			String valueString = "";
			int index = 0;
		
			
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				if (index == map.entrySet().size()-1) {
					
					keyString = keyString+entry.getKey();
					valueString = valueString+"?";
				}else
				{
					keyString = keyString+entry.getKey()+",";
					valueString = valueString+"?,";
				}
				index++;
			}
//			System.out.println(keyString);
//			System.out.println(valueString);
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
//			System.out.println("insert into "+tableName+" ("+keyString+")"+"values("+valueString+")");
			int x = 0;
			for (Map.Entry<String, Object> entry1 : map.entrySet()) {
//				System.out.println((String)entry1.getValue());
			
				preparedStatement.setString(x+1, (String)entry1.getValue());
				x= x+1;
			}
			
//			preparedStatement.setString(1, name);
//System.out.println("在内部的name为"+preparedStatement.toString());
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
			closeSql();
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
			preparedStatement = con.prepareStatement("insert into usertable (NAME,SEX,ACCOUNT,PASSWORD,KOULING,HEADURL)"+"values(?,?,?,?,?,?)");
			preparedStatement.setString(1, name);
			//System.out.println("在内部的name为"+name);
			preparedStatement.setInt(2, sex);
			preparedStatement.setString(3, account);
			preparedStatement.setString(4, password);
			
			preparedStatement.setString(5, kouLing);
			preparedStatement.setString(6, "test.jpg");
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
			String sql = "select * from "+tableName+" where "+privateKeyName+" >= (select "+privateKeyName+" from "+tableName+" limit "+fromInt+", 1) limit "+pageSize;
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
	//获取某个表中的所有数据并且返回JSONArray格式
	public JSONArray getTableAllDataReturnJsonArray(String tableName,int pageIndex,int pageSize,String privateKeyName) throws JSONException, SQLException {
		if (SqlOpen()) {
			statement = con.createStatement();
			int fromInt = (pageIndex-1)*pageSize;			
//			SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
			String sql = "select * from "+tableName+" where "+privateKeyName+" >= (select "+privateKeyName+" from "+tableName+" limit "+fromInt+", 1) limit "+pageSize;
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
			     return array;	  
		}else
		{
			closeSql();
			return null;
		}
		
	

	}
	//倒叙去除表中所有数据 返回JSONArray
	public JSONArray getTableAllDataFromEndReturnJsonArray(String tableName,int pageIndex,int pageSize,String privateKeyName) throws JSONException, SQLException {
		if (SqlOpen()) {
			statement = con.createStatement();
			int fromInt = (pageIndex-1)*pageSize;			
//			SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
			String sql = "select * from "+tableName+" where "+privateKeyName+" <= (select "+privateKeyName+" from "+tableName+" order by "+privateKeyName+" desc limit "+fromInt+", 1) order by "+privateKeyName+" desc limit "+pageSize;
//			System.out.println(sql);
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
			     return array;	  
		}else
		{
			closeSql();
			return null;
		}
		
	

	}
	
	//根据一个或多个字段参数值找到元素 并修改其对应的字段值
	public Boolean changeDataAccording(String tableName,Map<String, String> conditionMap,Map<String, String> changeMap) {
//		update emp set sal = ? where ename = ?
		String keyStr = "";
		String updateStr = "";
		System.out.println("进入according");
	for (Map.Entry<String, String> entry : conditionMap.entrySet()) {
		if (keyStr.equals("")) {
			keyStr = entry.getKey()+"="+entry.getValue();
		}else
		{
			keyStr = keyStr+"And"+entry.getKey()+"="+entry.getValue();
		}
	}
	for (Map.Entry<String, String> entry : changeMap.entrySet()) {
		if (updateStr.equals("")) {
			updateStr = entry.getKey()+"='"+entry.getValue()+"'";
		}else
		{
			updateStr = updateStr+","+entry.getKey()+"='"+entry.getValue()+"'";
		}
	}
	String sql = "update "+tableName+" set "+updateStr+" where "+keyStr+"";
//  System.out.println(sql);
	if (SqlOpen()) {
		try {
			statement = con.createStatement();
			statement.executeUpdate(sql);
			closeSql();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			 return false;
		}
	}else
	{
	 return false;
	}
	
		
	}
	//根据分页获取某个表中的某些字段的值
	public String getTableSpecialData(String tableName,int pageIndex,int pageSize,String privateKeyName,String[] returnKeyNameArray,boolean isDown) throws JSONException, SQLException {

	    
	   	 
		if (SqlOpen()) {
			String allText = "";
			for (int i = 0; i < returnKeyNameArray.length; i++) {
				if (i == returnKeyNameArray.length-1) {
					allText = allText+returnKeyNameArray[i];
				}else
				{
					allText = allText+returnKeyNameArray[i]+",";
				}
			}
			statement = con.createStatement();
			//Ҫִ�е���ݿ����
			int fromInt = (pageIndex-1)*pageSize;
			//int toInt = pageIndex*pageSize;
//			SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
			String sql = "";
			if (isDown) {
				sql = "select "+allText+" from "+tableName+" where "+privateKeyName+" <= (select "+privateKeyName+" from "+tableName+" order by "+privateKeyName+" desc limit "+fromInt+", 1) order by "+privateKeyName+" desc limit "+pageSize;
			}else
			{
			sql = "select "+allText+" from "+tableName+" where "+privateKeyName+" >= (select "+privateKeyName+" from "+tableName+" limit "+fromInt+", 1) limit "+pageSize;
			}
			
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
	
	//根据分页获取某个表中的某些字段的值 返回数组
		public List<Map<String, Object>> getTableSpecialDataReturnArray(String tableName,int pageIndex,int pageSize,String privateKeyName,String[] returnKeyNameArray,boolean isDown,String condition) throws JSONException, SQLException {

		    
		   	 
			if (SqlOpen()) {
				String allText = "";
				for (int i = 0; i < returnKeyNameArray.length; i++) {
					if (i == returnKeyNameArray.length-1) {
						allText = allText+returnKeyNameArray[i];
					}else
					{
						allText = allText+returnKeyNameArray[i]+",";
					}
				}
				statement = con.createStatement();
				//Ҫִ�е���ݿ����
				int fromInt = (pageIndex-1)*pageSize;
				//int toInt = pageIndex*pageSize;
//				SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
				String sql = "";
				String condition1 = "";
				if (condition != "" && condition != null) {
					condition1 = " and "+condition;
					condition = " where "+condition;
				}else
				{
					condition = "";
				}
				if (isDown) {
					
						
					
					sql = "select "+allText+" from "+tableName+" where "+privateKeyName+" <= (select "+privateKeyName+" from "+tableName+" "+condition+" order by "+privateKeyName+" desc limit "+fromInt+", 1) "+condition1+" order by "+privateKeyName+" desc limit "+pageSize;
				}else
				{
				sql = "select "+allText+" from "+tableName+" where "+privateKeyName+" >= (select "+privateKeyName+" from "+tableName+" "+condition+" limit "+fromInt+", 1) "+condition1+" limit "+pageSize;
				}
				
				//3.ResultSet�� ������Ż�ȡ�Ľ��
				resultSet = statement.executeQuery(sql);

//				JSONArray array = new JSONArray();	
				List<Map<String, Object>> data = resultTurnRoList(resultSet);	
//				 for (Map<String, Object> rowItem : data) {
//						JSONObject jsonObject = new JSONObject();
//						for (Map.Entry<String, Object> entry : rowItem.entrySet()) {
//							jsonObject.put(entry.getKey(), entry.getValue());
//						}
//						array.put(jsonObject);
//					}
					//  System.out.println(array.toString());
				     closeSql();
				     return data;
					
					
//				}
//				  String string = resultSetToJson(rs);
			
//				  rs.close();
//				  con.close();
//				  return string;
				  
			}else
			{
				closeSql();
				return null;
			}
		}
	
	
	
	//根据限制条件分页条件返回一个数组
		public List<Map<String, Object>> FilterTableDataAccording(String tableName,int pageIndex,int pageSize,String privateKeyName,String conditionKey,String conditionValue) throws JSONException, SQLException {

	    
	   	 
			if (SqlOpen()) {
			
				statement = con.createStatement();
				//Ҫִ�е���ݿ����
				int fromInt = (pageIndex-1)*pageSize;
				//int toInt = pageIndex*pageSize;
//				SELECT * FROM table WHERE id >= (SELECT id FROM table LIMIT 1000000, 1) LIMIT 10;
				String sql = "select * from "+tableName+" where "+privateKeyName+" >= (select "+privateKeyName+" from "+tableName+" where "+conditionKey+"="+conditionValue+" limit "+fromInt+", 1) AND "+conditionKey+"="+conditionValue+" limit "+pageSize;
//				System.out.println(sql);
				//3.ResultSet�� ������Ż�ȡ�Ľ��
				resultSet = statement.executeQuery(sql);

				
				List<Map<String, Object>> data = resultTurnRoList(resultSet);	
			
					//  System.out.println(array.toString());
				     closeSql();
				     return data;
  
			}else
			{
				closeSql();
				return null;
			}
			
		

		}
	/**
	 * 获取帖子详细信息
	 * @throws SQLException 
	 * @throws JSONException 
	 */
		public JSONObject getArticelDetail(String articleID,int pageIndex) throws JSONException, SQLException {
			JSONObject jsonObject = new JSONObject();
			String[] articleStrArray = {"ARTICLENAME","UID","ARTICLECONTENT","ARTICLEIMG","ARTICLEDATE","DISCUSSCOUNT","LOOKCOUNT","ARTICLEID"};
			
		Map<String,Object> articleMap = getOtherText("article", "articleID", articleID, articleStrArray).get(0);
		String[] authorStrArray = {"NAME","SEX","HEADURL","UID","MANAGER"};
	   Map<String, Object> authorMap = getOtherText("usertable", "UID",(String) articleMap.get("UID"), authorStrArray).get(0);
	   articleMap.put("author",authorMap);
		jsonObject.put("articleDetail", mapTurnToJson(articleMap));
		List<Map<String, Object>> discussArray = new ArrayList<>();
			discussArray = FilterTableDataAccording("discuss", pageIndex, 10, "discussID", "articleID", articleID);
			for (int i = 0; i < discussArray.size(); i++) {
				Map<String, Object> map = discussArray.get(i);
				
				 Map<String, Object> discussAuthorMap = getOtherText("usertable", "UID",(String) map.get("from_id"), authorStrArray).get(0);
				 map.put("author",discussAuthorMap);
				
				String replyCount = (String) map.get("replyCount");
               if (Integer.parseInt(replyCount) > 0) {
            	   List<Map<String, Object>> replyArray = new ArrayList<>();
            	   
            	   replyArray = FilterTableDataAccording("reply", 1, 10, "ID", "comment_id", (String) map.get("discussID"));
            	
            	   for (int j = 0; j < replyArray.size(); j++) {
					Map<String, Object> replyMap = replyArray.get(j);
					 Map<String, Object> replyAuthorMap = getOtherText("usertable", "UID",(String) replyMap.get("from_uid"), authorStrArray).get(0);
					 Map<String, Object> replyToAuthorMap = getOtherText("usertable", "UID",(String) replyMap.get("to_uid"), authorStrArray).get(0);
					replyMap.put("author", replyAuthorMap);
					replyMap.put("toPerson", replyToAuthorMap);
				
				}
            	   if (replyArray != null) {
					map.put("reply", replyArray);
					
				}
			}else{
				List<Map<String, Object>> replyArray = new ArrayList<>();
				map.put("reply", replyArray);
			}
			}
			
			jsonObject.put("discussDetail", listTurnToJson(discussArray));
			return jsonObject;
		}
		//让表中某一行某一个字段自增
		public boolean incrementWithTable(String tableName,String keyName,String condition){
			String sql = "update "+tableName+" set "+keyName+" = "+keyName+"+1 "+condition+"";
		//  System.out.println(sql);
			if (SqlOpen()) {
				try {
					statement = con.createStatement();
					statement.executeUpdate(sql);
					closeSql();
					return true;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					 return false;
				}
			}else
			{
			 return false;
			}
		}
 //根据一个值扫描表，得到这个值对应的值
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
	//检查某个表中的某个字段的某个值是否存在了
	public boolean checkUserIsExist(String tableName,String value,String name){
		String sql="select "+name+" from "+tableName+" where "+name+"=?";
		//System.out.println(sql);
		if (SqlOpen()) {
			  try
		         { 
		             
		        	 PreparedStatement ps=con.prepareStatement(sql);
		        	 ps.setString(1, value);
//		        	 System.out.println(value);
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
//				System.out.println("columnName=="+metaData.getColumnName(i)+" obkect===="+resultSet.getObject(i)+"");
				String columnName = metaData.getColumnName(i);
				String Value = "";
				if (resultSet.getObject(i) != null) {
					Value = resultSet.getObject(i).toString();
				}
				
				
				map.put(columnName, Value);
			}
			data.add(map);
		}
//	     System.out.println(data);
	     return data;
	     
	    
	}
	
	//将得到的resultset 转化为字典的格式弄出来
		public Map<String, Object> resultTurnRoMap(ResultSet resultSet) throws SQLException {
			
			
			java.sql.ResultSetMetaData metaData;
				metaData = resultSet.getMetaData();
		    // System.out.println(metaData);
		     int columnCount = metaData.getColumnCount();
		     Map<String, Object> map = new HashMap<String,Object>();
//		     System.out.println("����Ϊ====="+columnCount);
//		     System.out.println("����Ϊ====="+resultSet.getRow());
		     while (resultSet.next()) {
//		    	 job = rs.getString("job");
//					id = rs.getString("ename");
//					System.out.println(id+"\t"+job);
		    	// System.out.println("nextnextyneyx");
				
				for (int i = 1; i <= columnCount; i++) {
					
				//System.out.println(metaData.getColumnName(i));
//					System.out.println("columnName=="+metaData.getColumnName(i)+" obkect===="+resultSet.getObject(i)+"");
					String columnName = metaData.getColumnName(i);
					String Value = "";
					if (resultSet.getObject(i) != null) {
						Value = resultSet.getObject(i).toString();
					}
					
					
					map.put(columnName, Value);
				}
				
			}
//		     System.out.println(data);
		     return map;
		     
		    
		}
	
	//根据转换成的数组转换成jsonArray
	public JSONArray listTurnToJson(List<Map<String, Object>> data) throws JSONException {
		JSONArray array = new JSONArray();
		 for (Map<String, Object> rowItem : data) {
				JSONObject jsonObject = new JSONObject();
				for (Map.Entry<String, Object> entry : rowItem.entrySet()) {
					if (entry.getValue() instanceof java.util.List ) {
						jsonObject.put(entry.getKey(), listTurnToJson((java.util.List)entry.getValue()));
					}else if (entry.getValue() instanceof Map<?, ?>) {
						jsonObject.put(entry.getKey(), mapTurnToJson((Map<String, Object>)entry.getValue()));
					}else
					{
						jsonObject.put(entry.getKey(), entry.getValue());
					}
				  
				
					
				}
				array.put(jsonObject);
			}
		return array;
	}
	
	public JSONObject mapTurnToJson(Map<String, Object> data) throws JSONException {
//		JSONArray array = new JSONArray();
		JSONObject jsonObject = new JSONObject();
				for (Map.Entry<String, Object> entry : data.entrySet()) {
					
					if (entry.getValue() instanceof java.util.List ) {
						jsonObject.put(entry.getKey(), listTurnToJson((java.util.List)entry.getValue()));
					}else if (entry.getValue() instanceof Map<?, ?>) {
						jsonObject.put(entry.getKey(), mapTurnToJson((Map<String, Object>)entry.getValue()));
					}else
					{
						jsonObject.put(entry.getKey(), entry.getValue());
					}
					
						
				}

			
		return jsonObject;
	}
	
	
}
