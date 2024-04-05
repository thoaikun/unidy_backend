package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.responses.PostResponse;
import com.unidy.backend.domains.entity.ScheduleJobs;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.repositories.Neo4j_PostRepository;
import com.unidy.backend.repositories.ScheduleJobsRepository;
import com.unidy.backend.services.servicesInterface.AdminService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ScheduleJobsRepository scheduleJobsRepository;
    private final Scheduler scheduler;
    private final Neo4j_PostRepository neo4jPostRepository;
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

    @Override
    public ResponseEntity<?> blockOrUnblockPost(int postId, String status) {
        try {
            List<PostNode> postNode =  neo4jPostRepository.findPostNodeByPostId(String.valueOf(postId));
            PostNode post = postNode.get(0);
            post.setIsBlock(Objects.equals(status, "1"));
            return ResponseEntity.ok().body(new SuccessReponse("Success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> deletePost(String postId) {
        try {
            neo4jPostRepository.deletePostByPostId(postId);
            return ResponseEntity.ok().body(new SuccessReponse("Delete success"));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }

    @Override
    public ResponseEntity<?> getPostByDate(Date fromDate, Date toDate, int skip, int limit) {
        try {
            List<PostResponse> posts = neo4jPostRepository.findPostNodeByDate(fromDate,toDate,skip,limit);
            return ResponseEntity.ok().body(posts);
        } catch (Exception e){
            return  ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }
}
