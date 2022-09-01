package co.develhope.forum.services;

import co.develhope.forum.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * NB controller only for testing purposes
 */
@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendActivationEmail(UserModel userModel) {
        SimpleMailMessage sms = new SimpleMailMessage();
        sms.setTo(userModel.getUserEmail());
        sms.setFrom("develhopetest87@gmail.com");
        sms.setReplyTo("develhopetest87@gmail.com");
        sms.setSubject("Benvenuto " + userModel.getUserFirstName());
        sms.setText("Benvenuto nel Forum DevelHope");
        javaMailSender.send(sms);

    }
}