package dev.ikhtiyor.springbootfullemail.controller;

import dev.ikhtiyor.springbootfullemail.payload.Request;
import dev.ikhtiyor.springbootfullemail.payload.Response;
import dev.ikhtiyor.springbootfullemail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
public class EmailController {

    final
    EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }


    @PostMapping("/html")
    public Response sendEmail(@RequestBody Request request) {
        return emailService.sendEmail(request);
    }


    @PostMapping("/text")
    public HttpEntity<?> sendEmailWithText(
            @RequestBody Request request
    ) {
        Response response = emailService.sendEmailWithText(request);
        return ResponseEntity.status(response.isStatus() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);
    }

    @PostMapping("/attachment")
    public HttpEntity<?> sendEmailWithAttachment(
            @RequestBody Request request
    ) {
        Response response = emailService.sendEmailWithAttachment(request);
        return ResponseEntity.status(response.isStatus() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);
    }
}
