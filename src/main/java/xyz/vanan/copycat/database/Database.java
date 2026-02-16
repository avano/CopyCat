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
import java.util.List;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.TransactionScoped;
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

    /**
     * Persists the payload.
     * @param payload payload to persist
     */
    @Transactional
    public void persist(Payload payload) {
        em.persist(payload);
    }

    /**
     * After each insert into the database - when the transaction is done - persist the state to the filesystem.
     *
     * @param ignored not used
     */
    void persistToFilesystem(@Observes @Destroyed(TransactionScoped.class) Object ignored) {
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
     * @param count result count
     * @return list of payload instances
     */
    public List<Payload> list(int count) {
        return em.createNamedQuery("list", Payload.class).setParameter(1, count).getResultList();
    }

    /**
     * Gets the last payload from the database or null when the database is empty.
     * @return the last payload or null
     */
    public Payload getLast() {
        final List<Payload> list = list(1);
        return list.isEmpty() ? null : list.getFirst();
    }
}
