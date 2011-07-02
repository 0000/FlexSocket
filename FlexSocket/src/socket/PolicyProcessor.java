import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PolicyProcessor extends Thread {
	
	private Socket socket;
	private boolean connect;
	
	private OutputStream out;
	private InputStream in;
	
	public PolicyProcessor(Socket socket) throws Exception{
		this.socket=socket;
		
		out=socket.getOutputStream();
		in=socket.getInputStream();
	}
	public void run() {			
		try{
			byte[] b=new byte[1024];
			String data=null;
			int length=0;
			
			byte[] rtnData=new String("<cross-domain-policy>"+
									  "<site-control permitted-cross-domain-policies=\"master-only\"/>"+
									  "<allow-access-from domain=\"*\" to-ports=\"4321\"/>"+
									  "</cross-domain-policy>\0").getBytes();
			while(!connect){
				length=in.read(b);
				data=new String(b,0,length);
				
				if(data.indexOf("<policy-file-request/>")!=-1){
					System.out.println("Flex Policy Processor :<policy-file-request/>요청을 받았습니다.");
					
					this.out.write(rtnData);
					this.out.flush();
					disconnect();
					
					System.out.println("Flex Policy Processor :<policy-file-request/>요청에 대한 정책정보를 전송했습니다.");
				}else{
					System.out.println("Flex Policy Processor : 비정상요청 : "+data);
					
					disconnect();
				}
			}
			System.out.println("Flex Policy Processor 종료됩니다");			
		}catch(Exception e){
			System.out.println(e);
		}finally{
			disconnect();
		}
	}
	//접속을 끊는다.
	public void disconnect(){
		connect=true;
		try{in.close();}catch(Exception e){}
		try{out.close();}catch(Exception e){}
		try{socket.close();}catch(Exception e){}
	}
}