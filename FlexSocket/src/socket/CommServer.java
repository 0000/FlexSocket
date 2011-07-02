import java.util.*;
import java.io.*;
import java.net.*;

/**
 *
 * CommServer
 * <BR><BR>
 * Generic Flash Communication Server.  All communications sent and received
 * are (must be) terminated with a null character ('\0') consistent with
 * the way Flash does socket communications.  Any client which uses this
 * protocol should also work.
 * <BR><BR>
 * The server accepts messages from connected clients and broadcasts (verbatim)
 * those messages to all connected clients.  When clients connect or disconnect
 * the server also broadcasts the number of clients via a NUMCLIENTS tag.
 *
 * Usage: java CommServer [port]
 *
 * @author  Derek Clayton   derek_clayton@iceinc.com
 * @version 1.0.1
 */

public class CommServer {
    private Vector clients = new Vector();  // 클라이언트 리스트
    ServerSocket server;                    // 서버
    
    //생성자메소드(포트를 받아서 서버객체생성)
    public CommServer(int port) throws Exception{
        startServer(port);
    }

    //서버 스타트 메소드
    private void startServer(int port) throws Exception {
        writeActivity("Attempting to Start Server");//컴 서버객체를 생성하면서 시간 뿌리
        
        
        try {
            // --- 새로운 서버소켓 생성(포트를 인자로 하여).
            server = new ServerSocket(port);
            writeActivity("Server Started on Port: " + port);
            // --- while the server is active...
            while(true) {
                // --- 클라이언트가 커넥션을 하면 accept를 하여 클라이언트 소켓을 생성
                Socket socket = server.accept();
                //클라이언트 생성(서버객체와 클라이언트 소켓을 인자로 보낸다.)
                CSClient client = new CSClient(this, socket);
                writeActivity(client.getIP() + " connected to the server.");
                // --- 새로운 클라이언트 백터에 추가 
                clients.addElement(client);
                // --- 클라이언트 스래드 시작
                client.start();		
                // --- 클라이언트의 수 xml로 뿌리기
                //broadcastMessage("<NUMCLIENTS>" + clients.size() + "</NUMCLIENTS>");
                broadcastMessage("당신은 " + clients.size() + "번째 방문자입니다.");
            }
        } catch(IOException ioe) {
            writeActivity("Server Error...Stopping Server");
            // kill this server
            killServer();
        } 
    }

    /**
     * Broadcasts a message too all connected clients.  Messages are terminated
     * with a null character.
     * @param   message    The message to broadcast.
    */
    public synchronized void broadcastMessage(String message) throws Exception{
        // --- add the null character to the message
        message += '\0';
        
        // --- 각각의 클라이언트에게 메세지를 보낸다.
        Enumeration enumeration = clients.elements();
        while (enumeration.hasMoreElements()) {
            CSClient client = (CSClient)enumeration.nextElement();
            client.send(message);
        }
        
        System.out.println(message);
    }

    /**
     * Removes clients from the client list.
     * @param   client    The CSClient to remove.
    */
    public void removeClient(CSClient client) throws Exception{
        writeActivity(client.getIP() + " has left the server.");
        
        // --- remove the client from the list
        clients.removeElement(client);
        
        // --- broadcast the new number of clients
        broadcastMessage("<NUMCLIENTS>" + clients.size() + "</NUMCLIENTS>");
    }

    /**
     * Writes a message to System.out.println in the format
     * [mm/dd/yy hh:mm:ss] message.
     * @param   activity    The message.
    */
    public void writeActivity(String activity) {
        // --- get the current date and time
        Calendar cal = Calendar.getInstance();
        activity = "[" + cal.get(Calendar.MONTH) 
                 + "/" + cal.get(Calendar.DAY_OF_MONTH) 
                 + "/" + cal.get(Calendar.YEAR) 
                 + " " 
                 + cal.get(Calendar.HOUR_OF_DAY) 
                 + ":" + cal.get(Calendar.MINUTE) 
                 + ":" + cal.get(Calendar.SECOND) 
                 + "] " + activity + "\n";

        // --- display the activity
        System.out.print(activity);
    }

    /**
     * Stops the server.
    */
    private void killServer() {
        try {
            // --- stop the server
            server.close();
            writeActivity("Server Stopped");
        } catch (IOException ioe) {
            writeActivity("Error while stopping Server");
        }
    }
    
    public static void main(String args[]) throws Exception{
        // --- if correct number of arguments
        if(args.length == 1) {
            CommServer myCS = new CommServer(Integer.parseInt(args[0]));
        } else {
        // otherwise give correct usage
            System.out.println("Usage: java CommServer [port]");
        }
    }
}
