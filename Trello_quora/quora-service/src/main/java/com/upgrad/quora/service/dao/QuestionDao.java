package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * This function is used for database interaction of inserting a new question
     *
     * @param question: Question to be entered
     * @param userAuthTokenEntity: For fetching user Details
     * @return
     */
    public QuestionEntity createQuestion(String question, UserAuthTokenEntity userAuthTokenEntity)
    {
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthTokenEntity.getUser());
        questionEntity.setQuestion(question);
        questionEntity.setCreatedDate(ZonedDateTime.now(ZoneId.systemDefault()));
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     *
     * @return
     */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createQuery("SELECT * FROM QuestionEntity", QuestionEntity.class).getResultList();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     * for a particular question UUID
     *
     * @return
     */
    public QuestionEntity getQuestionById(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionById", QuestionEntity.class).setParameter("uuid",uuid).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of fetching a list of available questions
     * for a particular user UUID
     *
     * @return
     */
    public List<QuestionEntity> getQuestionsByUserUUId(final String uuid) {
        try {
            return entityManager.createNamedQuery("questionByUserId", QuestionEntity.class).setParameter("user_id",uuid).getResultList();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of updating a question
     *
     * @param uuid: UUID of Question to be updated
     * @param updatedContent: updated Question
     * @return
     */
    public QuestionEntity updateQuestionContent(final String uuid, final String updatedContent)
    {
        try
        {
            QuestionEntity questionEntity = getQuestionById(uuid);
            int num =  entityManager.createQuery("UPDATE QuestionEntity qt SET qt.question = updatedQuestion WHERE qt.uuid =:uuid").setParameter("uuid",uuid).setParameter("updatesQuestion", updatedContent).executeUpdate();

            if(num == 0)
                throw null;
            else
                return questionEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }

    /***
     * This function is used for database interaction of deleting a question
     *
     * @param uuid: UUID of Question to be deleted
     * @return
     */
    public QuestionEntity deleteQuestion(final String uuid)
    {
        try
        {
            QuestionEntity questionEntity = getQuestionById(uuid);
            int num =  entityManager.createQuery("DELETE FROM QuestionEntity qt WHERE qt.uuid =:uuid").setParameter("uuid",uuid).executeUpdate();

            if(num == 0)
                throw null;
            else
                return questionEntity;
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }
}
