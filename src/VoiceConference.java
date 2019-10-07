import java.net.InetAddress;
import java.net.UnknownHostException;

public class VoiceConference {

    static boolean isMulticast = false;
    private static InetAddress hostIP;
    private static RecordPlayback recordPlayback;
    private static UDPServer server;
    private static UDPClient client;
    private static MulticastClient multiClient;
    private static MulticastServer multiServer;

    public static void main(String args[]){

        if(args.length!=1 || !isValidIP(args[0])){
            System.out.println("Enter a valid client IP address");
            System.exit(1);
        }

        try {
            hostIP = InetAddress.getByName(args[0]);
            if(hostIP.isMulticastAddress()) {
                startMulticastServer(hostIP);
                isMulticast=true;
            }else{
                startUDPserverAndClient(hostIP);
            }
        } catch (UnknownHostException e) {
            System.out.println("Address cannot be translate. Enter a valid address. ");
            e.printStackTrace();
            System.exit(2);
        }catch (InterruptedException e){
            System.out.println("Sleep interrupted");
        }
    }


    public static boolean isValidIP(String hostIP) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return hostIP.matches(PATTERN);
    }

    public static void startMulticastServer(InetAddress hostIP) throws InterruptedException{

        client = new UDPClient(hostIP);
        recordPlayback = new RecordPlayback(client);

        multiServer = new MulticastServer(recordPlayback,hostIP);
        multiServer.start();

        Thread.sleep(1000);

        recordPlayback.captureVoice();

    }

    public static void startUDPserverAndClient(InetAddress hostIP) throws InterruptedException{

        client = new UDPClient(hostIP);
        recordPlayback = new RecordPlayback(client);

        server = new UDPServer(recordPlayback);
        server.start();

        Thread.sleep(1000);

        recordPlayback.captureVoice();

    }

}
