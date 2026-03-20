package com.patrones.u2;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Demo: Singleton + Factory Method===\n");

        // Demostración Singleton: misma instancia desde distintos contextos
        NotificationLogger logger1 = NotificationLogger.INSTANCE;
        NotificationLogger logger2 = NotificationLogger.INSTANCE;
        System.out.println("Misma instancia: " + (logger1 == logger2));

        // Demostración Factory Method: crear notificadores por tipo
        Notifier email = NotifierFactory.create("email");
        Notifier sms = NotifierFactory.create("sms");
        Notifier push = NotifierFactory.create("push");

        // Enviar notificaciones — el Singleton registra todas
        email.send("cliente@mail.com", "Su pedido #1001 fue confirmado");
        sms.send("+57-300-0000001", "Pedido #1001 confirmado");
        push.send("device-token-abc123", "Nuevo pedido listo para enviar");

        // Registrar un tipo nuevo en tiempo de ejecucion (OCP demostrado)
        NotifierFactory.register("slack", () -> new Notifier() {
            public String channel() {
                return "SLACK";
            }

            public void send(String r, String m) {
                NotificationLogger.INSTANCE.log(channel(), r, m);
            }
        });
        NotifierFactory.create("slack").send("#pedidos", "Pedido #1001 procesado");

        // Imprimir historial — el Singleton tiene TODOS los registros
        NotificationLogger.INSTANCE.printAll();
    }

}
