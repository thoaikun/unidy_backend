package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.requests.MomoConfirmRequest;
import com.unidy.backend.domains.dto.requests.MomoRequest;
import com.unidy.backend.domains.dto.responses.MomoResponse;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.services.servicesInterface.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.glacier.model.StatusCode;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Date;
import java.util.Formatter;
import java.text.SimpleDateFormat;


@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final Environment environment;
    public ResponseEntity<?> executeTransaction (Principal connectedUser, Long totalAmount) throws NoSuchAlgorithmException, InvalidKeyException {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String partnerCode = environment.getProperty("PARTNER_CODE");
        String accessKey = environment.getProperty("ACCESS_KEY");
        String secretKey = environment.getProperty("SECRET_KEY");
        String ipnURL = environment.getProperty("IPN_URL");
        String redirectURL = environment.getProperty("REDIRECT_URL");
        String url = "https://test-payment.momo.vn:443/v2/gateway/api/create";
        String extraData = "";
        String orderId = partnerCode +  outputFormat.format(new Date());
        String orderInfo = user.getUserId().toString() + "Donation";
        String requestId = partnerCode + outputFormat.format(new Date());

        try {
            assert secretKey != null;
            String signature = generateSignature(accessKey,totalAmount,extraData,ipnURL,orderId,orderInfo,partnerCode,redirectURL,requestId,"captureWallet",secretKey);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            MomoRequest momoRequest = MomoRequest.builder()
                    .partnerCode(partnerCode)
                    .accessKey(accessKey)
                    .secretKey(secretKey)
                    .requestId(requestId)
                    .amount(totalAmount)
                    .storeId("Unidy")
                    .orderId(orderId)
                    .orderInfo(orderInfo)
                    .ipnUrl(ipnURL)
                    .redirectUrl(redirectURL)
                    .lang("en")
                    .requestType("captureWallet")
                    .signature(signature)
                    .extraData(extraData)
                    .build() ;
            HttpEntity<MomoRequest> requestEntity = new HttpEntity<>(momoRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            String responseData = response.getBody();
            System.out.println(responseData);
            // Check response
            if (response.getStatusCode() == HttpStatusCode.valueOf(500)){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Can't call api from recommend service"));
            }
            return ResponseEntity.ok().body(responseData);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    public void handleTransaction(MomoResponse momoResponse) throws NoSuchAlgorithmException, InvalidKeyException {
        //confirm transaction
        String url = "https://test-payment.momo.vn:443/v2/gateway/api/create";

        String partnerCode = environment.getProperty("PARTNER_CODE");
        String accessKey = environment.getProperty("ACCESS_KEY");
        String secretKey = environment.getProperty("SECRET_KEY");
        String description = "Ủng hộ tiền thành công";
        assert secretKey != null;
        String signature = generateSignatureConfirm(accessKey, momoResponse.getAmount(),description, momoResponse.getOrderId(),partnerCode, momoResponse.getRequestId(), "capture",secretKey);
        System.out.println(momoResponse.getOrderId().toString() + momoResponse.getAmount().toString()+ momoResponse.getRequestId().toString());
        try {
            if (momoResponse.getResultCode().equals(9000)) {
                System.out.println(momoResponse.getSignature());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                MomoConfirmRequest request = MomoConfirmRequest.builder()
                        .partnerCode(momoResponse.getPartnerCode())
                        .requestId(momoResponse.getRequestId())
                        .requestId(momoResponse.getOrderId())
                        .requestType("capture")
                        .lang("en")
                        .amount(momoResponse.getAmount())
                        .description(description)
                        .signature(signature)
                        .build();
                HttpEntity<MomoConfirmRequest> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                String responseData = response.getBody();
                System.out.println(responseData);
                // Check response
//                if (response.getStatusCode() == HttpStatusCode.valueOf(500)){
//                    System.out.println("Transaction fail");
//                }
                System.out.println("Transaction success");
            }
            else {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                MomoConfirmRequest request = MomoConfirmRequest.builder()
                        .partnerCode(momoResponse.getPartnerCode())
                        .requestId(momoResponse.getRequestId())
                        .requestId(momoResponse.getOrderId())
                        .requestType("cancel")
                        .lang("en")
                        .amount(momoResponse.getAmount())
                        .description("Giao dịch không thành công")
                        .signature(momoResponse.getSignature())
                        .build();
                HttpEntity<MomoConfirmRequest> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
                String responseData = response.getBody();
                System.out.println(responseData);
                // Check response
                if (response.getStatusCode() == HttpStatusCode.valueOf(500)){
                    System.out.println("Transaction fail");
                }
                System.out.println("Transaction cancel");
            }
        } catch (Exception e){
            System.out.println("exception: "+ e.toString());
        }
    }

    public static String generateSignature(String accessKey, Long amount, String extraData, String ipnUrl,
                                           String orderId, String orderInfo, String partnerCode, String redirectUrl,
                                           String requestId, String requestType, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);

        byte[] signatureBytes = sha256Hmac.doFinal(rawSignature.getBytes());

        return byteArrayToHexString(signatureBytes);
    }

    public static String generateSignatureConfirm(String accessKey, Long amount, String description,
                                           String orderId, String partnerCode,
                                           String requestId, String requestType, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&description=" + description +
                "&orderId=" + orderId +
                "&partnerCode=" + partnerCode +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKeySpec);

        byte[] signatureBytes = sha256Hmac.doFinal(rawSignature.getBytes());

        return byteArrayToHexString(signatureBytes);
    }

    private static String byteArrayToHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
