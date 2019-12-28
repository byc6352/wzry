/**
 * 
 */
package notification;



import util.ConfigCt;
import accessibility.QiangHongBaoService;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>Created 16/2/4 涓嬪崍11:16.</p>
 * <p><a href="mailto:codeboy2013@gmail.com">Email:codeboy2013@gmail.com</a></p>
 * <p><a href="http://www.happycodeboy.com">LeonLee Blog</a></p>
 *
 * @author LeonLee
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class QHBNotificationService extends NotificationListenerService {
	ManagerNotificationJob managerNotificationJob;
    private static QHBNotificationService service;

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onListenerConnected();
        }
        managerNotificationJob=ManagerNotificationJob.getInstance();
    }

    private ConfigCt getConfig() {
        return ConfigCt.getInstance(this);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        if(ConfigCt.DEBUG) {
            Log.i(ConfigCt.TAG, "onNotificationPosted:"+sbn.getPackageName());
       
        }
        //QiangHongBaoService.handeNotificationPosted(new IStatusBarNotification() {
        managerNotificationJob.handeNotificationPosted(new IStatusBarNotification() {
            @Override
            public String getPackageName() {
                return sbn.getPackageName();
            }

            @Override
            public Notification getNotification() {
                return sbn.getNotification();
            }
            @Override
            public long getPostTime() {
                return sbn.getPostTime();
            }
        });
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onNotificationRemoved(sbn);
        }
        if(ConfigCt.DEBUG) {
            Log.i(ConfigCt.TAG, "onNotificationRemoved");
            //if(ConfigCt.DEBUG)Toast.makeText(this, "onNotificationRemoved", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onListenerConnected() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onListenerConnected();
        }

        Log.i(ConfigCt.TAG, "onNotificationListenerConnected");
        //if(ConfigCt.DEBUG)Toast.makeText(this, "onNotificationListenerConnected", Toast.LENGTH_LONG).show();
        service = this;
        //发送广播，已经连接上了
        //Intent intent = new Intent(Config.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT);
        //sendBroadcast(intent);
        managerNotificationJob.onCreateJob(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(ConfigCt.TAG, "onDestroy");
        service = null;
        //if(ConfigCt.DEBUG)Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
        toggleNotificationListenerService();
        //发送广播，断开连接
        //Intent intent = new Intent(Config.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT);
        //sendBroadcast(intent);
        managerNotificationJob.onStopJob();
    }

    /*是否启动通知栏监听*/
    public static boolean isRunning() {
        if(service == null) {
            return false;
        }
        return true;
    }
    public static boolean notificationListenerEnable(Context context) {  
        boolean enable = false;  
        String packageName = context.getPackageName();  
        String flat= Settings.Secure.getString(context.getContentResolver(),"enabled_notification_listeners");  
        if (flat != null) {  
            enable= flat.contains(packageName);  
        }  
        return enable;  
    }  

    /**打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public  static void openNotificationServiceSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //Toast.makeText(this, R.string.tips, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**打开通知栏设置*/
    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, notification.QHBNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, notification.QHBNotificationService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
    /*
       public boolean isNotificationListenerEnabled(Context context) {
        //Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        //if (packageNames.contains(context.getPackageName())) {
         //   return true;
        //}
        return false;
    }
     * 
     * */
}

