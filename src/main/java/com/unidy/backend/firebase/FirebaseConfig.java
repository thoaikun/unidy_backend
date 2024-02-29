package com.unidy.backend.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.netty.util.internal.ResourcesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Configuration
public class FirebaseConfig {
    @Value("classpath:unidy_firebase_key.json")
    Resource resourceFile;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(resourceFile.getFile());
        FirebaseOptions.Builder firebaseOptionBuilder = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount));
        FirebaseOptions options = firebaseOptionBuilder.build();
        return FirebaseApp.initializeApp(options);
    }
}
