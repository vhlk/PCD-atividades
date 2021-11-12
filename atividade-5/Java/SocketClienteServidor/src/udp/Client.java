package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        
        String host = "localhost";

        System.out.println("Digite uma porta para se comunicar");

        Scanner scanner = new Scanner(System.in);
        int port = scanner.nextInt();

        try {
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket socket = new DatagramSocket();
            
            System.out.println("Digite 'quit' a qualquer momento para encerrar a conversa");

            while (true) {
                System.out.println("Digite uma mensagem para enviar:");
                String message = scanner.nextLine();

                if(message.contains("quit")){
                    break;
                }

                byte[] messageToByte = message.getBytes();
            
                DatagramPacket request = new DatagramPacket(messageToByte, messageToByte.length, address, port);
                socket.send(request);
            }
            socket.close();

        } catch (Exception e) {
            System.out.println("Um erro ocorreu: %d".formatted(e.getMessage()));
            scanner.close();
        }
    }
}
