/**
 * 
 */
package util;

import activity.SplashActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * @author byc
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        //Intent service = new Intent(context,XXXclass);
        //context.startService(service);
        //Log.v("TAG", "开机自动服务自动启动.....");
        //启动应用，参数为需要自动启动的应用的包名
        //String packageName=context.getPackageName().toString();
        //Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(packageName);
        //context.startActivity(intent1 );
    	
    	//intent = new Intent(Intent.ACTION_MAIN);
    	//intent.addCategory(Intent.CATEGORY_HOME);
    	//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	//context.startActivity(intent);
    	SplashActivity.startSplashActivity(context);
    	Log.i(ConfigCt.TAG, intent.getAction());
        if (intent.getAction().equals(ACTION)) {

        }
        /*
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) { 
   	      Intent startIntent = new Intent(context, OrderService.class);   
   	      startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
   	      context.startActivity(startIntent);  
    	 } 
    	 */
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) { 
            //Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            //mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(mainActivityIntent);
      	 } 
    }
}
