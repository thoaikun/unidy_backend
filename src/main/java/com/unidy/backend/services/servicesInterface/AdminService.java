package com.unidy.backend.services.servicesInterface;

import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface AdminService {
    ResponseEntity<?> runOrStopJob(int jobId);

    ResponseEntity<?> blockOrUnblockPost(int postId, String status);

    ResponseEntity<?> deletePost(String postId);

    ResponseEntity<?> getPostByDate(Date fromDate, Date toDate, int pageNumber, int pageSize);
}
