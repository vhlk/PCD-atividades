package udp;

import java.net.InetAddress;

public class User {
    private final InetAddress address;
    private final int port;
    
    public User(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getUserAdress() { return this.address; }

    public int getUserPort() { return this.port; }
}
