package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class WriteThread extends Thread {
	private Socket socket;
	private BufferedReader inClient;
	private PrintWriter out;
	private PublicKey publicKey;
	private String name;
	
	public WriteThread(Socket socket, PublicKey publicKey, String name) throws IOException {
		this.socket = socket;
		this.publicKey = publicKey;
		this.name = name;
	}

	@Override
	public void run() {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			inClient = new BufferedReader(new InputStreamReader(System.in));
			
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			while(!socket.isClosed()) {
				if(inClient.ready()) {
					String message = inClient.readLine();
					if(message != null) {
						if(message.equals(":read")) {
							System.out.print("No. of messages to read (Enter 0 for all): ");
							int count = Integer.parseInt(inClient.readLine());
							new ReadFromFile(count).start();
						} else if (message.equals(":quit")){
							out.println(message);
						} else {
							out.println(Base64.getEncoder().encodeToString(
									cipher.doFinal(new String("["+name+"]: "+message).getBytes("UTF-8")))
							);
							new WriteToFile(message).start();
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}

}
