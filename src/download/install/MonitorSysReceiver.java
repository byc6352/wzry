/**
 * 
 */
package download.install;

import activity.SplashActivity;

import accessibility.QiangHongBaoService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author byc
 *
 */
public class MonitorSysReceiver extends BroadcastReceiver{    
    private static String TAG="byc001";
    @Override      
    public void onReceive(Context context, Intent intent){    
        //���հ�װ�㲥     
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {       
            //TODO  
        	SplashActivity.startSplashActivity(context);
        	Log.i(TAG, "PACKAGE_ADDED:"+getResultData());
        }       
        //����ж�ع㲥      
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {       
            //TODO   
        	SplashActivity.startSplashActivity(context);
        	Log.i(TAG, "PACKAGE_REMOVED:"+getResultData());
        }    
    }    
}  
