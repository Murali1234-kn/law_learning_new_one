package com.my.application.demo.a.pplication.Otp_handling_and_cachemanagement;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
@Service
public class PhoneOtpService {
    @Autowired
    public CacheManager cacheManager;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void cachePhoneOtp(String phone, String phoneotp) {
        Cache cache = cacheManager.getCache("phoneOtpCache");
        Element element = new Element(phone, phoneotp);
        System.out.println("cache  phoneotp" + element);
        element.setTimeToLive((int) TimeUnit.MINUTES.toSeconds(5000));
        cache.put(element);
    }
    public boolean validatePhoneOtp(String phone, String phoneotp) {
        String cachedValue = getCachedPhoneOtp(phone);
        System.out.println("cached value phone otp" + cachedValue);
        System.out.println("phone otp" + phoneotp);
        return cachedValue != null && cachedValue.equals(phoneotp);
    }
    public String getCachedPhoneOtp(String phone) {
        Cache cache = cacheManager.getCache("phoneOtpCache");
        Element cachedOtp = cache.get(phone);
        System.out.println("cachedotp in phone" + cachedOtp);
        if (cachedOtp != null) {
            return (String) cachedOtp.getObjectValue();
        }
        return null;
    }
    public boolean sendPhoneOtp(String phone, String phoneotp) {
        try {
            String account_sid = "AC020d6ca5c5f77b47e921a402f1ecda2a";
            String auth_token = "888b58269c06179fd8cc7cf85ccb67db";
            String trial_number = "+17319374329";
            String defaultCountryCode = "+91";

            Twilio.init(account_sid, auth_token);
            String otp = phoneotp;

            String messageBody = "Your OTP code is: " + otp;
            System.out.println("phone otp is " + otp);

            Message message = Message.creator(
                    new PhoneNumber(defaultCountryCode + phone),
                    new PhoneNumber(trial_number),
                    messageBody
            ).create();

            if (message.getSid() != null) {
                // Cache the OTP you generated for the phone
                cachePhoneOtp(phone, otp);
                System.out.println("otp successful: " + message.getSid());
                return true;
            } else {
                System.out.println("failed to send OTP");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

    //2factor
/*  public String generateAndSendPhoneOtp(String phone) {
      try {
          String apiKey = "d7a62e23-6663-11ee-addf-0200cd936042";
          String apiUrl = "https://2factor.in/API/V1/" + apiKey + "/SMS/" + phone + "/AUTOGEN/OTP1";

          OkHttpClient client = new OkHttpClient().newBuilder().build();

          Request request = new Request.Builder()
                          .url(apiUrl)
                          .method("GET", null)
                          .build();

                  Response response = client.newCall(request).execute();
                  String responseBody = response.body().string();

                  // Parse the response JSON and extract the OTP
                  ObjectMapper objectMapper = new ObjectMapper();
                  JsonNode jsonResponse = objectMapper.readTree(responseBody);

                  if (jsonResponse != null && jsonResponse.has("OTP")) {
                      String phoneOtp = jsonResponse.get("OTP").asText();
                      cacheOtp(phone, phoneOtp);

                      System.out.println("OTP is " + phoneOtp);
                      return phoneOtp;
                  } else {
                      return "OTP not found in response";
                  }
              } catch (Exception e) {
                  e.printStackTrace();
                  return "Error generating or sending OTP";
              }
          }*/
//2factor
  /* public String generateAndSendPhoneOtp(String phone) {
        try {
            String apiUrl = "https://2factor.in/API/V1/d7a62e23-6663-11ee-addf-0200cd936042/SMS/{phone}/AUTOGEN";
            apiUrl = apiUrl.replace("{d7a62e23-6663-11ee-addf-0200cd936042}", twoFactorApiKey)
                    .replace("{phone}", phone);
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(apiUrl);

            String requestBody = "{ \"method\": \"sms\" }";
            httpPost.setEntity(new StringEntity(requestBody));

            CloseableHttpResponse response = client.execute(httpPost);

            HttpEntity entity = response.getEntity();
            String responseText = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) {
                System.out.println("SMS OTP sent successfully.");
                return "SMS OTP sent successfully.";
            }
            else
            {
                // Request failed
                System.out.println("Failed to send SMS OTP. Response: " + responseText);
                return "Failed to send SMS OTP.";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Error generating or sending OTP";
        }
    }*/
//twilio

//nexmo or vonage
  /* public String generateAndSendPhoneOtp(String phone) {
       try {
           String apiKey = "19106476";
           String apiSecret = "QbFp4oPgMl4TIuH5";
           String vonagePhoneNumber = "916300131956";

           VonageClient client = VonageClient.builder()
                   .apiKey(apiKey)
                   .apiSecret(apiSecret)
                   .build();

           String otp = generatephoneOtp();
           String messageBody = "Your OTP code is: " + otp;

           TextMessage message = new TextMessage(vonagePhoneNumber, phone, messageBody);

           SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

           if (response.getMessages().get(0).getErrorText() == null) {
               cacheOtp(phone, otp);
               String messageID = generateUniqueMessageID();
               System.out.println("otp is:"+otp);
               return "OTP sent successfully. Message ID: " +otp;
           } else {
               return "Failed to send OTP. Error: " + response.getMessages().get(0).getErrorText();
           }
       } catch (Exception e) {
           e.printStackTrace();
           return "Error generating or sending OTP";
       }
   }
    private String generateUniqueMessageID() {
        // Generate a unique message ID, e.g., using UUID
        return UUID.randomUUID().toString();
    }*/
//mesg91
   /* public String sendPhoneOtp(String phone) {
        try {
            String widgetId = "336a6c6c7665373632363430";
            String authToken = "407796AY9IOizlii65279882P1";
            String identifier = phone;

            String otp = generateOtp();

            String jsonPayload = "{"
                    + "\"widgetId\":\"" + widgetId + "\","
                    + "\"tokenAuth\":\"" + authToken + "\","
                    + "\"identifier\":\"" + identifier + "\","
                    + "\"otp\":\"" + otp + "\""
                    + "}";
            URL url = new URL("https://control.msg91.com/api/v5/flow/");

            // Open a connection to the server
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200)
            {
                System.out.println("Success Response: " +otp);
                return "Success Response: " + connection.getResponseMessage();
            } else {
                System.out.println("Failure Response: " + connection.getResponseMessage());
                return "Failure Response: " + connection.getResponseMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating or sending OTP";
        }
    }
}
*/
//vonage
   /* public String generateAndSendPhoneOtp(String phone) {
        try {
            String apiKey = "19106476";
            String apiSecret = "QbFp4oPgMl4TIuH5";
            String vonagePhoneNumber = "+916300131956"; // virtual number register with vonage
            VonageClient client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();

            String otp = generateOtp();
            String messageBody = "Your OTP code is: " + otp;

            String fullPhoneNumber = +91+ phone;

            TextMessage message = new TextMessage(vonagePhoneNumber, fullPhoneNumber, messageBody);
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            if (response.getMessages().get(0).getErrorText() == null) {
                cacheOtp(fullPhoneNumber, otp);
                String messageID = generateUniqueMessageID();
                System.out.println("otp is:" + otp);
                return "OTP sent successfully " + messageID;
            }
            else
            {
                System.out.println("failed to send");

                return "Failed to send OTP. Error: " + response.getMessages().get(0).getErrorText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating or sending OTP";
        }
    }
    private String generateUniqueMessageID() {
        return UUID.randomUUID().toString();
    }*/
//vonage
 /*public boolean sendPhoneOtp(String phone, String phoneotp) {
        try {
            String apiKey = "5b61ddb3";
            String apiSecret = "6Kn8yighuDf6Fbbt";
            String vonagePhoneNumber = "+916300131956"; // virtual number registered with Vonage
            VonageClient client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();
            String messageBody = "Your OTP code is: " + phoneotp;

            String fullPhoneNumber = "+91" + phone;

            TextMessage message = new TextMessage(vonagePhoneNumber, fullPhoneNumber, messageBody);
            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            if (response.getMessages().get(0).getErrorText() == null)
            {
                cachePhoneOtp(fullPhoneNumber, phoneotp);
                System.out.println("Phoneotp: " + phoneotp);
                return true; // Return true when OTP is sent successfully
            } else {
                System.out.println("Failed to send OTP: " + response.getMessages().get(0).getErrorText());
                return false; // Return false when OTP sending fails
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of exceptions
        }
    }*/