package com.royal.msshelter.patterns.adapter;

public interface MessagingAdapter {

    void sendMessage(String to, String subject, String content);
}
