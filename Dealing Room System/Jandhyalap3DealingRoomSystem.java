import java.rmi.registry.LocateRegistry;
import java.net.Socket;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.io.*;
import java.net.ServerSocket;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;                                             


public class Jandhyalap3DealingRoomSystem {
	//creating a thread pool
    private static ExecutorService es = Executors.newFixedThreadPool(10);
	//making the registry object
	public static Registry r;
    private static JandhyalaP3PublishSubscribeService sr;
    public static void main(String[] args) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", "192.168.0.8");
        r = LocateRegistry.createRegistry(9090);
        sr = new JandhyalaP3PublishSubscribeService();
		//binding the server object with registry
        r.rebind("Proj3jandhyala", sr);
        try {
            ServerSocket ws = new ServerSocket(14683);
			//Server is starting now
            System.out.println("\nserver started");
            while (true) {
				//accepting the connection from client
                Socket ccs = ws.accept();
                System.out.println("\nclient is now connected !");
                ClientHandler ch = new ClientHandler(ccs);
                es.submit(ch); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class ClientHandler extends Thread {
        DataOutputStream otc; //out To Client
        Socket ccs;
        BufferedReader ifc; 
        String cs; 
        String username = "";

        public ClientHandler(Socket connectionSocket) {
            this.ccs = connectionSocket;
        }
        public void run() {
            try {
                String[] ld = new String[2];
                while (!ccs.isClosed()) {
                    try {
                        ifc = new BufferedReader(new InputStreamReader(ccs.getInputStream()));
                        otc = new DataOutputStream(ccs.getOutputStream());
                        cs = ifc.readLine();
                        System.out.println("\nENTERED DETAILS " + " : \n" + cs);
                        String command = cs.split("#")[0];
                        if (command.equalsIgnoreCase("LOGIN")) {  
                            ld = cs.split("#")[1].split(":");
                            loginValidate(ld);
                        } else if (command.equalsIgnoreCase("CLOSE")) {
                            System.out.println("the connection is closed!! " + username);
                            ifc.close();
                            otc.close();
                            ccs.close();
                            sr.deleteUserHandler(username);
                        }
                    } catch (Exception e) {
                        System.out.println("connection disconnected ! ");
                        ccs.close();
                        sr.deleteUserHandler(username);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void loginValidate(String[] ld) {
            int constatus = 1;
            String userstatus = "INVALID USERNAME !!";
            File file = new File("userList.txt");
            try {
                Scanner scn = new Scanner(file);
                String datauser[];
                while (scn.hasNextLine()) {
                    String inputData = scn.nextLine();
                    datauser = inputData.split(",");
                    if (datauser[0].equals(ld[0])) {
                        constatus = 2;
                        userstatus = "INVALID PASSWORD !!";
                        if (datauser[1].equals(ld[1])) {
                            constatus = 99;
                            username = ld[0];
                            userstatus = "LOGIN SUCCESSFUL FOR" + ld[0];
                            sr.addUserHandler(username, this);
                            break;
                        }
                    }
                }
                scn.close();
                System.out.println(userstatus);
                sendMsgToClient("LOGIN RESPONSE:" + constatus);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendMsgToClient(String mesg) throws IOException {
            otc.writeBytes(mesg + '\n');
            System.out.println("MESSAGE SENT: " + mesg);
        }

    }
}