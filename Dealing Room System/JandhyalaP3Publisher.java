import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.Naming;

public class JandhyalaP3Publisher {
    public static String[] mytopics = {"Bitcoin", "Husd", "Holo", "Flow", "Waves"};
    public static String srURL;

    public static void main(String[] args) throws IOException, NotBoundException {
        System.out.println("Publisher");
        System.out.println("*************************************");
        String sIp = "localhost";
        int portNum = 14683;
        boolean loggedIn = false;
        BufferedReader uInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\nplease enter ip address: ");
        System.out.println("----------------- ");
        sIp = uInput.readLine();
        srURL = "rmi://"+sIp+":9090/Proj3jandhyala";
        Socket csoc = new Socket(sIp, portNum);
        DataOutputStream ooutStream = new DataOutputStream(csoc.getOutputStream());
        BufferedReader serIn = new BufferedReader(new InputStreamReader(csoc.getInputStream()));

        //This loop iterates until successful login
        while (!loggedIn) {
            StringBuilder message = new StringBuilder("Login#");
            System.out.println("login username: ");
            System.out.println("--------------- ");
            message.append(uInput.readLine());
            message.append(":");
            System.out.println("please give the password ");
            System.out.println("-------------- ");
            message.append(uInput.readLine());
            ooutStream.writeBytes(message.toString() + '\n');
            System.out.println("request for login sent to server");
            String dfs = serIn.readLine();
            if (dfs.contains("99")) {
                loggedIn = true;
                System.out.println("successful login\n\n");
            } else if (dfs.contains("#1"))
                System.out.println("[msg frm server]: invalid username" + "\n");
            else if (dfs.contains("#2"))
                System.out.println("[msg frm server]: invaliod password" + "\n");

        }

        while (!csoc.isClosed()) {
            displayMenu();
            String uinp = uInput.readLine();
            String dfs;
            int pos =0;
            boolean vinput = true;
            try {
                pos = Integer.parseInt(uinp) -1;
            } catch (Exception e) {
                System.out.println("incorrect input");
                vinput = false;
            }
            boolean isSuccess;
            try {
                if (uinp.equalsIgnoreCase("0")) {
                    ooutStream.writeBytes("close#" + '\n');
                    csoc.close();
                    break;
                } else if (vinput) {
                    System.out.println("msg to respective subscriber: ");
                    String msg = uInput.readLine();
                    JandhyalaP3PublishSubscribeInterface service = (JandhyalaP3PublishSubscribeInterface) Naming.lookup(srURL);
                    service.msgPublish(mytopics[pos], msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("end connection");
    }

    public static void displayMenu() {
        System.out.println("\nchoose a topic");
        System.out.println("0. Exit");
        for (int i = 0; i < mytopics.length; i++)
            System.out.println((i + 1) + ". " + mytopics[i]);
    }

}