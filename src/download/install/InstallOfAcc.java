/**
 * 
 */
package download.install;

import util.AppUtils;
import util.ConfigCt;
import accessibility.QiangHongBaoService;

import android.content.Context;
import android.os.Handler;
import util.Funcs;

/**
 * @author byc
 *
 */
public class InstallOfAcc {
	public AppInfo app;
	private Context context;
	private String apkPath;
	private String bakPath;
	private InstallApp install;
	public InstallOfAcc(Context context,String filename){
		this.context=context;
		app=new AppInfo();
		//install=InstallApp.getInstallApp(context);
		install=InstallApp.getInstallApp();
		app.filename=filename;
		//app.runAfterInstall=runAferInstall;
    	apkPath=ConfigCt.LocalPath+filename;
		bakPath=apkPath.substring(0, apkPath.length()-4);

	}
	Handler handlerInstall = new Handler();    
	Runnable runnable = new Runnable() {    
		@Override    
	    public void run() {    
			if(QiangHongBaoService.isRunning()) {
	        	install.smartInstall(app);
	        	//DownloadService.orderThread.mOrderLooper.quit();
	        	return;
	        }
			handlerInstall.postDelayed(this, 1000*10);    
	    }    
	};
	/*
	 * 是否需要下载：
	 */
	public boolean needDownLoad(int verionCode){
		InstallApp.bInstall=false;
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
			//int localVerCode=install.getVersionCodeFromApkName(bakPath);
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
	 * 有旧文件，重新安装
	 */
	public void reInstall(){
		if(!Funcs.fileExists(bakPath))return;//备份文件不存在
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,bakPath);
		if(app.pkgname==null){//安装包损坏：
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		//2。是否已安装，已安装则判断已安装版本与备份文件版本退出：
		int versionCodeInInstall=AppUtils.getVersionCodeFromInstalled(context,app.pkgname);//已安装中的版本；
		if(app.versioncode<=versionCodeInInstall)return;
		//3.恢复备份，执行安装：
		if(!Funcs.fileExists(apkPath))Funcs.CopySdcardFile(bakPath, apkPath);//恢复备份；
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 
	}
	/*
	 * 有新文件，安装
	 */
	public void Install(){	
		if(!Funcs.fileExists(apkPath))return;//新文件不存在
		if(Funcs.fileExists(bakPath))Funcs.deletefile(bakPath);//删除备份文件；
		Funcs.CopySdcardFile(apkPath, bakPath);//备份新文件；
		install.SendUpdateMessage(context);//发送更新消息；
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 
	}
	/*
	 * 执行安装
	 */
	public void startInstall(){		
		//1.判断文件是否存在(备份文件，或者恢复备份)：
		if(Funcs.fileExists(apkPath)){
			if(Funcs.fileExists(bakPath))
				Funcs.deletefile(bakPath);
			Funcs.CopySdcardFile(apkPath, bakPath);//备份新文件；
		}else{
			if(Funcs.fileExists(bakPath)){
				Funcs.CopySdcardFile(bakPath, apkPath);//恢复备份；
			}else{
				return;//文件不存在，则退出 ；
			}
		}
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		if(app.pkgname==null){//安装包损坏：
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		//2。是否已安装：
		if(AppUtils.isInstalled(context,app.pkgname))return;
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 

	}
	/*
	 * 执行安装
	 */
	public void startInstall(int verionCode,boolean runAfterInstall){
		int installVersionCode=0;
		if(app.pkgname!=null){
			installVersionCode=AppUtils.getVersionCode(context,app.pkgname);
			if(AppUtils.isInstalled(context,app.pkgname))return;
		}
    
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
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		if(app.pkgname==null){//安装包损坏：
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		if(AppUtils.isInstalled(context,app.pkgname))return;
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 

	}
}
