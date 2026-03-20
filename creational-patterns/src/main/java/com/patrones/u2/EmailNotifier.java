package com.patrones.u2;

public class EmailNotifier implements Notifier {

    public String channel() {
        return "EMAIL";
    }

    public void send(String recipient, String message) {
        NotificationLogger.INSTANCE.log(channel(), recipient, message);
    }

}
