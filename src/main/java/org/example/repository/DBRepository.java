package org.example.repository;

import org.example.model.Packet;
import org.example.util.Constants;

import javax.persistence.*;
import java.util.List;

public class DBRepository implements Repository {

//    private final EntityManager entityManager;
//
//    public DBRepository() {
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
//        this.entityManager = entityManagerFactory.createEntityManager();
//    }

    @Override
    public void addPacket(Packet packet) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(packet);
        entityManager.getTransaction().commit();

        entityManager.clear();
        entityManagerFactory.close();
    }

    @Override
    public void removePacket(Packet packet) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Packet foundPacket = entityManager.find(Packet.class, packet.getPacketID());
        entityManager.getTransaction().begin();
        entityManager.remove(foundPacket);
        entityManager.getTransaction().commit();

        entityManager.clear();
        entityManagerFactory.close();
    }

    @Override
    public List<Packet> getAllPackets() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        List<Packet> existingPackets = entityManager.createQuery("SELECT p FROM Packet p", Packet.class).getResultList();
        entityManager.getTransaction().commit();

        entityManager.clear();
        entityManagerFactory.close();

        return existingPackets;
    }
}
