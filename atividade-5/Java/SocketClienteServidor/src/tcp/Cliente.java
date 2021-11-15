package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
	private static final int SERVER_PORT = 8888;

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
			pw = new PrintWriter(socket.getOutputStream());
			pw.println("abcdef");
			pw.flush();
			
		} catch (IOException e) {	
			System.err.println("Erro ao enviar a mensagem! "+e.getMessage());
			return;
		}
		
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			System.out.println(br.readLine());
		} catch (IOException e) { 
			System.err.println("Erro ao receber mensagem! "+e.getMessage());
			return;
		}
		
		try {
			socket.close();
		} catch (IOException ignored) { }
		
	}

}
