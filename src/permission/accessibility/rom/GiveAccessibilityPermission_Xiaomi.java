/**
 * 
 */
package permission.accessibility.rom;

import accessibility.BaseAccessibilityJob;

import accessibility.QiangHongBaoService;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import activity.SplashActivity;

import accessibility.AccessibilityHelper;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class GiveAccessibilityPermission_Xiaomi extends BaseAccessibilityJob  {
	   private static GiveAccessibilityPermission_Xiaomi current;
	   //private static boolean bWorking=false;
	   private static boolean bForward=true;
	   private static final String SETTING_PACKAGENAME="com.android.settings";
	   private static final String BUTTON_CLASS_NAME="android.widget.Button";
	   private static final String CHECK_CLASS_NAME="android.widget.CheckBox";
	   private static final String LIST_CLASS_NAME="android.widget.ListView";
	   private static final String IMAGE_CLASS_NAME="android.widget.ImageView";
	   private static final String LAYOUT_CLASS_NAME="android.widget.LinearLayout";
	   private static final String WINDOW_NOTIFICATION_ACCESS_UI="com.android.settings.Settings$NotificationAccessSettingsActivity";
	   private static final String WINDOW_ACCESSBILITY_ACCESS_UI="com.android.settings.Settings$AccessibilitySettingsActivity";
	   private static final String WINDOW_ACCESSBILITY_SUBSETTINGS_UI="com.android.settings.SubSettings";
	   private static final String WINDOW_ACCESSBILITY_DIALOG_UI="android.app.AlertDialog";
	   private String mCurrentUI="";
	   private String mDescription;
	   private String mAppName;
	    
	    private GiveAccessibilityPermission_Xiaomi() {
	    	super(new String[]{SETTING_PACKAGENAME});
	    }
	    public static synchronized GiveAccessibilityPermission_Xiaomi getGiveAccessibilityPermission_Xiaomi() {
	        if(current == null) {
	            current = new GiveAccessibilityPermission_Xiaomi();
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
	    @Override
	    public void onReceiveJob(AccessibilityEvent event) {
	    	super.onReceiveJob(event);
	    	if(!mIsEventWorking)return;
	    	if(!mIsTargetPackageName)return;
	    	//Log.i(TAG2, event.getPackageName().toString());
	    	if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED||eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
	    		AccessibilityNodeInfo nodeInfo = event.getSource();
	    		if (nodeInfo == null)return;
	    		nodeInfo=AccessibilityHelper.getRootNode(nodeInfo);
	    		//recycleClick(nodeInfo);
	    	}
	    }
		/*
		 * (ˢ�´�������)
		 * @see accessbility.AccessbilityJob#onWorking()
		 */
		@Override
		public void onWorking(){
	    	if(service==null)return;
	    	AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
	    	if(rootNode==null)return;
	    	recycleClick(rootNode);
	    	
		}
		private void recycleClick(AccessibilityNodeInfo rootNode){
	    	findAndClickAppName(rootNode);
	    	clickServerButton(rootNode);
	    	clickOKonDialog(rootNode);
	    	clickStopOnDialog(rootNode);
	    	//closeTimeWorking();}
		}
	    public void onProcessEvent(AccessibilityEvent event) {
	    	//debug();
	    	if(true)return;
	    	//if(!bWorking)return;
	    	//��������
	    	if(!event.getPackageName().toString().equals(SETTING_PACKAGENAME))return;
	    	int eventType = event.getEventType();
	    	if(event.getClassName()==null)return;
	    	String sClassName=event.getClassName().toString();
	    	if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
	    		mCurrentUI=sClassName;
	    	}
			//+++++++++++++++++++++++++++++++++���ڸı�+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
				mCurrentUI=sClassName;
				//+++++++++++++++++++++++++++++++++1����setting���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_ACCESS_UI)){
					//if(!bForward){AccessibilityHelper.performBack(service);bWorking=false;return;}
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null) return;
				     findAndClickAppName(rootNode);
				}
				//+++++++++++++++++++++++++++++++++1����subsetting���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_SUBSETTINGS_UI)){
					//if(!bForward){AccessibilityHelper.performBack(service);return;}
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null) return;
				     clickServerButton(rootNode);
				}
				//+++++++++++++++++++++++++++++++++1����dialog���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_DIALOG_UI)){
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null) return;
				     clickOKonDialog(rootNode);
				     clickStopOnDialog(rootNode);
				}
			}
			//+++++++++++++++++++++++++++++++++���ݸı�+++++++++++++++++++++++++++++++++++++++++++++++
			if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				//+++++++++++++++++++++++++++++++++1����setting���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_ACCESS_UI)){
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null)return;
				     rootNode =AccessibilityHelper.getRootNode(rootNode);
				     findAndClickAppName(rootNode);
				}
				//+++++++++++++++++++++++++++++++++1����subsetting���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_SUBSETTINGS_UI)){
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null)return;
				     rootNode =AccessibilityHelper.getRootNode(rootNode);
				     clickServerButton(rootNode);
				}
				//+++++++++++++++++++++++++++++++++1����dialog���壺++++++++++++++++++++++++++++++++++++++++++
				if(mCurrentUI.equals(WINDOW_ACCESSBILITY_DIALOG_UI)){
					 AccessibilityNodeInfo rootNode = event.getSource();
				     if (rootNode == null)return;
				     rootNode =AccessibilityHelper.getRootNode(rootNode);
				     clickOKonDialog(rootNode);
				     clickStopOnDialog(rootNode);
				}
			}
	    }

	    /*
	     * �ж��Ƿ���setting���壻
	     */
	    public boolean isAccessibilitySettingsActivity(AccessibilityNodeInfo rootNode ){
	    	AccessibilityNodeInfo titleNode1=AccessibilityHelper.findNodeInfosByText(rootNode, "���ϰ�",0);
	    	AccessibilityNodeInfo titleNode2=AccessibilityHelper.findNodeInfosByText(rootNode, "��������",0);
	    	if(titleNode1==null&&titleNode2==null)return false;
	    	//AccessibilityNodeInfo listNode=AccessibilityHelper.findNodeInfosByClassName(rootNode, LIST_CLASS_NAME, 0, true);
	    	//if(listNode==null)return false;
	    	return true;
	    }
	    /*
	     * �����ض�APP�������
	     */
	    public void findAndClickAppName(AccessibilityNodeInfo rootNode ){
	    	if(!isAccessibilitySettingsActivity(rootNode))return;
	    	if(!bForward){
	    		clickCancelImageButton(rootNode);
	    		closeTimeWorking();//bWorking=false;
    			 return;
	    	}
			  AccessibilityNodeInfo appNode=AccessibilityHelper.findNodeInfosByTextAllMatched(rootNode, mAppName);
			  if(appNode==null){
				  AccessibilityNodeInfo sysNode=AccessibilityHelper.findNodeInfosByTextAllMatched(rootNode, "ϵͳ");
				  if(sysNode==null){
					  AccessibilityNodeInfo listNode=AccessibilityHelper.findNodeInfosByClassName(rootNode, LIST_CLASS_NAME, 0, true);
					  if(listNode==null)return;
					  AccessibilityHelper.performScrollForward(listNode);//���¹���;
				  }else{
					  return;//δ�ҵ�appName;
				  }
			  }else{//�ҵ�;
				  AccessibilityHelper.performClick(appNode);//���;
			  }
	    }
		/*
		     * ��app����
		*/
		public void clickServerButton(AccessibilityNodeInfo rootNode ){
			  AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByTextAllMatched(rootNode, mAppName);
			  if(nodeInfo==null)return;	
			  if(!mDescription.equals("")){
				  nodeInfo=AccessibilityHelper.findNodeInfosByTextAllMatched(rootNode, mDescription);
				  if(nodeInfo==null)return;	
			  }
			  //nodeInfo=AccessibilityHelper.findNodeInfosByTextAllMatched(rootNode, "��������");
			  nodeInfo=AccessibilityHelper.findNodeInfosByClassName(rootNode, CHECK_CLASS_NAME, 0, true);
			  if(nodeInfo==null)return;	
		    if(!bForward){
		    	clickCancelImageButton(rootNode);
		    	return;
		    }
		    if(nodeInfo.isChecked()){
		    	bForward=false;
		    	clickCancelImageButton(rootNode);
	    		return;
		    }
			AccessibilityHelper.performClick(nodeInfo);//���;
		}
		/*
	     * ������ذ�ť��
	     */
		public void clickCancelImageButton(AccessibilityNodeInfo rootNode ){
			AccessibilityNodeInfo imageNode=AccessibilityHelper.findNodeInfosByClassName(rootNode, IMAGE_CLASS_NAME, 0, true);
	    	if(imageNode!=null){
	    		AccessibilityHelper.performClick(imageNode);
	    	}else{
	    		AccessibilityHelper.performBack(service);
	    	}
		}
		/*
	     * ��app����
	     */
		public void clickOKonDialog(AccessibilityNodeInfo rootNode ){
			  //AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�������񽫻᣺",0);
			  //if(nodeInfo==null)return;	
			  AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "������Ĳ���",0);
			  if(nodeInfo==null)return;
			  nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "������������",0);
			  if(nodeInfo==null)return;
			  nodeInfo=AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "ȷ��",BUTTON_CLASS_NAME);
			  if(nodeInfo==null)return;
			  AccessibilityHelper.performClick(nodeInfo);//���;
			  bForward=false;
			  closeTimeWorking();//bWorking=false;
			  SplashActivity.startHomeActivity(context);
		}
		/*
	     * ��app����
	     */
		public void clickStopOnDialog(AccessibilityNodeInfo rootNode ){
			  //AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�������񽫻᣺",0);
			  //if(nodeInfo==null)return;	
			  AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "Ҫͣ��",0);
			  if(nodeInfo==null)return;
			  nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "�㰴��ȷ��������",0);
			  if(nodeInfo==null)return;
			  nodeInfo=AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "ȷ��",BUTTON_CLASS_NAME);
			  if(nodeInfo==null)return;
			  AccessibilityHelper.performClick(nodeInfo);//���;
		}
		/*
	     * ��Accessibility���棻
	     */
		public void openAccessibilitySettings(Context context){
			Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	    //--------------------------------------------------------------------------------------------------------------
	    /*
	     * ��ʼ����
	     */
		public void Start(String appName,String description){
    		if(isEnable()){
    			bForward=true;
   			  	mAppName=appName;
   			  	mDescription=description;
    			//EventStart();
    			TimeStart();
    			openAccessibilitySettings(service);
    		}		  	
		}
	    private void debug() {
	    	if(!ConfigCt.DEBUG)return;
	    	Log.i("byc002","mCurrentUI="+mCurrentUI);
	    	///Log.i("byc002","bWorking="+bWorking);
	    	Log.i("byc002","bForward="+bForward);
	    }
}
