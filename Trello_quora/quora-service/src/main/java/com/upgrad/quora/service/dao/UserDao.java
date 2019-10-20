package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity)
    {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username",userName).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserEntity getUserByUserId(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateUser(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }

    public UserAuthTokenEntity getUserAuthToken(final String accessToken)
    {
        try
        {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserAuthTokenEntity getUserHasSignedOut(final UserEntity user)
    {
        try
        {
            return entityManager.createNamedQuery("userSignedOut",UserAuthTokenEntity.class).setParameter("user",user).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public  UserAuthTokenEntity getUserHasSignedIn(final UserEntity user)
    {
        try
        {
            return entityManager.createNamedQuery("userSignedIn",UserAuthTokenEntity.class).setParameter("user",user).setFirstResult(0).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserAuthTokenEntity updateSingOut(final ZonedDateTime logOutTime, final String accessToken)
    {
        try
        {
            int num =  entityManager.createQuery("UPDATE UserAuthTokenEntity ut SET ut.logoutAt = :logoutAt WHERE ut.accessToken =:accessToken").setParameter("accessToken",accessToken).setParameter("logoutAt",logOutTime).executeUpdate();

            if(num == 0)
                throw null;
            else
                return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthTokenEntity.class).setParameter("accessToken",accessToken).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    public UserEntity deleteUser(final String uuid)
    {
        try
        {
            UserEntity userEntity = getUserByUserId(uuid);
            int num =  entityManager.createQuery("DELETE FROM UserEntity ut WHERE ut.uuid =:uuid").setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return userEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }


}
