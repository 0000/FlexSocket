import java.io.*;
import java.net.*;

/**
 *
 * CSClient
 * <BR><BR>
 * Client for the CommServer.
 *
 * @author  Derek Clayton   derek_clayton@iceinc.com
 * @version 1.0.2
 */

public class CSClient extends Thread {
    private Thread thrThis;         // client thread
    private Socket socket;          // 서버에서 받아온 클라이언트 소켓으로 지정된다.
    private CommServer server;      // 서버에서 받아온 서버객체로 지정된다.
    private String ip;              // the ip of this client
    protected BufferedReader in;    // captures incoming messages
    protected BufferedWriter out;      // sends outgoing messages

    /**
     * Constructor for the CSClient.  Initializes the CSClient properties.
     * @param   server    The server to which this client is connected.
     * @param   socket    The socket through which this client has connected.
    */
    public CSClient(CommServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        
        // --- init the reader and writer
        try {
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));//문자를 버퍼에 담기 위해 버퍼리더 생성
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));//문자를 출력하기 위해 프린트라이터 생
        } catch(IOException ioe) {
            server.writeActivity("Client IP: " + ip + " could not be " 
            + "initialized and has been disconnected.");
            killClient();
        }
    }

    /**
     * Thread run method.  Monitors incoming messages.
    */	
    public void run(){
        try {
            char charBuffer[] = new char[1];
            
            // --- 스트림이 들어오는 동안
            while(in.read(charBuffer,0,1) != -1) {
            	
                // --- create a string buffer to hold the incoming stream
                StringBuffer stringBuffer = new StringBuffer(8192);
                
            
                // --- charBuffer[0]이 공백이 아닌 동안
                while((int)charBuffer[0] != 10 && charBuffer[0] != '\0') {	// 공백 확인
                //while((int)charBuffer[0] != 10) {	// Socket 사용시 공백 확인
                //while(charBuffer[0] != '\0') {	// XMLSocket 사용시 공백 확인
                	
                    // --- add the character to our buffer
                    stringBuffer.append(charBuffer[0]);
                    
                    in.read(charBuffer, 0 ,1);
                }
                // --- broadcast the message
               
               	server.broadcastMessage(stringBuffer.toString());	// XMLSocket 한글
            }
        } catch(IOException ioe) {
            server.writeActivity("Client IP: " + ip + " caused a read error " 
            + ioe + " : " + ioe.getMessage() + "and has been disconnected.");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally {
            killClient();
        }
    }

    /**
     * Gets the ip of this client.
     * @return   ip    this client's ip
    */
    public String getIP() {
        return ip;
    }
    
    /**
     * Sends a message to this client. Called by the server's broadcast method.
     * @param   message    The message to send.
    */
    public void send(String message) {
    	
    	System.out.println("11111111111"+message);
    	try
    	{
    		//message = new String( message.getBytes("UTF-8"), "8859_1");
    	}
    	catch ( Exception e )
    	{
    		e.printStackTrace();
    	}
    	
    	System.out.println("22222222222222222222"+message);
        // --- 버퍼로 메세지를 담는다.
    	try
    	{
    		out.write(message);
    		out.flush();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        
        // --- flush the buffer and check for errors
        // --- if error then kill this client
        
        
    }
 
    /**
     * Kills this client. 
    */   
    private void killClient() {
       
       

        // --- close open connections and references
        try {
        	 // --- tell the server to remove the client from the client list    
        	 server.removeClient(this);
        	 
            in.close();
            out.close();
            socket.close();            
            thrThis = null;
        } catch (IOException ioe) {
            server.writeActivity("Client IP: " + ip + " caused an error "
            + "while disconnecting.");
        }
        catch( Exception e)
        {
        	e.printStackTrace();
        }
    }
}