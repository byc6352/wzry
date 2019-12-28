/**
 * 
 */
package protector;


import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import protector.AliveJobService;

/**JobScheduler�����࣬����ģʽ
 * ִ��ϵͳ����
 *
 * Created by jianddongguo on 2017/7/10.
 * http://blog.csdn.net/andrexpert
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)//5.0�Ժ����  
public class JobSchedulerManager {
	private static final int RUN_INTERVAL=60*1000*10;
    private static final int JOB_ID = 1;
    private static JobSchedulerManager mJobManager;
    private JobScheduler mJobScheduler;
    private Context mContext;

    private JobSchedulerManager(Context ctxt){
        this.mContext = ctxt;
        mJobScheduler = (JobScheduler)ctxt.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    public final static JobSchedulerManager getJobSchedulerInstance(Context ctxt){
        if(mJobManager == null){
            mJobManager = new JobSchedulerManager(ctxt);
        }
        return mJobManager;
    }

    @TargetApi(21)
    public void startJobScheduler(){
        // ���JobService�Ѿ�������API<21������
        if(AliveJobService.isJobServiceAlive() || isBelowLOLLIPOP()){
            return;
        }
        // ����JobInfo���󣬴��ݸ�JobSchedulerService
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,new ComponentName(mContext, AliveJobService.class));
        // ����ÿ2��ִ��һ������
        builder.setPeriodic(RUN_INTERVAL);
        //builder.setPeriodic(1000*60*15);
        // �����豸����ʱ��ִ�и�����
        builder.setPersisted(true);
        // ������������ִ�и�����
        //builder.setRequiresCharging(true);
        JobInfo info = builder.build();
        //��ʼ��ʱִ�и�ϵͳ����
        mJobScheduler.schedule(info);
    }

    @TargetApi(21)
    public void stopJobScheduler(){
        if(isBelowLOLLIPOP())
            return;
        mJobScheduler.cancelAll();
    }

    private boolean isBelowLOLLIPOP(){
        // API< 21
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}

