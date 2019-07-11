package server.integration;

import server.model.User;

import javax.persistence.*;

public class UserDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> managerThreadLocal = new ThreadLocal<>();

    public UserDAO(){
        this.entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.netprog.jpa");
    }

    public User findUser(String username){
        EntityManager entityManager = openEntityManager();
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username=:uname");
        query.setParameter("uname", username);
        User user;

        try{
            user = (User) query.getSingleResult();
        }catch (NoResultException e){
            user = null;
        }

        entityManager.close();
        return user;
    }

    public void storeUser(User user){
        EntityManager entityManager = beginTransaction();
        entityManager.persist(user);
        committransaction();
    }

    public void destroyUser(User user){
        EntityManager entityManager = beginTransaction();
        entityManager.remove(entityManager.merge(user));
        committransaction();
    }

    private EntityManager openEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

    private EntityManager beginTransaction(){
        EntityManager entityManager = openEntityManager();
        managerThreadLocal.set(entityManager);
        EntityTransaction transaction = entityManager.getTransaction();
        if (!transaction.isActive()) transaction.begin();
        return entityManager;
    }

    private void committransaction(){
        managerThreadLocal.get().getTransaction().commit();
    }
}
