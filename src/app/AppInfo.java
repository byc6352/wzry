package app;

import android.graphics.drawable.Drawable;

/**
 * App��Ϣ��
 */
public class AppInfo {
    public static final String FLAG_SYSTEM="SYSTEM";//ϵͳԤװAPP��־��
    public static final String FLAG_THIRD="THIRD";//������APP��־��
    public static final String FLAG_SDCARD="SDCARD";//SDCard��APPP��־��
    public static final String FLAG_NONE="NONE";//δ����ı�־��
    // ����
    private String packageName;
    // APP��
    private String appName;
    // ͼ��
    private Drawable icon;
    // �汾��
    private String versionName;
    // Ȩ��
    private String[] permissions;
    // ��Activity������
    private String launchActivityName; 
    // app���:
    private String flag; 
    
    public String getLaunchActivityName() {
        return launchActivityName;
    }

    public void setLaunchActivityName(String launchActivityName) {
        this.launchActivityName = launchActivityName;
    }

    public AppInfo() {}

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    };
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
    
}