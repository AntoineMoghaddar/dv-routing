package algorithm;

import framework.Packet;

import java.io.*;

public class Util {
    public static byte[] serializeRoutingTable(Object inputObj) {
        try {
            //Convert object to byteArray
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(inputObj);
            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object getForwardingTableFromPacket(Packet packet) {
        if (packet.getRawData().length == 0)
            return null;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(packet.getRawData());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            // no check needed
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
