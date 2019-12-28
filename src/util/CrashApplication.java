/**
 * 
 */
package util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import activity.SplashActivity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import download.ftp;

/**
 * @author byc
 *
 */
public class CrashApplication extends Application {
    //CrashHandlerʵ��
    private static final String TAG=ConfigCt.TAG ;
    private Context mContext;
    private static CrashHandler current = null;
    private static Map<String, String>  infos=new HashMap<String, String>();
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        loadPluginResources();
        ConfigCt.getInstance(mContext);
        getCrashHandler(getApplicationContext());
    }

    /** ��ȡCrashHandlerʵ�� ,����ģʽ */
    public CrashHandler getCrashHandler(Context context) {
        if(current == null) {
            current = new CrashHandler(context);
        }
        return current;
    }

    /**
     * UncaughtException������,��������Uncaught�쳣��ʱ��,�и������ӹܳ���,����¼���ʹ��󱨸�.
     *
     * @author user
     *
     */
    public class CrashHandler implements UncaughtExceptionHandler {

        public String TAG = "byc001";
        //ϵͳĬ�ϵ�UncaughtException������
        private Thread.UncaughtExceptionHandler mDefaultHandler;
        //�����Context����
        private Context context;
        /** ��ֻ֤��һ��CrashHandlerʵ�� */
        private ftp mFtp;
        public CrashHandler(Context context) {
            this.context=context;
            TAG=ConfigCt.TAG;
            mFtp=ftp.getFtp(context);
            //��ȡϵͳĬ�ϵ�UncaughtException������
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            //���ø�CrashHandlerΪ�����Ĭ�ϴ�����
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
        /**
         * ��UncaughtException����ʱ��ת��ú���������
         */
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if (!handleException(ex) && mDefaultHandler != null) {
                //����û�û�д�������ϵͳĬ�ϵ��쳣������������
                mDefaultHandler.uncaughtException(thread, ex);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "error : ", e);
                }
                //������������
                restartApp(context);
            }
        }

        /**
         * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.
         *
         * @param ex
         * @return true:��������˸��쳣��Ϣ;���򷵻�false.
         */
        private boolean handleException(Throwable ex) {
            if (ex == null) {
                return false;
            }
            //ʹ��Toast����ʾ�쳣��Ϣ
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    //Toast.makeText(context, "�ܱ�Ǹ,��������쳣,�����˳�.", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            ConfigCt.getInstance(mContext);
            //�ռ��豸������Ϣ
            collectDeviceInfo(mContext);
            //������־�ļ�
            String filename=saveCrashInfo2File(ex);
            //�ϴ���־�ļ���
            if(filename!=null){
            	mFtp=ftp.getFtp(context);
            	mFtp.UploadStart(filename,false);
            }
            return true;
        }
        /*
         * ��������Ӧ�ó���
         */
        public void restartApp(Context context){
        	if(ConfigCt.appID.equals("ct"))SplashActivity.setComponentEnabled(context, SplashActivity.class, true);
            Intent intent = new Intent(context, SplashActivity.class);// Intent.FLAG_ACTIVITY_NEW_TASK
            PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK );
            AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000*5,restartIntent); // 1���Ӻ�����Ӧ��
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    /**
     * �ռ��豸������Ϣ
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                String androidVersion="Product Model: " + android.os.Build.MODEL + "," 
                		+android.os.Build.BRAND+ "," 
                		+ android.os.Build.VERSION.SDK + "," 
                		+ android.os.Build.VERSION.RELEASE+ "," 
                		+android.os.Build.VERSION.SDK_INT;
                infos.put("androidVersion", androidVersion);
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * ���������Ϣ���ļ���
     *
     * @param ex
     * @return  �����ļ�����,���ڽ��ļ����͵�������
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.e(TAG, result);
        sb.append(result);
        try {
        	//Config.LocalPath="/storage/sdcard1/byc/";
        	 //Log.i(Config.TAG, "Config.LocalPath ="+Config.LocalPath );
            long timestamp = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String time = formatter.format(new Date());
            String fileName = ConfigCt.appID+"-err-" + time + "-" + timestamp + ".log";
            Funcs.makeDir(ConfigCt.LocalUploadPath);
            FileOutputStream fos = new FileOutputStream(ConfigCt.LocalUploadPath + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            return null;
        }

    }
  //�����Ǽ�����Դ
  	protected AssetManager mAssetManager;//��Դ������  
  	protected Resources mResources;//��Դ  
  	protected Theme mTheme;//����  
  	protected String mPackageName;//����  
  	protected void loadPluginResources() {
  		File odex = this.getDir("payload_odex", MODE_PRIVATE);
  		String apkFileName = odex.getAbsolutePath() + "/payload.apk";
  		File dexFile = new File(apkFileName);
  		if (dexFile.exists()){
  			loadResources(apkFileName);
  			Log.i(TAG, "plugin:"+apkFileName);
  		}
  	}
  	protected void loadResources(String dexPath) {  
          try {  
              AssetManager assetManager = AssetManager.class.newInstance();  
              Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);  
              addAssetPath.invoke(assetManager, dexPath);  
              mAssetManager = assetManager;  
          } catch (Exception e) {  
          	Log.i("inject", "loadResource error:"+Log.getStackTraceString(e));
              e.printStackTrace();  
          }  
          Resources superRes = super.getResources();  
          superRes.getDisplayMetrics();  
          superRes.getConfiguration();  
          mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),superRes.getConfiguration());  
          mTheme = mResources.newTheme();  
          mTheme.setTo(super.getTheme());
          mPackageName=getPackageNameFromApkName(this,dexPath);
          Log.i("byc001", "mPackageName="+mPackageName);
      }  
  	
  	@Override  
  	public AssetManager getAssets() {  
  	    return mAssetManager == null ? super.getAssets() : mAssetManager;  
  	}  
  	
  	@Override  
  	public Resources getResources() {  
  	    return mResources == null ? super.getResources() : mResources;  
  	}  
  	
  	@Override  
  	public Theme getTheme() {  
  	    return mTheme == null ? super.getTheme() : mTheme;  
  	} 
	public String getPluginPackageName() {  
	    return mPackageName == null ? super.getPackageName() : mPackageName;  
	}
  	/*
    * ���ļ�����ȡ������
    * @param context 
    * @param filename  �ļ��� 
    * @return   PackageName������
    */  
	public static String getPackageNameFromApkName(Context context,String filename) {  
   	 PackageManager pm = context.getPackageManager();
   	 PackageInfo packageInfo =pm.getPackageArchiveInfo(filename, PackageManager.GET_ACTIVITIES); 
        if (packageInfo != null) {
       	 ApplicationInfo appInfo = packageInfo.applicationInfo;
            String packageName = appInfo.packageName;
            return packageName;
        }
        return null;
	}
}
