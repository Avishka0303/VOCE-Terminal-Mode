import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer extends Thread{
    
    private DatagramPacket datagramPacket;
    private MulticastSocket multicastSocket;
    private RecordPlayback audioService;
    
    private byte[] buffer;
    
    public MulticastServer(RecordPlayback playback, InetAddress groupIP){
        
        try {
            
            this.audioService = playback;
            
            buffer = new byte[ProgramData.PACKET_SIZE *4 ];
            //initiaize the multicast Socket.
            multicastSocket = new MulticastSocket(ProgramData.MUL_PORT_NUMBER);
            //create the datagram packet.
            datagramPacket = new DatagramPacket(buffer, ProgramData.PACKET_SIZE *4 );
            //join with group.
            multicastSocket.joinGroup(groupIP);
            
        }catch (IOException ex1){
            System.out.println("IO Exception has been generate");
        }
        
    }

    @Override
    public void run(){

        System.out.println("Multicast server is online. ");

        while(true){
            
            try{
                
                //--------------------- Recieve the data packet-------------------------------
                multicastSocket.receive(datagramPacket);
                System.out.println(datagramPacket.getAddress());

                //--------------------- Deseriaize the object --------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream inputObject = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)inputObject.readObject();

                System.out.println("Packet index "+packet.packetNo);
                
                //--------------------- Send to audio output  --------------------------------
                audioService.playVoice(packet.voice_buffer);
                
            }catch(IOException ex){
                System.out.println("Error in multicast recieve.");
                ex.printStackTrace();
            }catch(ClassNotFoundException ex1){
                System.out.println("Error in read object");
            }
            
        }
        
    }
    
}
