package udp;

public class ServerProgram {
	private static final int SERVER_PORT = 8888;
	
    public static void main(String[] args) {        
        var server = new Server(SERVER_PORT);
        server.run();
        System.out.println("Servidor escutando na porta %d".formatted(SERVER_PORT));
    }
}
