package com.unidy.backend.controllers;

import com.unidy.backend.services.servicesInterface.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor

//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/runOrStopJob")
    public ResponseEntity<?> runOrStopScheduleJob(@RequestParam("jobId") int jobId){
        return adminService.runOrStopJob(jobId);
    }

    @PatchMapping("/posts/{postId}/block/{block}")
    public ResponseEntity<?> blockOrUnblockPost(@PathVariable int postId,@PathVariable String block){
        return adminService.blockOrUnblockPost(postId,block);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId){
        return adminService.deletePost(postId);
    }

    @GetMapping("/posts/date")
    public ResponseEntity<?> getPostByDate(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date toDate,
            @RequestParam("pageNumber") int skip,
            @RequestParam("pageSize") int limit
    ) {
        return adminService.getPostByDate(fromDate, toDate, skip, limit);
    }
}
