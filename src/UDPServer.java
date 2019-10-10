import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends Thread{
    
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private RecordPlayback audioService;
    
    private byte[] buffer;
    public static boolean isOnline = true;
    private byte reArrangeBuffer[][]; 
    
    public UDPServer(RecordPlayback playback){
        
        try {
            
            this.audioService = playback;
            
            //buffer for read input data
            buffer = new byte[ProgramData.PACKET_SIZE * 4];
            
            //buffer for rearranging the packets
            this.reArrangeBuffer = new byte[ProgramData.MEM_SIZE][ProgramData.PACKET_SIZE];
            
            //construct the socket.
            datagramSocket = new DatagramSocket(ProgramData.PORT_NUMBER);
           
            //create the datagram packet
            datagramPacket = new DatagramPacket( buffer , ProgramData.PACKET_SIZE * 4 );
            
        } catch (IOException ex1) {
            System.out.println("Socket is used by another program.");
            System.exit(1);
        }
        
    }
    
    
    @Override
    public void run() {
            
        System.out.println("Server is online");
        
        long startTime = System.currentTimeMillis();
        long endTime = 0 ;
        long packetCount=0;
        int packetNo=0;
        int packetLossCount=0;
        
        while(isOnline){

            try{

                //-------------------- Recieve byte array from datagram socket --------------
                datagramSocket.receive(datagramPacket);    
                packetCount++;
                
                //--------------------- Deseriaize the object --------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)objectStream.readObject();
                
                System.out.println( "Packet has to arrived : " +( packetNo+1)+" arrived packet : "+packet.packetIndex);
                packetNo = packet.packetIndex;
                
//                if(reArrangeBuffer[packetNo%ProgramData.MEM_SIZE]==null)
//                    reArrangeBuffer[packetNo%ProgramData.MEM_SIZE] = packet.voice_buffer;    

                //--------------------- Send to audio output  --------------------------------
                audioService.playVoice(packet.voice_buffer);
                
                endTime = System.currentTimeMillis();
                
                if( (endTime-startTime) >= 60000 ){
                    startTime = System.currentTimeMillis();
                    System.out.println("-------------------------------------received packet count : "+packetCount );
                    packetCount = 0 ;
                }
                
            }catch(IOException e){
                System.out.println("Error in reception check for that.");
                e.printStackTrace();
            }catch(ClassNotFoundException e1){
                System.out.println("Error in deserialization.");
                e1.printStackTrace();
            }
            
        }
        datagramSocket.close();
    }
}

