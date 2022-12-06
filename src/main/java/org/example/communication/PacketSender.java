package org.example.communication;

import org.example.model.Packet;
import org.example.repository.DBRepository;
import org.example.repository.Repository;
import org.example.util.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PacketSender extends Thread {
    private final List<Packet> packets;
    private final Repository repository;
    private final Socket socket;

    public PacketSender(Socket socket) {
        this.socket = socket;
        this.repository = new DBRepository();
        this.packets = Collections.synchronizedList(new ArrayList<>());

        List<Packet> pastPackets = repository.getAllPackets();
        if (pastPackets != null && !pastPackets.isEmpty()) {
            this.packets.addAll(repository.getAllPackets());
        }

        this.start();
    }

    public void addPacket(Packet packet) {
        repository.addPacket(packet);
        packets.add(packet);
        System.out.println("--->" + packet);
    }

    @Override
    public void run() {
        try (DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
            while (true) {
                List<Packet> packetsSnapshot = List.copyOf(packets);
                for (Packet packet : packetsSnapshot) {
                    long timeLeft = System.currentTimeMillis() - (packet.getArrivalTime() + packet.getDelayInSeconds() * 1000L);
                    if (timeLeft >= 0 && timeLeft <= Constants.ACCEPTABLE_DELAY) {
                        sendPacket(packet, outputStream);
                        break;
                    }
                    if (timeLeft > Constants.ACCEPTABLE_DELAY) {
                        notifyExpiredPacket(packet);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private void sendPacket(Packet packet, OutputStream outputStream) throws IOException {
        outputStream.write(packet.getBytes());
        outputStream.flush();
        repository.removePacket(packet);
        packets.remove(packet);
        System.out.println("Return to server [" + System.currentTimeMillis() + "] " + packet + " Ping: " +
                (System.currentTimeMillis() - (packet.getArrivalTime() + packet.getDelayInSeconds() * 1000L)) + " MS");
    }

    private void notifyExpiredPacket(Packet packet) {
        repository.removePacket(packet);
        packets.remove(packet);
        System.out.println("EXPIRED PACKET [" + System.currentTimeMillis() + "] " + packet + " Ping: " +
                (System.currentTimeMillis() - (packet.getArrivalTime() + packet.getDelayInSeconds() * 1000L)) + " MS");
    }

}
