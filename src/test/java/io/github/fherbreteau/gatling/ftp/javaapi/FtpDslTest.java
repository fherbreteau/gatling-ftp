package io.github.fherbreteau.gatling.ftp.javaapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import io.gatling.commons.validation.Success;
import io.gatling.commons.validation.Validation;
import io.gatling.javaapi.core.Session;
import io.github.fherbreteau.gatling.ftp.javaapi.action.FtpActionBuilder;
import org.junit.jupiter.api.Test;
import scala.Function1;

class FtpDslTest {

    private final Function1<io.gatling.core.session.Session, Validation<String>> operationName = s -> new Success<>("test-op");
    private final Ftp ftpDsl = new Ftp(operationName);
    private final Function<Session, String> fct = s -> "dir";

    @Test
    void shouldCreateActionBuilderForLsWithString() {
        FtpActionBuilder builder = ftpDsl.ls("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForLsWithFunction() {
        FtpActionBuilder builder = ftpDsl.ls(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForChDirWithString() {
        FtpActionBuilder builder = ftpDsl.chdir("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForChDirWithFunction() {
        FtpActionBuilder builder = ftpDsl.chdir(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMkDirWithString() {
        FtpActionBuilder builder = ftpDsl.mkdir("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMkDirWithFunction() {
        FtpActionBuilder builder = ftpDsl.mkdir(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMoveWithString() {
        FtpActionBuilder builder = ftpDsl.move("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForMoveWithFunction() {
        FtpActionBuilder builder = ftpDsl.move(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForCopyWithString() {
        FtpActionBuilder builder = ftpDsl.copy("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForCopyWithFunction() {
        FtpActionBuilder builder = ftpDsl.copy(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadWithString() {
        FtpActionBuilder builder = ftpDsl.upload("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadWithFunction() {
        FtpActionBuilder builder = ftpDsl.upload(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadSrcDestWithString() {
        FtpActionBuilder builder = ftpDsl.upload("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForUploadSrcDestWithFunction() {
        FtpActionBuilder builder = ftpDsl.upload(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadWithString() {
        FtpActionBuilder builder = ftpDsl.download("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadWithFunction() {
        FtpActionBuilder builder = ftpDsl.download(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadSrcDestWithString() {
        FtpActionBuilder builder = ftpDsl.download("dir", "dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDownloadSrcDestWithFunction() {
        FtpActionBuilder builder = ftpDsl.download(fct, fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDeleteWithString() {
        FtpActionBuilder builder = ftpDsl.delete("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForDeleteWithFunction() {
        FtpActionBuilder builder = ftpDsl.delete(fct);
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForRmDirWithString() {
        FtpActionBuilder builder = ftpDsl.rmdir("dir");
        assertThat(builder.asScala()).isNotNull();
    }

    @Test
    void shouldCreateActionBuilderForRmDirWithFunction() {
        FtpActionBuilder builder = ftpDsl.rmdir(fct);
        assertThat(builder.asScala()).isNotNull();
    }
}
