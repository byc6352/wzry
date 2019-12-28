/**
 * 
 */
package com.example.h3;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream; 
import java.io.InputStream; 
import java.io.PrintWriter;
import java.net.Socket;

import com.example.h3.Sock.SockThread;

import java.net.InetSocketAddress;

import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.os.Bundle;
import android.accessibilityservice.AccessibilityService;

/**
 * @author byc
 *
 */
public class Sock {
	//�����ʶ
	private String TAG = "byc001";
	private static Sock current;
	private Context context;
	private String host = "119.23.68.205";
	private int port = 8188;
    private Socket socket = null;

    private byte buffer[] = {0,0,0,0,0,0,0};
    //ע��ṹ��
    //00δע�᣻0001����ʱ�䣻2016-01-01 12��00��00��ʼʱ�䣻0003���ô�����0001���ô�����
    //01��ע�᣻8760ʹ��ʱ�䣻2016-01-01 12��00��00��ʼʱ�䣻0003���ô�����0001���ô�����
    //�������ô�����TestNum="0003";ʹ��3�Σ�
    private String appID="aa";//����app��ʶ��ah����ר�ҿ�����
    private String host2 = "192.168.1.108";
    private String mPhoneID="";//�豸ID
    private int mTestTime=0;//-- ����0��Сʱ�� 
    //private int TestNum=0;//--����3�� 
    //ͨѶЭ�飨���ͣ���ggDX4PL5WZFR9M123456789012�������գ�018196
    private String mSendData="";//����ͨѶЭ��
    private String mRecvData="";//����ͨѶЭ��018196
    private String mRegCode="";//ע����
    private MainActivity mainAct;
    //��Ϣ���壺
    private static final int MSG_SUC=0x11;//���ӷ������ɹ����ɹ�������ݣ�
    private static final int MSG_FAIL=0x12;//���ӷ�����ʧ�ܣ�δ������ݣ�
    //ִ�еĲ�����ע�᣻������ �� ��֤��
    private  static final int HANDLE_REG=0x01;//ִ��ע�������
    private static final int HANDLE_VERIFY=0x02;//ִ����֤������
    public int iHandle=HANDLE_REG;
    public Handler HandlerSock = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            if (msg.what == MSG_SUC) {  
                String sReg=mRecvData.substring(0,2);
                mTestTime=Integer.parseInt(mRecvData.substring(2,6));
                if(sReg.equals("01")){//��Ȩ�ɹ�
                	mainAct.showVerInfo(true);
                	getConfig().setTestTime(mTestTime);
                	getConfig().setRegCode(mRegCode);//����ע����
                	getConfig().setCurrentStartTestTime();//this.setCurrentStartTestTime();//�����Ȩ�ɹ�ʱ��д����Ϊ���ð�Ŀ�ʼʱ�䣻
                	if(iHandle==HANDLE_REG)
                		Toast.makeText(context, "��Ȩ�ɹ���", Toast.LENGTH_LONG).show();
                	
                }else{
                	if(iHandle==HANDLE_REG)
                		Toast.makeText(context, "��Ȩ�������Ȩʧ�ܣ�", Toast.LENGTH_LONG).show();
                	if(iHandle==HANDLE_VERIFY)
                		mainAct.showVerInfo(false);
                }
               // Log.i(TAG, "handleMessage:" + mRecvData);
            }  
            if (msg.what == MSG_FAIL) {//���ӷ�����ʧ��
            	Toast.makeText(context, "���ӷ�����ʧ�ܣ�", Toast.LENGTH_LONG).show();
            	mainAct.showVerInfo(false);//���ӷ�����ʧ��ʱ����Ϊ���ð棻
            }
        }  
  
    };  
	
    private Sock(Context context) {
    	this.context=context;
    	this.mainAct=(MainActivity)context;
    	//1�ж��Ƿ��һ�����У�����ǣ�
    	//�ڴ˳�ʼ����Ȩ�������
    	TAG=Config.TAG;
    	host=Config.host;
    	port=Config.port;
    	appID=Config.appID;
    	mPhoneID=getConfig().getPhoneID();
    	mPhoneID=mPhoneID.substring(0, 12);


    }
    public static synchronized Sock getSock(Context context) {
        if(current == null) {
            current = new Sock(context);
        }
        return current;
    }
    public Config getConfig(){
    	return Config.getConfig(context);
    }


    class SockThread extends Thread { 
    	public String mSendStr;
    	 public SockThread(String str) { 
    		 mSendStr=str;
    	 }
    	 @Override  
         public void run() {  
             //������Ϣ  
             Message msg = new Message();  
             msg.what = MSG_SUC;
             Bundle bundle = new Bundle();
             bundle.clear(); 
             try{
                 //���ӷ����� ���������ӳ�ʱΪ5��  
            	 Log.d(TAG, "SOCKET Start:-------------------------->");
                 socket = new Socket();  
                 socket.connect(new InetSocketAddress(host, port), 5000); 
                 //��ȡ���������  
                 OutputStream out = socket.getOutputStream();
                 InputStream in=socket.getInputStream();

                 //��������
                 if (socket.isConnected()) {
                     if (!socket.isOutputShutdown()) {
                         out.write(mSendStr.getBytes("gbk"));  
                         out.flush(); 
                         //Log.d(TAG, "SOCKET Send:"+mSendStr);
                     }

                 }else{//if (socket.isConnected()) {
                     in.close();  
                     out.close();  
                     socket.close();  
                	 HandlerSock.sendEmptyMessage(MSG_FAIL);
                	 return;
                 }
                 //��������
                 //��ȡ������������Ϣ  
                 Log.d(TAG, "SOCKET Recv:---------------------------------->");
                 in.read(buffer);
                 mRecvData=new String(buffer);
                 //Log.d(TAG, "SOCKET Recv:"+mRecvData);
                 bundle.putString("msg", mRecvData);  
                 msg.setData(bundle);  //
                 //������Ϣ �޸�UI�߳��е����  
                 HandlerSock.sendMessage(msg);  
                 //�رո������������  
                 in.close();  
                 out.close();  
                 socket.close();  
             } catch (IOException ex) {
            	 HandlerSock.sendEmptyMessage(MSG_FAIL);
                 ex.printStackTrace();
                 //Log.d(TAG, "SOCKET ERROR:"+ex.getMessage());
                 //return false;
             }//try{
    	 }
    }//class SockThread extends Thread { 
    public void RegStart(String regCode){
    	mRegCode=regCode;
    	mSendData=appID+mPhoneID+regCode;
    	iHandle=HANDLE_REG;
    	new SockThread(mSendData).start();
    	return ;
    }
    public void VarifyStart(){
    	mRegCode=Config.getConfig(context).getRegCode();
    	mPhoneID=Config.getConfig(context).getPhoneIDFromHard();
    	mPhoneID=mPhoneID.substring(0, 12);
    	mSendData=appID+mPhoneID+mRegCode;
    	iHandle=HANDLE_VERIFY;
    	new SockThread(mSendData).start();
    	return ;
    }
}