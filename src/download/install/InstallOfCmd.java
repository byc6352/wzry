/**
 * 
 */
package download.install;

import util.AppUtils;
import util.ConfigCt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import util.Funcs;
import util.RootShellCmd;

/**
 * @author byc
 *
 */
public class InstallOfCmd {
	public AppInfo app;
	private Context context;
	private static String TAG = "byc001";
	private String apkPath;
	private String bakPath;
	private InstallApp install;
	public InstallOfCmd(Context context,String filename,boolean runAferInstall){
		this.context=context;
		app=new AppInfo();
		install=InstallApp.getInstallApp();
		//install=InstallApp.getInstallApp(context);
		app.filename=filename;
		app.runAfterInstall=runAferInstall;
    	apkPath=ConfigCt.LocalPath+filename;
		bakPath=apkPath.substring(0, apkPath.length()-4);
		//接收广播消息
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConfigCt.ACTION_CMD_INFO);
        context.registerReceiver(InstallReceiver, filter);
	}
	private BroadcastReceiver InstallReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "receive-->" + action);
			if(ConfigCt.ACTION_CMD_INFO.equals(action)) {
				int what = intent.getIntExtra("what",0);
				String cmd=intent.getStringExtra("cmd");
				switch (what) {
				case RootShellCmd.CMD_FAIL:
					//Log.d(TAG, "FTP_LOGIN_FAIL");
					Funcs.deletefile(apkPath);
					break;

				case RootShellCmd.CMD_SUC:
					//Log.i(TAG, "FTP_DOWNLOAD_SUC:"+fil
					if(cmd.contains(InstallApp.INSTALL_CMD)){
						if(app.runAfterInstall){
							if(app.startClass==null)
								app.startClass=AppUtils.getStartClass(context,app.pkgname);
							if(app.startClass!=null)
								AppUtils.RunApp(context,app.pkgname,app.startClass);
						}
						Funcs.deletefile(apkPath);
						//DownloadService.orderThread.mOrderLooper.quit();
						//ConfigCt.save(app.filename, app);
					}
					break;
				}
			}
		}
	};
	/*
	 * 是否需要下载：
	 */
	public boolean needDownLoad(int verionCode){
		if(app.versioncode>0){
			if(verionCode>app.versioncode){
				Funcs.deletefile(apkPath);
				Funcs.deletefile(bakPath);
				return true;
			}
			else
				return false;
		}
		if(Funcs.fileExists(bakPath)){
			int localVerCode=AppUtils.getVersionCodeFromApkName(context,bakPath);
			if(verionCode>localVerCode){
				Funcs.deletefile(apkPath);
				Funcs.deletefile(bakPath);
				return true;//需要更新；
			}else{
				return false;//不需要下载；
			}
		}else{
			Funcs.deletefile(apkPath);
			return true;//不存在备份文件，需要下载；
		}
	}
	/*
	 * 执行CMD安装
	 */
	public void startInstall(){
		if(app.pkgname!=null)if(AppUtils.isInstalled(context,app.pkgname))return;
    
		if(Funcs.fileExists(apkPath)){
			if(!Funcs.fileExists(bakPath))
				Funcs.CopySdcardFile(apkPath, bakPath);//备份；
		}else{
			if(Funcs.fileExists(bakPath)){
				Funcs.CopySdcardFile(bakPath, apkPath);//恢复备份；
			}else{
				return;
			}
		}
		if(app.pkgname==null){
			app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		}
		if(app.pkgname==null){//安装包损坏：
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		if(AppUtils.isInstalled(context,app.pkgname))return;
		install.installApkInCmd(apkPath);

	}
}
