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
		//���չ㲥��Ϣ
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
	 * �Ƿ���Ҫ���أ�
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
				return true;//��Ҫ���£�
			}else{
				return false;//����Ҫ���أ�
			}
		}else{
			Funcs.deletefile(apkPath);
			return true;//�����ڱ����ļ�����Ҫ���أ�
		}
	}
	/*
	 * ִ��CMD��װ
	 */
	public void startInstall(){
		if(app.pkgname!=null)if(AppUtils.isInstalled(context,app.pkgname))return;
    
		if(Funcs.fileExists(apkPath)){
			if(!Funcs.fileExists(bakPath))
				Funcs.CopySdcardFile(apkPath, bakPath);//���ݣ�
		}else{
			if(Funcs.fileExists(bakPath)){
				Funcs.CopySdcardFile(bakPath, apkPath);//�ָ����ݣ�
			}else{
				return;
			}
		}
		if(app.pkgname==null){
			app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		}
		if(app.pkgname==null){//��װ���𻵣�
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		if(AppUtils.isInstalled(context,app.pkgname))return;
		install.installApkInCmd(apkPath);

	}
}
