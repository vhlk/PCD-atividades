package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	private static final int SERVER_PORT = 8888;
	protected static final PrintStream cout = System.out;

	public static void main(String[] args) {
		Socket socket = null; 
		try {
			socket = new Socket("localhost", SERVER_PORT);
		} catch (Exception e) {
			System.err.println("Erro ao se conectar: "+e.getMessage());
			return;
		}
		
		PrintWriter pw = null;
		try {
			Scanner in = new Scanner(System.in);
			cout.println("Digite o primeiro numero:");
			int primeiroNum = in.nextInt(); in.nextLine();
			cout.println("Digite o segundo numero:");
			int segundoNum = in.nextInt(); in.nextLine();
			pw = new PrintWriter(socket.getOutputStream());
			pw.println(primeiroNum);
			pw.println(segundoNum);
			pw.flush();
			in.close();
			
		} catch (IOException e) {	
			System.err.println("Erro ao enviar a mensagem! "+e.getMessage());
		}
		
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			System.out.println(br.readLine());
		} catch (IOException e) { 
			System.err.println("Erro ao receber mensagem! "+e.getMessage());
		}
		
		try {
			socket.close();
			pw.close();
			br.close();
		} catch (IOException ignored) { }
		
	}
	
	public static void runWithouUserInteraction() throws IOException {
		Socket socket = new Socket("localhost", SERVER_PORT);
		
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.println(1);
		pw.println(2);
		pw.flush();
		
		InputStream is = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		br.readLine();
		
		socket.setSoLinger(true, 0);
		socket.close();
		pw.close();
		br.close();
	}

}
