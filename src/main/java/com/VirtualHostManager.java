package main.java.com;

import java.util.HashMap;
import java.util.Map;

public class VirtualHostManager {
    private static VirtualHostManager instance;
    private final Map<String, String> virtualHosts;

    private VirtualHostManager() {
        virtualHosts = new HashMap<>();
    }

    public static synchronized VirtualHostManager getInstance() {
        if (instance == null) {
            instance = new VirtualHostManager();
        }
        return instance;
    }

    public Map<String, String> getVirtualHosts() {
        return virtualHosts;
    }

    public void addVirtualHost(String domain, String directory) {
        virtualHosts.put(domain, directory);
    }

    public void removeVirtualHost(String domain) {
        virtualHosts.remove(domain);
    }
}
