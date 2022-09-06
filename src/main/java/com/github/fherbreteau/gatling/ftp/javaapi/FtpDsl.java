package com.github.fherbreteau.gatling.ftp.javaapi;

import com.github.fherbreteau.gatling.ftp.javaapi.protocol.FtpProtocolBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.core.internal.Expressions;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class FtpDsl {

    public static final FtpProtocolBuilder ftp =
            new FtpProtocolBuilder(com.github.fherbreteau.gatling.ftp.protocol.FtpProtocolBuilder.apply(
                    io.gatling.core.Predef.configuration()));

    private FtpDsl() {
    }

    @Nonnull
    public static Ftp sftp(@Nonnull String name) {
        return new Ftp(Expressions.toStringExpression(name));
    }

    @Nonnull
    public static Ftp sftp(@Nonnull Function<Session, String> name) {
        return new Ftp(Expressions.javaFunctionToExpression(name));
    }

}
