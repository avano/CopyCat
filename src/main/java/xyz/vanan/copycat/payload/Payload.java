package xyz.vanan.copycat.payload;

import java.time.Instant;
import java.util.Arrays;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

/**
 * A class that represents one entry in the database.
 */
@Entity
@NamedQuery(name = "list", query = "SELECT p FROM Payload p ORDER BY p.id DESC LIMIT ?1")
public class Payload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private PayloadType mediaType;
    private byte[] data;
    private Instant createdAt;

    public Payload() {
    }

    public Payload(byte[] data) {
        this.mediaType = Utils.getPayloadType(data);
        this.data = data;
        this.createdAt = Instant.now();
    }

    public Payload(long id, byte[] data) {
        this.id = id;
        this.data = data;
        this.mediaType = Utils.getPayloadType(data);
        this.createdAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public PayloadType getMediaType() {
        return mediaType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Payload payload = (Payload) o;
        return id == payload.id && Arrays.equals(data, payload.data) && mediaType.equals(payload.mediaType) && createdAt.equals(
            payload.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Arrays.hashCode(data);
        result = 31 * result + mediaType.hashCode();
        result = 31 * result + createdAt.hashCode();
        return result;
    }
}
