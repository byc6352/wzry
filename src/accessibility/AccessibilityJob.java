/**
 * 
 */
package accessibility;
import android.view.accessibility.AccessibilityEvent;

import notification.IStatusBarNotification;
/**
 * @author byc
 *
 */
public interface AccessibilityJob {
	String[] getTargetPackageName();//��ȡҪ����İ���
	boolean isTargetPackageName(String pkg);//�Ƿ���Ŀ�����
	boolean isEnable();//�Ƿ���ã�
    void onCreateJob(QiangHongBaoService service);//����������
    void onStopJob();//ֹͣ������
    void onReceiveJob(AccessibilityEvent event);//�¼�������
    void EventStart();//��ʼ�¼����� 
    void EventTimeStart();//��ʼ��ʱ�¼�����
    void closeEventWorking();//�ر��¼�����
    void onWorking();//ˢ�´������̣�
    void TimeStart();//��ʼˢ�´���
    void closeTimeWorking();//�ر�ˢ�´���
}
