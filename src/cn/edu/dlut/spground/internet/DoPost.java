package cn.edu.dlut.spground.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import cn.edu.dlut.spground.global.LoginInfo;
import cn.edu.dlut.spground.global.UserInfo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;

//post线程类
public class DoPost implements Runnable {

	//接受参数httpclient,handler
	private HttpClient postClient;
	private Handler sendHandler;
	private String url;
	private String cookieValue="";
	private String sessionID="";
	private String userName,passWord;
	private HttpPost post;
	private Boolean rememberme;
	private  int event;//,0首次登陆，1为cookie登陆,2为不需要权限的post操作
	//构造函数0,用于登陆使用
	public DoPost(HttpClient postClient,Handler sendHandler,String url,String userName,String passWord,Boolean rememberme,int event)
	{
		
		this.postClient=postClient;
		this.sendHandler=sendHandler;
		this.url=url;
		this.userName=userName;
		this.passWord=passWord;
		this.event=event;
		this.rememberme=rememberme;
		
	}
	//构造函数1用于cookie登陆使用
	public DoPost(HttpClient postClient,Handler sendHandler,String url,int event)
	{
		this.postClient=postClient;
		this.sendHandler=sendHandler;
		this.url=url;
		this.event=event;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//如果是登陆时事件++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(event==0)
		{
			//开始加密密码，生成密码摘要
			//得到时间戳
			String nowtime=getTimeTag();
			passWord=doEncrypt(passWord,nowtime);
			post=new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid",userName));
			params.add(new BasicNameValuePair("password",passWord));
			params.add(new BasicNameValuePair("nowtime",nowtime));
			if(rememberme)//如果选择记住密码，就做好存放服务器的cookie的准备
		       params.add(new BasicNameValuePair("cookie","remember-me"));//如果用户勾选记住密码
			
				
			try {
				//发送post请求,获得回复实例
				post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
				HttpResponse response = postClient.execute(post);
				//判断http通讯完成
				if(response.getStatusLine().getStatusCode()==200)
				{
					//服务器返回的页面内容，据此判断是否登陆成功。。这个有待改进
					HttpEntity entity = response.getEntity();
					BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = "";
					String tem=null;
					while((tem = br.readLine())!=null)
					{
						line+=tem;
					}
					//取出cookie的全部值
					List<Cookie> cookies=((AbstractHttpClient)postClient).getCookieStore().getCookies();
					//为cookieValue和sessionID赋值
					UserInfo userInfo=new UserInfo();
					for(int i=0;i<cookies.size();i++)
					{
						if(cookies.get(i).getName().equals("SESSION_LOGIN_USERNAME"))
							{
							   cookieValue=cookies.get(i).getValue();
							   
							}
			            if(cookies.get(i).getName().equals("JSESSIONID"))
			            	{
			            	   sessionID=cookies.get(i).getValue();
			            	  
					        }
					}
					String str="";
					if(!line.contains("首页"))
						        str="false";//登录失败 
					else//首次登陆成功
					{
						if(rememberme)//选择记住我才将cookie值塞入文件
						     userInfo.setcookieValue(cookieValue);//赋值
						LoginInfo.sessionID=sessionID;//会话值
						str="cookie:"+cookieValue+"\n"+"SessionID"+sessionID;
					}
					Message msg1 = new Message();
					msg1.obj=str;
					msg1.what=0x123;
					Looper.prepare();
					sendHandler.sendMessage(msg1);
					Looper.loop();
					
				}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		else if(event==1)//用cookie登陆
		{
			
			post=new HttpPost(url);
			addCookieHeader(post);
			try {
				HttpResponse response = postClient.execute(post);
				
				if(response.getStatusLine().getStatusCode()==200)
				{
					//服务器返回的页面内容，据此判断是否登陆成功。。这个有待改进
					HttpEntity entity = response.getEntity();
					BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = "";
					String tem=null;
					while((tem = br.readLine())!=null)
					{
						line+=tem;
					}
					//取出cookie的全部值
					List<Cookie> cookies=((AbstractHttpClient)postClient).getCookieStore().getCookies();
					for(int i=0;i<cookies.size();i++)
					{
						if(cookies.get(i).getName().equals("SESSION_LOGIN_USERNAME"))
							{
							   cookieValue=cookies.get(i).getValue();
							   
							}
			            if(cookies.get(i).getName().equals("JSESSIONID"))
			            	{
			            	   sessionID=cookies.get(i).getValue();
			            	  
					        }
					}
					String str="";
					if(!line.contains("首页"))
						        str="false";//登录失败 
					else//首次登陆成功
					{
						//if(rememberme)//选择记住我才将cookie值塞入文件
						     //userInfo.setcookieValue(cookieValue);//赋值
						LoginInfo.sessionID=sessionID;//会话值
						str="cookie:"+cookieValue+"\n"+"SessionID"+sessionID;
					}
					
					Message msg1 = new Message();
					msg1.obj=str;
					msg1.what=0x1235;
					Looper.prepare();
					sendHandler.sendMessage(msg1);
					Looper.loop();
					
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			
		}
		else if(event==2)//无需权限的post请求
		{
			System.out.println("通讯开始");
			System.out.println("通讯开始");
			post=new HttpPost(url);
			//HttpGet hG=new HttpGet(url);
			try {
				
				BasicNameValuePair t1=new BasicNameValuePair("dataType","json");
		      	BasicNameValuePair t2=new BasicNameValuePair("sport_id","-1");
		      	List<NameValuePair> parames =new ArrayList<NameValuePair>();
		        parames.add(t2);
		      	parames.add(t1);
				post.setEntity(new UrlEncodedFormEntity(parames,HTTP.UTF_8));
				post.setHeader("Cookie", LoginInfo.sessionID);
				//回复实例
				HttpResponse hR=postClient.execute(post);
				if(hR.getStatusLine().getStatusCode()==200)
				{
					System.out.println("通讯完成");
					String str="",temp;
					HttpEntity response=hR.getEntity();
					//获得内容的缓冲字符流
					BufferedReader br=new BufferedReader(new InputStreamReader(response.getContent()));
					while((temp=br.readLine())!=null)
					{
						str+=temp;;
						
					}
					Message msg=new Message();
					//发送消息
					msg=new Message();
					msg.what=0x1234;
					msg.obj=str;
					Looper.prepare();
					this.sendHandler.sendMessage(msg);
					Looper.loop();
				}
				
				
				
				
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		/*else if(event==3)
		{
			HttpGet get=new HttpGet(url);
			
           try {
				
				
				get.setHeader("Cookie","JSESSIONID="+ LoginInfo.sessionID);
				System.out.println("JSESSIONID="+ LoginInfo.sessionID);
				//回复实例
				HttpResponse hR=(new DefaultHttpClient()).execute(get);
				if(hR.getStatusLine().getStatusCode()==200)
				{
					System.out.println("通讯完成");
					String str="",temp;
					HttpEntity response=hR.getEntity();
					//获得内容的缓冲字符流
					BufferedReader br=new BufferedReader(new InputStreamReader(response.getContent()));
					while((temp=br.readLine())!=null)
					{
						str+=temp;;
						
					}
					Message msg=new Message();
					//发送消息
					msg=new Message();
					msg.what=0x123;
					msg.obj=str;
					Looper.prepare();
					this.sendHandler.sendMessage(msg);
					Looper.loop();
				}
				
				
				
				
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		
	}

	//得到当前时间戳
	private String getTimeTag()
	{
		//取系统当前时间
		String nowtime;
    	Time time=new Time();
    	time.setToNow();
    	int year =time.year;
    	int month=time.month;
    	int day=time.monthDay;
    	int hour = time.hour; // 0-23  
    	int minute = time.minute;  
    	int second = time.second; 
    	//合成时间字符串字符串
    	nowtime=Integer.toString(year)+Integer.toString(month)+Integer.toString(day)+Integer.toString(hour)+Integer.toString(minute)+Integer.toString(second);
    	return nowtime;
	
	}
	//对密码进行SHA-256加密
	private String doEncrypt(String passWord,String nowtime)
	{
		//先对密码做一次摘要
		String tempPassWord=SHA.getSHA(passWord);
		//密码加上时间戳做一次摘要
		String result=SHA.getSHA(tempPassWord+nowtime);
		return result;
	}
	//增加cookie首部行字段
	private void addCookieHeader(HttpPost post)
	{
		UserInfo userInfo=new UserInfo();
		post.setHeader("Cookie", userInfo.getcookieName()+"="+userInfo.getcookieValue()+";");
		
	}
	
	
	
}
//SHA算法类
class SHA {
	
	private static String Encrypt(String strSrc, String encName) {
	    MessageDigest md = null;
	    StringBuilder sb = new StringBuilder();
	 //将传进来的字符串变成字节数组
	    byte[] bt = strSrc.getBytes();
	    try {
	    	//新建一个算法加密实例
	        md = MessageDigest.getInstance(encName);
	        //加密结果返回一个字节数组
	        byte[] result = md.digest(bt);
	        //将字节数组以16进制输出字符串
	        for (byte b : result) {
	            sb.append(String.format("%02x", b));
	        }
	    } catch (NoSuchAlgorithmException e) {
	        return null;
	    }
	    return sb.toString();
	}
	public static String getSHA(String str)
	{
		
		return Encrypt(str,"SHA-256");
	}
}
