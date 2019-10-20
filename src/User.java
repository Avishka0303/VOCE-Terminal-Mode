public class User {

    byte voice_buffer[][] = new byte[ProgramData.MEM_SIZE][ProgramData.PACKET_SIZE];
    String userIP;
    int lastIndex = -1;
    int disArrangements;
    int arrivedPackets;

    public User( String userIP){
        this.userIP = userIP;
        resetData();
    }

    public User(){ }

    public void resetData(){
        for(int i=0;i<ProgramData.MEM_SIZE;i++) {
            voice_buffer[i] = null;
        }
        lastIndex = -1;
        disArrangements = 0;
        arrivedPackets = 0;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }
}
