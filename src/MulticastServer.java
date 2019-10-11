import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class MulticastServer extends Thread{
    
    private DatagramPacket datagramPacket;
    private MulticastSocket multicastSocket;
    private RecordPlayback audioService;

    HashMap<Integer,User> usersMap = new HashMap<Integer, User>();
    
    private byte[] buffer;
    private byte[][] userBuffer;
    public boolean isOnline = true;
    
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

        System.out.println("Multicast server is online.");

        while(isOnline){
            
            try{
                
                //--------------------- Recieve the data packet-------------------------------
                multicastSocket.receive(datagramPacket);

                //--------------------- Deserialize the object --------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream inputObject = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)inputObject.readObject();

                //generate the hashcode for the user
                InetAddress senderIp = datagramPacket.getAddress();
                int userHash = senderIp.hashCode();

                //---------------------- Packet rearranging and user controlling ----------------

                User user;
                if(usersMap.containsKey(userHash)){
                    user = usersMap.get(userHash);
                }else{
                    user = new User(senderIp.toString());
                    usersMap.put(userHash,user);
                }

                int pIndex = packet.packetIndex;

                if( user.lastIndex!=pIndex )
                    user.disArrangements++;

                user.lastIndex = pIndex;

                user.voice_buffer[packet.packetIndex]=packet.voice_buffer;

                user.arrivedPackets++;

                if( pIndex == 15 || (user.arrivedPackets-ProgramData.MEM_SIZE)>3 ){
                    System.out.println( "UserData : "+user.userIP+
                                        "\narrived : "+user.arrivedPackets+
                                        "\ndisordered : "+user.disArrangements +"\n");
                    user.resetData();
                }

                //--------------------- Send to audio output  --------------------------------
                audioService.playVoice(packet.voice_buffer);
                
            }catch(IOException ex){
                System.out.println("Error in multicast recieve.");
                ex.printStackTrace();
            }catch(ClassNotFoundException ex1){
                System.out.println("Error in read object");
            }
        }

        multicastSocket.close();
        
    }
    
}
