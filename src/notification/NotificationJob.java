/**
 * 
 */
package notification;



/**
 * @author ASUS
 *
 */
public interface NotificationJob {
	String[] getTargetPackageName();//��ȡҪ����İ���
	boolean isTargetPackageName(String pkg);//�Ƿ���Ŀ�����
	void setTargetPackageName(String[] pkgs);//����Ҫ����İ���
	boolean isEnable();//�Ƿ���ã�
    void onCreateJob(QHBNotificationService service);//����������
    void onStopJob();//ֹͣ������
    void onReceiveJob(IStatusBarNotification mysbn);//�¼�������
    void EventStart();//��ʼ�¼�����
    void closeEventWorking();//�ر��¼�����
}
