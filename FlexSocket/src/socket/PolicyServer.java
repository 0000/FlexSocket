import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PolicyServer extends Thread {
	
	private static PolicyServer policyServer=new PolicyServer();
	private ServerSocket server;
	
	public PolicyServer(){
		try{
			System.out.println("Flex Policy 서버 초기화중.");
			
			server=new ServerSocket(5001);
			this.start();
			
			System.out.println("Flex Policy가 시작되었습니다.");
		}catch(IOException e){
			System.out.println(e);
			try{if(server!=null)server.close();}catch(Exception e1){}
		}		
	}
	
	public static PolicyServer getInstance(){
		return policyServer;
	}
	
	public void run(){
		Socket client=null;
		
		while(true){
			try{
				client=server.accept();
				new PolicyProcessor(client).start();
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
}