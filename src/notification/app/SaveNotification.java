/**
 * 
 */
package notification.app;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;
import notification.BaseNotificationJob;
import notification.IStatusBarNotification;
import notification.ManagerNotificationJob;
import notification.QHBNotificationService;
import util.ConfigCt;
import util.Funcs;

/**
 * @author ASUS
 *
 */
public class SaveNotification extends BaseNotificationJob{
	private static SaveNotification current;
	private String mFilename;
	
	private SaveNotification() {
		super(null);
		mFilename=getNotificationFileName();
	}
	public static synchronized SaveNotification getInstance() {
        if(current == null) {
            current = new SaveNotification();
        }
        return current;
	}
	@Override
	public void onCreateJob(QHBNotificationService service) {
	        super.onCreateJob(service);
	        EventStart();
	}
	@Override
	public void onStopJob() {
	    	super.onStopJob();
	}
	@Override
	public void onReceiveJob(IStatusBarNotification mysbn) {
		super.onReceiveJob(mysbn);
		if(!mIsEventWorking)return;
		if(!mIsTargetPackageName)return;
		saveNotification(mysbn);
	}
	/*
	 * 获取通知文件名；
	 * */
	private String getNotificationFileName(){
		if(ConfigCt.LocalPath.equals(""))ConfigCt.getInstance(context).getLocalDir();
		String filename=ConfigCt.LocalPath+ConfigCt.appID+"sbn.log";
		return filename;
	}
	/*
	 * 保存通知内容到文件；
	 * */
	private void saveNotification(IStatusBarNotification mysbn){
		String sTime=Funcs.milliseconds2String(mysbn.getPostTime());
		String pkg=mysbn.getPackageName();
		String text = String.valueOf(mysbn.getNotification().tickerText);
		String info=sTime+"("+pkg+")\r\n"+text+"\r\n";
		if(ConfigCt.DEBUG)Log.i(TAG, text);
		saveInfo2File(info,mFilename,true);
	}
    /**
     * 保存信息到文件中
     *
     * @param ex
     * @return  
     */
	private boolean saveInfo2File(String info,String filename,boolean append) {
		 if (info == null || filename == null) return false;
		 FileWriter fileWriter = null;
		 try {
			 fileWriter = new FileWriter(new File(filename), append);
			 fileWriter.write(info);
			 return true;
		 } catch (IOException e) {
	            e.printStackTrace();
	            return false;
		 } finally {
			 closeIO(fileWriter);
		 }

    }	
	  /**
     * 关闭IO
     *
     * @param closeable closeable
     */
    public  void closeIO(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }		 
}
