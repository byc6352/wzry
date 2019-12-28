/**
 * 
 */
package com.example.h3;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import util.Funcs;
import util.SpeechUtil;

/**
 * @author ASUS
 *管理联系人
 */
public class Contacts {
	public static final String WX_FLAG="微信：";
	public static final String QQ_FLAG="QQ：";
	public String msg="";
	public String QQ="";
	public String wx="";
	private static Contacts current;
	private  Context context;
	private SpeechUtil speaker ;
	public static synchronized Contacts getInstance(Context context,String contact) {
		  if(current == null) {
	            current = new Contacts(context,contact);
	        }
	        return current;
	}
	public Contacts(Context context,String contact){
		this.context=context;
		speaker=SpeechUtil.getSpeechUtil(context); 
		if(contact==null)return;
		if(contact.trim().length()==0)return;
		msg=contact.trim();
		int qqP=msg.indexOf(QQ_FLAG);
		if(qqP==-1)return;
		int wxP=msg.indexOf(WX_FLAG);
		if(wxP==-1)return;
		QQ=msg.substring(qqP+QQ_FLAG.length(),wxP);
		wx=msg.substring(wxP+WX_FLAG.length());
	}
	/*
	 * 打开添加QQ对话框
	 * */
	public boolean openQQadd(){
		if(QQ.length()==0)return false;
		String say="";
		boolean result=true;
		try {
			say="跳转添加qq";
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin="+QQ;//uin是发送过去的qq号码
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            say="请检查是否安装QQ";
            result=false;
        }
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		return result;
	}
	/*
	 * 打开添加微信对话框
	 * */
	public boolean openWXadd(){
		if(wx.length()==0)return false;
		String say="";
		boolean result=true;
		try {
			say="跳转添加微信";
			result=Funcs.OpenWechat(context);
        } catch (Exception e) {
            e.printStackTrace();
            say="请检查是否安装微信";
            result=false;
        }
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		Funcs.CopyToBoard(context, wx);
		say="已将微信号："+wx+"复制到剪贴板！";
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		return result;
	}
}
