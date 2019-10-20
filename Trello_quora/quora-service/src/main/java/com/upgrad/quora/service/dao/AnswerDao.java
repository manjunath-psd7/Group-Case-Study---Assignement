package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity getAnswersToQuestion(final String questionId) {
        try {
            return entityManager.createNamedQuery("getByQuestionId", AnswerEntity.class).setParameter("question_id", questionId).getSingleResult();
        }
        catch (NoResultException nre)
        {
            return null;
        }
    }
}
