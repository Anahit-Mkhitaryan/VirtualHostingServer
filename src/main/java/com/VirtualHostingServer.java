package main.java.com;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class VirtualHostingServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        server.createContext("/", new StaticFileHandler());
        server.createContext("/admin", new AdminHandler());

        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Server started on port 8081");
    }
}

