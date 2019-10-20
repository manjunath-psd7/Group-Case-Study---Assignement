package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    public AnswerEntity getAnswersToQuestion(final String questionId, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        //UserEntity userEntity = userDao.getUser(userUuid);
        //if(userEntity == null)
        //{
        //    throw new ResourceNotFoundException("USR-001","User not found");
        //}
        //return  userEntity;
        //throw  new UnauthorizedException("ATH-002","you are not authorized to fetch user details");

        throw new AuthorizationFailedException("h","bb");

    }
}
