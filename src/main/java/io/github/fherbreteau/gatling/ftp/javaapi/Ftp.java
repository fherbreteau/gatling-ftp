package io.github.fherbreteau.gatling.ftp.javaapi;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.ftp.javaapi.action.FtpActionBuilder;
import scala.Function1;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

public class Ftp {

    private final Function1<Session, Validation<String>> name;

    Ftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    public FtpActionBuilder mkdir(@Nonnull String directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).mkdir(Expressions.toStringExpression(directory)));
    }

    public FtpActionBuilder mkdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).mkdir(Expressions.javaFunctionToExpression(directory)));
    }

    public FtpActionBuilder move(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public FtpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public FtpActionBuilder copy(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public FtpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public FtpActionBuilder upload(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder upload(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public FtpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                    @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public FtpActionBuilder download(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder download(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    public FtpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                      @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    public FtpActionBuilder delete(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.toStringExpression(file)));
    }

    public FtpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    public FtpActionBuilder rmdir(@Nonnull String directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).rmdir(Expressions.toStringExpression(directory)));
    }

    public FtpActionBuilder rmdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).rmdir(Expressions.javaFunctionToExpression(directory)));
    }
}
