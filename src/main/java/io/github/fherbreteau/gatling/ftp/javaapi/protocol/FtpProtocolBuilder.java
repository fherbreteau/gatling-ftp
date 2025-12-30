package io.github.fherbreteau.gatling.ftp.javaapi.protocol;

import static io.gatling.javaapi.core.internal.Expressions.toStringExpression;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;

import java.nio.file.Path;

/**
 * DSL for building FTP protocol configurations
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class FtpProtocolBuilder implements ProtocolBuilder {

    private final io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder wrapped;

    public FtpProtocolBuilder(io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Define the server that will be used for all command.
     * @param server the address of the sftp server
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder server(String server) {
        return new FtpProtocolBuilder(wrapped.server(server));
    }

    /**
     * Define the port that will be used for all command.
     * @param port the port of the sftp server
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder port(int port) {
        return new FtpProtocolBuilder(wrapped.port(port));
    }

    /**
     * Define an authentication using username and password.
     * @param username the name of the user
     * @param password the password of the user
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder credentials(String username, String password) {
        return new FtpProtocolBuilder(wrapped.credentials(toStringExpression(username), toStringExpression(password)));
    }

    /**
     * Define the activation of the passive mode.
     * @param passive the passive mode activation
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder passiveMode(boolean passive) {
        return new FtpProtocolBuilder(wrapped.passiveMode(passive));
    }

    /**
     * Define the activation of the logging of the protocol.
     * @param enable the logging activation
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder protocolLogging(boolean enable) {
        return new FtpProtocolBuilder(wrapped.protocolLogging(enable));
    }

    /**
     * Define the local path to be used. Used as local source path and local destination path if not defined.
     * @param path the local path
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder localPath(Path path) {
        return localSourcePath(path).localDestinationPath(path);
    }

    /**
     * Define the local path for the sources to be used.
     * @param sourcePath the local path for the source
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder localSourcePath(Path sourcePath) {
        return new FtpProtocolBuilder(wrapped.localSourcePath(sourcePath));
    }

    /**
     * Define the local path for the destinations to be used.
     * @param destinationPath the local path for the destination
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder localDestinationPath(Path destinationPath) {
        return new FtpProtocolBuilder(wrapped.localDestinationPath(destinationPath));
    }

    /**
     * Define the remote path to be used. Used as remote source path and remote destination path if not defined.
     * @param path the local path
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder remotePath(String path) {
        return remoteSourcePath(path).remoteDestinationPath(path);
    }

    /**
     * Define the remote path for the sources to be used.
     * @param sourcePath the remote path for the source
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder remoteSourcePath(String sourcePath) {
        return new FtpProtocolBuilder(wrapped.remoteSourcePath(sourcePath));
    }

    /**
     * Define the remote path for the destinations to be used.
     * @param destinationPath the remote path for the destination
     * @return a new HttpProtocolBuilder instance
     */
    public FtpProtocolBuilder remoteDestinationPath(String destinationPath) {
        return new FtpProtocolBuilder(wrapped.remoteDestinationPath(destinationPath));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Protocol protocol() {
        return wrapped.protocol();
    }
}
