package com.andresv2.apirest.service;

import com.google.cloud.secretmanager.v1.*;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GcpSecretService {
    @Value("${google.secret.email}")
    String emailToRead; // for test the accoutn will be avargas@applab.mx
    @Value("${spring.cloud.secret.url}") //this will be a string to split it on 3 parts ProyectId-SecretId-Version -> Example "18827454519-secretPasswords-latest"
    String secretUrl;
    // Service Properties
    @Value("${imap.protocol}")
    private String protocol;
    @Value("${imap.host}")
    private String host;
    @Value("${imap.port}")
    private String port;

    // Project Variables
    String[] secretManagerProperties;
    private String TOKEN = "";

    public String readEmail(String email) throws Exception {
        if(email != null)
            emailToRead = email;

        try {
            // Obtener las credenciales del Secret Manager                  projectId                       SecretName                  Version
            JSONArray credentialsJson = new JSONArray(accessSecretVersion(secretManagerProperties[0], secretManagerProperties[1], secretManagerProperties[2]));
            JSONArray messagess = new JSONArray();

            if(credentialsJson.length() < 0)
                return "The File on Secret Manager is Empty, Please Validate";

            // Parse credentials (asumiendo que están en formato JSON)
            // Aquí se asume que tienes un campo 'username' y 'password' en el JSON
            AtomicReference<JSONObject> authenticationJson = new AtomicReference<>();
            credentialsJson.forEach(o -> {
                JSONObject account = (JSONObject) o;
                if(account.getString("email").equalsIgnoreCase(emailToRead))
                    authenticationJson.set(account);
            });

            if(authenticationJson.get() == null)
                return "This Email doesn't have an account on the Secret Manager File, Please Add the credentials the file";

            String username = extractValue(authenticationJson.get().toString(), "email"); // this can be username or email
            String password = extractValue(authenticationJson.get().toString(), "password"); // this is the password generated from password Application on the security Section of the google account after enabling the 2 steps of validation
//            String client_ID = extractValue(authenticationJson.get().toString(), "client_id");
//            String secret_ID = extractValue(authenticationJson.get().toString(), "secret_id");
//            String refresh_Token = extractValue(authenticationJson.get().toString(), "refresh_token");

            // Configuración de las propiedades para la conexión con el servidor de correo
            Properties properties = new Properties();
            properties.put("mail.store.protocol", protocol); // IMAP Config - [protocol : imap -> host : imap.gmail.com -> port : 993] POP3 Config [protocol : imap -> host : imap.gmail.com -> port : 993]
            properties.put("mail." + protocol + ".host", host);
            properties.put("mail." + protocol + ".port", port);
            properties.put("mail." + protocol + ".ssl.enable", "true");// required for Gmail

            if(host.contains("outlook")){
                properties.put("mail." + protocol + ".sasl.enable", "true");
                properties.put("mail." + protocol + ".sasl.mechanisms", "XOAUTH2");
                properties.put("mail." + protocol + ".auth.login.disable", "true");
                properties.put("mail." + protocol + ".auth.plain.disable", "true");

                password = getAccessToken("clientId", "secretId","refresh");
            }

            // Iniciar sesión en el servidor de correo
            Session session = Session.getInstance(properties);

            Store store = session.getStore();
            store.connect(username, password);
//            store.connect(username, getAccessToken(client_ID, secret_ID, refresh_Token));

            // Acceder a la carpeta de correos (INBOX)
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Filters to Search the Messages
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        /*  If we need to serach By Range of Dates Use This
            SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, someFutureDate);
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, somePastDate);
            SearchTerm andTerm = new AndTerm(olderThan, newerThan);
        */

            SearchTerm todayDate = new ReceivedDateTerm(ComparisonTerm.EQ, sdf.parse(sdf.format(new Date())));  // only today's messages
            FlagTerm unseenFlagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);                      // only not Seen Messages

            Message[] messages = inbox.search(new AndTerm(unseenFlagTerm, todayDate));

            for (Message message : messages) {
                JSONObject messageJson = new JSONObject();
                // here will be all the methods or services for the messages ( create Solicitudes, create Emisiones, etc.)
                messageJson.put(String.valueOf(message.getReceivedDate()), getTextPlainMessage(message));
                messagess.put(messageJson);
                //this only will be set if the message did its method or service complete
                message.setFlag(Flags.Flag.SEEN, true);
            }

            inbox.close(false);
            store.close();

            return messagess.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "Error on The Process";
    }

    private String accessSecretVersion(String projectId, String secretId, String versionId) throws Exception {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretVersionName.toString())
                    .build();

            return client.accessSecretVersion(request).getPayload().getData().toStringUtf8();
        }
    }

    private String getAccessToken(String clientId, String secretClientId, String refreshToken){
        if (!TOKEN.equalsIgnoreCase("")) {
            OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("client_id", clientId)
                    .addFormDataPart("client_secret", secretClientId)
                    .addFormDataPart("refresh_token", refreshToken)
                    .addFormDataPart("grant_type", "refresh_token")
                    .build();
            Request request = new Request.Builder()
                    .url("https://accounts.google.com/o/oauth2/token")
                    .post(formBody)
                    .build();
            Call call = client.newCall(request);
            Response response;
            try {
                response = client.newCall(request).execute();
                String responseString = response.body().string();
                JSONObject json = new JSONObject(responseString);
                String accessToken = json.getString("access_token");
                TOKEN = accessToken;
                response.body().close();
                return accessToken;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return TOKEN;
        }
    }

    private String getTextPlainMessage(Message message){
        StringBuilder plainTextContent = new StringBuilder();
        try {
            if (message.isMimeType("text/plain")) {
                plainTextContent = new StringBuilder(message.getContent().toString());
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart multipart = (MimeMultipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        plainTextContent.append(bodyPart.getContent().toString());
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return plainTextContent.toString();
    }

    private String extractValue(String json, String key) {
        // Implementación para extraer un valor específico de un JSON simple
        // Aquí puedes usar alguna librería JSON como Jackson o Gson
        // Esto es solo un ejemplo simplificado
        int index = json.indexOf(key);
        int start = json.indexOf(":", index) + 2;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
