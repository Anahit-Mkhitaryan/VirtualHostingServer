package main.java.com;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class AdminHandler implements HttpHandler {
    private static final String HOSTS_FILE_PATH_WINDOWS = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    private static final String HOSTS_FILE_PATH_UNIX = "/etc/hosts";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            handlePostRequest(exchange);
        } else {
            handleGetRequest(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        VirtualHostManager manager = VirtualHostManager.getInstance();
        StringBuilder response = new StringBuilder();

        response.append("<html><body><h1>Admin Interface</h1>")
                .append("<form method='post'>")
                .append("Domain: <input type='text' name='domain'><br>")
                .append("Directory: <input type='text' name='directory'><br>")
                .append("<input type='submit' value='Add'>")
                .append("</form>")
                .append("<h2>Current Hosts</h2><ul>");

        for (Map.Entry<String, String> entry : manager.getVirtualHosts().entrySet()) {
            response.append("<li>").append(entry.getKey()).append(" -> ").append(entry.getValue())
                    .append(" <form method='post' style='display:inline;'>")
                    .append("<input type='hidden' name='remove' value='").append(entry.getKey()).append("'>")
                    .append("<input type='submit' value='Remove'>")
                    .append("</form>")
                    .append("</li>");
        }

        response.append("</ul></body></html>");

        byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String query = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String[] params = query.split("&");
        String domain = null;
        String directory = null;
        String remove = null;

        for (String param : params) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                if ("domain".equals(pair[0])) {
                    domain = pair[1];
                } else if ("directory".equals(pair[0])) {
                    directory = pair[1];
                } else if ("remove".equals(pair[0])) {
                    remove = pair[1];
                }
            }
        }

        if (remove != null) {
            VirtualHostManager.getInstance().removeVirtualHost(remove);
            removeHostEntry(remove);
        } else if (domain != null && directory != null) {
            String ip = IPUtils.getLocalIPAddress(); // Automatically get the local IP address
            VirtualHostManager.getInstance().addVirtualHost(domain, directory);
            addHostEntry(domain, ip);
        }

        // Redirect to the admin page to display the updated host list
        exchange.getResponseHeaders().set("Location", "/admin");
        exchange.sendResponseHeaders(302, -1);
    }

    private void addHostEntry(String domain, String ip) throws IOException {
        String entry = ip + "   " + domain;
        String hostsFilePath = getHostsFilePath();
        Path path = Paths.get(hostsFilePath);
        List<String> lines = Files.readAllLines(path);
        if (lines.stream().noneMatch(line -> line.contains(domain))) {
            Files.write(path, (entry + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
            System.out.println("Added entry: " + entry);
        } else {
            System.out.println("Entry already exists: " + entry);
        }
    }

    private void removeHostEntry(String domain) throws IOException {
        String hostsFilePath = getHostsFilePath();
        Path path = Paths.get(hostsFilePath);
        List<String> lines = Files.readAllLines(path);
        List<String> updatedLines = lines.stream().filter(line -> !line.contains(domain)).toList();
        Files.write(path, updatedLines);
        System.out.println("Removed entry for domain: " + domain);
    }

    private String getHostsFilePath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return HOSTS_FILE_PATH_WINDOWS;
        } else {
            return HOSTS_FILE_PATH_UNIX;
        }
    }
}
