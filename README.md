# Sistema de Notificaciones — Patrones Creacionales

**Unidad 2 · Post-Contenido 1**
**Curso:** Patrones de Diseño de Software
**Universidad de Santander — UDES**
**Estudiante:** Jair Sanjuan Suarez
**Año:** 2026

---

## Descripción del Proyecto

Sistema de notificaciones para una aplicación de e-commerce construido en Java con Maven. El proyecto demuestra la implementación correcta de dos patrones creacionales del catálogo GoF:

- **Singleton (variante `enum`)** — garantiza una única instancia del gestor de logs en toda la aplicación, thread-safe por diseño de la JVM.
- **Factory Method con registro dinámico** — permite crear distintos tipos de notificadores (Email, SMS, Push, Slack) sin que el código cliente conozca las clases concretas, aplicando el principio Open/Closed (OCP).

---

## Estructura del Proyecto

```
creational-patterns/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── patrones/
                    └── u2/
                        ├── NotificationLogger.java   ← Singleton (enum)
                        ├── Notifier.java             ← Interfaz Product
                        ├── EmailNotifier.java        ← Concrete Product
                        ├── SmsNotifier.java          ← Concrete Product
                        ├── PushNotifier.java         ← Concrete Product
                        ├── NotifierFactory.java      ← Factory Method
                        └── Main.java                 ← Clase de demostración
```

---

## Análisis de Patrones Implementados

### 1. Singleton — `NotificationLogger`

**Problema que resuelve:**
En una aplicación de e-commerce, múltiples componentes (módulo de pedidos, módulo de pagos, módulo de envíos) necesitan registrar notificaciones enviadas. Si cada componente crea su propia instancia del logger, el historial queda fragmentado y es imposible tener una visión unificada de todas las notificaciones de la sesión.

**Solución aplicada:**
Se implementó mediante la variante `enum`, que es la forma más robusta de Singleton en Java porque:

- La JVM garantiza que solo se crea una instancia del enum, incluso en entornos multihilo.
- Es completamente thread-safe sin necesidad de bloques `synchronized` ni `volatile`.
- Es resistente a ataques de deserialización y reflexión que rompen implementaciones tradicionales.

```java
public enum NotificationLogger {
    INSTANCE;
    // Una sola instancia accesible desde cualquier clase como:
    // NotificationLogger.INSTANCE.log(...)
}
```

**Beneficio concreto:** Todas las notificaciones enviadas por cualquier canal quedan registradas en un único historial centralizado, accesible con `NotificationLogger.INSTANCE.printAll()`.

---

### 2. Factory Method — `NotifierFactory`

**Problema que resuelve:**
El código de negocio necesita crear notificadores de distintos tipos (email, SMS, push) según la preferencia del usuario o la configuración del sistema. Si el código cliente instancia directamente `new EmailNotifier()` o `new SmsNotifier()`, queda acoplado a las clases concretas — agregar un nuevo canal (por ejemplo, Slack o WhatsApp) requeriría modificar el código de negocio en múltiples lugares.

**Solución aplicada:**
Se implementó una factory con registro dinámico usando `Map<String, Supplier<Notifier>>`. Este enfoque aplica el principio Open/Closed (OCP):

- **Abierto para extensión:** agregar un nuevo canal solo requiere llamar a `NotifierFactory.register("slack", SlackNotifier::new)`.
- **Cerrado para modificación:** la lógica existente de la factory no se toca.

```java
// El cliente solo conoce la interfaz Notifier, nunca la clase concreta
Notifier email = NotifierFactory.create("email");
Notifier sms   = NotifierFactory.create("sms");

// Registrar un canal nuevo en tiempo de ejecución sin modificar la factory
NotifierFactory.register("slack", () -> new SlackNotifier());
```

**Beneficio concreto:** El sistema puede soportar nuevos canales de notificación en tiempo de ejecución sin recompilar ni modificar la factory ni el código cliente.

---

## Relación entre los dos Patrones

El Singleton y el Factory Method colaboran directamente en este sistema:

```
Main
 │
 ├── NotifierFactory.create("email")  →  EmailNotifier
 │                                           │
 │                                           └── NotificationLogger.INSTANCE.log(...)
 │                                                        ↑
 ├── NotifierFactory.create("sms")   →  SmsNotifier ─────┤  (misma instancia)
 │                                                        │
 └── NotifierFactory.create("push")  →  PushNotifier ────┘
```

Cada notificador concreto, independientemente del canal, usa la misma instancia del logger Singleton para registrar el evento. Esto garantiza que el historial final contiene **todas** las notificaciones enviadas, sin importar cuántos canales se usaron.

---

## Prerrequisitos

| Herramienta | Versión |
|-------------|---------|
| Java JDK | 17 o superior |
| Apache Maven | 3.8 o superior |
| VS Code | Última estable |
| Git | 2.x o superior |

---

## Instrucciones de Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/sanjuan/sanjuan-post1-u2.git
cd creational-patterns
```

### 2. Compilar el proyecto

```bash
mvn compile
```

Salida esperada:
```
[INFO] BUILD SUCCESS
```

### 3. Ejecutar la clase Main

```bash
mvn exec:java -Dexec.mainClass="com.patrones.u2.Main"
```

O alternativamente:

```bash
java -cp target\classes com.patrones.u2.Main
```

---

## Salida Esperada en Consola

```
=== Demo: Singleton + Factory Method ===

Misma instancia: true
[2026-03-19 20:44:xx] [EMAIL] -> cliente@mail.com: Su pedido #1001 fue confirmado
[2026-03-19 20:44:xx] [SMS]   -> +57-300-0000001: Pedido #1001 confirmado
[2026-03-19 20:44:xx] [PUSH]  -> device-token-abc123: Nuevo pedido listo para enviar
[2026-03-19 20:44:xx] [SLACK] -> #pedidos: Pedido #1001 procesado

=== Historial de Notificaciones ===
[2026-03-19 20:44:xx] [EMAIL] -> cliente@mail.com: Su pedido #1001 fue confirmado
[2026-03-19 20:44:xx] [SMS]   -> +57-300-0000001: Pedido #1001 confirmado
[2026-03-19 20:44:xx] [PUSH]  -> device-token-abc123: Nuevo pedido listo para enviar
[2026-03-19 20:44:xx] [SLACK] -> #pedidos: Pedido #1001 procesado
Total: 4 notificaciones
```

### Captura de Pantalla de la Ejecución

>
> `![Salida Main](/creational-patterns/src/img/CapturaSalidaMain.jpg)`

---

## Decisiones de Diseño

| Decisión | Alternativa descartada | Justificación |
|----------|----------------------|---------------|
| Singleton con `enum` | DCL con `volatile` | El enum es más simple, thread-safe por la JVM y resistente a reflexión/deserialización |
| Factory con `Map<Supplier>` | Switch/case por tipo | El Map aplica OCP — agregar un canal no modifica lógica existente |
| Interfaz `Notifier` con `channel()` | Sin método `channel()` | Permite al logger etiquetar cada registro sin hacer `instanceof` |
| `List.copyOf()` en `getEntries()` | Retornar la lista directamente | Evita que código externo modifique el historial interno del Singleton |

---

## Commits del Proyecto

```
feat(singleton): implementar NotificationLogger como enum Singleton thread-safe
feat(factory-method): implementar NotifierFactory con registro dinámico via Supplier
feat: agregar Main de demostración y README con análisis de patrones
```

---

## Autor

**Jair Sanjuan Suarez**
Ingeniería de Sistemas — Universidad de Santander (UDES)
2026
