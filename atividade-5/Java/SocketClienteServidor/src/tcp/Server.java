package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int SERVER_PORT = 8888; 
	public static boolean stopServer = false;
	protected static final PrintStream cOut = System.err;

	public static void main(String[] args) {
		ServerSocket sSocket = null;

		try {			
			sSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			cOut.println("Erro ao iniciar servidor :( " + e.getMessage());
		}

		while (!stopServer) {
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
			System.err.println("Nao foi possivel pegar as informacoes sobre o cliente :( "+e.getMessage());
			return;
		}
		
		try {
			int primeiroNum = Integer.parseInt(br.readLine());
			int segundoNum = Integer.parseInt(br.readLine());
			
			// System.out.println("Recebido: "+primeiroNum+"+"+segundoNum);
			
			pw.println("Soma = "+(primeiroNum+segundoNum));
			pw.flush();
		} catch (IOException e) {
			System.err.println("Erro na transmissao dos dados :(");
			return;
		}
	}
}
