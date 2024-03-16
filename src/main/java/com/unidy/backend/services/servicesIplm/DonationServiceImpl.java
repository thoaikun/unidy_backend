package com.unidy.backend.services.servicesIplm;

import com.google.gson.Gson;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.requests.MomoRequest;
import com.unidy.backend.domains.dto.requests.MomoWebHookRequest;
import com.unidy.backend.domains.dto.responses.MomoResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.repositories.*;
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
import java.util.*;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final Environment environment;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final Neo4j_CampaignRepository neo4jCampaignRepository;
    private final OrganizationRepository organizationRepository;

    public ResponseEntity<?> executeTransaction (Principal connectedUser, Long totalAmount, int organizationUserId, int campaignId) throws NoSuchAlgorithmException, InvalidKeyException {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        String jsonData = "{\"campaignId\":" + campaignId + ",\"organizationUserId\":" + organizationUserId + "}";
        String base64Data = encodeBase64(jsonData);

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String partnerCode = environment.getProperty("PARTNER_CODE");
        String accessKey = environment.getProperty("ACCESS_KEY");
        String secretKey = environment.getProperty("SECRET_KEY");
        String ipnURL = environment.getProperty("IPN_URL");
        String url = "https://test-payment.momo.vn:443/v2/gateway/api/create";
        String extraData = base64Data;
        String orderId = partnerCode +  outputFormat.format(new Date()) + "-" + user.getUserId();
        String orderInfo = user.getFullName() + " donation";
        String requestId = partnerCode + outputFormat.format(new Date());
        String redirectURL = getRedirectURL(orderId);

        try {
            Campaign campaign = campaignRepository.findCampaignByCampaignId(campaignId);
            if (campaign == null){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("campaign id not found"));
            }

            Optional<Organization> organization = organizationRepository.findByUserId(organizationUserId);
            if (organization.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponseDto("organization user id not found"));
            }
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
                String orderId = momoResponse.getOrderId();
                int indexOfDash = orderId.indexOf("-");

                String userIdString = orderId.substring(indexOfDash + 1);
                int userId = Integer.parseInt(userIdString);
                Transaction transaction = Transaction.builder()
                        .transactionCode(momoResponse.getRequestId())
                        .transactionTime(new Date(momoResponse.getResponseTime()))
                        .transactionType(momoResponse.getOrderType())
                        .transactionAmount(momoResponse.getAmount())
                        .signature(momoResponse.getSignature())
                        .organizationUserId(dataObject.getOrganizationUserId())
                        .campaignId(dataObject.campaignId)
                        .userId(userId)
                        .build();

                transactionRepository.save(transaction);
                Campaign campaign = campaignRepository.findCampaignByCampaignId(dataObject.getCampaignId());
                campaign.setDonationBudgetReceived((int) (campaign.getDonationBudgetReceived() + momoResponse.getAmount()));
                campaignRepository.save(campaign);

                CampaignNode campaignNode = neo4jCampaignRepository.findCampaignNodeByCampaignId(String.valueOf(dataObject.getCampaignId()));
                campaignNode.setDonationBudgetReceived((int) (campaignNode.getDonationBudgetReceived() + momoResponse.getAmount()));
                neo4jCampaignRepository.save(campaignNode);

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

    private String getRedirectURL(String orderId) {
        String baseUrl = environment.getProperty("FIREBASE_DYNAMIC_LINK_API");
        String apiKey = environment.getProperty("FIREBASE_API_KEY");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = baseUrl + "?key=" + apiKey;

        // Create dynamicLinkInfo map
        Map<String, Object> dynamicLinkInfo = new HashMap<>();
        dynamicLinkInfo.put("domainUriPrefix", "https://unidyteam.page.link");
        dynamicLinkInfo.put("link", "https://unidy-frontend-dev.vercel.app/donation?orderId" + orderId);
        Map<String, String> androidInfo = new HashMap<>();
        androidInfo.put("androidPackageName", "com.example.unidy_mobile");
        dynamicLinkInfo.put("androidInfo", androidInfo);
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("dynamicLinkInfo", dynamicLinkInfo);
        String requestBody = new Gson().toJson(requestBodyMap);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseData = response.getBody();
        Map<String, Object> responseMap = new Gson().fromJson(responseData, Map.class);

        return (String) responseMap.get("shortLink");
    }

    @Getter
    static class MyDataObject {
        private Integer organizationUserId;
        private Integer campaignId;
    }
}
