package xyz.vanan.copycat.config;

import java.util.Optional;

import io.smallrye.config.ConfigMapping;

@ConfigMapping
public interface Config {
    boolean useToken();

    Optional<String> token();

    String databaseFile();

    int resultCount();

    String purgeInterval();
}
