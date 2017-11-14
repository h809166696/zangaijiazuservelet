package zangaijiazuProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Servlet implementation class articleManager
 */
@WebServlet("/articleManager")
public class articleManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public articleManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		mysqlManager manager = new mysqlManager();
		//0 �˺��ظ� -1 �ǳ��ظ� -2 ���� 1 �ɹ�
		
		response.setHeader("Access-Control-Allow-Origin", "*"); 
		request.setCharacterEncoding("UTF-8");
		//response.setHeader("content-type","text/html;charset=UTF-8");
		Map<String, String[]> map = request.getParameterMap();
		response.setCharacterEncoding("UTF-8");
		
		response.setContentType("text/html; charset=utf-8");
			//	request.setCharacterEncoding("GB2312");
		PrintWriter out = response.getWriter();
		if (map.containsKey("action")) {
			
			String actionStr = map.get("action")[0];
			//获取帖子详情
			
			if (actionStr.equals("getArticleDetail")) {
				String pageIndex = map.get("pageIndex")[0];
				
				 if (pageIndex.equals("1")) {
					 
				boolean isSuccess = manager.incrementWithTable("article", "LOOKCOUNT", " where ARTICLEID="+map.get("articleID")[0]+"");
			
				}
			try {
				JSONObject object = manager.getArticelDetail(map.get("articleID")[0],Integer.parseInt(pageIndex));
				out.write(object.toString());
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				out.write("-1");
			}
				
				//获取帖子
			}else if (actionStr.equals("getArticle")) {
				try {
					String pageIndex = map.get("pageIndex")[0];
					String pageSize = map.get("pageSize")[0];
					JSONArray articleArray = manager.getTableAllDataFromEndReturnJsonArray("article", Integer.parseInt(pageIndex), Integer.parseInt(pageSize), "lastEditTime");
					for (int i = 0; i < articleArray.length(); i++) {
						JSONObject articleObject = articleArray.getJSONObject(i);
						String uid = articleObject.getString("UID");
						String newDate = articleObject.getString("lastEditTime").replace(".0", "");
						articleObject.put("lastEditTime", newDate);
						String [] strAtrray = {"UID","NAME","HEADURL","DESCRIPTION"};
						List<Map<String, Object>> userList = manager.getOtherText("usertable", "UID", uid, strAtrray);
						JSONObject userObject = manager.mapTurnToJson(userList.get(0));
						articleObject.put("AUTHOR", userObject);
						
					}
					
					out.write(articleArray.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.write("-1");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.write("-1");
				}
			}else if (actionStr.equals("getUser")) {
				
				try {
					String pageIndex = map.get("pageIndex")[0];
					String pageSize = map.get("pageSize")[0];
					String[] strArray = {"NAME","SEX","HEADURL","UID","MANAGER","DESCRIPTION"};
					out.write(manager.getTableSpecialData("usertable",Integer.parseInt(pageIndex),Integer.parseInt(pageSize), "UID", strArray,true));
				} catch (NumberFormatException | JSONException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.write("-1");
				}
				
			}else if (actionStr.equals("getReply")){
				String pageIndex = map.get("pageIndex")[0];
				String commentId = map.get("commentId")[0];
				String[] strArrauy = {"ID","content","from_uid","to_uid","TIME","comment_id"};
				try {
					String[] authorStrArray = {"NAME","SEX","HEADURL","UID","MANAGER"};
					System.out.println(commentId+"  "+pageIndex);
					List<Map<String, Object>> list = manager.getTableSpecialDataReturnArray("reply", Integer.parseInt(pageIndex), 10, "ID", strArrauy, false,"comment_id="+commentId+"");
				    System.out.println(list);
					for (Map<String, Object> replyMap : list) {
						
						Map<String, Object> replyAuthorMap = manager.getOtherText("usertable", "UID",(String) replyMap.get("from_uid"), authorStrArray).get(0);
						 Map<String, Object> replyToAuthorMap = manager.getOtherText("usertable", "UID",(String) replyMap.get("to_uid"), authorStrArray).get(0);
						replyMap.put("author", replyAuthorMap);
						replyMap.put("toPerson", replyToAuthorMap);
						
						
					}
				
					JSONArray jsonArray = manager.listTurnToJson(list);
					out.write(jsonArray.toString());
				} catch (NumberFormatException | JSONException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.write("-1");
				}
				
			}
			else if (actionStr.equals("publishReply")){
				String articleID = map.get("articleID")[0];
				String comment_id = map.get("commentid")[0];
				String content = map.get("content")[0];
				String from_uid = map.get("fromuid")[0];
				String to_uid = map.get("touid")[0];
			  if (isNoNullOrEmptyStr(articleID) && isNoNullOrEmptyStr(comment_id) && isNoNullOrEmptyStr(content) && isNoNullOrEmptyStr(from_uid)&& isNoNullOrEmptyStr(to_uid)) {
				//评论的回复数加1
				  String[] strArray1 = {"replyCount"};
					List<Map<String, Object>> articleList = manager.getOtherText("discuss", "discussID",comment_id, strArray1);
				
					String str34 = (String)articleList.get(0).get("replyCount");
				 
				 int count =  Integer.parseInt(str34);
				 count++;
				
				 Map<String, String> conditionMap = new HashMap<>();
				 conditionMap.put("discussID",comment_id);
				 Map<String, String> changeMap = new HashMap<>();
				
				 changeMap.put("replyCount",Integer.toString(count));
			  boolean isSuccess	= manager.changeDataAccording("discuss", conditionMap, changeMap);
			  if (isSuccess) {
				//帖子的回复数加1
				  String[] strArray2 = {"DISCUSSCOUNT","lastEditTime"};
					List<Map<String, Object>> articleList1 = manager.getOtherText("article", "ARTICLEID",articleID, strArray2);
				
					String str3 = (String)articleList1.get(0).get("DISCUSSCOUNT");
				
				 int count1 =  Integer.parseInt(str3);
				 count1++;
				 
				 Map<String, String> conditionMap1 = new HashMap<>();
				 conditionMap1.put("ARTICLEID",articleID);
				 Map<String, String> changeMap1 = new HashMap<>();
				
				 changeMap1.put("DISCUSSCOUNT",Integer.toString(count1));
				 changeMap1.put("lastEditTime",getNowDate());
			  boolean isSuccess1	= manager.changeDataAccording("article", conditionMap1, changeMap1);
			  if (isSuccess1) {
				  Map<String, Object> dataMap = new HashMap<>();
				  dataMap.put("articleID", articleID);
				  dataMap.put("comment_id", comment_id);
				  dataMap.put("content", content);
				  dataMap.put("from_uid", from_uid);
				  dataMap.put("to_uid", to_uid);
				  dataMap.put("TIME", getNowDate());
			if (manager.addData("reply", dataMap)) {
				out.write("1");
			}else{
				out.write("0");
			}
			} else {
				out.write("0");
			}
			  
			}else
			{
				out.write("0");
			}
			}else
			{
				//参数不完整
				out.write("-1");
			}
			}
		}
	}
   public boolean isNoNullOrEmptyStr(String string) {
	if (string.equals("")|| string == null) {
		return false;
	}else
	{
		return true;
	}
}
   public String getNowDate() {
	Date now = new Date();
	SimpleDateFormat dSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	return dSimpleDateFormat.format(now).toString();
}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
