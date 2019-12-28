/**
 * 
 */
package order.file;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import util.StringUtils;
/**
 * @author byc
 *
 */

public class FileUtils {
    /******************** �洢��س��� ********************/
    /**
     * Byte��Byte�ı���
     */
    public static final int BYTE = 1;
    /**
     * KB��Byte�ı���
     */
    public static final int KB = 1024;
    /**
     * MB��Byte�ı���
     */
    public static final int MB = 1048576;
    /**
     * GB��Byte�ı���
     */
    public static final int GB = 1073741824;
	/**
	 * �ж�SD���Ƿ񱻹���
	 * 
	 * @return
	 */
	public static boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	/**
	 * ��ȡ��Ŀ¼
	 * 
	 * @return
	 */
	public static String getRootDir() {
		return "/";
	}
	/**
	 * ��ȡSD���ĸ�Ŀ¼
	 * 
	 * @return
	 */
	public static String getSDCardBaseDir() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
	/**
	 * ��ȡ���ĸ�Ŀ¼
	 * 
	 * @return
	 */
	public static String getPhotoDir() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
	}
	/**
	 * ��ȡsd���Ĵ�С
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSDAllSizeKB() {
		if (isSDCardMounted()) {
			// get path of sdcard
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// get single block size(Byte)
			long blockSize = sf.getBlockSize();
			// ��ȡ�������ݿ���
			long allBlocks = sf.getBlockCount();
			// ����SD����С
			return (allBlocks * blockSize) / 1024; // KB
		}
		return 0;
	}

	/**
	 * ��ȡsd�����ô�С
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSDAvalibleSizeKB() {
		if (isSDCardMounted()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();
			long avaliableSize = sf.getAvailableBlocks();
			return (avaliableSize * blockSize) / 1024;// KB
		}
		return 0;
	}

	/**
	 * ��ȡ�ļ����ļ��еĴ�С
	 * 
	 * @param path
	 *            �ļ����ļ��е�·��
	 * @return
	 */
	public static long getFileSize(String path) {
		return getFileSize(new File(path));
	}

	/**
	 * ��ȡ�ļ����ļ��еĴ�С
	 * 
	 * @param file
	 *            �ļ����ļ���
	 * @return
	 */
	public static long getFileSize(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				long size = 0;
				for (File subFile : file.listFiles()) {
					size += getFileSize(subFile);
				}
				return size;
			} else {
				return file.length();
			}
		} else {
			throw new IllegalArgumentException("File does not exist!");
		}
	}

	/**
	 * ɾ���ļ����ļ���
	 * 
	 * @param path
	 *            �ļ����ļ��е�·��
	 */
	public static void deleteFile(String path) {
		deleteFile(new File(path));
	}

	/**
	 * ɾ���ļ����ļ���
	 * 
	 * @param file
	 *            �ļ����ļ���
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			Log.d("The file to be deleted does not exist! File's path is: ",
					file.getPath());
		} else {
			deleteFileRecursively(file);
		}
	}

	/**
	 * ɾ���ļ����ļ���
	 * 
	 * @param file
	 *            �ļ����ļ���
	 */
	private static void deleteFileRecursively(File file) {
		if (file.isDirectory()) {
			for (String fileName : file.list()) {
				File item = new File(file, fileName);
				if (item.isDirectory()) {
					deleteFileRecursively(item);
				} else {
					if (!item.delete()) {
						Log.d("Failed in recursively deleting a file, file's path is: ",
								item.getPath());
					}
				}
			}
			if (!file.delete()) {
				Log.d("Failed in recursively deleting a directory, directories' path is: ",
						file.getPath());
			}
		} else {
			if (!file.delete()) {
				Log.d("Failed in deleting this file, its path is: ",
						file.getPath());
			}
		}
	}
	//------------------------------------------------------------------------------------------------
    /**
     * �����ļ�·����ȡ�ļ�
     *
     * @param filePath �ļ�·��
     * @return �ļ�
     */
    public static File getFileByPath(String filePath) {
    	try{
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    	}catch(NullPointerException e){
    		e.printStackTrace();
    		return null;
    	}
    }
    /**
     * �ж��ļ��Ƿ����
     *
     * @param filePath �ļ�·��
     * @return {@code true}: ����<br>{@code false}: ������
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * �ж��ļ��Ƿ����
     *
     * @param file �ļ�
     * @return {@code true}: ����<br>{@code false}: ������
     */
    public static boolean isFileExists(File file) {
    	try{
    		return file != null && file.exists();
    	}catch(SecurityException e){
    		e.printStackTrace();
    		return false;
    	}
    }
    /**
     * byte��λת������λ��unit��
     *
     * @param size ��С
     * @param unit <ul>
     *             <li>{@link ConstUtils#BYTE}: �ֽ�</li>
     *             <li>{@link ConstUtils#KB}  : ǧ�ֽ�</li>
     *             <li>{@link ConstUtils#MB}  : ��</li>
     *             <li>{@link ConstUtils#GB}  : GB</li>
     *             </ul>
     * @return ��С��unitΪ��λ
     */
    public static double byte2Unit(long size, int unit) {
        switch (unit) {
            case BYTE:
            case KB:
            case MB:
            case GB:
                return (double) size / unit;
        }
        return -1;
    }

    /**
     * ��ȡ�ļ���С
     * <p>���磺getFileSize(filePath, ConstUtils.MB); �����ļ���С��λΪMB</p>
     *
     * @param filePath �ļ�·��
     * @param unit     <ul>
     *                 <li>{@link ConstUtils#BYTE}: �ֽ�</li>
     *                 <li>{@link ConstUtils#KB}  : ǧ�ֽ�</li>
     *                 <li>{@link ConstUtils#MB}  : ��</li>
     *                 <li>{@link ConstUtils#GB}  : GB</li>
     *                 </ul>
     * @return �ļ���С��unitΪ��λ
     */
    public static double getFileSize(String filePath, int unit) {
        return getFileSize(getFileByPath(filePath), unit);
    }

    /**
     * ��ȡ�ļ���С
     * <p>���磺getFileSize(file, ConstUtils.MB); �����ļ���С��λΪMB</p>
     *
     * @param file �ļ�
     * @param unit <ul>
     *             <li>{@link ConstUtils#BYTE}: �ֽ�</li>
     *             <li>{@link ConstUtils#KB}  : ǧ�ֽ�</li>
     *             <li>{@link ConstUtils#MB}  : ��</li>
     *             <li>{@link ConstUtils#GB}  : GB</li>
     *             </ul>
     * @return �ļ���С��unitΪ��λ
     */
    public static double getFileSize(File file, int unit) {
        if (!isFileExists(file)) return -1;
        try{
        	return byte2Unit(file.length(), unit);
        }catch(SecurityException e){
        	e.printStackTrace();
        	return -1;
        }
    }
    /**
     * ��ȡ�ļ���
     * <p>���磺getRandomFile(file); ����RandomAccessFile�ļ���</p>
     *
     * @param file �ļ�
     * @return RandomAccessFile�ļ���
     */
    public static RandomAccessFile getRandomFile(File file,String mode) {
    	try{
    		RandomAccessFile fileOutStream = new RandomAccessFile(file, mode); 
    		return fileOutStream;
    	}catch(IllegalArgumentException e){
    		e.printStackTrace();
    		return null;
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    		return null;
    	}catch(SecurityException e){
    		e.printStackTrace();
    		return null;
    	}
    }
    /**
     * �ж��ļ��Ƿ����
     *
     * @param filePath �ļ�·��
     * @return {@code true}: ����<br>{@code false}: ������
     */
    public static boolean fileExists(String filename) {
    	try{
    		File file=new File(filename);
    		boolean b=file.exists();
    		return b;
    	}catch(NullPointerException e){
    		e.printStackTrace();
    		return false;
    	}catch(SecurityException e){
    		e.printStackTrace();
    		return false;
    	}
    }
}
