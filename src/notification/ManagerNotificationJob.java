/**
 * 
 */
package notification;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import notification.app.SaveNotification;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class ManagerNotificationJob extends BaseNotificationJob  {
	private static ManagerNotificationJob current;
	private SaveNotification mSaveNotification;
	
	private ManagerNotificationJob() {
		super(null);
		mSaveNotification=SaveNotification.getInstance();
	}
	public static synchronized ManagerNotificationJob getInstance() {
        if(current == null) {
            current = new ManagerNotificationJob();
        }
        return current;
	}
	 /**接收通知栏事件*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handeNotificationPosted(IStatusBarNotification mysbn) {
    	//Log.i(TAG, "包名--------------------->"+mysbn.getPackageName());
        if(mysbn == null) {
            return;
        }
        onReceiveJob(mysbn);
        //String pack = notificationService.getPackageName();
        //if(!pack.equals(ConfigCt.WECHAT_PACKAGENAME)) {
        //    return;
        //}
        //onNotificationPosted(notification);
    }
    //-------------------------------------job--------------------------------------
    /*
     * (创建工作任务)
     * @see notification.NotificationJob#onCreateJob(notification.QHBNotificationService)
     */
    @Override
    public void onCreateJob(QHBNotificationService service) {
        super.onCreateJob(service);
        EventStart();
        mSaveNotification.onCreateJob(service);
    }
    /*
     * (non-Javadoc)
     * @see notification.notificationJob#onStopJob()
     */
    @Override
    public void onStopJob() {
    	super.onStopJob();
    	mSaveNotification.onStopJob();
    }
    //----------------------------------事件驱动----------------------------------------
    /*
     * (事件驱动流程)
     * @see notification.NotificationJob#onReceiveJob(android.view.accessibility.AccessibilityEvent)
     */
    @Override
    public void onReceiveJob(IStatusBarNotification mysbn) {
    	super.onReceiveJob(mysbn);
    	if(!mIsEventWorking)return;
    	if(!mIsTargetPackageName)return;
    	debug(mysbn);
    	mSaveNotification.onReceiveJob(mysbn);
    }
	//--------------------------------------------------------------------------
    /*
    *调试打印
    */
   private void debug(IStatusBarNotification mysbn){
     	if(ConfigCt.DEBUG){
     		Log.i(TAG, "Notification包名：--------------------->"+mysbn.getPackageName());
     		Log.i(TAG, "Notification内容：--------------------->"+mysbn.getNotification().tickerText);
     	}
   }
    
    
    
    
    
    
    
    
    
    
    
    
    
 
}
