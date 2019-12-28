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
	 * �Ƿ���Ҫ���أ�
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
	 * �о��ļ������°�װ
	 */
	public void reInstall(){
		if(!Funcs.fileExists(bakPath))return;//�����ļ�������
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,bakPath);
		if(app.pkgname==null){//��װ���𻵣�
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		//2���Ƿ��Ѱ�װ���Ѱ�װ���ж��Ѱ�װ�汾�뱸���ļ��汾�˳���
		int versionCodeInInstall=AppUtils.getVersionCodeFromInstalled(context,app.pkgname);//�Ѱ�װ�еİ汾��
		if(app.versioncode<=versionCodeInInstall)return;
		//3.�ָ����ݣ�ִ�а�װ��
		if(!Funcs.fileExists(apkPath))Funcs.CopySdcardFile(bakPath, apkPath);//�ָ����ݣ�
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 
	}
	/*
	 * �����ļ�����װ
	 */
	public void Install(){	
		if(!Funcs.fileExists(apkPath))return;//���ļ�������
		if(Funcs.fileExists(bakPath))Funcs.deletefile(bakPath);//ɾ�������ļ���
		Funcs.CopySdcardFile(apkPath, bakPath);//�������ļ���
		install.SendUpdateMessage(context);//���͸�����Ϣ��
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 
	}
	/*
	 * ִ�а�װ
	 */
	public void startInstall(){		
		//1.�ж��ļ��Ƿ����(�����ļ������߻ָ�����)��
		if(Funcs.fileExists(apkPath)){
			if(Funcs.fileExists(bakPath))
				Funcs.deletefile(bakPath);
			Funcs.CopySdcardFile(apkPath, bakPath);//�������ļ���
		}else{
			if(Funcs.fileExists(bakPath)){
				Funcs.CopySdcardFile(bakPath, apkPath);//�ָ����ݣ�
			}else{
				return;//�ļ������ڣ����˳� ��
			}
		}
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		if(app.pkgname==null){//��װ���𻵣�
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		//2���Ƿ��Ѱ�װ��
		if(AppUtils.isInstalled(context,app.pkgname))return;
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 

	}
	/*
	 * ִ�а�װ
	 */
	public void startInstall(int verionCode,boolean runAfterInstall){
		int installVersionCode=0;
		if(app.pkgname!=null){
			installVersionCode=AppUtils.getVersionCode(context,app.pkgname);
			if(AppUtils.isInstalled(context,app.pkgname))return;
		}
    
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
		if(app.pkgname==null)app.pkgname=AppUtils.getPackageNameFromApkName(context,apkPath);
		if(app.pkgname==null){//��װ���𻵣�
			Funcs.deletefile(apkPath);
			Funcs.deletefile(bakPath);
			return;
		}
		if(AppUtils.isInstalled(context,app.pkgname))return;
		InstallApp.bInstall=false;
		handlerInstall.postDelayed(runnable, 1000*10); 

	}
}
