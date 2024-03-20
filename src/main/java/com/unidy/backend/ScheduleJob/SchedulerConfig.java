package com.unidy.backend.ScheduleJob;

import com.unidy.backend.domains.entity.ScheduleJobs;
import com.unidy.backend.repositories.ScheduleJobsRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.quartz.Scheduler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulerConfig implements ApplicationListener<ApplicationReadyEvent> {
        private final ScheduleJobsRepository scheduleJobsRepository;
        private final Scheduler scheduler; // Inject Scheduler vào SchedulerConfig

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            try {
                scheduler.start(); // Khởi động Scheduler một lần duy nhất

                List<ScheduleJobs> jobs = scheduleJobsRepository.findAll();
                for (ScheduleJobs job : jobs){
                    Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(job.getFunctionPath());
                    JobDetail jobDetail = JobBuilder.newJob(jobClass)
                            .withIdentity("scheduleJob" + job.getId().toString(), "group1")
                            .build();
                    Trigger trigger = TriggerBuilder.newTrigger()
                            .withIdentity("trigger1" + job.getId().toString(), "group1")
                            .withSchedule(CronScheduleBuilder.cronSchedule(job.getScheduleTime()))
                            .build();

                    scheduler.scheduleJob(jobDetail, trigger);
                    if (job.getStatus().equals("RUNNING")){
                        scheduler.resumeJob(JobKey.jobKey("scheduleJob" + job.getId().toString(), "group1"));
                    } else {
                        scheduler.pauseJob(JobKey.jobKey("scheduleJob" + job.getId().toString(), "group1"));
                    }
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }