package xyz.vanan.copycat.payload;

/**
 * Enum of all supported payload types together with their media type.
 */
public enum PayloadType {
    JPG("image/jpg"), PNG("image/png"), TEXT("text/plain");

    private final String value;

    PayloadType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
