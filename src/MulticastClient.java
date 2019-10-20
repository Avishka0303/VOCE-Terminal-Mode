import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*Sending data multicast address*/
public class MulticastClient {

    private DatagramSocket socket;
    private InetAddress groupAddress;
    private int packetCount;
    
    public MulticastClient(InetAddress hostIP){
        try {
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
                ObjectOutputStream outputObject = new ObjectOutputStream(byteOutput)   )
        {

            if(packetCount==129) packetCount = 0;

            DataPacket packet = new DataPacket( (packetCount++)%ProgramData.MEM_SIZE ,data );

            //serialize the data packet.
            outputObject.writeObject(packet);
            outputObject.flush();

            byte[] objectData = byteOutput.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(objectData,objectData.length,groupAddress,ProgramData.MUL_PORT_NUMBER);
            socket.send(dataPacket);
            
        } catch (IOException ex) {
            System.out.println("Error in multicast packet sending.");
        }

    }
}