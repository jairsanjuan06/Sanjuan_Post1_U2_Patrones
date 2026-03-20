package com.patrones.u2;

public class SmsNotifier implements Notifier {
    public String channel() {
        return "SMS";
    }

    public void send(String recipient, String message) {
        NotificationLogger.INSTANCE.log(channel(), recipient, message);
    }
}
