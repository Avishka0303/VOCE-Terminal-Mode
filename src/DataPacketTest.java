import static org.junit.Assert.*;
import org.junit.Test;

import java.io.*;

/*This class is for junit testing.*/
public class DataPacketTest {
    @Test
    public void evaluatePacket() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[500];
        int index = 5;

        for(int i=0;i<500;i++)
            buffer[i] = 1;
        DataPacket dataPacket = new DataPacket(index,buffer);
        assertArrayEquals(buffer,dataPacket.voice_buffer);
        assertEquals(index,dataPacket.packetIndex);

        //-------------------- check serialization and deserialization --------------------------
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ObjectOutputStream outputObject = null;
        try {
            outputObject = new ObjectOutputStream(byteOutput);
            outputObject.writeObject(dataPacket);
            outputObject.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] objectData = byteOutput.toByteArray();

        //Deseralization
        ByteArrayInputStream inputStream = new ByteArrayInputStream(objectData);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        DataPacket packet = (DataPacket)objectStream.readObject();

        int pin =packet.packetIndex;
        byte buf[] = packet.voice_buffer;

        assertEquals(pin,index);
        assertArrayEquals(buf,buffer);

    }
}