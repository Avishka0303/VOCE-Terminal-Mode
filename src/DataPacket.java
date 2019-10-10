import java.io.Serializable;

public class DataPacket implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    int packetIndex;
    byte voice_buffer[];
    
    public DataPacket(int count , byte data[]){
        this.voice_buffer = data;
        this.packetIndex = count;
    }
    
}
