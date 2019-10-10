import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPClient {
    
    private InetAddress hostAddress;
    private DatagramSocket udpSocket;
    private int packetCount;
    private int port;
    
    public UDPClient(InetAddress peerIPAddress,int port){
        try {
            this.port = port ;
            this.hostAddress = peerIPAddress;
            this.udpSocket   = new DatagramSocket();
        }catch(SocketException ex1){
            System.out.println("This socket is busy");
        }
    }
    
    public void UDPSendPacket(byte[] data){
        
        try{
            
            if(packetCount==129) packetCount=0;
            
            DataPacket packet = new DataPacket((packetCount++)%ProgramData.MEM_SIZE ,data);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream outputObject = new ObjectOutputStream(byteOutput);
            outputObject.writeObject(packet);
            outputObject.flush();
            
            byte[] objectData = byteOutput.toByteArray();
            DatagramPacket dataPacket = new DatagramPacket(objectData , objectData.length , hostAddress  ,port);
            
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
