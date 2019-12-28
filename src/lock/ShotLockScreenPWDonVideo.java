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
	private static ShotLockScreenPWDonVideo current;//ʵ����
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
     * ������Ļ�¼�
     */
    private void UnLockScreenReceiver(Context context){
        filter = new IntentFilter();
        // ��Ļ�����㲥
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // ��Ļ�����㲥
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // ��Ļ�����㲥
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // ��������Դ���������ػ����Ի���������ʱϵͳ�ᷢ������㲥
        // example����ʱ����õ�ϵͳ�Ի���Ȩ�޿��ܸܺߣ��Ḳ��������������ߡ��ػ����Ի���֮�ϣ�
        // ���Լ�������㲥�����յ�ʱ�������Լ��ĶԻ�������pad���½ǲ��ֵ����ĶԻ���
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
     * ��ʱ����5���Ӻ��Զ���ֹ¼����
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
