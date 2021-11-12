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
            System.out.println("Servidor executando na porta %d, aguardando mensagens...".formatted(this.port));
            while(true) {
                DatagramPacket request = new DatagramPacket(new byte[512], 512);
                socket.receive(request);
                
                checkUserForAdd(request.getAddress(), port);
    
                String receivedMessage = new String(request.getData(), "UTF-8");
                String message = String.format("Porta %d enviou: %s", request.getPort(), receivedMessage);
                System.out.println(message);

                byte[] messageToSend = message.getBytes();

                for (User user : this.activedUser) {
                    DatagramPacket response = new DatagramPacket(messageToSend, messageToSend.length, user.getUserAdress(), user.getUserPort());
                    socket.send(response);
                }

                Thread.sleep(1000);
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
