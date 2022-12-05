package org.example.model;

import org.example.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Entity
@Table(name = "packet")
public class Packet {
    @Id
    private int packetID;
    @Column(name = "bytes", nullable = false)
    private byte[] bytes;
    @Column(name = "length")
    private int packetLength;
    @Column(name = "delay")
    private int delayInSeconds;
    @Column(name = "arrivalTime")
    private long arrivalTime;

    public Packet() {
    }

    public Packet(byte[] bytes) {
        this.arrivalTime = System.currentTimeMillis();
        this.bytes = bytes;
        extractPacketLength();
        extractPacketID();
        extractDelay();
    }

    private void extractPacketLength() {
        byte[] segment = new byte[Constants.SEGMENT_SIZE];
        System.arraycopy(this.bytes, Constants.SEGMENT_SIZE, segment, 0, Constants.SEGMENT_SIZE);
        this.packetLength = ByteBuffer.wrap(segment).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private void extractPacketID() {
        byte[] segment = new byte[Constants.SEGMENT_SIZE];
        System.arraycopy(this.bytes, Constants.SEGMENT_SIZE * 2, segment, 0, Constants.SEGMENT_SIZE);
        this.packetID = ByteBuffer.wrap(segment).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private void extractDelay() {
        byte[] segment = new byte[Constants.SEGMENT_SIZE];
        System.arraycopy(this.bytes, Constants.SEGMENT_SIZE * 3, segment, 0, Constants.SEGMENT_SIZE);
        this.delayInSeconds = ByteBuffer.wrap(segment).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getPacketLength() {
        return packetLength;
    }

    public int getPacketID() {
        return packetID;
    }

    public int getDelayInSeconds() {
        return delayInSeconds;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "Arrival: " + arrivalTime +
                ", ID: " + packetID +
                ", Delay: " + delayInSeconds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Packet packet = (Packet) o;

        if (arrivalTime != packet.arrivalTime) return false;
        if (packetLength != packet.packetLength) return false;
        if (packetID != packet.packetID) return false;
        if (delayInSeconds != packet.delayInSeconds) return false;
        return Arrays.equals(bytes, packet.bytes);
    }

    @Override
    public int hashCode() {
        int result = (int) (arrivalTime ^ (arrivalTime >>> 32));
        result = 31 * result + Arrays.hashCode(bytes);
        result = 31 * result + packetLength;
        result = 31 * result + packetID;
        result = 31 * result + delayInSeconds;
        return result;
    }
}
