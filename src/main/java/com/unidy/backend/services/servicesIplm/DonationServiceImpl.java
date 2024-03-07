package com.unidy.backend.services.servicesIplm;

import com.google.gson.Gson;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.requests.MomoRequest;
import com.unidy.backend.domains.dto.requests.MomoWebHookRequest;
import com.unidy.backend.domains.dto.responses.MomoResponse;
import com.unidy.backend.domains.entity.Sponsor;
import com.unidy.backend.domains.entity.SponsorTransaction;
import com.unidy.backend.domains.entity.Transaction;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.repositories.SponsorRepository;
import com.unidy.backend.repositories.SponsorTransactionRepository;
import com.unidy.backend.repositories.TransactionRepository;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.services.servicesInterface.DonationService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Date;
import java.util.Formatter;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final Environment environment;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SponsorRepository sponsorRepository;
    private final SponsorTransactionRepository sponsorTransactionRepository;
    public ResponseEntity<?> executeTransaction (Principal connectedUser, Long totalAmount, int organizationUserId, int campaignId) throws NoSuchAlgorithmException, InvalidKeyException {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        String jsonData = "{\"campaignId\":" + campaignId + ",\"organizationUserId\":" + organizationUserId + "}";

        String base64Data = encodeBase64(jsonData);


        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String partnerCode = environment.getProperty("PARTNER_CODE");
        String accessKey = environment.getProperty("ACCESS_KEY");
        String secretKey = environment.getProperty("SECRET_KEY");
        String ipnURL = environment.getProperty("IPN_URL");
        String redirectURL = environment.getProperty("REDIRECT_URL");
        String url = "https://test-payment.momo.vn:443/v2/gateway/api/create";
        String extraData = base64Data;
        String orderId = partnerCode +  outputFormat.format(new Date()) + "-" + user.getUserId() ;
        String orderInfo = user.getFullName() + " donation";
        String requestId = partnerCode + outputFormat.format(new Date());


        try {
            assert secretKey != null;
            String signature = generateMomoCreateTransactionSignature(accessKey,totalAmount,extraData,ipnURL,orderId,orderInfo,partnerCode,redirectURL,requestId,"captureWallet",secretKey);

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
            MomoResponse momoResponse = new Gson().fromJson(responseData, MomoResponse.class);
            return ResponseEntity.ok().body(momoResponse);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @Transactional
    public void handleTransaction(MomoWebHookRequest momoResponse) {
        try {
            Gson gson = new Gson();
            MyDataObject dataObject = gson.fromJson(decodeBase64(momoResponse.getExtraData().trim()), MyDataObject.class);
            if (momoResponse.getResultCode().equals(0)){
                Transaction transaction = Transaction.builder()
                        .transactionCode(momoResponse.getRequestId())
                        .transactionTime(new Date(momoResponse.getResponseTime()))
                        .transactionType(momoResponse.getOrderType())
                        .transactionAmount(momoResponse.getAmount())
                        .signature(momoResponse.getSignature())
                        .organizationUserId(dataObject.getOrganizationId())
                        .campaignId(dataObject.campaignId)
                        .build();

                transactionRepository.save(transaction);
                String orderId = momoResponse.getOrderId();
                int indexOfDash = orderId.indexOf("-");

                String userIdString = orderId.substring(indexOfDash + 1);
                int userId = Integer.parseInt(userIdString);

                System.out.println("userId: " + userId);
                User user = userRepository.findByUserId(userId);
                if (!user.getRole().equals(Role.SPONSOR)){
                    user.setRole(Role.SPONSOR);
                    userRepository.save(user);
                    Sponsor sponsor = Sponsor.builder()
                            .sponsorName(user.getFullName())
                            .userId(user.getUserId())
                            .email(user.getEmail())
                            .build();
                    sponsorRepository.save(sponsor);
                }

                Optional<Sponsor> sponsor = sponsorRepository.findByUserId(user.getUserId());
                SponsorTransaction newTransaction = SponsorTransaction.builder()
                        .transactionId(transaction.getTransactionId())
                        .sponsorId(sponsor.get().getSponsorId())
                        .build();
                sponsorTransactionRepository.save(newTransaction);
                System.out.println("Log Transaction Successful");
            }
            else {
                System.out.println("Transaction fail");
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public static String generateMomoCreateTransactionSignature(String accessKey, Long amount, String extraData, String ipnUrl,
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

    private static String byteArrayToHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static String encodeBase64(String jsonData) {
        byte[] encodedBytes = Base64.getEncoder().encode(jsonData.getBytes());
        return new String(encodedBytes);
    }

    private static String decodeBase64(String base64Data) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data.getBytes());
        return new String(decodedBytes);
    }

    @Getter
    static class MyDataObject {
        private int organizationId;
        private int campaignId;
    }
}
