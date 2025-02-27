package com.itwill.rest.service;

import java.util.Properties;
import java.util.Random;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.socketFactory.port}")
    private int socketPort;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttls;
    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean startlls_required;
    @Value("${spring.mail.properties.mail.smtp.socketFactory.fallback}")
    private boolean fallback;
    @Value("${AdminMail.id}")
    private String id;
    @Value("${AdminMail.password}")
    private String password;

    public EmailService() {
        // 생성자에서 JavaMailSenderImpl을 설정하지 않고,
        // EmailConfig를 통해 설정된 JavaMailSender를 주입받아야 합니다.
    }
    
    public Properties getMailProperties() {
        Properties pt = new Properties();
        pt.put("mail.smtp.socketFactory.port", socketPort);
        pt.put("mail.smtp.auth", auth);
        pt.put("mail.smtp.starttls.enable", starttls);
        pt.put("mail.smtp.starttls.required", startlls_required);
        pt.put("mail.smtp.socketFactory.fallback", fallback);
        pt.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return pt;
    }

    public MimeMessage createMessage(String to) throws Exception {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, to);
        message.setSubject("이메일 인증 테스트");

        String authCode = createKey();
        String msgg = "";
        msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 안녕하세요 임준호입니다. </h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>감사합니다.<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authCode + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("your-email@gmail.com", "임준호"));

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) {
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97)); // a~z
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65)); // A~Z
                    break;
                case 2:
                    key.append(rnd.nextInt(10)); // 0~9
                    break;
            }
        }
        return key.toString();
    }

    public String sendSimpleMessage(String to) throws Exception {
        MimeMessage message = createMessage(to);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return createKey();
    }
}
