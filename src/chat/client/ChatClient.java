package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
	private static final String SERVER_ADDRESS = "192.168.56.1";
	private static final int SERVER_PORT = 9090;
	
	public static void main(String[] args) {
		Scanner scanner = null;
		Socket socket = null;
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;
		
		try {
			
			// 1. 키보드 연결
			scanner = new Scanner( System.in );

			// 2. socket 생성
			socket = new Socket();

			// 3. 연결
			socket.connect( new InetSocketAddress( SERVER_ADDRESS, SERVER_PORT ) );

			// 4. reader/ writer 생성
			bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );

			// 5. join 프로토콜
			System.out.print("닉네임>>");
			String nickname = scanner.nextLine();
			printWriter.println("join:" + nickname);
			printWriter.flush();
			bufferedReader.readLine();
			//System.out.println( data );
			
			// 6. ChatClientRecevieThread 시작
			Thread thread = new ChatClientReceiveThread(bufferedReader);
			thread.start();
			
			// 7. 키보드 입력 처리
			while (true) {
				//System.out.print(">>");
				String input = scanner.nextLine();

				if ("quit".equals(input) == true) {
					// 8. quit 프로토콜 처리
					printWriter.println("quit");
					printWriter.flush();
					break;
				} else {
					// 9. 메시지 처리
					printWriter.println( "message:" + input );
					// socket.getOutputStream().write( ("message:" + input + "\r\n").getBytes( "UTF-8" )  );
					// socket.getOutputStream().flush();
					printWriter.flush();
				}
			}
		} catch (Exception ex) {
			log("error:" + ex);
		} finally {
			// 10. 자원정리
			try {
				if( scanner != null ) {
					scanner.close();
				}
				if( bufferedReader != null ) {
					bufferedReader.close();
				}
				if( printWriter != null ) {
					printWriter.close();
				}
				if( socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch( IOException ex ) {
				log( "error:" + ex );
			}
		}
	}

	public static void log(String log) {
		System.out.println("[chat-client] " + log);
	}
}
