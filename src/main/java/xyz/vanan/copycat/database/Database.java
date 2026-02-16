package xyz.vanan.copycat.database;

import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.configuration.DurationConverter;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import xyz.vanan.copycat.config.Config;
import xyz.vanan.copycat.payload.Payload;

/**
 * A bean to interract with the underlying database.
 */
@ApplicationScoped
public class Database {
    private static final Logger LOG = Logger.getLogger(Database.class);

    @Inject
    AgroalDataSource ds;

    @Inject
    Config config;

    @Inject
    EntityManager em;

    @Inject
    Event<FileSystemSyncEvent> event;

    /**
     * Persists the payload.
     *
     * @param payload payload to persist
     */
    @Transactional
    public void persist(Payload payload) {
        em.persist(payload);
        event.fire(new FileSystemSyncEvent());
    }

    /**
     * Persists the database state to the filesystem after each modification.
     *
     * @param ignored not used
     */
    void persistToFilesystem(@Observes(during = TransactionPhase.AFTER_SUCCESS) FileSystemSyncEvent ignored) {
        try (Connection conn = ds.getConnection(); Statement statement = conn.createStatement()) {
            final Path backup = Paths.get("backup.db").toAbsolutePath();
            // Execute the backup
            statement.executeUpdate("backup to " + backup);
            // Atomically replace the DB file with its backup
            Files.move(backup, Paths.get(config.databaseFile()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            LOG.tracef("Persisted database state to %s", backup);
        } catch (IOException | SQLException e) {
            LOG.warn("Unable to persist the DB state to filesystem", e);
        }
    }

    /**
     * Returns the last N elements from the database.
     *
     * @param count result count
     * @return list of payload instances
     */
    public List<Payload> list(int count) {
        return em.createNamedQuery("list", Payload.class).setParameter(1, count).getResultList();
    }

    /**
     * Gets the last payload from the database or null when the database is empty.
     *
     * @return the last payload or null
     */
    public Payload getLast() {
        final List<Payload> list = list(1);
        return list.isEmpty() ? null : list.getFirst();
    }

    @Scheduled(every = "{purge-interval}")
    @Transactional
    void purge() {
        Instant timestamp = Instant.now().minus(DurationConverter.parseDuration(config.purgeInterval()));
        LOG.debugf("Purging entries older than %s", config.purgeInterval());
        final int removed = em.createNamedQuery("purge").setParameter(1, timestamp).executeUpdate();
        if (removed > 0) {
            LOG.tracef("Deleted %d %s", removed, removed == 1 ? "entry" : "entries");
            event.fire(new FileSystemSyncEvent());
        }
    }
}
