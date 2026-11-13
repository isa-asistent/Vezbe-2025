package isa.vezbe2.async_example.service;

import isa.vezbe2.async_example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    /*
     * Koriscenje klase za ocitavanje vrednosti iz application.properties fajla
     */
    @Autowired
    private Environment env;

    /*
     * Anotacija za oznacavanje asinhronog zadatka
     * Vise informacija na: https://docs.spring.io/spring/docs/current/spring-framework-reference/integration.html#scheduling
     */
    @Async
    public void sendNotificationAsync(User user) throws MailException, InterruptedException {
        System.out.println("Async metoda se izvrsava u drugom Threadu u odnosu na prihvaceni zahtev. Thread id: " + Thread.currentThread().getId());
        Thread.sleep(10000);

        System.out.println("Slanje emaila...");
        sendEmail(user.getEmailAddress(), "Primer slanja emaila pomoću asinhronog Spring taska", "Pozdrav " + user.getFirstName() + ",\n\nhvala što pratiš ISA.");
        System.out.println("Email poslat!");
    }

    public void sendNotificationSync(User user) throws MailException, InterruptedException {
        System.out.println("Sync metoda se izvrsava u istom Threadu koji je i prihvatio zahtev. Thread id: " + Thread.currentThread().getId());
        Thread.sleep(10000);
        System.out.println("Slanje emaila...");
        sendEmail(user.getEmailAddress(), "Primer slanja emaila pomocu sinhronog Spring taska", "Pozdrav " + user.getFirstName() + ",\n\nhvala što pratiš ISA.");
        System.out.println("Email poslat!");
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(env.getProperty("spring.mail.username"));
        mail.setSubject(subject);
        mail.setText(body);
        javaMailSender.send(mail);
    }
}
