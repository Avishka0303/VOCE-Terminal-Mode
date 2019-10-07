import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
    
    private InetAddress hostAddress;
    private DatagramSocket udpSocket;
    private int packetCount;
    
    public UDPClient(InetAddress peerIPAddress){
        try {
            this.hostAddress = peerIPAddress;
            this.udpSocket   = new DatagramSocket();
        }catch(SocketException ex1){
            System.out.println("This socket is busy");
        }
    }
    
    public void UDPSendPacket(byte[] data){
        
        try{
            
            if(packetCount==128) packetCount=0;
            
            DataPacket packet = new DataPacket((packetCount++),data);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream outputObject = new ObjectOutputStream(byteOutput);
            outputObject.writeObject(packet);
            outputObject.flush();
            
            byte[] objectData = byteOutput.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(objectData , objectData.length , hostAddress  ,ProgramData.PORT_NUMBER);
            
            try{
                udpSocket.send(dataPacket);
            }catch(IOException ex){
                System.out.println("Error in packet sending protocol.");
            }
            
        }catch(IOException ex){
            System.out.println("Error in serialization.");
        }
        
    }
}
