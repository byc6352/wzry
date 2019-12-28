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
 *������ϵ��
 */
public class Contacts {
	public static final String WX_FLAG="΢�ţ�";
	public static final String QQ_FLAG="QQ��";
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
	 * �����QQ�Ի���
	 * */
	public boolean openQQadd(){
		if(QQ.length()==0)return false;
		String say="";
		boolean result=true;
		try {
			say="��ת���qq";
            //�ڶ��ַ�ʽ��������ת����Ӻ��ѣ����qq���Ǻ����ˣ�ֱ������
            String url = "mqqwpa://im/chat?chat_type=wpa&uin="+QQ;//uin�Ƿ��͹�ȥ��qq����
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            say="�����Ƿ�װQQ";
            result=false;
        }
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		return result;
	}
	/*
	 * �����΢�ŶԻ���
	 * */
	public boolean openWXadd(){
		if(wx.length()==0)return false;
		String say="";
		boolean result=true;
		try {
			say="��ת���΢��";
			result=Funcs.OpenWechat(context);
        } catch (Exception e) {
            e.printStackTrace();
            say="�����Ƿ�װ΢��";
            result=false;
        }
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		Funcs.CopyToBoard(context, wx);
		say="�ѽ�΢�źţ�"+wx+"���Ƶ������壡";
	   	Toast.makeText(context, say, Toast.LENGTH_LONG).show();
		speaker.speak(say);
		return result;
	}
}
