package com.unidy.backend.pubnub;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubnubConfig {

    @Bean
    public PubNub pubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-88a34604-aa8f-46bb-a378-86a841e04003");
        pnConfiguration.setPublishKey("pub-c-2576ab30-7ab9-422e-b301-4a26295f4f75");
        return new PubNub(pnConfiguration);
    }
}
