package io.github.fherbreteau.gatling.ftp.javaapi;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import io.gatling.javaapi.core.internal.Expressions;
import io.github.fherbreteau.gatling.ftp.javaapi.action.FtpActionBuilder;
import scala.Function1;

import jakarta.annotation.Nonnull;
import java.util.function.Function;

/**
 * DSL for bootstrapping FTP command.
 *
 * <p>Immutable, so all methods return a new occurrence and leave the original unmodified.
 */
public class Ftp {

    private final Function1<Session, Validation<String>> name;

    Ftp(Function1<io.gatling.core.session.Session, Validation<String>> name) {
        this.name = name;
    }

    /**
     * Define a mkdir command.
     * @param directory the directory to create, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder mkdir(@Nonnull String directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).mkdir(Expressions.toStringExpression(directory)));
    }

    /**
     * Define a mkdir command.
     * @param directory the directory to create, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder mkdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).mkdir(Expressions.javaFunctionToExpression(directory)));
    }

    /**
     * Define a move command.
     * @param source the source to move, expressed as a Gatling Expression Language String
     * @param destination the destination, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder move(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a move command.
     * @param source the source to move, expressed as a function
     * @param destination the destination, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder move(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).move(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a copy command.
     * @param source the source to move, expressed as a Gatling Expression Language String
     * @param destination the destination, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder copy(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a copy command.
     * @param source the source to move, expressed as a function
     * @param destination the destination, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder copy(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                  @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).copy(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define an upload command.
     * @param file the file to upload, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder upload(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.toStringExpression(file)));
    }

    /**
     * Define an upload command.
     * @param file the file to upload, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define an upload command.
     * @param source the source to upload, expressed as a Gatling Expression Language String
     * @param destination the destination to upload to, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder upload(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define an upload command.
     * @param source the source to upload, expressed as a function
     * @param destination the destination to upload to, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder upload(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                    @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).upload(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a download command.
     * @param file the file to download, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder download(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.toStringExpression(file)));
    }

    /**
     * Define a download command.
     * @param file the file to download, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define a download command.
     * @param source the source to download, expressed as a Gatling Expression Language String
     * @param destination the destination to download to, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder download(@Nonnull String source, @Nonnull String destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.toStringExpression(source), Expressions.toStringExpression(destination)));
    }

    /**
     * Define a download command.
     * @param source the source to download, expressed as a function
     * @param destination the destination to download to, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder download(@Nonnull Function<io.gatling.javaapi.core.Session, String> source,
                                      @Nonnull Function<io.gatling.javaapi.core.Session, String> destination) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).download(Expressions.javaFunctionToExpression(source), Expressions.javaFunctionToExpression(destination)));
    }

    /**
     * Define a delete command.
     * @param file the file to delete, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder delete(@Nonnull String file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.toStringExpression(file)));
    }

    /**
     * Define a delete command.
     * @param file the file to delete, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder delete(@Nonnull Function<io.gatling.javaapi.core.Session, String> file) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).delete(Expressions.javaFunctionToExpression(file)));
    }

    /**
     * Define a rmdir command.
     * @param directory the directory to delete, expressed as a Gatling Expression Language String
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder rmdir(@Nonnull String directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).rmdir(Expressions.toStringExpression(directory)));
    }

    /**
     * Define a rmdir command.
     * @param directory the directory to delete, expressed as a function
     * @return a new instance of SftpActionBuilder
     */
    public FtpActionBuilder rmdir(@Nonnull Function<io.gatling.javaapi.core.Session, String> directory) {
        return new FtpActionBuilder(new io.github.fherbreteau.gatling.ftp.Ftp(name).rmdir(Expressions.javaFunctionToExpression(directory)));
    }
}
