package com.unidy.backend.pubnub;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PubnubService {

    @Autowired
    private PubNub pubnub;

    public ResponseEntity<?> sendNotification(String channel, Object message) {
        try {
            PNPublishResult result = pubnub.publish()
                    .channel(channel)
                    .message(message)
                    .sync();

            System.out.println("Publish Result: " + result);
            return ResponseEntity.ok().body(new SuccessReponse("Send notification success"));
        } catch (PubNubException e) {
            return  ResponseEntity.badRequest().body(new ErrorResponseDto("Send notification fail"));
        }
    }
}
