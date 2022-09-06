package com.github.fherbreteau.gatling.ftp.javaapi;

import com.github.fherbreteau.gatling.ftp.javaapi.action.FtpActionBuilder;
import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import scala.Function1;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class Ftp {

    private final Function1<Session, Validation<String>> name;

    Ftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    public FtpActionBuilder move(@Nonnull String file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder copy(@Nonnull String file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder delete(@Nonnull String file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder upload(@Nonnull String file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder download(@Nonnull String file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new com.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.javaFunctionToExpression(file)));
    }
}
