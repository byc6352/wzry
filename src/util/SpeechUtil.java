/**
 * 
 */
package util;


import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.example.h3.Config;
import android.content.Context;
import android.util.Log;
/**
 * @author byc
 *
 */
public class SpeechUtil {
	
	private static SpeechUtil current;
	private Context context;
	private  String TAG = "byc001";//���Ա�ʶ��
	private SpeechSynthesizer mSpeechSynthesizer;
	private String  mSpeaker="0";//����ģʽ��0--��ͨŮ����1--��ͨ������2--�ر�������3--���������
	private boolean mSpeaking=true;//�Ƿ�����
	
	private static final String MY_APP_ID = "9529675";
	private static final String MY_API_KEY = "6b3tkbm1Bv7mn1Z5UR96gL6v";
	private static final String MY_SECRET_KEY= "9IQqmTGKwUino6GUAk2kWGUYw3Yl9QgV";
	
	public SpeechUtil(Context context) {
		this.context = context;
		TAG=Config.TAG;
		this.mSpeaker=Config.speaker;
		this.mSpeaking=Config.bSpeaking;
		//initialEnv();
		initialTts();
	}
    public static synchronized SpeechUtil getSpeechUtil(Context context) {
        if(current == null) {
            current = new SpeechUtil(context);
        }
        return current;
    }
 
    /*
     * ���÷���ģʽ��
     */
    public void setSpeaker(String speaker){
    	mSpeaker=speaker;
    	this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, mSpeaker);
    }
    /*
     * �򿪻�ر�������ʾ��
     */
    public void setSpeaking(boolean bSpeaking){
    	this.mSpeaking=bSpeaking;
    }
    /*
     * �ر�������ʾ��
     */
    public void stopSpeaking(){
    	mSpeechSynthesizer.stop();
    	//mSpeechSynthesizer.
    }
    /*
     * ��ʼ��TTS��
     */
    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(context);
        // ���滻Ϊ����������ƽ̨ע��Ӧ�õõ���apikey��secretkey (������Ȩ)
        this.mSpeechSynthesizer.setApiKey(MY_API_KEY, MY_SECRET_KEY);
        // �����ˣ��������棩�����ò���Ϊ0,1,2,3���������������˻ᶯ̬���ӣ���ֵ����ο��ĵ������ĵ�˵��Ϊ׼��0--��ͨŮ����1--��ͨ������2--�ر�������3--���������������
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, mSpeaker);
        // ����Mixģʽ�ĺϳɲ���
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);//MIX_MODE_DEFAULT
        // ��ʼ��tts
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
    }
    //@Override
    private void onDestroy() {
        this.mSpeechSynthesizer.release();
        //super.onDestroy();
    }

    public void speak(String text) {
    	if(!mSpeaking)return;
        //��Ҫ�ϳɵ��ı�text�ĳ��Ȳ��ܳ���1024��GBK�ֽڡ�
        int result = this.mSpeechSynthesizer.speak(text);
        if (result < 0) {
            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }
    private void toPrint(String str) {
    	Log.w(TAG, "TTS:"+str);
    	
    }
}
