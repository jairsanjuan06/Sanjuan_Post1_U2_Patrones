package com.patrones.u2;

public class PushNotifier implements Notifier {
    public String channel() {
        return "PUSH";
    }

    public void send(String recipient, String message) {

    }
}