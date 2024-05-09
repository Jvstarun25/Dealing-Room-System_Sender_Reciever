import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface JandhyalaP3PublishSubscribeInterface extends Remote {
    public void msgPublish(String topic, String msg) throws RemoteException;
    public void uSubsc(String topic, String subscriber)throws RemoteException;
    public void userUnsubscribe(String topic, String subscriber)throws RemoteException;
    public List<String> viewSub(String userName) throws  RemoteException;
}
