package download;

import java.io.File;


import util.ConfigCt;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import download.ftp.ftpThread;
import it.sauronsoftware.ftp4j.FTPClient;
import util.Funcs;

public class ftp {
	//�����ʶ
	private String TAG = "byc001";
	private static ftp current;
	private Context context;
	private String host = "";
	private int portU = 21;
	private int portD = 21;
	private String userName="";
	private String password="";
    //ftp ��Ϣ����:
    private static final int FTP_MSG_BASE=0;//��½ʧ�ܣ�
    public static final int FTP_LOGIN_FAIL=FTP_MSG_BASE+1;//��½ʧ�ܣ�
    public static final int FTP_LOGIN_SUC=FTP_MSG_BASE+2;//��½�ɹ���
    public static final int FTP_DOWNLOAD_FAIL=FTP_MSG_BASE+3;//����ʧ�ܣ�
    public static final int FTP_DOWNLOAD_SUC=FTP_MSG_BASE+4;//���سɹ���
    public static String mFtpDirPath;//�����ļ�·����
    public static String mFtpDirUploadPath;//�����ļ��ϴ�·����
    private static final String FTP_DIR_NAME = "byc";//�����ļ�������
    private static final String BUNDLE_TAG_WHAT = "what";//bundle��־���¼����
    public static final String BUNDLE_TAG_CURRENT_FILE = "CurrentDownFilename";//bundle��־����ǰ���ص��ļ���
    public static final String BUNDLE_TAG_LOCAL_FILE = "CurrentLocalFilename";//bundle��־����ǰ���صı����ļ���
    private static final String BUNDLE_TAG_IS_DOWNLOAD = "isDownload";//��������
    public static final String ACTION_DOWNLOAD_INFO = "Action_Download_Info";

    public int mResult=FTP_MSG_BASE;//���ؽ��
    private boolean mUpdateDel=true;//�ϴ���ɺ�ɾ��
    
    public ftp(Context context) {  
    	this.context=context;
        this.host =ConfigCt.uIP; 
        this.portU = ConfigCt.uPortU;  
        this.portD = ConfigCt.uPortD;  
        this.userName = ConfigCt.ftpUserName;
        this.password = ConfigCt.ftpPwd; 
        if (mFtpDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mFtpDirPath = sdcardPath + "/" + FTP_DIR_NAME;
            mFtpDirUploadPath=mFtpDirPath+ "/upload";
        }
        makeDir(mFtpDirPath);
        makeDir(mFtpDirUploadPath);
    }  
    public static synchronized ftp getFtp(Context context) {
        if(current == null) {
            current = new ftp(context);
        }
        return current;
    }
    /*��ʼ����*/
    public void DownloadStart(String RemoteFileName){
    	mResult=FTP_MSG_BASE;
    	String LocalFileName=mFtpDirPath+ "/" + RemoteFileName;
    	deletefile(LocalFileName);
    	new ftpThread(RemoteFileName,portD).start();
    }
    /*��ʼ����*/
    public void DownloadStart(String RemoteFileName,String LocalFileName){
    	mResult=FTP_MSG_BASE;
    	deletefile(LocalFileName);
    	new ftpThread(RemoteFileName,true,LocalFileName,portD).start();
    }
    /*��ʼ�ϴ�*/
    public void UploadStart(String RemoteFileName){
    	mResult=FTP_MSG_BASE;
    	mUpdateDel=true;
    	String LocalFileName=mFtpDirUploadPath+ "/" + RemoteFileName;
    	if(!fileExists(LocalFileName))return;
    	ftpThread ftpUpdate=new ftpThread(RemoteFileName,false,portU);
    	ftpUpdate.start();
    }
    /*��ʼ�ϴ�*/
    public void UploadStart(String RemoteFileName,boolean bUpdateDel){
    	mResult=FTP_MSG_BASE;
    	mUpdateDel=bUpdateDel;
    	String LocalFileName=mFtpDirUploadPath+ "/" + RemoteFileName;
    	if(!fileExists(LocalFileName))return;
    	ftpThread ftpUpdate=new ftpThread(RemoteFileName,false,portU);
    	ftpUpdate.start();
    }
//------------------------------------------��Ϣ����-----------------------------------------------
    public Handler handlerFtp = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
			Bundle bundle = msg.getData();
			String CurrentDownFilename=(String)bundle.get(BUNDLE_TAG_CURRENT_FILE );
			String CurrentLocalFilename=(String)bundle.get(BUNDLE_TAG_LOCAL_FILE );
			boolean bDownload=(Boolean)bundle.get(BUNDLE_TAG_IS_DOWNLOAD);
        	switch (msg.what) {
			case FTP_LOGIN_FAIL:
				Log.i(TAG, "FTP_LOGIN_FAIL");
				break;
			case FTP_DOWNLOAD_SUC:
				Log.i(TAG, "FTP_DOWNLOAD_SUC��"+CurrentLocalFilename);
				break;
			case FTP_DOWNLOAD_FAIL:
				Log.i(TAG, "FTP_DOWNLOAD_FAIL");
				
				break;
        	}
        	mResult=msg.what;
        	if(!bDownload&&mUpdateDel)deletefile(CurrentLocalFilename);
			//���͹㲥��������ɣ�
        	if(bDownload){
        		Intent intent = new Intent(ACTION_DOWNLOAD_INFO);
        		intent.putExtra(BUNDLE_TAG_WHAT, msg.what);
        		intent.putExtra(BUNDLE_TAG_CURRENT_FILE,CurrentDownFilename);
        		intent.putExtra(BUNDLE_TAG_LOCAL_FILE,CurrentLocalFilename);
        		context.sendBroadcast(intent);
        	}
        }  
  
    };  
//__________________________________�����ļ�_______________________________________________________
    class ftpThread extends Thread {
    	private FTPClient ftpClient;
    	private String mLocalFileName;//�����ļ�����
    	private String mRemoteFileName;//Զ���ļ�����
    	private boolean bDownload=true;//�����ļ���
    	private int mPort=21;
        public ftpThread(String RemoteFileName,int port){
            mLocalFileName=mFtpDirPath+ "/" + RemoteFileName;
            mRemoteFileName=RemoteFileName;
            mPort=port;
        }
        public ftpThread(String RemoteFileName,boolean bDownload,int port){
        	this.bDownload=bDownload;
        	if(bDownload)
        		mLocalFileName=mFtpDirPath+ "/" + RemoteFileName;
        	else
        		mLocalFileName=mFtpDirUploadPath+ "/" + RemoteFileName;
            mRemoteFileName=RemoteFileName;
            mPort=port;
        }
        public ftpThread(String RemoteFileName,boolean bDownload,String LocalFileName,int port){
        	this.bDownload=bDownload;
        	mLocalFileName=LocalFileName;
            mRemoteFileName=RemoteFileName;
            mPort=port;
        }
		private void sendMSG(int iMsg){
			//if (ftpClient!=null)ftpClient.clo
			mResult=iMsg;
			Message msg = new Message();
			msg.what = iMsg;
			Bundle bundle = new Bundle();
			bundle.clear();
			bundle.putInt(BUNDLE_TAG_WHAT,iMsg);
			bundle.putBoolean(BUNDLE_TAG_IS_DOWNLOAD, bDownload);
			bundle.putString(BUNDLE_TAG_CURRENT_FILE, mRemoteFileName);
			bundle.putString(BUNDLE_TAG_LOCAL_FILE, mLocalFileName);
			msg.setData(bundle);  //
			handlerFtp.sendMessage(msg);
		}
     	 @Override  
       public void run() { 
     		try {
     			ftpClient = new FTPClient(); 
     			String[] welcome=ftpClient.connect(host, mPort);
     			if (welcome != null) {
  				for (String value : welcome) {
  					Log.i(TAG, "connect " + value);
  				}//for (String value : welcome) {
     			}//if (welcome != null) {
     			ftpClient.login(userName, password);
     		} catch (Exception ex) {
     			ex.printStackTrace();
     			//Log.i(TAG, "FTP_LOGIN_FAIL");
     			ftpClient=null;
     			sendMSG(FTP_LOGIN_FAIL);
     			return;
     		}//try
     		try {
     			ftpClient.setType(FTPClient.TYPE_BINARY);
     			if(bDownload)
     				ftpClient.download(mRemoteFileName, new File(mLocalFileName));
     			else
     				ftpClient.upload(new File(mLocalFileName));
     			ftpClient.disconnect(true);
     			//Log.i(TAG, "FTP_DOWNLOAD_SUC");
     			sendMSG(FTP_DOWNLOAD_SUC);

     		} catch (Exception ex) {
				ex.printStackTrace();
     			//Log.i(TAG, "FTP_DOWNLOAD_FAIL");
     			sendMSG(FTP_DOWNLOAD_FAIL);
			}
     		ftpClient=null;
     	 }//public void run() { 
      }// class ftpThread extends Thread {
  //_______________________________________����XML�ļ�_____________________________________________________

 
  //_______________________________________����_____________________________________________________
    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    private void deletefile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
	public boolean fileExists(String filename) {
        File file = new File(filename);
        if(file.exists()){
        	if(file.length()>0)return true;
        }
        return false;
    }
    //---------------------------------------------------------------------------------------------
   }
//
