package headers;

import java.util.Vector;
import java.util.zip.CRC32;

public class DataLinkLayer implements headerInterface{

    Vector<String> payloads = new Vector<>();

    public void getPayloadsFromTransport(Vector<String> payloads){
        this.payloads = payloads;
    }

    public Vector<String> getPayloads(){
        return payloads;
    }

    public boolean compare(String frame){
        boolean check = true;
        Long crcSource = Long.valueOf(frame.substring(0,12));
        String payload = frame.substring(12);
        Long crc = CRC32Generator(payload);
        if(!crc.equals(crcSource)){
            check = false;
            payloads.add("CRC_ERROR");
        }
        return check;
    }

    @Override
    public String writeHeader() {
        for(int i=0; i<payloads.size(); i++){
            String crcHeader = Long.toString(CRC32Generator(payloads.get(i)));
            crcHeader = setFixedLengthString(12, crcHeader);
            payloads.set(i, crcHeader+payloads.get(i));
        }
        return null;
    }

    @Override
    public String readHeader(String packet) {
        //payloads.add(packet);
        if(compare(packet)){
            String payload = packet.substring(12);
            payloads.add(payload);
        }
        return null;
    }

    public long CRC32Generator(String arg){
        String input = arg;
        CRC32 crc = new CRC32();
        crc.update(input.getBytes());
        System.out.println("input:" + input);
        System.out.println("CRC32:" + crc.getValue());
        return crc.getValue();
    }


    ///////////////////////////////////////////////////////////////////
    // Not needed in this implementation
    @Override
    public String writeFooter() {
        return null;
    }
    @Override
    public String readFooter() {
        return null;
    }
    public String setFixedLengthString(int size, String origin){
        while(origin.length() < size){
            origin = "0" + origin;
        }
        return origin;
    }
}
