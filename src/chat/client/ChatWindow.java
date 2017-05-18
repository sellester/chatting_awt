package chat.client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatWindow implements ActionListener {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	private String nickname;
	private String ipaddress;
	private int port;
	
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	private Socket socket;
	
	public ChatWindow(String name, String ipaddress, int port) {
		this.frame = new Frame(name);
		this.pannel = new Panel();
		this.buttonSend = new Button("Send");
		this.textField = new TextField();
		this.textArea = new TextArea(30, 80);
		
		this.nickname = name;
		this.ipaddress = ipaddress;
		this.port = port;
		this.socket = null;
		
		socketConn();
	} 	
	
	public void socketConn() {
		try {
			socket = new Socket();
			socket.connect( new InetSocketAddress( ipaddress, port ) );
		} catch( IOException ex ) {
			log("error:" + ex);
		} finally {
			//자원정리
			try {
				if( socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch( IOException ex ) {
				log( "error:" + ex );
			}
		}
		
		chatClient(socket);
	}
	
	public void chatClient( Socket socket ) {
		try {
			// 4. reader/ writer 생성
			bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );
			
			// 5. join 프로토콜
			printWriter.println("join:" + nickname);
			printWriter.flush();
			
			// 6. Thread 시작
			Thread thread = new Thread( new Runnable() {
				@Override
				public void run() {
					try {
						while( true ) {
							String data = bufferedReader.readLine();
							if (data == null || data.equals("qxit")) {
								break;
							}

							textArea.append( data );
						}
					} catch( IOException e ) {
						textArea.append("메세지 수신 에러!!\n");
						try {
							socket.close();
						} catch (IOException ex) {
							log( "error:" + ex );
						}
					} finally {
						try {
							if (bufferedReader != null) {
								bufferedReader.close();
							}
							if (printWriter != null) {
								printWriter.close();
							}
							if (socket != null && socket.isClosed() == false) {
								socket.close();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			thread.start();
			
		} catch (Exception ex) {
			log("error:" + ex);
		} 
	}

	public static void log(String log) {
		System.out.println("[chat-client] " + log);
	}
	
	public void show() {

		// Button
		buttonSend.addActionListener(this);
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
					actionPerformed(null);
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
		frame.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String message = textField.getText();
	
		if (message.equals("exit") == true) {
			printWriter.println("exit");
			printWriter.flush();
			System.exit(0);
		} else {
			printWriter.println("message:" + message);
			printWriter.flush();
		}
		
		textField.setText("");
		textField.requestFocus();
	}
}
