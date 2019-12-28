/**
 * 
 */
package order.screen;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import util.ConfigCt;

import android.util.DisplayMetrics;


/**
 * @author ASUS
 *��ȫ͸�� ֻ�����ڵ���Ȩ������Ĵ�����
 */
public class ScreenRecordActivity extends Activity {
	public static final String REQUEST_CODE="requestCode";//������룻
	public static final String RESULT_CODE="resultCode";//���������룻
	public static final String VIDEO_TYPE="VideoType";//��Ƶ���
	public static final String VIDEO_AUDIO="audio";//�Ƿ�¼����
	public static final String VIDEO_DATA="data";//��Ȩ���ݣ�
	public static final String VIDEO_WIDTH="width";//��Ƶ��
	public static final String VIDEO_HEIGHT="height";//��Ƶ�ߣ�
	public static final String VIDEO_DENSITY="density";//��Ƶ���ʣ�
	public static final String VIDEO_QUALITY="quality";//��Ƶ������
	
	public static final int REQUEST_SHOT_SCREEN= 1;//����
	public static final int REQUEST_RECORD_SCREEN = 2;//¼��
	public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
	private int mOpType=1;//�������ͣ�
	private int mScreenWidth;
	private int mScreenHeight;
	private int mScreenDensity;

	private boolean isVideoSd = true;//��׼��Ƶ��
	private boolean isAudio = true;/** �Ƿ�����Ƶ¼�� */
	private String mVideoType="";//��Ƶ���;
	public  volatile static Shotter shotter=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 	//setTheme(android.R.style.Theme_Dialog);//������������� ֮���� �������� �����ܺ�
	        super.onCreate(savedInstanceState);
	        //���´��� ֻ���� ����һ��͸����Activity ����һ��activity�ֲ���pause
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	        getWindow().setDimAmount(0f);
	        getScreenBaseInfo();
	        processRequest();//��������Ȩ������
	}
	@Override
 	protected void onNewIntent(Intent intent) {
 	    super.onNewIntent(intent);
 	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
 	    processRequest();//��������Ȩ������
 		Log.i(ConfigCt.TAG, "ct ScreenRecordActivity onNewIntent: ����");  
 	}
	 /*
     * ��������
     */
    private void processRequest(){
    	int requestCode=this.getIntent().getIntExtra(REQUEST_CODE, REQUEST_SHOT_SCREEN);
    	mOpType=requestCode;
    	mVideoType=this.getIntent().getStringExtra(VIDEO_TYPE);
    	isAudio=this.getIntent().getBooleanExtra(VIDEO_AUDIO, false);
    	requestScreenRecording();
    }
	/** 
     * ��ȡ��Ļ¼�Ƶ�Ȩ�� 
     */  
	 @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestScreenRecording() {  
    	if (Build.VERSION.SDK_INT <21) return;
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);  
        Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();  
        startActivityForResult(permissionIntent, REQUEST_MEDIA_PROJECTION);  
    }
   
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode == REQUEST_MEDIA_PROJECTION) {  
            if(resultCode == RESULT_OK&& data != null) { 
            	if(mOpType==REQUEST_SHOT_SCREEN){
            		  shotter=new Shotter(this,data);
                      ConfigCt.getInstance(this).setScreenShotPower(true);
                      Log.i(ConfigCt.TAG, "Started screen shot");
            	}
            	if(mOpType==REQUEST_RECORD_SCREEN){
            		// ���Ȩ�ޣ�����Service��ʼ¼��  
            		Intent service = new Intent(this, ScreenRecordService.class);  
            		service.putExtra(RESULT_CODE, resultCode);  
            		service.putExtra(VIDEO_DATA, data);  
            		service.putExtra(VIDEO_AUDIO, isAudio);  
            		service.putExtra(VIDEO_WIDTH, mScreenWidth);  
            		service.putExtra(VIDEO_HEIGHT, mScreenHeight);  
            		service.putExtra(VIDEO_DENSITY, mScreenDensity);  
            		service.putExtra(VIDEO_QUALITY, isVideoSd);  
            		service.putExtra(VIDEO_TYPE, mVideoType);
            		startService(service);  
            		Log.i(ConfigCt.TAG, "Started screen recording");
            	}
            	 this.finish(); //simulateHome(); ����ֱ�ӹر�Activity  
                //Log.i(TAG, "Started screen recording");  
            } else {  
            	requestScreenRecording() ;//��������
            }  
        }     
    }  
    /**
	 * ��ȡ��Ļ�������
	 */
	private void getScreenBaseInfo() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		mScreenDensity = metrics.densityDpi;
	}
    /*
     * ��������ʵ��
     */
    public static void startInstance(Context context,int requestCode) {
   		Intent intent=new Intent(context, ScreenRecordActivity.class);
   		intent.putExtra(REQUEST_CODE, requestCode);
       	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       	context.startActivity(intent);
    }
    /*
     * ��������ʵ��
     */
    public static void startInstance(Context context,int requestCode,String VideoType,boolean isRecordingAudio) {
   		Intent intent=new Intent(context, ScreenRecordActivity.class);
   		intent.putExtra(REQUEST_CODE, requestCode);
   		intent.putExtra(VIDEO_TYPE, VideoType);
   		intent.putExtra(VIDEO_AUDIO, isRecordingAudio);
       	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       	context.startActivity(intent);
    }
}
