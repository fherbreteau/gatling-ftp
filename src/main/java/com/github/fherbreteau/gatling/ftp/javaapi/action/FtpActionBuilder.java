package com.github.fherbreteau.gatling.ftp.javaapi.action;

import io.gatling.javaapi.core.ActionBuilder;

public class FtpActionBuilder implements ActionBuilder {

    private final com.github.fherbreteau.gatling.ftp.action.FtpActionBuilder wrapped;

    public FtpActionBuilder(com.github.fherbreteau.gatling.ftp.action.FtpActionBuilder wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return wrapped;
    }
}
