package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server extends Thread{

    private DatagramSocket socket;
    private final int port; 
    private ArrayList<User> activedUser;

    public Server(int port) {
        this.port = port;
        activedUser = new ArrayList<>();
    }

    @Override
    public void run() {

        try {
            this.socket = new DatagramSocket(this.port);
            byte[] buffer = new byte[512];
            System.out.println("Servidor executando na porta %d, aguardando mensagens...".formatted(this.port));
            DatagramPacket request;

            while(true) {
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                
                checkUserForAdd(request.getAddress(), port);
    
                String receivedMessage = new String(request.getData(), "UTF-8");
                String numeros[] = receivedMessage.split(" ");
                int primeiroNum = Integer.parseInt(numeros[0]);
                int segundoNum = Integer.parseInt(numeros[1].trim());
                System.out.println("Recebido: "+primeiroNum+"+"+segundoNum);

                byte[] messageToSend = ("Soma = "+(primeiroNum+segundoNum)).getBytes();
                DatagramPacket response = new DatagramPacket(messageToSend, messageToSend.length, request.getAddress(), request.getPort());
                socket.send(response);

                //sleep(1000);
                buffer = new byte[512];
            }
        } catch (Exception e) {
            System.out.println("Error ao inciar o servidor: %s".formatted(e.getMessage()));
            return;
        }
    }

    private void checkUserForAdd(InetAddress address, int port){
        
        for (User user : this.activedUser) {
            if(user.getUserAdress() == address && user.getUserPort() == port){
                return;
            }
        }
        this.activedUser.add(new User(address, port));
    }
}
