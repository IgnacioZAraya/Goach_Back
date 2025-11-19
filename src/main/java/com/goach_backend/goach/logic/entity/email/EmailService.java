package com.goach_backend.goach.logic.entity.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public String sendTextEmail(String userEmail, int code) throws IOException {
        Email from = new Email("igna8.1@outlook.com");
        String subject = "Your Private Code!";
        Email to = new Email(userEmail);
        Content content = new Content("text/plain", "Here's the code for setting your new password: " + code);
        Mail mail = new Mail(from, subject, to, content);

//        SendGrid sg = new SendGrid("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");
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//            logger.info(response.getBody());
//            return response.getBody();
//        } catch (IOException ex) {
//            throw ex;
//        }
        return "";
    }
}
