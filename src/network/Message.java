package network;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Immutable container for a single protocol message exchanged between the client and server.
// A Message has a MessageType and an optional map of string key-value payload fields.
// Instances are created via the static factory helpers or the Builder inner class.
//
// Example – building a LOGIN request:
//   Message msg = new Message.Builder(MessageType.LOGIN)
//           .put("username", "alice")
//           .put("password", "secret")
//           .build();
//   String json = JsonHelper.toJson(msg);
//   // {"type":"LOGIN","username":"alice","password":"secret"}
public final class Message {

    /** The type of this message. */
    private final MessageType type;

    /** Payload fields (unmodifiable). */
    private final Map<String, String> fields;

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    private Message(MessageType type, Map<String, String> fields) {
        this.type   = type;
        this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
    }

    /* ------------------------------------------------------------------ */
    /*  Accessors                                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Returns the message type.
     *
     * @return the {@link MessageType}
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Returns the value associated with {@code key}, or {@code null} if absent.
     *
     * @param key payload field name
     * @return string value or {@code null}
     */
    public String get(String key) {
        return fields.get(key);
    }

    /**
     * Returns the value associated with {@code key}, or {@code defaultValue} if absent.
     *
     * @param key          payload field name
     * @param defaultValue fallback value
     * @return string value or the default
     */
    public String getOrDefault(String key, String defaultValue) {
        return fields.getOrDefault(key, defaultValue);
    }

    /**
     * Returns an unmodifiable view of all payload fields.
     *
     * @return field map
     */
    public Map<String, String> getFields() {
        return fields;
    }

    /* ------------------------------------------------------------------ */
    /*  Convenience static factories                                       */
    /* ------------------------------------------------------------------ */

    /**
     * Creates a simple RESPONSE message with an OK status.
     *
     * @param text human-readable result text to send to the client
     * @return a RESPONSE message
     */
    public static Message ok(String text) {
        return new Builder(MessageType.RESPONSE)
                .put("status", "OK")
                .put("message", text)
                .build();
    }

    /**
     * Creates a simple RESPONSE message with an ERROR status.
     *
     * @param text human-readable error description
     * @return a RESPONSE message
     */
    public static Message error(String text) {
        return new Builder(MessageType.RESPONSE)
                .put("status", "ERROR")
                .put("message", text)
                .build();
    }

    /**
     * Creates a plain type-only message with no additional payload fields.
     *
     * @param type the message type
     * @return a Message of the given type
     */
    public static Message of(MessageType type) {
        return new Builder(type).build();
    }

    /* ------------------------------------------------------------------ */
    /*  Builder                                                            */
    /* ------------------------------------------------------------------ */

    /**
     * Fluent builder for {@link Message} instances.
     */
    public static final class Builder {

        private final MessageType type;
        private final Map<String, String> fields = new LinkedHashMap<>();

        /**
         * Starts a new builder for the specified message type.
         *
         * @param type the message type
         */
        public Builder(MessageType type) {
            this.type = type;
        }

        /**
         * Adds a key-value field to the message payload.
         *
         * @param key   field name
         * @param value field value
         * @return this builder (fluent)
         */
        public Builder put(String key, String value) {
            fields.put(key, value);
            return this;
        }

        /**
         * Builds and returns the immutable {@link Message}.
         *
         * @return the constructed message
         */
        public Message build() {
            return new Message(type, fields);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Object overrides                                                   */
    /* ------------------------------------------------------------------ */

    @Override
    public String toString() {
        return "Message{type=" + type + ", fields=" + fields + "}";
    }
}
