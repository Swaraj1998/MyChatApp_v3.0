package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
						} catch (IllegalArgumentException e) {
							plainMsg = msg;
						}
						System.out.println(plainMsg);
						Thread.sleep(15);
						new WriteToFile(plainMsg).start();
						if(msg.equals("You are disconnected."))
							break;
					}
				}
			}
			
			socket.close();
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InvalidKeyException | InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
