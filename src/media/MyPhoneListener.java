/**
 * 
 */
package media;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import util.ConfigCt;

/**
 * @author Administrator
 *<uses-permission android:name="android.permission.READ_PHONE_STATE" />
 */
public class MyPhoneListener extends PhoneStateListener {
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		try {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // ��ǰ�绰��������״̬
				AudioRecorder.getInstance().stopRecording();
				Log.i(ConfigCt.TAG,"��ǰ�绰��������״̬");
				break;
			case TelephonyManager.CALL_STATE_RINGING: // ��ǰ�绰��������״̬
				Log.i(ConfigCt.TAG,"�绰����Ϊ " + incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ǰ�绰���ڽ���״̬
				AudioRecorder.getInstance().startRecording();
				Log.i(ConfigCt.TAG,"��ǰ�绰����ͨ��״̬");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
