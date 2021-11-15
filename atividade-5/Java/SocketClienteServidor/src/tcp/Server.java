package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int SERVER_PORT = 8888; 
	private static boolean stopServer = false;
	protected static final PrintStream cOut = System.err;

	public static void main(String[] args) {
		ServerSocket sSocket = null;
		try {
			sSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) { 
			cOut.println("Nao foi possivel iniciar o servidor :( " + e.getMessage());	
			System.exit(-1);
		}
	
		while (true && !stopServer) {
			Socket cSocket = null;
			try {
				cSocket = sSocket.accept();
			} catch (IOException e) { cOut.println("Erro ao aceitar cliente :( " + e.getMessage()); }
			
			new AcceptClientThread(cSocket).start();
		}
		
		try {
			sSocket.close();
		} catch (IOException ignored) { }
	}

}

class AcceptClientThread extends Thread {
	private Socket socket;
	
	public AcceptClientThread(Socket sck) {
		this.socket = sck;
	}
	
	public void run() {
		InputStream is = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			pw = new PrintWriter(socket.getOutputStream());			
		} catch (IOException e) {
			System.err.println("N�o foi poss�vel pegar as informa��es sobre o cliente :( "+e.getMessage());
			return;
		}
		
		String data = "";
		
		while (true) {
			try {
				data = br.readLine();
				
				if (data == null || data == "") {
					pw.println("Erro!");
					continue;
				}
				
				System.out.println("Recebido: "+data);
				
				if (data.toLowerCase() == "q") return;
				
				pw.println("Voc� digitou: "+data);
				pw.flush();
			} catch (IOException e) {
				System.err.println("Erro na transmiss�o dos dados :(");
				return;
			}
		}
	}
}
