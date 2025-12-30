package io.github.fherbreteau.gatling.ftp.javaapi.action;

import io.gatling.javaapi.core.ActionBuilder;

/**
 * DSL for building FTP commands configurations
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class FtpActionBuilder implements ActionBuilder {

    private final io.github.fherbreteau.gatling.ftp.action.FtpActionBuilder wrapped;

    public FtpActionBuilder(io.github.fherbreteau.gatling.ftp.action.FtpActionBuilder wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped;
    }
}
