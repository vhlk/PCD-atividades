package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	private static final int SERVER_PORT = 8888;
	
    public static void main(String[] args) {
        
        String host = "localhost";
        
        Scanner in = new Scanner(System.in);

        try {
            InetAddress address = InetAddress.getByName(host);
            DatagramSocket socket = new DatagramSocket();

            while (true) {
            	System.out.println("Digite o primeiro numero ou quit para finalizar:");
            	String intent = in.nextLine();
            	if (intent.equals("q") || intent.equals("quit")) break;
            	
    			int primeiroNum = Integer.parseInt(intent);
    			System.out.println("Digite o segundo numero:");
    			int segundoNum = in.nextInt(); in.nextLine();

                byte[] messageToByte = (primeiroNum+" "+segundoNum).getBytes();
            
                DatagramPacket request = new DatagramPacket(messageToByte, messageToByte.length, address, SERVER_PORT);
                socket.send(request);
                
                byte[] buffer = new byte[512];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                
                System.out.println(new String(buffer));
            }
            socket.close();

        } catch (Exception e) {
            System.out.println("Um erro ocorreu: %d".formatted(e.getMessage()));
            in.close();
        }
    }
}
