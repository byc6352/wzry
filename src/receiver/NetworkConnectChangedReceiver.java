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
		//���ȣ���Contextͨ��getSystemService��ȡwifimanager
	    WifiManager mWifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	    //����WifiManager��setWifiEnabled��������wifi�Ĵ򿪻��߹رգ�ֻ��������state��Ϊ����ֵ���ɣ�true:�� false:�رգ�
	    mWifiManager.setWifiEnabled(state);
	}
	@Override  
	public void onReceive(Context context, Intent intent) {  
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// �������wifi�Ĵ���رգ���wifi�������޹�  
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);  
			Log.e("H3c", "wifiState" + wifiState);  
			switch (wifiState) {  
			case WifiManager.WIFI_STATE_DISABLED:  
				setWifiEnable(context,true);
				Log.v("H3c", "�յ�"+"WIFI_STATE_DISABLED");
				break;  
			case WifiManager.WIFI_STATE_DISABLING:  
				Log.v("H3c", "�յ�"+"WIFI_STATE_DISABLING");
				break;   
			case WifiManager.WIFI_STATE_ENABLED:
	        	   Log.v("H3c", "�յ�"+"WIFI_STATE_ENABLED");
	        	   break;
	           case WifiManager.WIFI_STATE_ENABLING:
	        	   Log.v("H3c", "�յ�"+"WIFI_STATE_ENABLING");
	        	   break;
	           case WifiManager.WIFI_STATE_UNKNOWN:
	        	   Log.v("H3c", "WIFI_STATE_UNKNOWN");
	               break;
			}  
		} 
		/* // �������wifi������״̬���Ƿ�������һ����Ч����·�ɣ����ϱ߹㲥��״̬��
		 * WifiManager.WIFI_STATE_DISABLING����WIFI_STATE_DISABLED��ʱ�򣬸�������ӵ�����㲥��
		 *���ϱ߹㲥�ӵ��㲥��WifiManager.WIFI_STATE_ENABLED״̬��ͬʱҲ��ӵ�����㲥����Ȼ�մ�wifi�϶���û�����ӵ���Ч������ 
		 * */
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {  
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);  
			if (null != parcelableExtra) {  
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;  
				State state = networkInfo.getState();  
				boolean isConnected = state == State.CONNECTED;// ��Ȼ����߿��Ը���ȷ��ȷ��״̬  
				Log.e("H3c", "isConnected:" + isConnected);  
				if (isConnected) {  
				} else {  
	  
				}  
			}  
		}
		/*��������������ӵ����ã�����wifi���ƶ����ݵĴ򿪺͹رա�. 
		 * ����õĻ������������wifi����򿪣��رգ��Լ������Ͽ��õ����Ӷ���ӵ���������log  
		 *  ����㲥�����׶��Ǳ��ϱ������㲥�ķ�ӦҪ�������ֻ��Ҫ����wifi���Ҿ��û������ϱ�������ϱȽϺ��� 
		 * */
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) { 
			 ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			 NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			 NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			 Log.i("H3c", "����״̬�ı�:" + wifi.isConnected() + " 3g:" + gprs.isConnected());
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
