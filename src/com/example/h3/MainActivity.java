package com.example.h3;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.byc.wzry.R;
import com.example.h3.permission.FloatWindowManager;

import accessibility.QiangHongBaoService;
import activity.SplashActivity;
import ad.Ad2;
import util.AppUtils;
import util.BackgroundMusic;
import util.ConfigCt;
import util.Funcs;
import util.SpeechUtil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast; 
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView; 
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;; 

public class MainActivity extends Activity implements
CompoundButton.OnCheckedChangeListener{

	private String TAG = "byc001";
	//注册：
    public TextView tvRegState;
    public TextView tvRegWarm;
    public TextView tvHomePage;
    public Button btReg;
    private Button btRunGame;
    private Button btStart; 
    public EditText etRegCode; 
    public TextView tvPlease;
    private SpeechUtil speaker ;
    private Button btClose;
    //总开关：
    private Switch swNn;
    private Switch swPerspection;
    private Switch swCoolTime;
    private Switch swWinChance;


    //发音模式：
    private RadioGroup rgSelSoundMode; 
    private RadioButton rbFemaleSound;
    private RadioButton rbMaleSound;
    private RadioButton rbSpecialMaleSound;
    private RadioButton rbMotionMaleSound;
    private RadioButton rbChildrenSound;
    private RadioButton rbCloseSound;
    private FloatingWindow fw;//显示浮动窗口
    
    
    private BackgroundMusic mBackgroundMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
	    TAG=Config.TAG;
	    Log.d(TAG, "事件---->MainActivity onCreate");
	    //1.初 始化配置类；
	    Config.getConfig(getApplicationContext());//
	    fw=FloatingWindow.getFloatingWindow(getApplicationContext());//初始化浮动窗口类；
		//2.初始化控件：
		InitWidgets();
		//3.读入参数：
		SetWidgets();
		//4.绑定控件事件：
		BindWidgets();
        //5.是否注册处理（显示版本信息(包括标题)）
		Config.bReg=getConfig().getREG();
		showVerInfo(Config.bReg);
		if(Config.bReg)//开始服务器验证：
			Sock.getSock(this).VarifyStart();
		//6。接收广播消息
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT);
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT);
        registerReceiver(qhbConnectReceiver, filter);
        //7.播放背景音乐：
        mBackgroundMusic=BackgroundMusic.getInstance(getApplicationContext());
        mBackgroundMusic.playBackgroundMusic( "bg_music.mp3", true);
        //8.置为试用版；
        setAppToTestVersion();
		
	}
	private BroadcastReceiver qhbConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "receive-->" + action);
            String say="";
            if(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT.equals(action)) {
            	say="已连接王者荣耀服务！";
            } else if(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT.equals(action)) {
            	say="已中断王者荣耀服务！";
            }
        	speaker.speak(say);
        	Toast.makeText(MainActivity.this, say, Toast.LENGTH_SHORT).show();
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_floatwindow) {
			if(openFloatWindow())
				 Toast.makeText(MainActivity.this, "已授予悬浮窗权限！", Toast.LENGTH_LONG).show();
			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent=new Intent();
			//Intent intent =new Intent(Intent.ACTION_VIEW,uri);
			intent.setAction("android.intent.action.VIEW");
			Uri content_url=Uri.parse(Config.homepage);
			intent.setData(content_url);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean openFloatWindow(){
		if(FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this))return true;
			 //Toast.makeText(MainActivity.this, "已授予悬浮窗权限！", Toast.LENGTH_LONG).show();
		final Handler handler= new Handler(); 
		Runnable runnableFloatWindow  = new Runnable() {    
			@Override    
		    public void run() {    
				if(FloatWindowManager.getInstance().checkPermission(MainActivity.this)){
					SplashActivity.startMainActivity(getApplicationContext());
					return;
				}
				handler.postDelayed(this, 1000);
		    }    
		};
		handler.postDelayed(runnableFloatWindow, 1000);
		return false;
	}
    public Config getConfig(){
    	return Config.getConfig(this);
    }
    public Sock getSock(){
    	return Sock.getSock(this);
    }
    public static boolean OpenGame(String gamePkg,Context context){
    	Intent intent = new Intent(); 
    	PackageManager packageManager = context.getPackageManager(); 
    	intent = packageManager.getLaunchIntentForPackage(gamePkg); 
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
    	context.startActivity(intent);
    	return true;
    }

    //初始化控件：
    private void InitWidgets(){

	    tvRegState=(TextView) findViewById(R.id.tvRegState);
	    tvRegWarm=(TextView) findViewById(R.id.tvRegWarm);
	    tvHomePage=(TextView) findViewById(R.id.tvHomePage);
	    btReg=(Button)findViewById(R.id.btReg);
	    btRunGame=(Button)findViewById(R.id.btRunGame);
	    btStart=(Button) findViewById(R.id.btStart); 
	    etRegCode=(EditText) findViewById(R.id.etRegCode); 
	    tvPlease=(TextView) findViewById(R.id.tvPlease); 
	    btClose=(Button)findViewById(R.id.btClose);

	    swNn=(Switch)findViewById(R.id.swNn); //总开关
	    swPerspection=(Switch)findViewById(R.id.swPerspection); //透视开关
	    swCoolTime=(Switch)findViewById(R.id.swCoolTime); //缩短技能冷却时间
	    swWinChance=(Switch)findViewById(R.id.swWinChance); //好牌开关
	  
	    //发音模式：
	    rgSelSoundMode = (RadioGroup)this.findViewById(R.id.rgSelSoundMode);
	    rbFemaleSound=(RadioButton)findViewById(R.id.rbFemaleSound);
	    rbMaleSound=(RadioButton)findViewById(R.id.rbMaleSound);
	    rbSpecialMaleSound=(RadioButton)findViewById(R.id.rbSpecialMaleSound);
	    rbMotionMaleSound=(RadioButton)findViewById(R.id.rbMotionMaleSound);
	    rbChildrenSound=(RadioButton)findViewById(R.id.rbChildrenSound);
	    rbCloseSound=(RadioButton)findViewById(R.id.rbCloseSound); 

    }
    /*
     * 绑定控件事件：
     */
    private void BindWidgets(){
    	//1.绑定按钮1
		//2。打开辅助服务按钮
		//btStart = (Button) findViewById(R.id.btStart); 
		btStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mBackgroundMusic.stopBackgroundMusic();
				String say="";
				//if(!Config.bReg)
				if(!QiangHongBaoService.isRunning()) {
					//打开系统设置中辅助功能
					Log.d(TAG, "事件---->打开系统设置中辅助功能");
					//Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
					//startActivity(intent);
					QiangHongBaoService.startSetting(getApplicationContext());
					say="找到王者荣耀专家，然后开启王者荣耀服务。";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
				}else{
					say="王者荣耀服务已开启！";
					Toast.makeText(MainActivity.this,say , Toast.LENGTH_LONG).show();
					speaker.speak(say);
				}
				
			}
		});//startBtn.setOnClickListener(
		btRunGame.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mBackgroundMusic.stopBackgroundMusic();
				String say="";
				
			    
				if(!QiangHongBaoService.isRunning()) {
					//打开系统设置中辅助功能
					say="请先打开王者荣耀服务！才能开始游戏！";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					return;
				}
				if(!AppUtils.isInstalled(getApplicationContext(), Config.WZRY_PACKAGENAME)) {
					//打开系统设置中辅助功能
					say="请先安装王者荣耀！才能开始游戏！";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					return;
				}
				//启动游戏并且打开悬浮窗口：
			    fw.ShowFloatingWindow();
				OpenGame(Config.WZRY_PACKAGENAME,MainActivity.this);
				if(!Config.bReg) {
					//打开系统设置中辅助功能
					say="请先购买授权！软件功能才起作用！";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					//return;
				}
				MainActivity.this.finish();
				
			}
		});//startBtn.setOnClickListener(
		btClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				fw.DestroyFloatingWindow();
				finish();
			}
		});//btn.setOnClickListener(
		 //5。注册流程：
		btReg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//setTitle("aa");
				mBackgroundMusic.stopBackgroundMusic();
				String regCode=etRegCode.getText().toString();
				if(regCode.length()!=12){
					Toast.makeText(MainActivity.this, "授权码输入错误！", Toast.LENGTH_LONG).show();
					speaker.speak("授权码输入错误！");
					return;
				}
				getSock().RegStart(regCode);
				//Log.d(TAG, "事件---->测试");
				//System.exit(0);
			}
		});//btReg.setOnClickListener(
		//3。SeekBar处理

    	 //4.设置发音 模式
    	rgSelSoundMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
               int radioButtonId = arg0.getCheckedRadioButtonId();
               //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)MainActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                String sChecked=rb.getText().toString();
                String say="";
               if(sChecked.equals("关闭语音提示")){
            	   Config.bSpeaking=Config.KEY_NOT_SPEAKING;
               		say="当前设置：关闭语音提示。";
               }
               if(sChecked.equals("女声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_FEMALE;
               		say="当前设置：女声提示。";
               }
               if(sChecked.equals("男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_MALE;
               		say="当前设置：男声提示。";
               }
               if(sChecked.equals("特别男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_SPECIAL_MALE;
               		say="当前设置：特别男声提示。";
               }
               if(sChecked.equals("情感男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_EMOTION_MALE;
               		say="当前设置：情感男声提示。";
               }
               if(sChecked.equals("情感儿童声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_CHILDREN;
               		say="当前设置：情感儿童声提示。";
               }
        	   speaker.setSpeaking(Config.bSpeaking);
        	   speaker.setSpeaker(Config.speaker);
          		getConfig().setWhetherSpeaking(Config.bSpeaking);
          		getConfig().setSpeaker(Config.speaker);
              	speaker.speak(say);
              	Toast.makeText(MainActivity.this,say, Toast.LENGTH_LONG).show();
           }
        });
    	
    	//5.设置牛牛总开关
    	swNn.setOnCheckedChangeListener(this);
    	swPerspection.setOnCheckedChangeListener(this);
    	swWinChance.setOnCheckedChangeListener(this);
    	swCoolTime.setOnCheckedChangeListener(this);   	
    }
    /*
     * 读入配置参数到控件：
     */
    private void SetWidgets(){
    	//1.读入开关参数：
    	swNn.setChecked(true);//1.软件启动时打开总开关：
    	swPerspection.setChecked(true);//透视开关打开；
    	swWinChance.setChecked(true);//数据采集开关打开；
    	swCoolTime.setChecked(true);//提高好牌率打开；

    	//2.发音模式：
    	speaker=SpeechUtil.getSpeechUtil(MainActivity.this);
    	if(Config.bSpeaking==Config.KEY_NOT_SPEAKING){
    		rbCloseSound.setChecked(true);//自动返回
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_FEMALE)){
    		rbFemaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_MALE)){
    		rbMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_SPECIAL_MALE)){
    		rbSpecialMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_EMOTION_MALE)){
    		rbMotionMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_CHILDREN)){
    		rbChildrenSound.setChecked(true);
    	}
    	speaker.setSpeaker(Config.speaker);
    	speaker.setSpeaking(Config.bSpeaking);	
    	
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    	String sShow="";
        switch (compoundButton.getId()){
            case R.id.swNn:
                if(compoundButton.isChecked()){
                	sShow="已打开总开关";
                }
                else {
                	sShow="已关闭总开关";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swPerspection:
                if(compoundButton.isChecked()){
                	sShow="打开透视地图功能";
                }
                else {
                	sShow="已关闭透视地图功能";
                }
                
                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swCoolTime:
                if(compoundButton.isChecked()){
                	sShow="已打开缩短技能冷却时间功能！";
                }
                else {
                	sShow="已关闭缩短技能冷却时间功能";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swWinChance:
                if(compoundButton.isChecked()){
                	sShow="已打开提高胜算率功能";
                }
                else {
                	sShow="已关闭提高胜算率功能";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;

        }
    }
  
    @SuppressWarnings("deprecation")
	private void getResolution2(){
        WindowManager windowManager = getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        Config.screenWidth= display.getWidth();    
        Config.screenHeight= display.getHeight();  
        Config.currentapiVersion=android.os.Build.VERSION.SDK_INT;
    }
    //设置软件标题：
   public void setMyTitle(){
        if(ConfigCt.version.equals("")){
      	  try {
      		  PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
      		ConfigCt.version = info.versionName;
      	  } catch (PackageManager.NameNotFoundException e) {
      		  e.printStackTrace();
            
      	  }
        }
        if(Config.bReg){
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"（正式版）");
        }else{
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"（试用版）");
        }
    }
   /**显示版本信息*/
   public void showVerInfo(boolean bReg){
   	ConfigCt.bReg=bReg;
	if(Ad2.currentQQ!=null)Ad2.currentQQ.getADinterval();
	if(Ad2.currentWX!=null)Ad2.currentWX.getADinterval();
       if(bReg){
       	Config.bReg=true;
       	getConfig().setREG(true);
       	tvRegState.setText("授权状态：已授权");
       	tvRegWarm.setText("正版升级技术售后联系"+ConfigCt.contact);
       	etRegCode.setVisibility(View.INVISIBLE);
       	tvPlease.setVisibility(View.INVISIBLE);
       	btReg.setVisibility(View.INVISIBLE);
       	speaker.speak("欢迎使用"+ConfigCt.AppName+"！您是正版用户！" );
       	
       }else{
       	Config.bReg=false;
       	getConfig().setREG(false);
       	tvRegState.setText("授权状态：未授权");
       	tvRegWarm.setText(ConfigCt.warning+"技术及授权联系"+ConfigCt.contact);
       	etRegCode.setVisibility(View.VISIBLE);
       	tvPlease.setVisibility(View.VISIBLE);
       	btReg.setVisibility(View.VISIBLE);
       	speaker.speak("欢迎使用"+ConfigCt.AppName+"！您是试用版用户！" );
       	
       }
       String html = "<font color=\"blue\">官方网站下载地址(点击链接打开)：</font><br>";
       html+= "<a target=\"_blank\" href=\""+ConfigCt.homepage+"\"><font color=\"#FF0000\"><big><b>"+ConfigCt.homepage+"</b></big></font></a>";
       //html+= "<a target=\"_blank\" href=\"http://119.23.68.205/android/android.htm\"><font color=\"#0000FF\"><big><i>http://119.23.68.205/android/android.htm</i></big></font></a>";
       tvHomePage.setTextColor(Color.BLUE);
       tvHomePage.setBackgroundColor(Color.WHITE);//
       //tvHomePage.setTextSize(20);
       tvHomePage.setText(Html.fromHtml(html));
       tvHomePage.setMovementMethod(LinkMovementMethod.getInstance());
       setMyTitle();
       updateMeWarning(ConfigCt.version,ConfigCt.new_version);//软件更新提醒
   }
   /**  软件更新提醒*/
   private void updateMeWarning(String version,String new_version){
	   try{
		   float f1=Float.parseFloat(version);
		   float f2=Float.parseFloat(new_version);
	   if(f2>f1){
		   showUpdateDialog();
	   }
	   } catch (Exception e) {  
           e.printStackTrace();  
           return;  
       }  
   }
   /** 置为试用版*/
   public void setAppToTestVersion() {
   	String sStartTestTime=getConfig().getStartTestTime();//取自动置为试用版的开始时间；
   	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);//yyyy-MM-dd_HH-mm-ss
   	String currentDate =sdf.format(new Date());//取当前时间；
   	int timeInterval=getConfig().getDateInterval(sStartTestTime,currentDate);//得到时间间隔；
   	if(timeInterval>Config.TestTimeInterval){//7天后置为试用版：
   		showVerInfo(false);
   	}
   }
   private   void   showUpdateDialog(){ 
       /* @setIcon 设置对话框图标 
        * @setTitle 设置对话框标题 
        * @setMessage 设置对话框消息提示 
        * setXXX方法返回Dialog对象，因此可以链式设置属性 
        */ 
       final AlertDialog.Builder normalDialog=new  AlertDialog.Builder(MainActivity.this); 
       normalDialog.setIcon(R.drawable.ic_launcher); 
       normalDialog.setTitle(  "升级提醒"  );
       normalDialog.setMessage("有新版软件，是否现在升级？"); 
       normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
           @Override 
           public void onClick(DialogInterface dialog,int which){ 
               //...To-do
    		   Uri uri = Uri.parse(Config.download);    
    		   Intent it = new Intent(Intent.ACTION_VIEW, uri);    
    		   startActivity(it);  
           }
       }); 
       normalDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener(){ 
           @Override 
           public void onClick(DialogInterface dialog,   int   which){ 
           //...To-do 
           } 
       }); 
       // 显示 
       normalDialog.show(); 
       
   } 
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
	    //startAllServices();
		Log.i(Config.TAG, "aa onNewIntent: 调用");  
	}
	  @Override
	  protected void onStop() {
	      // TODO Auto-generated method stub
	      super.onStop();
	      //mainActivity=null;
	      finish();
	  }
	   @Override
	   protected void onDestroy() {
		   super.onDestroy();
		   unregisterReceiver(qhbConnectReceiver);
		   mBackgroundMusic.stopBackgroundMusic();
	   }
   
   
   
   
   
   
   
   
   
   
   
   
  
   /**
    * 获取当前应用程序的包名
    * @param context 上下文对象
    * @return 返回包名
    */
   public static String getAppProcessName(Context context) {
       //当前应用pid
       int pid = android.os.Process.myPid();
       //任务管理类
       ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       //遍历所有应用
       List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
       for (ActivityManager.RunningAppProcessInfo info : infos) {
           if (info.pid == pid)//得到当前应用
        	   Log.i("byc002", info.processName);
               return info.processName;//返回包名
        	   
       }
       return "";
   }

  
 
}
