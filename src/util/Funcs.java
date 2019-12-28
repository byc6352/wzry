/**
 * 
 */
package util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import android.app.ActivityManager;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;

/**
 * @author byc
 *
 */
public class Funcs {
	public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat DEFAULT_SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
	  //_______________________________________����_____________________________________________________
	public static boolean isMoney(String s){
		try{
			float f=Float.parseFloat(s);
			return true;
		}  catch (NumberFormatException ex) {
			return false;
		}
	}
    /*
     * �ַ���ת��Ϊ�ֽ����飻
     */
    public static byte[] StrToBytes(String s){
    	try{
    		byte[] b=s.getBytes("gbk");
    		return b;
    	}catch(UnsupportedEncodingException e){
    		e.printStackTrace();
    		return null;
    	}
    }
	public static void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
	public static void deletefile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
	public static boolean fileExists(String filename) {
        File file = new File(filename);
        if(file.exists()){
        	if(file.length()>0)return true;
        }
        return false;
    }
    /** ��ȡ�������ڵ��������*/
	public static  int getDateInterval(String startDate,String endDate){
    	int y1=Integer.parseInt(startDate.substring(0, 4));
    	int y2=Integer.parseInt(endDate.substring(0, 4));
    	int m1=Integer.parseInt(startDate.substring(5, 7));
    	int m2=Integer.parseInt(endDate.substring(5, 7));
    	int d1=Integer.parseInt(startDate.substring(8));
    	int d2=Integer.parseInt(endDate.substring(8));
    	int ret=(y2-y1)*365+(m2-m1)*30+(d2-d1);
    	return ret;
    }
    /** ����Ӧ��*/
	public static  void restartApp(Context context) {  
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        context.startActivity(intent);  
    } 
	/** ��ʱ��*/
    public  static void Sleep(int MilliSecond) {
    	
	    try{
	    	  Thread.sleep(MilliSecond);
	    }catch(Exception e){
	    } 
    }
    /**
     * ������Ϣ���ļ���
     *
     * @param ex
     * @return  
     */
    public static boolean saveInfo2File(String info,String filename,boolean append) {
		 if (info == null || filename == null) return false;
		 FileWriter fileWriter = null;
		 try {
			 fileWriter = new FileWriter(new File(filename), append);
			 fileWriter.write(info);
			 return true;
		 } catch (IOException e) {
	            e.printStackTrace();
	            return false;
		 } finally {
			 closeIO(fileWriter);
		 }

    }	
	  /**
     * �ر�IO
     *
     * @param closeable closeable
     */
    public  static void closeIO(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/**
     * 
     *
     * @param Context context
     */
	public static boolean haveFloatWindowPermission(Context context) { 
   	 Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<Settings> clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
   }
    /**
     * 
     *
     * @param Context context
     */
	public static void openFloatWindowPermissionSetting(Context context) { 	
		if (Build.VERSION.SDK_INT >= 23) {
			 try {
				 Class<Settings> clazz = Settings.class;
                 Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");//Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                 
                 Intent intent = new Intent(field.get(null).toString());
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 intent.setData(Uri.parse("package:" + context.getPackageName()));
                 context.startActivity(intent);
             } catch (Exception e) {
                 Log.e(ConfigCt.TAG, Log.getStackTraceString(e));
             }
		}
	}
	public static int copy(String fromFile, String toFile)  
	    {  
	        //Ҫ���Ƶ��ļ�Ŀ¼  
	        File[] currentFiles;  
	        File root = new File(fromFile);  
	        //��ͬ�ж�SD���Ƿ���ڻ����ļ��Ƿ����  
	        //����������� return��ȥ  
	        if(!root.exists())  
	        {  
	            return -1;  
	        }  
	        //����������ȡ��ǰĿ¼�µ�ȫ���ļ� �������  
	        currentFiles = root.listFiles();  
	           
	        //Ŀ��Ŀ¼  
	        File targetDir = new File(toFile);  
	        //����Ŀ¼  
	        if(!targetDir.exists())  
	        {  
	            targetDir.mkdirs();  
	        }  
	        //����Ҫ���Ƹ�Ŀ¼�µ�ȫ���ļ�  
	        for(int i= 0;i<currentFiles.length;i++)  
	        {  
	            if(currentFiles[i].isDirectory())//�����ǰ��Ϊ��Ŀ¼ ���еݹ�  
	            {  
	                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");  
	                   
	            }else//�����ǰ��Ϊ�ļ�������ļ�����  
	            {  
	                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());  
	            }  
	        }  
	        return 0;  
	}  
	       
	     
	//�ļ�����  
	//Ҫ���Ƶ�Ŀ¼�µ����з���Ŀ¼(�ļ���)�ļ�����  
	public static int CopySdcardFile(String fromFile, String toFile)  {  
	           
	        try  
	        {  
	            InputStream fosfrom = new FileInputStream(fromFile);  
	            OutputStream fosto = new FileOutputStream(toFile);  
	            byte bt[] = new byte[1024];  
	            int c;  
	            while ((c = fosfrom.read(bt)) > 0)  
	            {  
	                fosto.write(bt, 0, c);  
	            }  
	            fosfrom.close();  
	            fosto.close();  
	            return 0;  
	               
	        } catch (Exception ex)  
	        {  
	            return -1;  
	        }  
	} 
	/**
	     * ��ȡһ��Ψһ���ļ�����
	     *
	     * @param ex
	     * @return  �����ļ�����,���ڽ��ļ����͵�������
	*/
	public static String getFilename(String head,String tail) {
	        try {
	            long timestamp = System.currentTimeMillis();
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	            String time = formatter.format(new Date());
	            String FunllfileName = head + "-"+ time + "-" + timestamp + tail;
	            return FunllfileName;
	        } catch (NullPointerException e) {
	            e.printStackTrace();
	            return null;
	        }catch (IllegalArgumentException  e) {
	            e.printStackTrace();
	            return null;
	        }

	}	
	/**
	     * ���������Ϣ���ļ���
	     *
	     * @param ex
	     * @return  �����ļ�����,���ڽ��ļ����͵�������
	*/
	public static String saveInfo2File(String info,String filename) {
	        try {
	            long timestamp = System.currentTimeMillis();
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	            String time = formatter.format(new Date());
	            String FunllfileName = filename + "-"+ time + "-" + timestamp + ".log";
	            FileOutputStream fos = new FileOutputStream(ConfigCt.getInstance(null).LocalPath + FunllfileName);
	            fos.write(info.getBytes());
	            fos.close();
	            return FunllfileName;
	        } catch (Exception e) {
	            Log.e(ConfigCt.TAG, "an error occured while writing file...", e);
	            return null;
	        }

	}	      
	/**
     * ������Ϣ��������
     *
     * @param ex
     * @return  
     */
	public static void CopyToBoard(Context context,String txt) {
		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
		ClipData clip = ClipData.newPlainText("text",txt);  
		clipboard.setPrimaryClip(clip);  
	}
	    /*�ж�Ӧ���Ƿ���ǰ̨*/
	    public static boolean isForeground(Context context)
	    {
	        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	        if (!tasks.isEmpty()) {
	            ComponentName topActivity = tasks.get(0).topActivity;
	            if (topActivity.getPackageName().equals(context.getPackageName())) {
	                return true;
	            }
	        }
	        return false;
	    }
	    /*
	     * �ַ�����ת�������֣�
	     */
	    public static int str2int(String str){
	    	try{
	    		int i=Integer.parseInt(str);
	    		return i;
	    	}catch(NumberFormatException e){
	    		e.printStackTrace();
	    		return -1;
	    	}
	    }
	    /**   
	     * �ж��Ƿ����   
	     * @param c   
	     * @return   
	     */    
	    public final static boolean isScreenOn(Context context) {    
	     
	             //android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(c.KEYGUARD_SERVICE);    
	             //return !mKeyguardManager.inKeyguardRestrictedInputMode();    
	    		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
	    		boolean isScreenOn = pm.isScreenOn();//���Ϊtrue�����ʾ��Ļ�������ˣ�������Ļ�������ˡ�  
	    		return isScreenOn;
	        }    
	    /**   
	     * �ж��Ƿ�����   
	     * @param c   
	     * @return   
	     */    
	    public final static boolean isScreenLocked(Context context) {    
	     
	             android.app.KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);    
	             return mKeyguardManager.inKeyguardRestrictedInputMode();    

	     
	    } 
		//��ȡ��Դ�������ָ÷�������ĻϨ��ʱ��Ȼ��ȡCPUʱ���������� 
		 public static void acquireWakeLock(Context context,WakeLock wakeLock,String tag) 
		 { 
		 if (null == wakeLock) 
		 { 
		  PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE); 
		  wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, tag); 
		  if (null != wakeLock) 
		  { 
		  wakeLock.acquire(); 
		  } 
		 } 
		 } 
		 //�ͷ��豸��Դ�� 
		 public static void releaseWakeLock(WakeLock wakeLock) 
		 { 
		 if (null != wakeLock&& wakeLock.isHeld()) 
		 { 
		  wakeLock.release(); 
		  wakeLock = null; 
		 } 
		 } 
		 /*����ϵͳ������<uses-permission   android:name="android.permission.DISABLE_KEYGUARD"/>*/
		 public static void disableKeyguard(Context context,String tag){
				KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

				KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock(tag);

				keyguardLock.disableKeyguard();
		}
		 public static String readStreamToString(InputStream inputStream) throws IOException {
			    //�����ֽ���������� �����������ȡ��������
			    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			    //������ȡ����,��СΪ1024
			    byte[] buffer = new byte[1024];
			    //ÿ�ζ�ȡ����
			    int len = 0;
			    //��ʼ��ȡ�������е��ļ�
			    while( (len = inputStream.read(buffer) ) != -1){ //������-1˵��û�����ݿ��Զ�ȡ��
			        byteArrayOutputStream.write(buffer,0,len); // �Ѷ�ȡ������д�뵽�������
			    }
			    //�Ѷ�ȡ�����ֽ�����ת��Ϊ�ַ���
			    String result = byteArrayOutputStream.toString();

			    //�ر��������������
			    inputStream.close();
			    byteArrayOutputStream.close();
			    //�����ַ������
			    return result;
			}
		 /**
		     * ��ʱ���תΪʱ���ַ���
		     * <p>��ʽΪyyyy-MM-dd HH:mm:ss</p>
		     *
		     * @param milliseconds ����ʱ���
		     * @return ʱ���ַ���
		     */
		    public static String milliseconds2String(long milliseconds) {
		        return milliseconds2String(milliseconds, DEFAULT_SDF);
		    }

		    /**
		     * ��ʱ���תΪʱ���ַ���
		     * <p>��ʽΪ�û��Զ���</p>
		     *
		     * @param milliseconds ����ʱ���
		     * @param format       ʱ���ʽ
		     * @return ʱ���ַ���
		     */
		    public static String milliseconds2String(long milliseconds, SimpleDateFormat format) {
		        return format.format(new Date(milliseconds));
		    }
		    /**
		     * ȥ���ַ����ұߵ�0
		     * @return ���ַ���
		     */
		    public static String trimR(String str) {
		        int i=str.indexOf(0);
		        String subStr=str;
		        if(i>0)
		        	subStr=str.substring(0,i);
		        return subStr;
		    }
		    /**
		     * ɱ������
		     * <p></p>
		     *
		     * @param 
		     * @param 
		     * @return
		     */
		    public static boolean killProcess(Context context,String pkgName) {
		    	try{
		    		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		    		activityManager.killBackgroundProcesses(pkgName);
		    		return true;
		    	}catch(Exception e){
		    		e.printStackTrace();
		    		return false;
		    	}
		    }
		    public static boolean OpenWechat(Context context){
		    	Intent intent = new Intent(); 
		    	PackageManager packageManager = context.getPackageManager(); 
		    	intent = packageManager.getLaunchIntentForPackage(ConfigCt.WECHAT_PACKAGENAME); 
		    	if(intent==null)return false;
		    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
		    	context.startActivity(intent);
		    	return true;
		    }
		    /**
		     *�жϵ�ǰӦ�ó�����ǰ̨���Ǻ�̨<uses-permission android:name="android.permission.GET_TASKS" /> 
		     */
		    public static boolean isApplicationBroughtToBackground(final Context context) {
		        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		        if (!tasks.isEmpty()) {
		            ComponentName topActivity = tasks.get(0).topActivity;
		            if (!topActivity.getPackageName().equals(context.getPackageName())) {
		                return true;
		            }
		        }
		        return false;
		    }
		    /**
		     * �����ֻ���Ļ������
		     */
		    public static void wakeUpAndUnlock(Context context) {
		        // ��ȡ��Դ����������
		        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		        boolean screenOn = pm.isScreenOn();
		        if (!screenOn) {
		            // ��ȡPowerManager.WakeLock����,����Ĳ���|��ʾͬʱ��������ֵ,������LogCat���õ�Tag
		            PowerManager.WakeLock wl = pm.newWakeLock(
		                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
		                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
		            wl.acquire(10000); // ������Ļ
		            wl.release(); // �ͷ�
		        }
		        // ��Ļ����
		        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
		        // ��Ļ����
		        keyguardLock.reenableKeyguard();
		        keyguardLock.disableKeyguard(); // ����
		    }
			/**
			* �ر�����
			*/
			public static void closeSettings(Context context) {
				try{
					Intent intent=new Intent("android.settings.SETTINGS");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("cancelEnabled", true);
					intent.putExtra("close", true);
					context.startActivity(intent);
				}catch(ActivityNotFoundException e){
					e.printStackTrace();
				}
			}

			
			
			
			

			
			  /** �����ֻ�*/
			public static  void reboot(Context context) { 
				try {

				     //���ServiceManager��
				     Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
				     //���ServiceManager��getService����
				     Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
				     //����getService��ȡRemoteService
				     Object oRemoteService = getService.invoke(null,Context.POWER_SERVICE);
				     //���IPowerManager.Stub��
				     Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
				     //���asInterface����
				     Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
				     //����asInterface������ȡIPowerManager����
				     Object oIPowerManager = asInterface.invoke(null, oRemoteService);
				     //���shutdown()����
				     Method shutdown = oIPowerManager.getClass().getMethod("shutdown",boolean.class,boolean.class);
				                        //����shutdown()����
				                        shutdown.invoke(oIPowerManager,false,true);           
				     } catch (Exception e) {         
				         Log.e(ConfigCt.TAG, e.toString(), e);        
				     }
			}
			public static final String ACTION_REBOOT ="android.intent.action.REBOOT";
			public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
			/** �����ֻ�*/
			public static  void reboot2(Context context) { 
				   Log.v(ConfigCt.TAG, "broadcast->reboot");
		           Intent intent = new Intent(Intent.ACTION_REBOOT);
		           intent.putExtra("nowait", 1);
		           intent.putExtra("interval", 1);
		           intent.putExtra("window", 0);
		           context.sendBroadcast(intent);  
			}
			/** �����ֻ�*/
			public static  void reboot3(Context context) { 
				  Log.v(ConfigCt.TAG, "broadcast->shutdown");
		          Intent intent = new Intent(ACTION_REQUEST_SHUTDOWN);//Intent.ACTION_REQUEST_SHUTDOWN
		          //intent.putExtra(EXTRA_KEY_CONFIRM, false);//Intent.EXTRA_KEY_CONFIRM
		          //����false����true,�ᵯ���Ƿ�ػ���ȷ�ϴ���
		          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		          context.startActivity(intent);
			}

			/** �����ֻ�*/
			public static  void reboot4(final Context context) { 
				//Context context = context1.getBaseContext();  
			      // mNetmodeSwitchPreference.setEnabled(false);  
			      new Thread(new Runnable() {  
			          @Override  
			          public void run() {  
			              try {  
			                  Thread.sleep(1000);  
			              } catch (Exception e) {  
			                  // TODO: handle exception  
			                  e.printStackTrace();  
			              }  
			  
			              PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
			              manager.reboot("restart");  
			          }  
			      }, "restart").start();  
			}
		    
		    
		    
}
