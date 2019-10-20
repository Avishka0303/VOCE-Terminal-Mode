import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*  This class is for receiving the data
*   From another client
*   And send them to the Sound output.
**/

public class UDPServer extends Thread{
    
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private RecordPlayback audioService;

    private int lastIndex=-1;
    private int arrivedPacketsCount;
    private int totalDisArrangements;
    
    private byte[] buffer;
    public static boolean isOnline = true;
    
    public UDPServer(RecordPlayback playback){
        
        try {
            
            this.audioService = playback;

            //buffer for read entire data packet
            buffer = new byte[ProgramData.PACKET_SIZE * 4];

            //instantiate the socket.
            datagramSocket = new DatagramSocket(ProgramData.PORT_NUMBER);
           
            //instantiate the datagram packet
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
        int min=0;

        while(isOnline){

            try{

                //-------------------- Receive byte array from datagram socket --------------
                datagramSocket.receive(datagramPacket);
                
                //--------------------- Deserialize the object --------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)objectStream.readObject();

                int pIndex = packet.packetIndex;

                //----------------------------packet reordering check ------------------------
                if( lastIndex+1 != pIndex ){
                    totalDisArrangements++;
                }

                lastIndex = pIndex;
                if (lastIndex==15)
                    lastIndex = -1;

                arrivedPacketsCount++;

                //--------------------- Send to audio output  ---------------------------------
                audioService.playVoice(packet.voice_buffer);

                //--------------------- print statistics about 1 minute -----------------------
                endTime = System.currentTimeMillis();

                if( (endTime-startTime) >= 60000 ){
                    startTime = System.currentTimeMillis();
                    System.out.println( "UserData : "+datagramPacket.getAddress().toString()+
                                        "\nminute "+(++min)+" statistics\narrived : "+arrivedPacketsCount+
                                        "\ndisordered : "+ totalDisArrangements +"\n");
                    resetData();
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

    private void resetData() {
        arrivedPacketsCount=0;
        totalDisArrangements=0;
    }
}

