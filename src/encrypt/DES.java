package encrypt;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
 
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.content.Context;


/**
 * @author Administrator
 *
 */
public class DES {
	private final byte[] DESIV = new byte[] { 0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };// ����
	private final static String CHARSET = "utf-8";
	private AlgorithmParameterSpec iv = null;// �����㷨�Ĳ����ӿ�
	private Key key = null;
	private String charset =CHARSET;
	
	private static DES current;
	/**
	 * ��ʼ��
	 * @param deSkey	��Կ
	 * @throws Exception
	 */
	public DES(String deSkey, String charset) throws Exception {
		this.charset = charset;
		DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// ������Կ����
		iv = new IvParameterSpec(DESIV);// ��������
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// �����Կ����
		key = keyFactory.generateSecret(keySpec);// �õ���Կ����
	}
	public DES(String deSkey) throws Exception {
		DESKeySpec keySpec = new DESKeySpec(deSkey.getBytes(this.charset));// ������Կ����
		iv = new IvParameterSpec(DESIV);// ��������
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// �����Կ����
		key = keyFactory.generateSecret(keySpec);// �õ���Կ����
	}
	public static synchronized DES getDes(String deSkey) {
        if(current == null) {
            current = getDes(deSkey,CHARSET);
        }
        return current;
	}
	public static synchronized DES getDes(String deSkey, String charset) {
        if(current == null) {
        	try {
        		current = new DES(deSkey,charset);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        return current;
	}
	/**
	 * ����
	 * @author ershuai
	 * @date 2017��4��19�� ����9:40:53
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String encode(String data) throws Exception {
		Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// �õ����ܶ���Cipher
		enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// ���ù���ģʽΪ����ģʽ��������Կ������
		byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
		return Base64.Encode(pasByte);
	}
	
	/**
	 * ����
	 * @author ershuai
	 * @date 2017��4��19�� ����9:41:01
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String decode(String data) throws Exception {
		Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		deCipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] pasByte = deCipher.doFinal(Base64.Decode(data));
		return new String(pasByte, "UTF-8");
	}
	

}