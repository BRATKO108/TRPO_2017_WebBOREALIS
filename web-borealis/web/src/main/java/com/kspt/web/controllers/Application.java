package com.kspt.web.controllers;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Application {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new WebAppContext("web/src/main/webapp", "/"));
        server.start();
    }
}
