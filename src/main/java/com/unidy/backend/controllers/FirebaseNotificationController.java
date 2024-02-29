package com.unidy.backend.controllers;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.InitFcmRequest;
import com.unidy.backend.firebase.FirebaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/firebaseNotification")
@Tag(name = "Firebase Notification", description = "Firebase Notification Controller")
public class FirebaseNotificationController {
    private final FirebaseService firebaseService;

    @PostMapping("/initFcmToken")
    public ResponseEntity<?> initFcmToken(
            @RequestBody @Valid InitFcmRequest request,
            Principal connectedUser
    ) {
        try {
            firebaseService.saveFcmToken(request.getFcmToken(), connectedUser);
            return ResponseEntity.ok().body(new SuccessReponse("Fcm Token Saved"));
        }
        catch (Exception error) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid Request"));
        }
    }
}
