package org.example.communication;

import org.example.model.Packet;
import org.example.util.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class PacketReceiver implements Runnable {

    @Override
    public void run() {
        try (Socket socket = new Socket(Constants.URL, Constants.PORT);
             DataInputStream stream = new DataInputStream(socket.getInputStream())) {

            PacketSender packetSender = new PacketSender(socket);
            packetSender.start();

            byte[] header = new byte[Constants.SEGMENT_SIZE];
            while (true) {
                stream.read(header);
                int packetID = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).getInt();

                if (packetID == 1) {
                    byte[] body = new byte[Constants.PACKET_SIZE - Constants.SEGMENT_SIZE];
                    stream.read(body);
                    packetSender.addPacket(new Packet(mergeArrays(header, body)));
                } else {
                    byte[] body = new byte[Constants.CANCEL_PACKET_SIZE - Constants.SEGMENT_SIZE];
                    stream.read(body);
                    System.out.println("***Cancel packet***");
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] mergeArrays(byte[] header, byte[] body) {
        byte[] merged = Arrays.copyOf(header, header.length + body.length);
        System.arraycopy(body, 0, merged, header.length, body.length);
        return merged;
    }
}
