package com.royal.msshelter.patterns.adapter;

public class EmailAdapter implements MessagingAdapter {

    private final EmailService emailService;

    public EmailAdapter() {
        this(new EmailService());
    }

    public EmailAdapter(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendMessage(String to, String subject, String content) {
        emailService.sendEmail(to, subject, content);
    }
}
