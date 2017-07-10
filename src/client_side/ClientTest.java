package client_side;

import javax.swing.JFrame;

public class ClientTest {
	public static void main(String args[]){
		
		Client client = new Client("192.168.43.218");//127.0.0.1
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startRunning();
	}
}
