public class User {

    byte voice_buffer[][] = new byte[ProgramData.MEM_SIZE][ProgramData.PACKET_SIZE];
    String userIP;
    int lastIndex;
    int disArrangments;
    int arrivedPackets;

    public User( String userIP){
        this.userIP = userIP;
        resetData();
    }

    public void resetData(){
        for(int i=0;i<ProgramData.MEM_SIZE;i++) {
            voice_buffer[i] = null;
        }
        lastIndex = 0;
        disArrangments = 0;
        arrivedPackets = 0;
    }

}
