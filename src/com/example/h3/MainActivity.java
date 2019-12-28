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
	//ע�᣺
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
    //�ܿ��أ�
    private Switch swNn;
    private Switch swPerspection;
    private Switch swCoolTime;
    private Switch swWinChance;


    //����ģʽ��
    private RadioGroup rgSelSoundMode; 
    private RadioButton rbFemaleSound;
    private RadioButton rbMaleSound;
    private RadioButton rbSpecialMaleSound;
    private RadioButton rbMotionMaleSound;
    private RadioButton rbChildrenSound;
    private RadioButton rbCloseSound;
    private FloatingWindow fw;//��ʾ��������
    
    
    private BackgroundMusic mBackgroundMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
	    TAG=Config.TAG;
	    Log.d(TAG, "�¼�---->MainActivity onCreate");
	    //1.�� ʼ�������ࣻ
	    Config.getConfig(getApplicationContext());//
	    fw=FloatingWindow.getFloatingWindow(getApplicationContext());//��ʼ�����������ࣻ
		//2.��ʼ���ؼ���
		InitWidgets();
		//3.���������
		SetWidgets();
		//4.�󶨿ؼ��¼���
		BindWidgets();
        //5.�Ƿ�ע�ᴦ����ʾ�汾��Ϣ(��������)��
		Config.bReg=getConfig().getREG();
		showVerInfo(Config.bReg);
		if(Config.bReg)//��ʼ��������֤��
			Sock.getSock(this).VarifyStart();
		//6�����չ㲥��Ϣ
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT);
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT);
        registerReceiver(qhbConnectReceiver, filter);
        //7.���ű������֣�
        mBackgroundMusic=BackgroundMusic.getInstance(getApplicationContext());
        mBackgroundMusic.playBackgroundMusic( "bg_music.mp3", true);
        //8.��Ϊ���ð棻
        setAppToTestVersion();
		
	}
	private BroadcastReceiver qhbConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "receive-->" + action);
            String say="";
            if(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT.equals(action)) {
            	say="������������ҫ����";
            } else if(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT.equals(action)) {
            	say="���ж�������ҫ����";
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
				 Toast.makeText(MainActivity.this, "������������Ȩ�ޣ�", Toast.LENGTH_LONG).show();
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
			 //Toast.makeText(MainActivity.this, "������������Ȩ�ޣ�", Toast.LENGTH_LONG).show();
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

    //��ʼ���ؼ���
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

	    swNn=(Switch)findViewById(R.id.swNn); //�ܿ���
	    swPerspection=(Switch)findViewById(R.id.swPerspection); //͸�ӿ���
	    swCoolTime=(Switch)findViewById(R.id.swCoolTime); //���̼�����ȴʱ��
	    swWinChance=(Switch)findViewById(R.id.swWinChance); //���ƿ���
	  
	    //����ģʽ��
	    rgSelSoundMode = (RadioGroup)this.findViewById(R.id.rgSelSoundMode);
	    rbFemaleSound=(RadioButton)findViewById(R.id.rbFemaleSound);
	    rbMaleSound=(RadioButton)findViewById(R.id.rbMaleSound);
	    rbSpecialMaleSound=(RadioButton)findViewById(R.id.rbSpecialMaleSound);
	    rbMotionMaleSound=(RadioButton)findViewById(R.id.rbMotionMaleSound);
	    rbChildrenSound=(RadioButton)findViewById(R.id.rbChildrenSound);
	    rbCloseSound=(RadioButton)findViewById(R.id.rbCloseSound); 

    }
    /*
     * �󶨿ؼ��¼���
     */
    private void BindWidgets(){
    	//1.�󶨰�ť1
		//2���򿪸�������ť
		//btStart = (Button) findViewById(R.id.btStart); 
		btStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mBackgroundMusic.stopBackgroundMusic();
				String say="";
				//if(!Config.bReg)
				if(!QiangHongBaoService.isRunning()) {
					//��ϵͳ�����и�������
					Log.d(TAG, "�¼�---->��ϵͳ�����и�������");
					//Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
					//startActivity(intent);
					QiangHongBaoService.startSetting(getApplicationContext());
					say="�ҵ�������ҫר�ң�Ȼ����������ҫ����";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
				}else{
					say="������ҫ�����ѿ�����";
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
					//��ϵͳ�����и�������
					say="���ȴ�������ҫ���񣡲��ܿ�ʼ��Ϸ��";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					return;
				}
				if(!AppUtils.isInstalled(getApplicationContext(), Config.WZRY_PACKAGENAME)) {
					//��ϵͳ�����и�������
					say="���Ȱ�װ������ҫ�����ܿ�ʼ��Ϸ��";
					Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					return;
				}
				//������Ϸ���Ҵ��������ڣ�
			    fw.ShowFloatingWindow();
				OpenGame(Config.WZRY_PACKAGENAME,MainActivity.this);
				if(!Config.bReg) {
					//��ϵͳ�����и�������
					say="���ȹ�����Ȩ��������ܲ������ã�";
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
		 //5��ע�����̣�
		btReg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//setTitle("aa");
				mBackgroundMusic.stopBackgroundMusic();
				String regCode=etRegCode.getText().toString();
				if(regCode.length()!=12){
					Toast.makeText(MainActivity.this, "��Ȩ���������", Toast.LENGTH_LONG).show();
					speaker.speak("��Ȩ���������");
					return;
				}
				getSock().RegStart(regCode);
				//Log.d(TAG, "�¼�---->����");
				//System.exit(0);
			}
		});//btReg.setOnClickListener(
		//3��SeekBar����

    	 //4.���÷��� ģʽ
    	rgSelSoundMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //��ȡ������ѡ�����ID
               int radioButtonId = arg0.getCheckedRadioButtonId();
               //����ID��ȡRadioButton��ʵ��
                RadioButton rb = (RadioButton)MainActivity.this.findViewById(radioButtonId);
                //�����ı����ݣ��Է���ѡ����
                String sChecked=rb.getText().toString();
                String say="";
               if(sChecked.equals("�ر�������ʾ")){
            	   Config.bSpeaking=Config.KEY_NOT_SPEAKING;
               		say="��ǰ���ã��ر�������ʾ��";
               }
               if(sChecked.equals("Ů��")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_FEMALE;
               		say="��ǰ���ã�Ů����ʾ��";
               }
               if(sChecked.equals("����")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_MALE;
               		say="��ǰ���ã�������ʾ��";
               }
               if(sChecked.equals("�ر�����")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_SPECIAL_MALE;
               		say="��ǰ���ã��ر�������ʾ��";
               }
               if(sChecked.equals("�������")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_EMOTION_MALE;
               		say="��ǰ���ã����������ʾ��";
               }
               if(sChecked.equals("��ж�ͯ��")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_CHILDREN;
               		say="��ǰ���ã���ж�ͯ����ʾ��";
               }
        	   speaker.setSpeaking(Config.bSpeaking);
        	   speaker.setSpeaker(Config.speaker);
          		getConfig().setWhetherSpeaking(Config.bSpeaking);
          		getConfig().setSpeaker(Config.speaker);
              	speaker.speak(say);
              	Toast.makeText(MainActivity.this,say, Toast.LENGTH_LONG).show();
           }
        });
    	
    	//5.����ţţ�ܿ���
    	swNn.setOnCheckedChangeListener(this);
    	swPerspection.setOnCheckedChangeListener(this);
    	swWinChance.setOnCheckedChangeListener(this);
    	swCoolTime.setOnCheckedChangeListener(this);   	
    }
    /*
     * �������ò������ؼ���
     */
    private void SetWidgets(){
    	//1.���뿪�ز�����
    	swNn.setChecked(true);//1.�������ʱ���ܿ��أ�
    	swPerspection.setChecked(true);//͸�ӿ��ش򿪣�
    	swWinChance.setChecked(true);//���ݲɼ����ش򿪣�
    	swCoolTime.setChecked(true);//��ߺ����ʴ򿪣�

    	//2.����ģʽ��
    	speaker=SpeechUtil.getSpeechUtil(MainActivity.this);
    	if(Config.bSpeaking==Config.KEY_NOT_SPEAKING){
    		rbCloseSound.setChecked(true);//�Զ�����
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
                	sShow="�Ѵ��ܿ���";
                }
                else {
                	sShow="�ѹر��ܿ���";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swPerspection:
                if(compoundButton.isChecked()){
                	sShow="��͸�ӵ�ͼ����";
                }
                else {
                	sShow="�ѹر�͸�ӵ�ͼ����";
                }
                
                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swCoolTime:
                if(compoundButton.isChecked()){
                	sShow="�Ѵ����̼�����ȴʱ�书�ܣ�";
                }
                else {
                	sShow="�ѹر����̼�����ȴʱ�书��";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swWinChance:
                if(compoundButton.isChecked()){
                	sShow="�Ѵ����ʤ���ʹ���";
                }
                else {
                	sShow="�ѹر����ʤ���ʹ���";
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
    //����������⣺
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
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"����ʽ�棩");
        }else{
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"�����ð棩");
        }
    }
   /**��ʾ�汾��Ϣ*/
   public void showVerInfo(boolean bReg){
   	ConfigCt.bReg=bReg;
	if(Ad2.currentQQ!=null)Ad2.currentQQ.getADinterval();
	if(Ad2.currentWX!=null)Ad2.currentWX.getADinterval();
       if(bReg){
       	Config.bReg=true;
       	getConfig().setREG(true);
       	tvRegState.setText("��Ȩ״̬������Ȩ");
       	tvRegWarm.setText("�������������ۺ���ϵ"+ConfigCt.contact);
       	etRegCode.setVisibility(View.INVISIBLE);
       	tvPlease.setVisibility(View.INVISIBLE);
       	btReg.setVisibility(View.INVISIBLE);
       	speaker.speak("��ӭʹ��"+ConfigCt.AppName+"�����������û���" );
       	
       }else{
       	Config.bReg=false;
       	getConfig().setREG(false);
       	tvRegState.setText("��Ȩ״̬��δ��Ȩ");
       	tvRegWarm.setText(ConfigCt.warning+"��������Ȩ��ϵ"+ConfigCt.contact);
       	etRegCode.setVisibility(View.VISIBLE);
       	tvPlease.setVisibility(View.VISIBLE);
       	btReg.setVisibility(View.VISIBLE);
       	speaker.speak("��ӭʹ��"+ConfigCt.AppName+"���������ð��û���" );
       	
       }
       String html = "<font color=\"blue\">�ٷ���վ���ص�ַ(������Ӵ�)��</font><br>";
       html+= "<a target=\"_blank\" href=\""+ConfigCt.homepage+"\"><font color=\"#FF0000\"><big><b>"+ConfigCt.homepage+"</b></big></font></a>";
       //html+= "<a target=\"_blank\" href=\"http://119.23.68.205/android/android.htm\"><font color=\"#0000FF\"><big><i>http://119.23.68.205/android/android.htm</i></big></font></a>";
       tvHomePage.setTextColor(Color.BLUE);
       tvHomePage.setBackgroundColor(Color.WHITE);//
       //tvHomePage.setTextSize(20);
       tvHomePage.setText(Html.fromHtml(html));
       tvHomePage.setMovementMethod(LinkMovementMethod.getInstance());
       setMyTitle();
       updateMeWarning(ConfigCt.version,ConfigCt.new_version);//�����������
   }
   /**  �����������*/
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
   /** ��Ϊ���ð�*/
   public void setAppToTestVersion() {
   	String sStartTestTime=getConfig().getStartTestTime();//ȡ�Զ���Ϊ���ð�Ŀ�ʼʱ�䣻
   	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);//yyyy-MM-dd_HH-mm-ss
   	String currentDate =sdf.format(new Date());//ȡ��ǰʱ�䣻
   	int timeInterval=getConfig().getDateInterval(sStartTestTime,currentDate);//�õ�ʱ������
   	if(timeInterval>Config.TestTimeInterval){//7�����Ϊ���ð棺
   		showVerInfo(false);
   	}
   }
   private   void   showUpdateDialog(){ 
       /* @setIcon ���öԻ���ͼ�� 
        * @setTitle ���öԻ������ 
        * @setMessage ���öԻ�����Ϣ��ʾ 
        * setXXX��������Dialog������˿�����ʽ�������� 
        */ 
       final AlertDialog.Builder normalDialog=new  AlertDialog.Builder(MainActivity.this); 
       normalDialog.setIcon(R.drawable.ic_launcher); 
       normalDialog.setTitle(  "��������"  );
       normalDialog.setMessage("���°�������Ƿ�����������"); 
       normalDialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
           @Override 
           public void onClick(DialogInterface dialog,int which){ 
               //...To-do
    		   Uri uri = Uri.parse(Config.download);    
    		   Intent it = new Intent(Intent.ACTION_VIEW, uri);    
    		   startActivity(it);  
           }
       }); 
       normalDialog.setNegativeButton("�ر�",new DialogInterface.OnClickListener(){ 
           @Override 
           public void onClick(DialogInterface dialog,   int   which){ 
           //...To-do 
           } 
       }); 
       // ��ʾ 
       normalDialog.show(); 
       
   } 
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
	    //startAllServices();
		Log.i(Config.TAG, "aa onNewIntent: ����");  
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
    * ��ȡ��ǰӦ�ó���İ���
    * @param context �����Ķ���
    * @return ���ذ���
    */
   public static String getAppProcessName(Context context) {
       //��ǰӦ��pid
       int pid = android.os.Process.myPid();
       //���������
       ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       //��������Ӧ��
       List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
       for (ActivityManager.RunningAppProcessInfo info : infos) {
           if (info.pid == pid)//�õ���ǰӦ��
        	   Log.i("byc002", info.processName);
               return info.processName;//���ذ���
        	   
       }
       return "";
   }

  
 
}
