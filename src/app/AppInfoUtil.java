package app;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import util.ConfigCt;

/**
 * ��ȡ�ֻ��ϰ�װ������APP����Ϣ ���AppInfo��ʹ��
 */
public class AppInfoUtil {
    public static final int GET_ALL_APP = 0; // ����APP
    public static final int GET_SYSTEM_APP = 1; // ϵͳԤװAPP
    public static final int GET_THIRD_APP = 2; // ������APP
    public static final int GET_SDCARD_APP = 3; // SDCard��APP


    private static AppInfoUtil infoUtil;

    private PackageManager pManager;

    // ����Ӧ��
    private List<PackageInfo> allPackageList;

    // ɸѡ���
    private List<PackageInfo> result;
    
    private Context mContext;

    /** ˽�й����� **/
    private AppInfoUtil(Context context) {
    	mContext=context;
        pManager = context.getPackageManager();
        result = new ArrayList<PackageInfo>();
    }

    /** ���� **/
    public static AppInfoUtil getInstance(Context context) {
        if (infoUtil == null) {
            infoUtil = new AppInfoUtil(context);
        }
        return infoUtil;
    }

    /** ��ȡ�Ѱ�װ��APP **/
    public List<AppInfo> getInstalledApps(int type) {
        // 0 ��ʾ�������κβ�����������������������
        // �汾�š�APPȨ��ֻ��ͨ��PackageInfo��ȡ�������ﲻʹ��getInstalledApplications()����
        allPackageList = pManager.getInstalledPackages(0);
        if (allPackageList == null) {
            Log.e("AppInfoUtil��", "getInstalledApps()�����е�allPackageListΪ��");
            return null;
        }
        // ����APP������
        Collections.sort(allPackageList, new PackageInfoComparator(pManager));
        // ɸѡ
        result.clear();
        String sFlag=AppInfo.FLAG_NONE;
        switch (type) {
        case GET_ALL_APP:
            result = allPackageList;
            break;
        case GET_SYSTEM_APP: // ϵͳ�Դ�APP
            for (PackageInfo info : allPackageList) {
                // FLAG_SYSTEM = 1<<0��if set, this application is installed in
                // the device's system image.
                // ����&���������ֽ����
                // 1����flags��ĩλΪ1����ϵͳAPP
                // 0����flags��ĩλΪ0������ϵͳAPP
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    result.add(info);
                }
            }
            break;
        case GET_THIRD_APP: // ������APP
            for (PackageInfo info : allPackageList) {
                // FLAG_SYSTEM = 1<<0��ͬ��
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    result.add(info);
                }
                // ������ϵͳ���򣬱��û��ֶ����º󣬸�ϵͳ����Ҳ��Ϊ������Ӧ�ó�����
                // FLAG_UPDATED_SYSTEM_APP = 1<<7�� this is set if this
                // application has been
                // install as an update to a built-in system application.
                else if ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                    result.add(info);
                }
            }
            sFlag=AppInfo.FLAG_THIRD;
            break;
        case GET_SDCARD_APP: // ��װ��SDCard��Ӧ�ó���
            for (PackageInfo info : allPackageList) {
                // FLAG_EXTERNAL_STORAGE = 1<<18��Set to true if the application
                // is
                // currently installed on external/removable/unprotected storage
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 1) {
                    result.add(info);
                }
            }
            sFlag=AppInfo.FLAG_SDCARD;
            break;
        }
        return getAppInfoByPackageInfo(result,sFlag);
    }

    public List<AppInfo> getAppInfoByIntent(Intent intent) {
        List<ResolveInfo> resolveInfos = pManager.queryIntentActivities(intent,
                PackageManager.GET_INTENT_FILTERS);
        // ����ϵͳ���� �� ����name����
        // ������Ὣϵͳ�Դ�App���û���װ��APP�ֿ�����
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(
                pManager));
        // // ������Ὣϵͳ�Դ�App���û���װ��APP�������
        // Collections.sort(resolveInfos, new DisplayNameComparator(pManager));
        return getAppInfobyResolveInfo(resolveInfos);
    }

    /** ��ȡ����Appͼ�� **/
    public Drawable getAppIcon(String packageName) throws NameNotFoundException {
        Drawable icon = pManager.getApplicationIcon(packageName);
        return icon;
    }

    /** ��ȡ����App���� **/
    public String getAppName(String packageName) throws NameNotFoundException {
        ApplicationInfo appInfo = pManager.getApplicationInfo(packageName, 0);
        String appName = pManager.getApplicationLabel(appInfo).toString();
        return appName;
    }

    /** ��ȡ����App�汾�� **/
    public String getAppVersion(String packageName)
            throws NameNotFoundException {
        PackageInfo packageInfo = pManager.getPackageInfo(packageName, 0);
        String appVersion = packageInfo.versionName;
        return appVersion;
    }

    /** ��ȡ����App������Ȩ�� **/
    public String[] getAppPermission(String packageName)
            throws NameNotFoundException {
        PackageInfo packageInfo = pManager.getPackageInfo(packageName,
                PackageManager.GET_PERMISSIONS);
        String[] permission = packageInfo.requestedPermissions;
        return permission;
    }

    /** ��ȡ����App��ǩ�� **/
    public String getAppSignature(String packageName)
            throws NameNotFoundException {
        PackageInfo packageInfo = pManager.getPackageInfo(packageName,
                PackageManager.GET_SIGNATURES);
        String allSignature = packageInfo.signatures[0].toCharsString();
        return allSignature;
    }

    // /** ʹ��ʾ�� **/
    // public static void main(String[] args) {
    // AppInfoUtil appInfoUtil = AppInfo.getInstance(context);
    //
    // // ��ȡ����APP
    // List<AppInfo> allAppInfo = appInfoUtil.getInstalledApps(GET_ALL_APP);
    // for (AppInfo app : allAppInfo) {
    // String packageName = app.getPackageName();
    // String appName = app.getAppName();
    // Drawable icon = app.getIcon();
    // String versionName = app.getVersionName();
    // String[] permissions = app.getPermissions();
    // // ���ɷ���...
    // }
    //
    // // ��ȡ����APP����Ϣ
    // String appName = appInfoUtil.getAppName(packageName);
    // ...
    // }

    /** ��PackageInfo��List����ȡApp��Ϣ **/
    private List<AppInfo> getAppInfoByPackageInfo(List<PackageInfo> list,String flag) {
        List<AppInfo> appList = new ArrayList<AppInfo>();
        for (PackageInfo info : list) {
            // ��ȡ��Ϣ
            String packageName = info.applicationInfo.packageName;
            String appName = pManager.getApplicationLabel(info.applicationInfo)
                    .toString();
            Drawable icon = pManager.getApplicationIcon(info.applicationInfo);
            // // Ҳ���������·�����ȡAPPͼ�꣬��Ȼ������
            // ApplicationInfo applicationInfo =
            // pManager.getApplicationInfo(packageName, 0);
            // Drawable icon = applicationInfo.loadIcon(pManager);
            String versionName = info.versionName;
            String[] permissions = info.requestedPermissions;
            String launchActivityName = getLaunchActivityName(packageName);
            // ������Ϣ
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setAppName(appName);
            appInfo.setIcon(icon);
            appInfo.setVersionName(versionName);
            appInfo.setPermissions(permissions);
            appInfo.setLaunchActivityName(launchActivityName);
            if(flag.equals(AppInfo.FLAG_NONE)){
            	appInfo.setFlag(getAppFlag(info.applicationInfo.flags));
            }else{
            	appInfo.setFlag(flag);
            }
            appList.add(appInfo);
        }
        return appList;
    }

    /** ��ResolveInfo��List����ȡApp��Ϣ **/
    private List<AppInfo> getAppInfobyResolveInfo(List<ResolveInfo> list) {
        List<AppInfo> appList = new ArrayList<AppInfo>();
        for (ResolveInfo info : list) {
            String packageName = info.activityInfo.packageName;
            String appName = info.loadLabel(pManager).toString();
            Drawable icon = info.loadIcon(pManager);
            String launchActivityName = getLaunchActivityName(packageName);
            AppInfo appInfo = new AppInfo();
            appInfo.setPackageName(packageName);
            appInfo.setAppName(appName);
            appInfo.setIcon(icon);
            appInfo.setLaunchActivityName(launchActivityName);
            appList.add(appInfo);
        }
        return appList;
    }

    /** ��ȡָ��������Activity�����������������а�������Activity **/
    private String getLaunchActivityName(String packageName) {
        // ����PackageInfo����ȡ�������е���Activity������Intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(packageName);
        List<ResolveInfo> resolveInfos = pManager.queryIntentActivities(intent,
                0);
        String mainActivityName = "";
        if (resolveInfos != null && resolveInfos.size() >= 1) {
            mainActivityName = resolveInfos.get(0).activityInfo.name;
        }
        return mainActivityName;
    }

    /** �˱Ƚ���ֱ�Ӹ���AndroidԴ�룬����ȴ���԰�ϵͳAPP���û�APP������У��ν⣿ **/
    private static class DisplayNameComparator implements
            Comparator<ResolveInfo> {
        public DisplayNameComparator(PackageManager pm) {
            mPM = pm;
        }

        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence sa = a.loadLabel(mPM);
            if (sa == null)
                sa = a.activityInfo.name;
            CharSequence sb = b.loadLabel(mPM);
            if (sb == null)
                sb = b.activityInfo.name;
            return sCollator.compare(sa.toString(), sb.toString());
        }

        private final Collator sCollator = Collator.getInstance();
        private PackageManager mPM;
    }

    /** �Զ����PackageInfo������ **/
    private static class PackageInfoComparator implements
            Comparator<PackageInfo> {
        public PackageInfoComparator(PackageManager pm) {
            mPM = pm;
        }

        public final int compare(PackageInfo a, PackageInfo b) {
            CharSequence sa = mPM.getApplicationLabel(a.applicationInfo);
            CharSequence sb = mPM.getApplicationLabel(b.applicationInfo);
            return sCollator.compare(sa.toString(), sb.toString());
        }

        private final Collator sCollator = Collator.getInstance();
        private PackageManager mPM;
    }
    /** �����ַ�����ʽ��app��־ **/
    private String getAppFlag(int flags) {
    	if ((flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
    		return AppInfo.FLAG_SYSTEM;
    	}
    	if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
    		 return AppInfo.FLAG_THIRD;
        }
    	if ((flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
    		return AppInfo.FLAG_THIRD;
    	}
    	if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 1) {
    		return AppInfo.FLAG_SDCARD;
    	}
    	return AppInfo.FLAG_NONE;
    }
    /*
	 * ��װ
	 */
    public static void installApk(Context context,String filename) {//
        Uri uri = Uri.fromFile(new File(filename));
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(localIntent);
    }
    /* ж��apk */    
   	public static void uninstallApk(Context context, String packageName) {    
   		Uri uri = Uri.parse("package:" + packageName);    
   		Intent intent = new Intent(Intent.ACTION_DELETE, uri); 
   		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   		context.startActivity(intent);    
   	} 
   	/** 
     * <��������> ����Ӧ�ó���
     * @param PackageName������
     * @return boolean [ִ������Ӧ�ó���ɹ���]
     */  
	public boolean RunApp(String pkgName) { 
		try{
			String mainClass=getLaunchActivityName(pkgName);
			if(mainClass.equals(""))return false;
			Intent intent = new Intent(Intent.ACTION_MAIN);   
			intent.addCategory(Intent.CATEGORY_LAUNCHER);  
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName cn = new ComponentName(pkgName, mainClass);               
			intent.setComponent(cn);  
			intent.putExtra("hide", true);
			mContext.startActivity(intent);
			return true;
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
			return false;
		}
    	
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
     * @param context 
     * @param PackageName������
     * @return boolean [ִ������Ӧ�ó���ɹ���]
     */
	public static boolean  RunApp(Context context,String pkg) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            // ��ȡָ��������Ӧ�ó����PackageInfoʵ��
              packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (NameNotFoundException e) {
            // δ�ҵ�ָ��������Ӧ�ó���
              e.printStackTrace();
            return false;
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
                return false;
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
        return true;
	}
	 /**
     * �жϱ�Ӧ���Ƿ���
     * �����Ҫ�жϱ�Ӧ���Ƿ��ں�̨����ǰ̨��getRunningTask
     * */
    public static boolean isAPPALive(Context mContext,String packageName){
        boolean isAPPRunning = false;
        // ��ȡactivity�������
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // ��ȡ�����������е�app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // ������������������
        for(ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList){
            if(packageName.equals(appInfo.processName)){
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }
	/**
     * <��������> ɱ������
     * @param context 
     * @param PackageName������
     * @return void []
     */
	public static void  killProcess(Context context,String pkg) {
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(pkg);
	}
}

