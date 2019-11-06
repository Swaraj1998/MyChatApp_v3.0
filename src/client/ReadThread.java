package client;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

import javax.crypto.*;

public class ReadThread extends Thread {
	private Socket socket;
	private BufferedReader inServer;
	private PrivateKey privateKey;
	
	public ReadThread(Socket socket, PrivateKey privateKey) throws IOException {
		this.socket = socket;
		this.privateKey = privateKey;
	}

	@Override
	public void run() {
		try {	
			inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			while(!socket.isClosed()) {
				if(inServer.ready()) {
					String msg = inServer.readLine();
					if(msg != null) {
						String plainMsg;
						try {
							plainMsg = new String(cipher.doFinal(Base64.getDecoder().decode(msg)), "UTF-8");
						} catch (BadPaddingException | IllegalArgumentException e) {
							plainMsg = msg;
						}
						System.out.println(plainMsg);
						new WriteToFile(plainMsg).start();
						if(msg.equals("You are disconnected."))
							break;
					}
				}
			}
			
			socket.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException | IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
