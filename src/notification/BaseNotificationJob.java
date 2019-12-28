/**
 * 
 */
package notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BaseNotificationJob implements NotificationJob {
	//程序标识
	protected String TAG ;	
	protected String TAG2 ;
	public Context context;
	protected String[] mPkgs;//所处理的包;
	protected QHBNotificationService service;
	protected boolean mIsEventWorking=false;//是否开始事件处理；
	protected boolean mIsTargetPackageName=false;//是否是本程序处理包；
    public  BaseNotificationJob(String[] pkgs) {
        TAG=ConfigCt.TAG;
        TAG2=ConfigCt.TAG2;
        mPkgs=pkgs;
    }
    /*
     * (创建工作任务)
     * @see notification.NotificationJob#onCreateJob(notification.QHBNotificationService)
     */
    @Override
    public void onCreateJob(QHBNotificationService service) {
        this.service = service;
        TAG=ConfigCt.TAG;
        TAG2=ConfigCt.TAG2;
        context=service.getApplicationContext();
    }
    /*
     * (non-Javadoc)
     * @see notification.notificationJob#onStopJob()
     */
    @Override
    public void onStopJob() {
    	service=null;
    	context=null;
    }
    @Override
    public boolean isEnable(){
    	if(service==null)
    		return false;
    	else
    		return true;
    }

    //----------------------------------pkg处理------------------------------
    /*
     * (是否是所处理的包?)
     * @see accessbility.AccessbilityJob#isTargetPackageName(java.lang.String)
     */
    @Override
    public boolean isTargetPackageName(String pkg){
    	if(mPkgs==null)return true;
    	if(mPkgs.length==0)return true;
    	if(pkg==null||pkg.equals(""))return false;
    	for(int i=0;i<mPkgs.length;i++){
    		if(mPkgs[i].equals(pkg))return true;
    	}
    	return false;
    }
    /*
     * (返回所处理的包)
     * @see notification.notificationJob#getTargetPackageName()
     */
    @Override
    public String[] getTargetPackageName(){
    	return mPkgs;
    }
    /*
     * (设置所处理的包)
     * @see notification.NotificationJob#setTargetPackageName()
     */
    @Override
    public void  setTargetPackageName(String[] pkgs) {
    	this.mPkgs=pkgs;
    }
    //----------------------------------事件驱动----------------------------------------
    /*
     * (事件驱动流程)
     * @see notification.NotificationJob#onReceiveJob(android.view.accessibility.AccessibilityEvent)
     */
    @Override
    public void onReceiveJob(IStatusBarNotification mysbn) {
    	
    	if(!mIsEventWorking)return;
    	
    	if(!isTargetPackageName(mysbn.getPackageName().toString())){
    		mIsTargetPackageName=false;
    		return;
    	}
    	mIsTargetPackageName=true;
    	
    }
    /*
     * (non-Javadoc)
     * @see notification.NotificationJob#EventStart()
     */
    @Override
    public void EventStart(){
    	mIsEventWorking=true;
    }
    /*
     * (non-Javadoc)
     * @see notification.NotificationJob#closeEventWorking()
     */
    @Override
    public void closeEventWorking(){
    	mIsEventWorking=false;
    }
}
