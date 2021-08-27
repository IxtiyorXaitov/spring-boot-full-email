package dev.ikhtiyor.springbootfullemail.service;

import dev.ikhtiyor.springbootfullemail.payload.Request;
import dev.ikhtiyor.springbootfullemail.payload.Response;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final Configuration config;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, Configuration config) {
        this.javaMailSender = javaMailSender;
        this.config = config;
    }

    @Value("${spring.mail.username}")
    String fromEmail;

    public Response sendEmail(Request request) {
        Map<String, Object> model = new HashMap<>();
        model.put("fullName", request.getFullName());
        model.put("message", request.getMessage());

        try {
            sendEmailWithHtml(
                    model,
                    request,
                    "email-template.ftl"
            );

            return new Response("Sended", true);

        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
            return new Response("Error", false);
        }
    }

    public void sendEmailWithHtml(Map<String, Object> model, Request request, String htmlFile) throws MessagingException, IOException, TemplateException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(fromEmail);
            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            // add attachment
            // helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
            Template t = config.getTemplate(htmlFile);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setTo(request.getSendTo());
            helper.setSubject(request.getSubject());
            helper.setText(html, true);
            // helper.setSubject("\uD83D\uDCDB noreply");
            javaMailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Response sendEmailWithText(Request request) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(request.getSendTo());
            msg.setSubject(request.getSubject());
            msg.setText(request.getMessage());

            javaMailSender.send(msg);
            return new Response("Sended", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("Error", false);
        }
    }

    public Response sendEmailWithAttachment(Request request) {
        try {

            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromEmail);
            // true = multipart message
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(request.getSendTo());

            helper.setSubject(request.getSubject());

            // default = text/plain
            //helper.setText("Check attachment for image!");

            // true = text/html
            // helper.setText(request.getMessage(), true);

            // false = text/html
            helper.setText(request.getMessage());

            Resource resource = new ClassPathResource("images/email_test_photo.png");


            helper.addAttachment("images/email_test_photo.png", resource);

            javaMailSender.send(msg);
            return new Response("Sended", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("Error", false);
        }
    }
}
