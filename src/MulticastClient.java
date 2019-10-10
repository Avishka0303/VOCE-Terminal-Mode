import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient {
    
    private MulticastSocket multicastSocket;
    private DatagramSocket socket;
    private InetAddress multicastAddress;
    private RecordPlayback audioService;
    private byte[] buffer;
    private int packetCount;
    
    public static boolean multicastOnline = true ;
    
    public MulticastClient(InetAddress hostIP){
        try {           
            //initiaize the multicast Socket.
            multicastSocket = new MulticastSocket();
            //create a datagram socket
            socket = new DatagramSocket();
            //initialize the multicast
            multicastAddress = hostIP;
        }catch (IOException ex1){
            System.out.println("IO Exception has been generate");
        }
    }
    
    public void sendDataPacket(byte[] data){ 
        
        try {

            DataPacket packet = new DataPacket( (packetCount++% 8) ,data );
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream outputObject = new ObjectOutputStream(byteOutput);
            outputObject.writeObject(packet);
            outputObject.flush();

            byte[] objectData = byteOutput.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(objectData,objectData.length,multicastAddress,ProgramData.MUL_PORT_NUMBER);
            //multicastSocket.send(dataPacket);
            socket.send(dataPacket);
            
        } catch (IOException ex) {
            System.out.println("Error in multicast packet sending.");
        }

    }
}
