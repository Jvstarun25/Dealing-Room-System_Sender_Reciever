import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.DataOutputStream;
import java.rmi.Naming;
import java.net.Socket;
import java.io.BufferedReader;
import java.util.List;
import java.rmi.NotBoundException;

public class JandhyalaP3Subscriber {
    public static String[] mytopics = {"Bitcoin", "Husd", "Holo", "Flow", "Waves"};
    public static Scanner sc = new Scanner(System.in);
    public static String sURL;
    public static void main(String[] args) throws IOException, NotBoundException {
        System.out.println("Subscriber");
        System.out.println("");
        String servIP = "localhost";
        int portNum = 14683;
        String uname ="";
        boolean logIn = false;
        BufferedReader userin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("enter the ip address ");
        servIP = userin.readLine();
        sURL = "rmi://"+servIP+":9090/Proj3jandhyala";
        Socket cs = new Socket(servIP, portNum);
        DataOutputStream os = new DataOutputStream(cs.getOutputStream());
        BufferedReader si = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        //This loop iterates until successful login
        while (!logIn) {
            StringBuilder msg = new StringBuilder("LOGIN#");
            System.out.println("enter the username: ");
            uname = userin.readLine();
            msg.append(uname);
            msg.append(":");
            System.out.println("enter the password: ");
            msg.append(userin.readLine());
            os.writeBytes(msg.toString() + '\n');
            System.out.println("request for login sent to server ");
            String dfs = si.readLine();
            if (dfs.contains("99")) {
                logIn = true;
                System.out.println("successful login !\n\n");
            } else if (dfs.contains("#1"))
                System.out.println("[message frm server]: incorrect password \n");
            else if (dfs.contains("#2"))
                System.out.println("[message frm server]: incorrect password \n");

        }
        ClientUserHandler hnd = new ClientUserHandler(si);
        hnd.start();
        while (!cs.isClosed()) {
           printMenu();
            String ipt = userin.readLine();
            int pos =0;
            try {
                if (ipt.equalsIgnoreCase("0")) {
                    //Requesting to close the connection
                    os.writeBytes("close#" + '\n');
                    cs.close();
                    hnd.readyToRun(false);
                    break;
                } else if (ipt.equalsIgnoreCase("1")) {
                    System.out.println("\nchoose one topic to subscribe");
                    for(int i=0; i<mytopics.length; i++){
                        System.out.println((i+1)+" "+mytopics[i]);
                    }
                    ipt = userin.readLine();
                    pos = Integer.parseInt(ipt) -1;
                    JandhyalaP3PublishSubscribeInterface service = (JandhyalaP3PublishSubscribeInterface) Naming.lookup(sURL);
                    service.uSubsc(mytopics[pos], uname);
                    System.out.println(mytopics[pos]+" selected topic subscribed:)");

                }else if (ipt.equalsIgnoreCase("2")) {

                    JandhyalaP3PublishSubscribeInterface service = (JandhyalaP3PublishSubscribeInterface) Naming.lookup(sURL);
                    List<String> subscribtions = service.viewSub(uname);
                    System.out.println("\n please enter the username ");
                    System.out.println(subscribtions);
                    ipt = userin.readLine();
                    if(subscribtions.contains(ipt)) {
                        service.userUnsubscribe(ipt, uname);
                        System.out.println(ipt+" selected topic unsubscribed"+ipt);
                    }else{
                        System.out.println("enter valid topic name:");
                    }
                }else if (ipt.equalsIgnoreCase("3")) {
                    JandhyalaP3PublishSubscribeInterface service = (JandhyalaP3PublishSubscribeInterface) Naming.lookup(sURL);
                    List<String> subscribtions = service.viewSub(uname);
                    System.out.println(subscribtions);
                }
            } catch (Exception e) {
                e.printStackTrace();
                hnd.readyToRun(false);
            }
        }
        hnd.readyToRun(false);
        System.out.println("connection ended !");
    }

    public static void printMenu() {
        System.out.println("select 1.view available 2. unsubscribe the topic 3. your subsciprions 0. exit");
    }

    public static class ClientUserHandler extends Thread{
        private boolean isTrue1 = true;
        private BufferedReader si;
        public ClientUserHandler(BufferedReader si){
            this.si = si;
        }

        public void readyToRun(boolean isTrue1){
            this.isTrue1 = isTrue1;
        }

        public void run(){
            while(isTrue1){
                try {
                    System.out.println("message recived from the server: "+si.readLine());
                } catch (IOException e) {
                }
            }

        }
    }
}
