package client;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

import javax.crypto.*;


public class WriteThread extends Thread {
	private Socket socket;
	private BufferedReader inClient;
	private PrintWriter out;
	private PublicKey publicKey;
	private String name;
	private boolean sendAuthName;
	
	public WriteThread(Socket socket, PublicKey publicKey, 
			String name, boolean sendAuthName) throws IOException {
		this.socket = socket;
		this.publicKey = publicKey;
		this.name = name;
		this.sendAuthName = sendAuthName;
	}

	@Override
	public void run() {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			inClient = new BufferedReader(new InputStreamReader(System.in));
			if (sendAuthName)
				out.println(name);
			
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
						} else if (message.equals(":quit")) {
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
			e.printStackTrace();
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} 
	}

}
