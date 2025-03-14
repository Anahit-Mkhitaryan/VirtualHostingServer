package main.java.com;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String host = exchange.getRequestHeaders().getFirst("Host");
        String response = "";

        if (host != null) {
            VirtualHostManager manager = VirtualHostManager.getInstance();
            String directory = manager.getVirtualHosts().get(host);

            if (directory != null) {
                String filePath = directory + "/index.html";
                if (Files.exists(Paths.get(filePath))) {
                    response = new String(Files.readAllBytes(Paths.get(filePath)));
                } else {
                    response = "<h1>404 Not Found</h1>";
                }
            } else {
                response = "<h1>Unknown Host</h1>";
            }
        } else {
            response = "<h1>No Host Provided</h1>";
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}


