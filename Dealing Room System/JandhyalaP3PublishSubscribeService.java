import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class JandhyalaP3PublishSubscribeService extends UnicastRemoteObject implements JandhyalaP3PublishSubscribeInterface {
    private HashMap<String, Set<String>> tSubs = new HashMap<>();
    private HashMap<String, Jandhyalap3DealingRoomSystem.ClientHandler> chm = new HashMap<>();
    public JandhyalaP3PublishSubscribeService() throws RemoteException {
    }

    public void addUserHandler(String userName, Jandhyalap3DealingRoomSystem.ClientHandler handler){
        chm.put(userName, handler);
    }

    public void deleteUserHandler(String userName){
        chm.remove(chm);
    }


    @Override
    public void msgPublish(String th, String msg) {
        for(String user: tSubs.get(th)){
            try {
                chm.get(user).sendMsgToClient(th+" : "+msg);
            }catch(Exception e){

            }
        }
    }

    @Override
    public void uSubsc(String th, String subb) {
        if(!tSubs.containsKey(th)){
            tSubs.put(th,new HashSet());
        }
        tSubs.get(th).add(subb);
    }

    @Override
    public void userUnsubscribe(String th, String subb) {
        tSubs.get(th).remove(subb);
    }

    @Override
    public List<String> viewSub(String userName) {
        List<String> subscriptions = new ArrayList<>();
        for(String th: tSubs.keySet()){
            if(tSubs.get(th).contains(userName)){
                subscriptions.add(th);
            }
        }
        return subscriptions;
    }

}
