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
    private InetAddress groupAddress;
    private int packetCount;
    
    public static boolean multicastOnline = true ;
    
    public MulticastClient(InetAddress hostIP){
        try {           
            //initiaize the multicast Socket.
            multicastSocket = new MulticastSocket();
            //create a datagram socket
            socket = new DatagramSocket();
            //initialize the multicast
            groupAddress = hostIP;
        }catch (IOException ex1){
            System.out.println("IO Exception has been generate");
        }
    }
    
    public void sendDataPacket(byte[] data){ 
        
        try (
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ObjectOutputStream outputObject = new ObjectOutputStream(byteOutput);
             ){

            if(packetCount==128) packetCount = 0;

            DataPacket packet = new DataPacket( (packetCount++) ,data );

            outputObject.writeObject(packet);
            outputObject.flush();

            byte[] objectData = byteOutput.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(objectData,objectData.length,groupAddress,ProgramData.MUL_PORT_NUMBER);
            //multicastSocket.send(dataPacket);
            socket.send(dataPacket);
            
        } catch (IOException ex) {
            System.out.println("Error in multicast packet sending.");
        }

    }
}
