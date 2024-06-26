package br.com.ses_sender.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SesException;

public class SESService {
    public static void sendMessage(String message) {
             AwsBasicCredentials awsCreds = AwsBasicCredentials.create("", "");

        Region region = Region.EU_CENTRAL_1;
        SesClient client = SesClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(region)
                .build();

        String bodyText = "Hello from SES ";

        String bodyHTML = "<html>"
                + "<head>"
                + "  <style>"
                + "    body {"
                + "      font-family: Arial, sans-serif;"
                + "      margin: 0;"
                + "      padding: 0;"
                + "      background-color: #f4f4f4;"
                + "    }"
                + "    .container {"
                + "      width: 100%;"
                + "      max-width: 600px;"
                + "      margin: 0 auto;"
                + "      background-color: #ffffff;"
                + "      padding: 20px;"
                + "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);"
                + "    }"
                + "    .header {"
                + "      background-color: #4CAF50;"
                + "      color: white;"
                + "      padding: 10px 0;"
                + "      text-align: center;"
                + "    }"
                + "    .content {"
                + "      padding: 20px;"
                + "      text-align: left;"
                + "    }"
                + "    .footer {"
                + "      background-color: #eeeeee;"
                + "      color: #333333;"
                + "      padding: 10px 0;"
                + "      text-align: center;"
                + "      font-size: 12px;"
                + "    }"
                + "  </style>"
                + "</head>"
                + "<body>"
                + "  <div class='container'>"
                + "    <div class='header'>"
                + "      <h1>Բարև</h1>"
                + "    </div>"
                + "    <div class='content'>"
                + "      <p>❤️</p>"
                + "      <p>Լավ ա լավ ա</p>"
                + "      <p>Ուշադրություն<br>Շնորհակալություն ուշադրության համար</p>"
                + "    </div>"
                + "    <div class='footer'>"
                + "      <p>&copy; 2024 sagittarius system</p>"
                + "    </div>"
                + "  </div>"
                + "</body>"
                + "</html>";


        try {
            send(client, "gariktepanosian@gmail.com", "acclaimagain@gmail.com", "tester",
                    bodyText, bodyHTML);
            client.close();

            System.out.println("Email sent");

        } catch (IOException | MessagingException e) {
            e.getStackTrace();
        }
    }

    public static void send(SesClient client,
                            String sender,
                            String recipient,
                            String subject,
                            String bodyText,
                            String bodyHTML) throws AddressException, MessagingException, IOException {

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain; charset=UTF-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

        MimeMultipart msgBody = new MimeMultipart();
        msgBody.addBodyPart(textPart);
        msgBody.addBodyPart(htmlPart);

        message.setContent(msgBody);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);

            SdkBytes data = SdkBytes.fromByteArray(arr);
            RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(rawEmailRequest);

        } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}