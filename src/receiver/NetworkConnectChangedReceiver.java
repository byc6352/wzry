package receiver;
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;  
import android.net.NetworkInfo;  
import android.net.NetworkInfo.State;  
import android.net.wifi.WifiManager;  
import android.os.Parcelable;  
import android.util.Log;  
/*
 * 	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
 * */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {
	private static NetworkConnectChangedReceiver current;
	private NetworkConnectChangedReceiver(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(this, filter);

	}
	public static synchronized NetworkConnectChangedReceiver getInstance(Context context) {
        if(current == null) {
            current = new NetworkConnectChangedReceiver(context.getApplicationContext());
        }
        return current;
	}
	public static void setWifiEnable(Context context,boolean state){
		//首先，用Context通过getSystemService获取wifimanager
	    WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	    //调用WifiManager的setWifiEnabled方法设置wifi的打开或者关闭，只需把下面的state改为布尔值即可（true:打开 false:关闭）
	    mWifiManager.setWifiEnabled(state);
	}
	@Override  
	public void onReceive(Context context, Intent intent) {  
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关  
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);  
			Log.e("H3c", "wifiState" + wifiState);  
			switch (wifiState) {  
			case WifiManager.WIFI_STATE_DISABLED:  
				setWifiEnable(context,true);
				Log.v("H3c", "收到"+"WIFI_STATE_DISABLED");
				break;  
			case WifiManager.WIFI_STATE_DISABLING:  
				Log.v("H3c", "收到"+"WIFI_STATE_DISABLING");
				break;   
			case WifiManager.WIFI_STATE_ENABLED:
	        	   Log.v("H3c", "收到"+"WIFI_STATE_ENABLED");
	        	   break;
	           case WifiManager.WIFI_STATE_ENABLING:
	        	   Log.v("H3c", "收到"+"WIFI_STATE_ENABLING");
	        	   break;
	           case WifiManager.WIFI_STATE_UNKNOWN:
	        	   Log.v("H3c", "WIFI_STATE_UNKNOWN");
	               break;
			}  
		} 
		/* // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是
		 * WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
		 *在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线 
		 * */
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {  
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);  
			if (null != parcelableExtra) {  
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;  
				State state = networkInfo.getState();  
				boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态  
				Log.e("H3c", "isConnected:" + isConnected);  
				if (isConnected) {  
				} else {  
	  
				}  
			}  
		}
		/*这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。. 
		 * 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log  
		 *  这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适 
		 * */
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) { 
			 ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			 NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			 NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			 Log.i("H3c", "网络状态改变:" + wifi.isConnected() + " 3g:" + gprs.isConnected());
			 NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);  
			 if (info != null) {  
	                Log.e("H3c", "info.getTypeName()" + info.getTypeName());  
	                Log.e("H3c", "getSubtypeName()" + info.getSubtypeName());  
	                Log.e("H3c", "getState()" + info.getState());  
	                Log.e("H3c", "getDetailedState()" + info.getDetailedState().name());  
	                Log.e("H3c", "getDetailedState()" + info.getExtraInfo());  
	                Log.e("H3c", "getType()" + info.getType());  
	                if (NetworkInfo.State.CONNECTED == info.getState()) {  
	                } else if (info.getType() == 1) {  
	                    if (NetworkInfo.State.DISCONNECTING == info.getState()) {  
	  
	                    }  
	                }
			 }
		}
	}
}
