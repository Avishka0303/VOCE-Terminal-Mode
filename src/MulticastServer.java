import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

/*
* This class is for receiving the datapackets in multicast mode and send to play them */

public class MulticastServer extends Thread{
    
    private DatagramPacket datagramPacket;
    private MulticastSocket multicastSocket;
    private RecordPlayback audioService;
    private InetAddress localHost;

    //Manage users connected
    HashMap<Integer,User> usersMap = new HashMap<Integer, User>();

    private byte[] buffer;
    public boolean isOnline = true;
    
    public MulticastServer(RecordPlayback playback, InetAddress groupIP){
        
        try {
            
            this.audioService = playback;
            
            buffer = new byte[ProgramData.PACKET_SIZE *4 ];
            //initialize the multicast Socket.
            multicastSocket = new MulticastSocket(ProgramData.MUL_PORT_NUMBER);
            //create the datagram packet.
            datagramPacket = new DatagramPacket(buffer, ProgramData.PACKET_SIZE *4 );
            //join with group.
            multicastSocket.joinGroup(groupIP);
            //to get the personal ip
            localHost = InetAddress.getLocalHost();
            
        }catch (IOException ex1){
            System.out.println("IO Exception has been generate");
        }
    }

    @Override
    public void run(){

        System.out.println("Multicast server is online.");

        long startTime = System.currentTimeMillis();
        long endTime = 0 ;
        int min = 0 ;
        while(isOnline){

            try{

                //--------------------- Recieve the data packet----------------------------------
                multicastSocket.receive(datagramPacket);

                //--------------------- Deserialize the object ----------------------------------
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
                ObjectInputStream inputObject = new ObjectInputStream(inputStream);
                DataPacket packet = (DataPacket)inputObject.readObject();

                //--------------------- generate the hashcode for the user ----------------------
                InetAddress senderIp = datagramPacket.getAddress();
                if(senderIp.equals(localHost))
                    continue;
                int userHash = senderIp.hashCode();

                //---------------------- Packet rearranging and user controlling ----------------

                User user;
                if(usersMap.containsKey(userHash)){
                    user = usersMap.get(userHash);
                }else{
                    user = new User(senderIp.toString());
                    usersMap.put(userHash,user);
                }

                //--------------------------------------------------------------------------------
                int pIndex = packet.packetIndex;

                if( (user.lastIndex+1)!=pIndex )
                    user.disArrangements++;

                user.lastIndex = pIndex;
                if (user.lastIndex==15)
                    user.lastIndex = -1;

                user.arrivedPackets++;

                //---------------------  Print the packet statistics after 1 minute ------------
                endTime = System.currentTimeMillis();
                if(( endTime-startTime) >= 60000){
                    startTime = System.currentTimeMillis();
                    for (Map.Entry<Integer,User> entry : usersMap.entrySet()){
                        User userd = (User)entry.getValue();
                        System.out.println( "UserData : "+user.userIP+
                                            "\nminute "+(++min)+" statistics\narrived : "+user.arrivedPackets+
                                            "\ndisordered : "+user.disArrangements +"\n");
                        user.resetData();
                    }
                }

                //--------------------- Send to audio output  --------------------------------
                audioService.playVoice(packet.voice_buffer);

            }catch(IOException ex){
                System.out.println("Error in multicast receive.");
                ex.printStackTrace();
            }catch(ClassNotFoundException ex1){
                System.out.println("Error in read object");
            }
        }

        multicastSocket.close();
    }
}
