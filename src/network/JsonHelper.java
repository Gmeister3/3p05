package network;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lightweight, dependency-free JSON serializer and deserializer for flat (non-nested)
 * {@link Message} objects.
 *
 * <p>Only flat string values are supported.  Nested objects and arrays are intentionally
 * out of scope because the protocol only uses key-value pairs of primitive values.</p>
 *
 * <p>The output format is standard JSON with string values escaped for {@code "}, {@code \},
 * newline ({@code \n}), and carriage-return ({@code \r}).</p>
 *
 * <p>Example round-trip:</p>
 * <pre>
 *   Message msg = new Message.Builder(MessageType.LOGIN)
 *           .put("username", "alice")
 *           .put("password", "s3cr3t")
 *           .build();
 *   String json = JsonHelper.toJson(msg);
 *   // → {"type":"LOGIN","username":"alice","password":"s3cr3t"}
 *
 *   Message parsed = JsonHelper.fromJson(json);
 *   // parsed.getType() == MessageType.LOGIN
 *   // parsed.get("username").equals("alice")
 * </pre>
 */
public final class JsonHelper {

    /** Private constructor – utility class, not meant to be instantiated. */
    private JsonHelper() { }

    /* ------------------------------------------------------------------ */
    /*  Serialization                                                      */
    /* ------------------------------------------------------------------ */

    /**
     * Serializes a {@link Message} to a single-line JSON string.
     *
     * <p>The {@code "type"} key always appears first, followed by all payload
     * fields in insertion order.</p>
     *
     * @param message the message to serialize
     * @return a JSON string, e.g. {@code {"type":"LOGIN","username":"alice"}}
     */
    public static String toJson(Message message) {
        StringBuilder sb = new StringBuilder("{");

        // Always emit "type" first
        sb.append("\"type\":\"").append(escape(message.getType().name())).append("\"");

        // Append remaining fields in insertion order
        for (Map.Entry<String, String> entry : message.getFields().entrySet()) {
            sb.append(",\"").append(escape(entry.getKey()))
              .append("\":\"").append(escape(entry.getValue())).append("\"");
        }

        sb.append("}");
        return sb.toString();
    }

    /* ------------------------------------------------------------------ */
    /*  Deserialization                                                    */
    /* ------------------------------------------------------------------ */

    /**
     * Parses a single-line JSON string back into a {@link Message}.
     *
     * <p>The string must be a flat JSON object where all values are strings
     * (i.e. enclosed in {@code "…"}).  A {@code "type"} field must be present
     * and must match a {@link MessageType} constant; any unrecognised message
     * type will result in an {@link IllegalArgumentException}.</p>
     *
     * @param json the JSON string to parse
     * @return the reconstructed {@link Message}
     * @throws IllegalArgumentException if the JSON is malformed or the type is unknown
     */
    public static Message fromJson(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Empty JSON string");
        }

        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Not a JSON object: " + json);
        }

        // Strip outer braces
        String content = trimmed.substring(1, trimmed.length() - 1).trim();

        Map<String, String> map = parseKeyValues(content);

        String typeStr = map.remove("type");
        if (typeStr == null) {
            throw new IllegalArgumentException("Missing \"type\" field in: " + json);
        }

        MessageType type;
        try {
            type = MessageType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown MessageType: " + typeStr);
        }

        Message.Builder builder = new Message.Builder(type);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.put(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    /* ------------------------------------------------------------------ */
    /*  Private helpers                                                    */
    /* ------------------------------------------------------------------ */

    /**
     * Parses a comma-separated sequence of {@code "key":"value"} pairs into a map.
     *
     * <p>The parser walks the string character-by-character so it correctly handles
     * escaped characters inside string values (e.g. {@code \"} inside a message text).</p>
     *
     * @param content the inner content of a JSON object (without surrounding braces)
     * @return ordered map of key → value strings
     */
    private static Map<String, String> parseKeyValues(String content) {
        Map<String, String> result = new LinkedHashMap<>();
        int i = 0;
        int len = content.length();

        while (i < len) {
            // Skip whitespace and comma separators
            while (i < len && (content.charAt(i) == ',' || Character.isWhitespace(content.charAt(i)))) {
                i++;
            }
            if (i >= len) break;

            // Expect opening quote of key
            if (content.charAt(i) != '"') {
                throw new IllegalArgumentException("Expected '\"' at position " + i + " in: " + content);
            }
            i++; // skip opening quote
            StringBuilder key = new StringBuilder();
            while (i < len && content.charAt(i) != '"') {
                if (content.charAt(i) == '\\' && i + 1 < len) {
                    i++; // skip backslash
                    key.append(unescape(content.charAt(i)));
                } else {
                    key.append(content.charAt(i));
                }
                i++;
            }
            i++; // skip closing quote of key

            // Skip colon and whitespace
            while (i < len && (content.charAt(i) == ':' || Character.isWhitespace(content.charAt(i)))) {
                i++;
            }

            // Expect opening quote of value
            if (i >= len || content.charAt(i) != '"') {
                throw new IllegalArgumentException("Expected '\"' for value at position " + i + " in: " + content);
            }
            i++; // skip opening quote
            StringBuilder value = new StringBuilder();
            while (i < len && content.charAt(i) != '"') {
                if (content.charAt(i) == '\\' && i + 1 < len) {
                    i++; // skip backslash
                    value.append(unescape(content.charAt(i)));
                } else {
                    value.append(content.charAt(i));
                }
                i++;
            }
            i++; // skip closing quote of value

            result.put(key.toString(), value.toString());
        }

        return result;
    }

    /**
     * Escapes special characters in a string value for JSON output.
     *
     * @param s the raw string
     * @return the escaped string
     */
    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Converts an escape-sequence character (the character after {@code \}) back to its
     * literal value.
     *
     * @param c the character following the backslash
     * @return the literal character it represents
     */
    private static char unescape(char c) {
        switch (c) {
            case '"':  return '"';
            case '\\': return '\\';
            case 'n':  return '\n';
            case 'r':  return '\r';
            case 't':  return '\t';
            default:   return c;
        }
    }
}
