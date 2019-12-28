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

    //��дonReceive����
    @Override
    public void onReceive(Context context, Intent intent) {
        //��ߵ�XXX.class����Ҫ�����ķ���
        //Intent service = new Intent(context,XXXclass);
        //context.startService(service);
        //Log.v("TAG", "�����Զ������Զ�����.....");
        //����Ӧ�ã�����Ϊ��Ҫ�Զ�������Ӧ�õİ���
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
            //Intent mainActivityIntent = new Intent(context, MainActivity.class);  // Ҫ������Activity
            //mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(mainActivityIntent);
      	 } 
    }
}
