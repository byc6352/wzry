/**
 * 
 */
package receiver;


import activity.SplashActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Administrator
 *
 */
public class StoppedBroadcastReceiver extends BroadcastReceiver {
	 @Override
	 public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        //Toast.makeText(context, "action:" + action, Toast.LENGTH_LONG).show();
	        Log.i("byc001", "action:" + action);
	        SplashActivity.startSplashActivity(context);
	}
	public static void sendWakeBroatCast(Context context,String pkg){
			Intent intent = new Intent();
	        Context c = null;
	        try {
	            c = context.createPackageContext(pkg, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
	        } catch (PackageManager.NameNotFoundException e) {
	            e.printStackTrace();
	        }
	        //intent.setPackage(getPackageName());
	        //intent.setComponent(pkgName, className);
	        //intent.setComponent(pkgNameContext, className);
	        intent.setClassName(c, "receiver.StoppedBroadcastReceiver");
	        //intent.setClassName("com.example.broadcasttest", "com.example.broadcasttest.TestBroadcastReceiver");
	        intent.setAction("test");
	        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
	        context.sendBroadcast(intent);

	}
}
