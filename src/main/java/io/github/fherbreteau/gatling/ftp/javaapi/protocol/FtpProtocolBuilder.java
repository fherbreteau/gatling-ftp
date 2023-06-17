package io.github.fherbreteau.gatling.ftp.javaapi.protocol;

import io.gatling.core.protocol.Protocol;
import io.gatling.javaapi.core.ProtocolBuilder;

import java.nio.file.Path;

public class FtpProtocolBuilder implements ProtocolBuilder {

    private final io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder wrapped;

    public FtpProtocolBuilder(io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder wrapped) {
        this.wrapped = wrapped;
    }

    public FtpProtocolBuilder server(String server) {
        return new FtpProtocolBuilder(wrapped.server(server));
    }

    public FtpProtocolBuilder port(int port) {
        return new FtpProtocolBuilder(wrapped.port(port));
    }

    public FtpProtocolBuilder credentials(String username, String password) {
        return new FtpProtocolBuilder(wrapped.credentials(username, password));
    }

    public FtpProtocolBuilder localPath(Path path) {
        return localSourcePath(path).localDestinationPath(path);
    }

    public FtpProtocolBuilder localSourcePath(Path sourcePath) {
        return new FtpProtocolBuilder(wrapped.localSourcePath(sourcePath));
    }

    public FtpProtocolBuilder localDestinationPath(Path destpath) {
        return new FtpProtocolBuilder(wrapped.localDestinationPath(destpath));
    }

    public FtpProtocolBuilder remotePath(String path) {
        return remoteSourcePath(path).remoteDestinationPath(path);
    }

    public FtpProtocolBuilder remoteSourcePath(String sourcePath) {
        return new FtpProtocolBuilder(wrapped.remoteSourcePath(sourcePath));
    }

    public FtpProtocolBuilder remoteDestinationPath(String destpath) {
        return new FtpProtocolBuilder(wrapped.remoteDestinationPath(destpath));
    }

    @Override
    public Protocol protocol() {
        return wrapped.protocol();
    }
}
