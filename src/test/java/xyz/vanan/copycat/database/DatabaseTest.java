package xyz.vanan.copycat.database;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import xyz.vanan.copycat.config.Config;
import xyz.vanan.copycat.payload.Payload;

@QuarkusTest
public class DatabaseTest {
    @Inject
    Config config;

    @Inject
    EntityManager em;

    @Inject
    Database db;

    @BeforeEach
    @Transactional
    public void cleanup() {
        em.createQuery("DELETE FROM Payload").executeUpdate();
    }

    @Test
    public void getLastShouldReturnNullWhenDbIsEmptyTest() {
        assertThat(db.getLast()).isNull();
    }

    @Test
    public void getLastShouldReturnLastRecordTest() {
        Payload p1 = new Payload("Hello world".getBytes());
        db.persist(p1);
        Payload p2 = new Payload("World hello".getBytes());
        db.persist(p2);
        assertThat(db.getLast().getData()).isEqualTo(p2.getData());
    }

    @Test
    public void shouldPersistToFileSystemTest() {
        Path dbFile = Paths.get(config.databaseFile());
        assertThat(dbFile).exists();
        // truncate the file
        try (FileOutputStream fos = new FileOutputStream(dbFile.toFile())) {
        } catch (IOException e) {
            throw new RuntimeException("Unable to truncate file", e);
        }
        assertThat(dbFile.toFile().length()).isEqualTo(0);
        db.persist(new Payload("Hello world".getBytes()));
        db.persistToFilesystem(null);
        assertThat(dbFile.toFile().length()).isGreaterThan(0);
    }

    @Test
    public void purgeShouldRemoveOldEntriesTest() {
        Instant old = Instant.now().minus(10, ChronoUnit.MINUTES);
        Payload p1 = new Payload("Hello world".getBytes(), old);
        db.persist(p1);
        Payload p2 = new Payload("World hello".getBytes(), old);
        db.persist(p2);

        db.purge();

        assertThat(db.getLast()).isNull();
    }

    @Test
    public void purgeShouldRetainNewerEntriesTest() {
        Instant future = Instant.now().plus(10, ChronoUnit.MINUTES);
        String body = "Hello world";
        Payload p1 = new Payload(body.getBytes(), future);
        db.persist(p1);

        db.purge();

        assertThat(db.getLast().getData()).isEqualTo(body.getBytes());
    }
}
