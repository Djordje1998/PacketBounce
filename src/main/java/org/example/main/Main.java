package org.example.main;

import org.example.communication.PacketReceiver;

public class Main {

    public static void main(String[] args) {
        new Thread(new PacketReceiver()).start();
    }

}
