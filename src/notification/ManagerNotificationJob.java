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
	 /**����֪ͨ���¼�*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void handeNotificationPosted(IStatusBarNotification mysbn) {
    	//Log.i(TAG, "����--------------------->"+mysbn.getPackageName());
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
     * (������������)
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
    //----------------------------------�¼�����----------------------------------------
    /*
     * (�¼���������)
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
    *���Դ�ӡ
    */
   private void debug(IStatusBarNotification mysbn){
     	if(ConfigCt.DEBUG){
     		Log.i(TAG, "Notification������--------------------->"+mysbn.getPackageName());
     		Log.i(TAG, "Notification���ݣ�--------------------->"+mysbn.getNotification().tickerText);
     	}
   }
    
    
    
    
    
    
    
    
    
    
    
    
    
 
}
