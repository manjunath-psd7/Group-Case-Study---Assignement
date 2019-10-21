package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String accessToken) throws AuthorizationFailedException
    {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to post a question");
        }
        else
        {
            questionEntity.setUser(userDao.getUserByAccessToken(accessToken));
            return questionDao.createQuestion(questionEntity);
        }
    }

    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to get all questions");
        }
        else
        {
            return questionDao.getAllQuestions();
        }
    }

    public List<QuestionEntity> getQuestionsByUserUUID(String user_uuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to get all questions posted by a specific user");
        }
        else
        {
            UserEntity userEntity = userDao.getUserByUserId(user_uuid);
            if(userEntity == null)
            {
                throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
            }
            else
            {
                    return questionDao.getQuestionsByUserUUId(userEntity);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final QuestionEntity questionEditedEntity,final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to edit the question");
        }
        else
        {
            QuestionEntity questionEntityResult = questionDao.getQuestionById(questionEditedEntity.getUuid());
            if(questionEntityResult == null)
            {
                throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
            }
            else if(questionEntityResult.getUser() != userAuthTokenEntity.getUser())
            {
                throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
            }
            else
            {
                return questionDao.updateQuestion(questionEditedEntity.getContent(),questionEditedEntity.getUuid(),questionEntityResult.getUser());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(String uuid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthTokenEntity.getLogoutAt()  != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out. Sign in first to delete the question");
        }
        else
        {
            QuestionEntity questionEntityResult = questionDao.getQuestionById(uuid);
            if(questionEntityResult == null)
            {
                throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
            }
            else if(questionEntityResult.getUser() != userAuthTokenEntity.getUser() && userAuthTokenEntity.getUser().getRole().equals("nonadmin"))
            {
                throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
            }
            else
            {
                return questionDao.deleteQuestion(uuid);
            }
        }
    }
}
