package zangaijiazuProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
 * Servlet implementation class user
 */
@WebServlet("/user")
public class user extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public user() {
        super();
        // TODO Auto-generated constructor stub
    }
    private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色  
        Random random = new Random();  
        if (fc > 255)  
                fc = 255;  
        if (fc < 0)  
                fc = 0;  
        if (bc > 255)  
                bc = 255;  
        if (bc < 0)  
                bc = 0;  
        int r = fc + random.nextInt(bc - fc);  
        int g = fc + random.nextInt(bc - fc);  
        int b = fc + random.nextInt(bc - fc);  
        return new Color(r, g, b);  
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
			if (actionStr.equals("register")) {
				String account = map.get("account")[0];
				String password = map.get("password")[0];
				String sex = map.get("sex")[0];
				String name = map.get("name")[0];
				String kouLing = map.get("kouLing")[0];
				//System.out.println(name);
//				name = new String(name.getBytes(), "UTF-8"); 
//				System.out.println(name);
//				name = new String(
//						name.getBytes("ISO-8859-1"), 
//				        "gb2312");
			if (manager.checkUserIsExist("usertable", account, "ACCOUNT")) {
				out.write("0");
			}else if(manager.checkUserIsExist("usertable", name, "NAME"))
			{out.write("-1");}else{
				
				boolean isSuccess = manager.addUser(name, account, password, Integer.parseInt(sex),kouLing);
				if (isSuccess) {
					out.write("1");
				}else
				{
					out.write("-2");
				}
				
			}
			}else if (actionStr.equals("getAllAccount")) {
				
					try {
						int pageIndex = Integer.parseInt(map.get("pageIndex")[0]);
						int pageSize = Integer.parseInt(map.get("pageSize")[0]);
						String jsonStr = manager.getTableAllData("usertable",pageIndex,pageSize,"indexKey");
						
						out.write("{\"users\":"+jsonStr+"}");
					} catch (JSONException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
			}else if (actionStr.equals("login")) {
				//-1 没有该账号 0密码错误 1 成功
				String account = map.get("account")[0];
				String password = map.get("password")[0];
				if (manager.checkUserIsExist("usertable", account, "ACCOUNT")) {
					String[] strArray = {"PASSWORD","NAME","SEX","ACCOUNT","PASSWORD","HEADURL","UID"};
					List<Map<String, Object>> data = manager.getOtherText("usertable", "ACCOUNT", account,strArray);
					
					if (data.get(0).get("PASSWORD").equals(password)) {
						try {
							JSONArray array = manager.listTurnToJson(data);
							out.write(array.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else
					{
						out.write("0");
					}
				}else
				{
					out.write("-1");
				}
			}else if (actionStr.equals("findPassword")) {
				String account = map.get("account")[0];
				String kouLing = map.get("kouLing")[0];
				if (manager.checkUserIsExist("usertable", account, "ACCOUNT")) {
					String[] strArray = {"PASSWORD","NAME","SEX","ACCOUNT","PASSWORD","HEADURL","UID","KOULING"};
					List<Map<String, Object>> data = manager.getOtherText("usertable", "ACCOUNT", account,strArray);
					
					if (data.get(0).get("KOULING").equals(kouLing)) {
						try {
							JSONArray array = manager.listTurnToJson(data);
							String Password = (String)data.get(0).get("PASSWORD");
							out.write(Password);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else
					{
						out.write("0");
					}
				}else
				{
					out.write("-1");
				}
			}else if (actionStr.equals("getCodePicture")) {
				  // 设置输出  
                response.setContentType("image/jpeg");  
                int width = 60;  
                int height = 20;  
                // 产生随机数  
                Random r = new Random();  
                // 把随机数绘制成图像  
                BufferedImage imgbuf = new BufferedImage(width, height,  
                                BufferedImage.TYPE_INT_RGB);// 产生缓冲图像,40宽20高  
                Graphics2D g = imgbuf.createGraphics();// 取得缓冲的绘制环境  
                // 开始绘制  
                g.setColor(getRandColor(200, 250));// 设定背景色  
                g.fillRect(0, 0, width, height);  
                // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到  
                g.setColor(getRandColor(160, 200));  
                for (int i = 0; i < 155; i++) {  
                        int x = r.nextInt(width);  
                        int y = r.nextInt(height);  
                        int xl = r.nextInt(12);  
                        int yl = r.nextInt(12);  
                        g.drawLine(x, y, x + xl, y + yl);  
                }  
                // 随机产生100个干扰点，使图像中的验证码不易被其他分析程序探测到  
                g.setColor(getRandColor(120, 240));  
                for (int i = 0; i < 100; i++) {  
                        int x = r.nextInt(width);  
                        int y = r.nextInt(height);  
                        g.drawOval(x, y, 0, 0);  
                }  
                g.setFont(new Font("Times New Roman", Font.PLAIN, 18));  
                String scode = "";  
                for (int i = 0; i < 4; i++) {  
                        String rand = String.valueOf(r.nextInt(10));  
                        scode += rand;  
                        g.setColor(new Color(20 + r.nextInt(110), 20 + r.nextInt(110),  
                                        20 + r.nextInt(110)));  
                        // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成  
                        g.drawString(rand, 13 * i + 6, 16);  
                }  
                request.getSession().setAttribute("code", scode);  
                System.out.println("code------->"+scode);  
                // 输出图像  
                ServletOutputStream out1 = response.getOutputStream();// 得到HTTP的流  
                // JPEGCodec.createJPEGEncoder(out);转码  
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out1);// 产生JPEG的图像加码器  
                encoder.encode(imgbuf);  
                out1.flush();
                
          
			}else if (actionStr.equals("insertShoose")){
				//-1 数据字段缺失 0 失败 成功返回商品id
				String gname = map.get("gname")[0];
				String price = map.get("price")[0];
				String desc = map.get("desc")[0];
				String shopID = map.get("shopID")[0];
				String typeNumber = map.get("typeNumber")[0];
				String gid = UUID.randomUUID().toString();
				gid = gid.replace("-", "");
				String[] keyArray = {"gname","price","desc","shopID","typeNumber","gid"};
				String[] valueArray = {gname,price,desc,shopID,typeNumber,gid};
				Map<String, Object> shopMap = new HashMap<>();
				for (int i = 0; i < valueArray.length; i++) {
					
					shopMap.put(keyArray[i], valueArray[i]);
				}
				if (checkStrArray(valueArray)) {
					 if (manager.addData("shoosedb",shopMap)) {
						 out.write(gid);
						}else
						{
							out.write("-1");
						}
				}else
				{
					out.write("-1");
				}
			
			}else if (actionStr.equals("add40xmessage")) {
			
				
				  Date date=new Date();
	
				  SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 String nowTime = simpleDateFormat2.format(date);
				 
				 String name = map.get("name")[0];
				 String address = map.get("address")[0];
				 String point = map.get("point")[0];
				 Map<String, Object> addmap = new HashMap<>();
				 addmap.put("Name", name);
				 addmap.put("addTime", nowTime);
				 addmap.put("address", address);
				 addmap.put("Point", point);
			  boolean isSuccess = manager.addData("409Message", addmap);
			  if (isSuccess) {
			 out.write("1");
			}else
			{
				 out.write("-1");
			}
			}else if (actionStr.equals("get40xMessage")) {
				String pageIndex = map.get("pageIndex")[0];
				String pageSize = map.get("pageSize")[0];
			try {
				String string = manager.getTableAllDataFromEndReturnJsonArray("409Message",Integer.parseInt(pageIndex), Integer.parseInt(pageSize), "ID").toString();
				out.write(string);
			} catch (NumberFormatException | JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				out.write("-1");
			}
			}
			else if(actionStr.equals("change40xName")){
				   String id = map.get("id")[0];
				   String name = map.get("name")[0];
				   Map<String, String> conditionMap = new HashMap<>();
				   conditionMap.put("ID", id);
				   Map<String, String> changeMap = new HashMap<>();
				   changeMap.put("Name", name);
				   boolean issuccess = manager.changeDataAccording("409Message", conditionMap, changeMap);
				}
			
			}
			
			
		
		
		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	 public boolean checkStrArray(String[] strArray) {
		for (String str: strArray) {
			if (str == null) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(request, response);
	}

}
