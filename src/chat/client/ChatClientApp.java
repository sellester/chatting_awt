package chat.client;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_ADDRESS = "127.0.1.1";
	private static final int SERVER_PORT = 9090;
	
	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);

		while( true ) {
			System.out.println("nick_name을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();
			if (name.isEmpty() == false ) {
				break;
			}
			
			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		
		scanner.close();

		new ChatWindow(name, SERVER_ADDRESS, SERVER_PORT).show();
	}

}
