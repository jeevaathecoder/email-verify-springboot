package com.jeeva.emailverification.event.listener;

import com.jeeva.emailverification.event.RegistrationCompleteEvent;
import com.jeeva.emailverification.user.User;
import com.jeeva.emailverification.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener
        implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;

    private User theUser;
    private final JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //1. Get the newly registered user

         theUser = event.getUser();

        //2. Create a verification token for the user

        String verficationToken = UUID.randomUUID().toString();

        //3. Save the verification token for the user

        userService.saveUserVerificationToken(theUser, verficationToken);

        //4. Build the verification URL to be sent to user
        String url = event.getApplicationUrl() + "/register/verifyEmail?token=" + verficationToken;

        //5. Send the verification email to the user


        try {
            sendVerificationEmail(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Port Service";
        String mailContent = "<p>Click the link to verify registration : <a href='" + url + "'>Verify</a></p>";

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("jeevaathecoder@gmail.com", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }

}