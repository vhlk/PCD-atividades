package udp;

import java.util.Scanner;

public class ServerProgram {
    public static void main(String[] args) {
        System.out.println("Escolha uma porta para o servidor executar");
    
        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();
        scanner.close();
        
        var server = new Server(port);
        server.run();
        System.out.printf("Servidor escutando na porta %d", port);
    }
}
