package chat;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatClientReceiveThread extends Thread {
	private BufferedReader bufferedReader;
	public ChatClientReceiveThread( BufferedReader bufferedReader ) {
		this.bufferedReader = bufferedReader;
	}
	
	@Override
	public void run() {
		try {
			while( true ) {
				String data = bufferedReader.readLine();
				if( data == null ) {
					break;
				}

				System.out.println( data );
			}
		} catch( IOException ex ) {
			ChatClient.log( "error:" + ex );
		}
	}
}