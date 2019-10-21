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
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity getUserSignedIn = userDao.getUserHasSignedIn(userAuthTokenEntity.getUser());
        if(getUserSignedIn == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        return questionDao.getAllQuestions();
    }

    public List<QuestionEntity> getQuestionsByUserUUID(String user_uuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity getUserSignedIn = userDao.getUserHasSignedIn(userAuthTokenEntity.getUser());
        if(getUserSignedIn == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        UserEntity userEntity = userDao.getUserByUserId(user_uuid);
        if(userEntity == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getQuestionsByUserUUId(user_uuid);
    }

    public QuestionEntity editQuestionContent(String updatedQuestion, String uuid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity getUserSignedIn = userDao.getUserHasSignedIn(userAuthTokenEntity.getUser());
        if(getUserSignedIn == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if(questionEntity.getUser() != getUserSignedIn.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        return questionDao.updateQuestionContent(questionEntity.getUuid(), updatedQuestion);
    }

    public QuestionEntity deleteQuestion(String uuid, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        UserAuthTokenEntity getUserSignedIn = userDao.getUserHasSignedIn(userAuthTokenEntity.getUser());
        if(getUserSignedIn == null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if(questionEntity.getUser() != getUserSignedIn.getUser() || !getUserSignedIn.getUser().getRole().equals("admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        return questionDao.deleteQuestion(questionEntity.getUuid());
    }
}
