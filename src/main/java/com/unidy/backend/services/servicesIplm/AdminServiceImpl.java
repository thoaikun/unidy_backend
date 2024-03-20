package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.entity.ScheduleJobs;
import com.unidy.backend.repositories.ScheduleJobsRepository;
import com.unidy.backend.services.servicesInterface.AdminService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ScheduleJobsRepository scheduleJobsRepository;
    private final Scheduler scheduler;
    @Override
    public ResponseEntity<?> runOrStopJob(int jobId) {
        Optional<ScheduleJobs> optionalScheduleJobs = scheduleJobsRepository.findById(jobId);

        if (optionalScheduleJobs.isPresent()) {
            ScheduleJobs scheduleJob = optionalScheduleJobs.get();
            try {
                JobKey jobKey = new JobKey("scheduleJob" + scheduleJob.getId(), "group1");
                if (scheduleJob.getStatus().equals("STOP")) {
                    scheduleJob.setStatus("RUNNING");
                    scheduleJobsRepository.save(scheduleJob);
                    scheduler.resumeJob(jobKey);
                    return ResponseEntity.ok().body(new SuccessReponse("Running job: "+ jobId));
                } else {
                    scheduleJob.setStatus("STOP");
                    scheduleJobsRepository.save(scheduleJob);
                    scheduler.pauseJob(jobKey);
                    return ResponseEntity.ok().body(new SuccessReponse("Stop job: "+ jobId));
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start/pause job.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
