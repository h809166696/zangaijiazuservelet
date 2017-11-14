package jiaGeXieYeProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.awt.Color;
import java.awt.Font;  
import java.awt.Graphics2D;  
import java.awt.image.BufferedImage;  
import com.sun.image.codec.jpeg.JPEGCodec;  
import com.sun.image.codec.jpeg.JPEGImageEncoder; 

/**
 * Servlet implementation class Manager
 */
@WebServlet("/Manager")
public class Manager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Manager() {
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
					if (actionStr.equals("addNewShop")) {
						// 1成功 0店铺名已存在-1 插入失败 -2参数出错
						String shopName = map.get("shopName")[0];
						String userID = map.get("uid")[0];
						if (shopName != null && userID != null) {
							if (manager.checkUserIsExist("shop", shopName, "sname")) {
								out.write("0");
							}else
							{
							String[] keyArray = {"uid","sname"};
							String[] valueArray = {userID,shopName};
							if (manager.addData("shop",keyArray,valueArray)) {
								out.write("1");
							}else
							{
								out.write("-1");	
							}
							}
						}else
						{
							out.write("-2");
						}
					}else if (actionStr.equals("addNewBrand")) {

						// 1成功 0品牌名已存在-1 插入失败 -2参数出错
						String brandName = map.get("brandName")[0];
					
						String shopID = map.get("shopID")[0];
						String[] valueArray = {brandName,shopID};
						if (manager.checkStrArray(valueArray)) {
							if (manager.checkUserIsExist("brand", brandName, "brandName")) {
								out.write("0");
							}else
							{
							String[] keyArray = {"brandName","shopID"};
							
							if (manager.addData("brand",keyArray,valueArray)) {
								out.write("1");
							}else
							{
								out.write("-1");	
							}
							}
						}else
						{
							out.write("-2");
						}
					
					}else if (actionStr.equals("addGood")) {


						// 成功返回商品ID 0在这个商店的商品名字已存在-1 插入失败 -2参数出错
						String goodName = map.get("goodName")[0];
						
						String shopID = map.get("shopID")[0];
						String price = map.get("price")[0];
						String desc = map.get("desc")[0];
						String typeNumber = map.get("typeNumber")[0];
						String brandID = map.get("brandID")[0];
						String reserve = map.get("reserve")[0];
						if (brandID == "") {
							brandID = "x";
						}
						String[] valueArray = {goodName,shopID,price,desc,typeNumber,brandID,reserve};
						
						if (manager.checkStrArray(valueArray)) {
							String[] checkKeyArray = {"shopID","gname"};
							String[] checkValueArray = {shopID,goodName};
							if (manager.checkZiDuanIsExist("goods", checkValueArray, checkKeyArray)) {
								out.write("0");
							}else
							{
							String[] keyArray = {"gname","shopID","price","DESCRIPTION","typeNumber","brandID","reserve"};
							
							if (manager.addData("goods",keyArray,valueArray)) {
								String[] strArray = {"sid","gname"};
								List<Map<String, Object>> data = manager.getOtherText("goods", "shopID", shopID, strArray);
								for (Map<String, Object> map2 : data) {
									if (map2.get("gname").equals(goodName)) {
										out.write(map2.get("sid").toString());
										return;
									}
								}
							}else
							{
								out.write("-1");	
							}
							}
						}else
						{
							out.write("-2");
						}
					
					
					}else if (actionStr.equals("checkShop")) {
						String uid = map.get("uid")[0];
						if (uid != null) {
							if (manager.checkUserIsExist("shop", uid, "uid")) {
								String[] strArray = {"sname","srate","satisfy","sid"};
								List<Map<String, Object>> data = manager.getOtherText("shop", "uid", uid,strArray);
							JSONArray array = null;
							try {
								array = manager.listTurnToJson(data);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							out.write(array.toString());
							}else
							{
								out.write("0");
							}
						}else {
							
							out.write("-1");
						}
					}else if (actionStr.equals("getBrandByShopID")) {
						String uid = map.get("shopID")[0];
						if (uid != null) {
							if (manager.checkUserIsExist("brand", uid, "shopID")) {
								String[] strArray = {"brandName","brandID"};
								List<Map<String, Object>> data = manager.getOtherText("brand", "shopID", uid,strArray);
							JSONArray array = null;
							try {
								array = manager.listTurnToJson(data);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							out.write(array.toString());
							}else
							{
								out.write("0");
							}
						}else {
							
							out.write("-1");
						}
					}else if (actionStr.equals("getGoodsByShopID")) {
						String uid = map.get("shopID")[0];
						if (uid != null) {
							if (manager.checkUserIsExist("goods", uid, "shopID")) {
								String[] strArray = {"gname","sid","price","DESCRIPTION","brandID"};
								List<Map<String, Object>> data = manager.getOtherText("goods", "shopID", uid,strArray);
							JSONArray array = null;
							try {
								array = manager.listTurnToJson(data);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
							
								e.printStackTrace();
							}
							out.write(array.toString());
							}else
							{
								out.write("0");
							}
						}else {
							
							out.write("-1");
						}
					}
				}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
