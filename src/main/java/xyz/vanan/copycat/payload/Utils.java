package xyz.vanan.copycat.payload;

/**
 * Utility methods for interacting with payloads.
 */
public final class Utils {
    private Utils() {
    }

    /**
     * Determines the payload type based on the content.
     * @param data content
     * @return {@link PayloadType}
     */
    public static PayloadType getPayloadType(byte[] data) {
        // jpg starts with FF D8 FF
        if (startsWith(data, 0xFF, 0xD8, 0xFF)) {
            return PayloadType.JPG;
        }

        // png starts with 89 50 4E 47 0D 0A 1A 0A
        if (startsWith(data, 0x89, 0x50, 0x4E, 0x47,  0x0D, 0x0A, 0x1A, 0x0A)) {
            return PayloadType.PNG;
        }

        // assume text otherwise
        return PayloadType.TEXT;
    }

    /**
     * Checks whether the data start with given bytes
     * @param data data
     * @param bytes bytes
     * @return true/false
     */
    private static boolean startsWith(byte[] data, int... bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (data[i] != (byte) bytes[i]) {
                return false;
            }
        }
        return true;
    }
}
