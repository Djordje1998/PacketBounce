package org.example.repository;

import org.example.model.Packet;

import java.util.List;

public interface Repository {

    void addPacket(Packet packet);

    void removePacket(Packet packet);

    List<Packet> getAllPackets();

}
