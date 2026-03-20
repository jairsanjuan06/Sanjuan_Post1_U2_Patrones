package com.patrones.u2;

public interface Notifier {
    /**
     * Envía una notificación al destinatario.
     *
     * @param recipient identificador del destinatario (email, número, token)
     * @param message   contenido del mensaje
     */
    void send(String recipient, String message);

    /**
     * Retorna el identificador del canal de este notificador.
     */
    String channel();
}
