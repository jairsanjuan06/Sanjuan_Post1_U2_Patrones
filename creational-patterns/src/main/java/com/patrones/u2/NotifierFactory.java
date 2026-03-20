package com.patrones.u2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * NotifierFactory: Factory Method con registro dinamico.
 * Aplica OCP: nuevos tipos se registran sin modificar logica existente.
 */

public class NotifierFactory {

    private static final Map<String, Supplier<Notifier>> REGISTRY = new HashMap<>();
    static {
        // Registro inicial de factory methods
        REGISTRY.put("email", EmailNotifier::new);
        REGISTRY.put("sms", SmsNotifier::new);
        REGISTRY.put("push", PushNotifier::new);
    }

    /**
     * Registra un nuevo tipo de notificador en tiempo de ejecucion.
     * Permite agregar canales sin recompilar la factory.
     */
    public static void register(String type, Supplier<Notifier> factory) {
        REGISTRY.put(type.toLowerCase(), factory);
    }

    /**
     * Crea un Notifier del tipo solicitado.
     * 
     * @throws IllegalArgumentException si el tipo no esta registrado
     */
    public static Notifier create(String type) {
        Supplier<Notifier> factory = REGISTRY.get(type.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException(
                    "Tipo de notificador no registrado: " + type +
                            ". Tipos disponibles: " + REGISTRY.keySet());
        }
        return factory.get();
    }

}
