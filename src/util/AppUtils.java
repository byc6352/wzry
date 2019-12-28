/**
 * 
 */
package util;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import download.install.AppInfo;

import java.util.List; 
/**
 * @author ASUS
 *
 */
public class AppUtils {
	/**  
     * �����������ж�ĳһӦ���Ƿ���������  
     *  
     * @param context     ������  
     * @param packageName Ӧ�õİ���  
     * @return true ��ʾ�������У�false��ʾû������  
     */  
    public static boolean isAppRunning(Context context, String packageName) {  
        boolean isAppRunning = false;  
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);  
        if (list.size() <= 0) {  
            return false;  
        }  
        for (ActivityManager.RunningTaskInfo info : list) {  
            if (info.baseActivity.getPackageName().equals(packageName)) {  
                return true;  
            }  
        }  
        return false;  
    }  
	/** 
	* �ж�ĳ�������Ƿ��������еķ��� 
	* @param serviceName 
	*            �ǰ���+��������������磺net.loonggg.testbackstage.TestService�� 
	* @return true�����������У�false�������û���������� 
	*/  
    public static boolean isServiceRunning(Context context,String pkg,String serviceName) {  
    	ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
    	List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(100);  
    	if (runningServiceInfos.size() <= 0)return false;   
    	for (int i = 0; i < runningServiceInfos.size(); i++) {  
    		String packageName=runningServiceInfos.get(i).service.getPackageName().toString();
    		String className = runningServiceInfos.get(i).service.getClassName().toString();  
    		if (className.equals(serviceName)&&pkg.equals(packageName))return true;
    	}  
    	return false;  
    } 
	 /* ж��apk */    
	public static void uninstallApk(Context context, String packageName) {    
		Uri uri = Uri.parse("package:" + packageName);    
		Intent intent = new Intent(Intent.ACTION_DELETE, uri); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);    
	}  
	/** 
     * ���У�
     * @param context 
     * @param filename  �ļ��� 
     * @return   PackageName������
     */  
	public static boolean RunApp(Context context,String pkgName,String mainClass) { 
		try{
			Intent intent = new Intent(Intent.ACTION_MAIN);   
			intent.addCategory(Intent.CATEGORY_LAUNCHER);  
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn = new ComponentName(pkgName, mainClass);               
			intent.setComponent(cn);  
			intent.putExtra("hide", true);
			context.startActivity(intent);
			return true;
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
			return false;
		}
    	
	}
	/**
     * <��������> ����Ӧ�ó���
     * 
     * @return void [��������˵��]
     */
	public static void RunApp(Context context,String pkg) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // ��ȡָ��������Ӧ�ó����PackageInfoʵ��
              packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (NameNotFoundException e) {
            // δ�ҵ�ָ��������Ӧ�ó���
              e.printStackTrace();
            return;
        }
        if (packageInfo != null) {
            // �Ѱ�װӦ��
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageInfo.packageName);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(
                    resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                ri = apps.iterator().next();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (ri != null) {
                // ��ȡӦ�ó����Ӧ������Activity����
                  String className = ri.activityInfo.name;
                // ����Ӧ�ó����Ӧ��Activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = new ComponentName(pkg, className);
                intent.setComponent(componentName);
                intent.putExtra("hide", true);
                context.startActivity(intent);
            }
        }
	}
	/**
     * <��������> ��ȡ�����ࣺ
     * 
     * @return string [�����������]
     */
	public static String getStartClass(Context context,String pkg) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // ��ȡָ��������Ӧ�ó����PackageInfoʵ��
              packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (NameNotFoundException e) {
            // δ�ҵ�ָ��������Ӧ�ó���
              e.printStackTrace();
            return null;
        }
        if (packageInfo != null) {
            // �Ѱ�װӦ��
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageInfo.packageName);
            List<ResolveInfo> apps = packageManager.queryIntentActivities(
                    resolveIntent, 0);
            ResolveInfo ri = null;
            try {
                ri = apps.iterator().next();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (ri != null) {
                // ��ȡӦ�ó����Ӧ������Activity����
                  String className = ri.activityInfo.name;
                  return className;
            }
        }
        return null;
	}
	/** 
     * ���ļ�����ȡ�汾��,������
     * @param context 
     * @param filename  �ļ��� 
     * @return   int �汾��
     */  
	public static boolean getAppInfoFromApkFile(Context context,String filename,AppInfo app) { 
    	if(app==null)return false;
    	 PackageManager pm = context.getPackageManager();
    	 PackageInfo packageInfo =pm.getPackageArchiveInfo(filename, PackageManager.GET_ACTIVITIES); 
         if (packageInfo != null) {
        	 ApplicationInfo appInfo = packageInfo.applicationInfo;
             app.pkgname= appInfo.packageName;
             app.versioncode=packageInfo.versionCode;
             return true;
         }
         return false;
	}

	/** 
     * ���ļ�����ȡ������
     * @param context 
     * @param filename  �ļ��� 
     * @return   PackageName������
     */  
	public static String getPackageNameFromApkName(Context context,String filename) {  
    	 PackageManager pm = context.getPackageManager();
    	 PackageInfo packageInfo =pm.getPackageArchiveInfo(filename, PackageManager.GET_ACTIVITIES); 
         if (packageInfo != null) {
        	 ApplicationInfo appInfo = packageInfo.applicationInfo;
             String packageName = appInfo.packageName;
             return packageName;
         }
         return null;
	}
	/** 
     * ���ļ�����ȡ�汾�ţ�
     * @param context 
     * @param filename  �ļ��� 
     * @return   int �汾��
     */  
	public static int getVersionCodeFromApkName(Context context,String filename) {  
    	 PackageManager pm = context.getPackageManager();
    	 PackageInfo packageInfo =pm.getPackageArchiveInfo(filename, PackageManager.GET_ACTIVITIES); 
         if (packageInfo != null) {
             int versioncode = packageInfo.versionCode;
             return versioncode;
         }
         return 0;
	}
	/** 
     * �ж��ֻ��Ƿ�װĳ��Ӧ�� ,�����ذ汾�ţ�
     * @param context 
     * @param appPackageName  Ӧ�ð��� 
     * @return   int �汾�ţ�
     */  
	public static int getVersionCode(Context context,String appPackageName) {  
        PackageManager packageManager = context.getPackageManager();// ��ȡpackagemanager  
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ��ȡ�����Ѱ�װ����İ���Ϣ  
        if (pinfo != null) {  
            for (int i = 0; i < pinfo.size(); i++) {  
                String pn = pinfo.get(i).packageName;  
                if (appPackageName.equals(pn)) {  
                    return pinfo.get(i).versionCode;  
                }  
            }  
        }  
        return 0;  
	} 
	/** 
     * 
     * @param context 
     * @param appPackageName  Ӧ�ð��� 
     * @return   true����װ��false��δ��װ 
     */  
	public static int getVersionCodeFromInstalled(Context context,String appPackageName) {  
        PackageManager packageManager = context.getPackageManager();// ��ȡpackagemanager  
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ��ȡ�����Ѱ�װ����İ���Ϣ  
        if (pinfo != null) {  
            for (int i = 0; i < pinfo.size(); i++) {  
                String pn = pinfo.get(i).packageName;  
                ///pinfo.get(i).v
                if (appPackageName.equals(pn)) {  
                	return pinfo.get(i).versionCode;
                  
                }  
            }  
        }  
        return 0;  
	} 
	/** 
     * �ж��ֻ��Ƿ�װĳ��Ӧ�� 
     * @param context 
     * @param appPackageName  Ӧ�ð��� 
     * @return   true����װ��false��δ��װ 
     */  
	public static boolean isInstalled(Context context,String appPackageName) {  
        PackageManager packageManager = context.getPackageManager();// ��ȡpackagemanager  
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ��ȡ�����Ѱ�װ����İ���Ϣ  
        if (pinfo != null) {  
            for (int i = 0; i < pinfo.size(); i++) {  
                String pn = pinfo.get(i).packageName;  
                if (appPackageName.equals(pn)) {  
                    return true;  
                }  
            }  
        }  
        return false;  
	}

}
