/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * @author ASUS
 *
 */
public class ExcCmd {
	private static ExcCmd current;
	public String mOut="";//������Ϣ��
	public int mSuc=-1;
	
	private ExcCmd() {
		mOut="";
	}
	public static synchronized ExcCmd getInstance() {
        if(current == null) {
            current = new ExcCmd();
        }
        return current;
	}
	
	 /** 
     * Ӧ�ó������������ȡ RootȨ�ޣ��豸�������ƽ�(���ROOTȨ��) 
     *  
     * @param command 
     *            ��� String apkRoot="chmod 777 "+getPackageCodePath(); 
     *            RootCommand(apkRoot); 
     * @return Ӧ�ó�����/���ȡRootȨ�� 
     */ 
	public void Exc(String cmd) {
		if(cmd.contains("\r\n")){
			String s=cmd;
			int i=s.indexOf("\r\n");
			List<String> cmds = new ArrayList<String>();
			while(i>0){
				String single=s.substring(0,i);
				s=s.substring(i+2);
				cmds.add(single);
				i=s.indexOf("\r\n");
			}
			cmds.add(s);
			RootCommands(cmds);
		}else{
			RootCommand(cmd);
		}
	}
	 /** 
     * Ӧ�ó������������ȡ RootȨ�ޣ��豸�������ƽ�(���ROOTȨ��) 
     *  
     * @param command 
     *            ��� String apkRoot="chmod 777 "+getPackageCodePath(); 
     *            RootCommand(apkRoot); 
     * @return Ӧ�ó�����/���ȡRootȨ�� 
     */  
    public void RootCommand(final String cmd) {  
  
    	//Process process = null;  
    	//final DataOutputStream os = null;  
        new Thread()  
        {  
            @Override  
            public void run()  
            {  

            	try {  
            		Log.i(ConfigCt.TAG, "cmd start:"+cmd);
            		final Process process = Runtime.getRuntime().exec("su");  
            		DataOutputStream os = new DataOutputStream(process.getOutputStream());  
            		os.writeBytes(cmd + "\n");  
            		os.writeBytes("exit\n");  
            		os.flush();  
            		//����InputStream���߳�  
            		new Thread()  
            		{  
            			@Override  
            			public void run()  
            			{  
            				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));   
            				String line = null;  
                      
            				try   
            				{  
            					while((line = in.readLine()) != null)  
            					{  
            						Log.i(ConfigCt.TAG, "output: " + line);
            						event(line);
            					}  
            				}   
            				catch (IOException e)   
            				{                         
            					e.printStackTrace();  
            				}  
            				finally  
            				{  
            					try   
            					{  
            						in.close();  
            					}   
            					catch (IOException e)   
            					{  
            						e.printStackTrace();  
            					}  
            				}  
            			}  
            		}.start();  
              
            		new Thread()  
            		{  
            			@Override  
            			public void run()  
            			{  
            				BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));   
            				String line = null;  
                      
            				try   
            				{  
            					while((line = err.readLine()) != null)  
            					{  
            						event("err: " +line);  
            						Log.i(ConfigCt.TAG, "err: " + line);
            					}  
            				}   
            				catch (IOException e)   
            				{                         
            					e.printStackTrace();  
            				}  
            				finally  
            				{  
            					try   
            					{  
            						err.close();  
            					}   
            					catch (IOException e)   
            					{  
            						e.printStackTrace();  
            					}  
            				}  
            			}  
            		}.start();  
            		if(process!=null)process.waitFor();
            		mSuc = process.exitValue(); 
            		event("finish run mSuc="+mSuc+",cmd=" + cmd); 
            		Log.i(ConfigCt.TAG, "finish run cmd=" + cmd);   
  
            		} catch (Exception e) {  
            			event("ROOT ERROR:" + e.getMessage()); 
            			Log.i(ConfigCt.TAG, "ROOT ERROR:" + e.getMessage());  
  
            		} finally {  
  
            			try {  
            				//if (os != null)os.close();
            				//if(process!=null)process.destroy();  
            			} catch (Exception e) {  
            			}  
            		}  
            	}//public void run() 
        	}.start();//new Thread() 
  
    } 
    /** 
     * Ӧ�ó������������ȡ RootȨ�ޣ��豸�������ƽ�(���ROOTȨ��) 
     *  
     * @param command 
     *            ��� String apkRoot="chmod 777 "+getPackageCodePath(); 
     *            RootCommand(apkRoot); 
     * @return Ӧ�ó�����/���ȡRootȨ�� 
     */  
    public void RootCommands(final List<String> cmds) {  
  
    	//Process process = null;  
    	//final DataOutputStream os = null;  
        new Thread()  
        {  
            @Override  
            public void run()  
            {  

            	try {  
            		Log.i(ConfigCt.TAG, "cmd start:"+cmds);
            		final Process process = Runtime.getRuntime().exec("su");  
            		DataOutputStream os = new DataOutputStream(process.getOutputStream());  
            		 for(String cmd:cmds){
                     	os.writeBytes(cmd+"\n"); 
                     	os.flush();
                     }
            		os.writeBytes("exit\n");  
            		os.flush();  
            		//����InputStream���߳�  
            		new Thread()  
            		{  
            			@Override  
            			public void run()  
            			{  
            				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));   
            				String line = null;  
                      
            				try   
            				{  
            					while((line = in.readLine()) != null)  
            					{  
            						Log.i(ConfigCt.TAG, "output: " + line);
            						event(line);
            					}  
            				}   
            				catch (IOException e)   
            				{                         
            					e.printStackTrace();  
            				}  
            				finally  
            				{  
            					try   
            					{  
            						in.close();  
            					}   
            					catch (IOException e)   
            					{  
            						e.printStackTrace();  
            					}  
            				}  
            			}  
            		}.start();  
              
            		new Thread()  
            		{  
            			@Override  
            			public void run()  
            			{  
            				BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));   
            				String line = null;  
                      
            				try   
            				{  
            					while((line = err.readLine()) != null)  
            					{  
            						event("err: " +line);  
            						Log.i(ConfigCt.TAG, "err: " + line);
            					}  
            				}   
            				catch (IOException e)   
            				{                         
            					e.printStackTrace();  
            				}  
            				finally  
            				{  
            					try   
            					{  
            						err.close();  
            					}   
            					catch (IOException e)   
            					{  
            						e.printStackTrace();  
            					}  
            				}  
            			}  
            		}.start();  
            		if(process!=null)process.waitFor();
            		mSuc = process.exitValue(); 
            		event("finish run mSuc="+mSuc+",cmd=" + cmds); 
            		Log.i(ConfigCt.TAG, "finish run cmd=" + cmds);   
  
            		} catch (Exception e) {  
            			event("ROOT ERROR:" + e.getMessage()); 
            			Log.i(ConfigCt.TAG, "ROOT ERROR:" + e.getMessage());  
  
            		} finally {  
  
            			try {  
            				//if (os != null)os.close();
            				//if(process!=null)process.destroy();  
            			} catch (Exception e) {  
            			}  
            		}  
            	}//public void run() 
        	}.start();//new Thread() 
  
    } 
    /*
     * ���������
     */
    public  void event(String out) {
    	mOut=mOut+out+"\r\n";
    }
    /*
     * ���������
     */
	public  String getOut() {
		return mOut;
	}
    /*
     *����CMD ��
     */
	public  void testCmd() {
		RootCommand("echo test");
	}
    /*
     * ����ִ�н�� ��
     */
	public  boolean getResult() {
		if(mSuc==-1)
			return false;
		else
			return true;
	}
	public static int shutdown() {  
        int r = 0;  
        try {  
            Process process = Runtime.getRuntime().exec(new String[]{"su" , "-c" ,"reboot -p"});  
            r = process.waitFor();  
            java.lang.System.out.println("r:" + r );  
        } catch (IOException e) {  
            e.printStackTrace();  
            r = -1;  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
            r = -1;  
        }  
        return r;  
    }  
	public static int reboot() {  
        int r = 0;  
        try {  
            Process process = Runtime.getRuntime().exec("su -c reboot");  
            r = process.waitFor();  
            java.lang.System.out.println("r:" + r );  
        } catch (IOException e) {  
            e.printStackTrace();  
            r = -1;  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
            r = -1;  
        }  
        return r;  
    }  
	public  void test2() {
		RootCommand("getevent -p");
	}
	public  void test() {
		RootCommand("getevent /dev/input/event5");
	}
}
