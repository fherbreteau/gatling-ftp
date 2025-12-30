package io.github.fherbreteau.gatling.ftp.javaapi;

import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.ftp.javaapi.protocol.FtpProtocolBuilder;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

/**
 * The entrypoint of the Gatling FTP DSL
 */
public class FtpDsl {

    /**
     * Bootstrap a FTP protocol configuration
     */
    public static final FtpProtocolBuilder ftp =
            new FtpProtocolBuilder(io.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder.apply(
                    io.gatling.core.Predef.configuration()));

    private FtpDsl() {
    }

    /**
     * Bootstrap an FTP command configuration
     *
     * @param name the FTP command name, expressed as a Gatling Expression Language String
     * @return the next DSL step
     */
    @Nonnull
    public static Ftp ftp(@Nonnull String name) {
        return new Ftp(Expressions.toStringExpression(name));
    }

    /**
     * Bootstrap an FTP command configuration
     *
     * @param name the FTP command name, expressed as a function
     * @return the next DSL step
     */
    @Nonnull
    public static Ftp ftp(@Nonnull Function<Session, String> name) {
        return new Ftp(Expressions.javaFunctionToExpression(name));
    }

}
