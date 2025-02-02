import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class RecordPlayback {

    boolean stopCapture = false;
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

    UDPClient u_client;
    MulticastClient m_client;

    boolean isMulticast = false;

    byte tempBuffer[] = new byte[ProgramData.PACKET_SIZE];
    
    public RecordPlayback(Object client){
        readyAudioSystem();
        if(client instanceof UDPClient)
            u_client = (UDPClient)client;
        else{
            m_client = (MulticastClient)client;
            isMulticast=true;
        }
    }

    public RecordPlayback(){}

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private void readyAudioSystem() {

        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
            System.out.println("Available mixers:");
            Mixer mixer = null;
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                System.out.println(cnt + " " + mixerInfo[cnt].getName());
                mixer = AudioSystem.getMixer(mixerInfo[cnt]);

                Line.Info[] lineInfos = mixer.getTargetLineInfo();
                if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                    System.out.println(cnt + " Mic is supported!");
                    break;
                }
            }

            audioFormat = getAudioFormat();     //get the audio format
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            //Setting the maximum volume
            FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(control.getMaximum());

        } catch (LineUnavailableException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void captureVoice() {

        Thread recordingThread = new Thread(() -> {

            System.out.println("Mic is online now.....");
            stopCapture = false;
            try {
                int readCount;
                while (!stopCapture) {
                    readCount = targetDataLine.read(tempBuffer, 0, tempBuffer.length);  //capture sound into tempBuffer
                    if (readCount > 0) {
                        if(isMulticast)
                            m_client.sendDataPacket(tempBuffer);
                        else
                            u_client.UDPSendPacket(tempBuffer);
                    }
                }
                byteArrayOutputStream.close();
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }

        });

        recordingThread.start();

    }
    
    public void playVoice(byte voiceData[]){
        sourceDataLine.write(voiceData, 0, ProgramData.PACKET_SIZE);
    }
}
