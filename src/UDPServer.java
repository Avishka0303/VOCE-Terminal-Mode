import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
* This class is for receiving the data
*
*
* */

public class UDPServer extends Thread{
    
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private RecordPlayback audioService;
    private User user;
    
    private byte[] buffer;
    public static boolean isOnline = true;
    
    public UDPServer(RecordPlayback playback){
        
        try {
            
            this.audioService = playback;

            //initialize the user
            user = new User();
            
            //buffer for read entire data packet
            buffer = new byte[ProgramData.PACKET_SIZE * 4];

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
        
        while(isOnline){

            try{

                //-------------------- Recieve byte array from datagram socket --------------
                datagramSocket.receive(datagramPacket);
                
                //--------------------- Deseriaize the object --------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)objectStream.readObject();


                int pIndex = packet.packetIndex;

                if( user.lastIndex!=pIndex )
                    user.disArrangements++;

                user.lastIndex = pIndex;
                user.arrivedPackets++;

                //--------------------- Send to audio output  --------------------------------
                audioService.playVoice(packet.voice_buffer);

                endTime = System.currentTimeMillis();
                if( (endTime-startTime) >= 60000 ){
                    startTime = System.currentTimeMillis();
                    System.out.println( "UserData : "+user.userIP+
                                        "\narrived : "+user.arrivedPackets+
                                        "\ndisordered : "+user.disArrangements +"\n");
                    user.resetData();
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

