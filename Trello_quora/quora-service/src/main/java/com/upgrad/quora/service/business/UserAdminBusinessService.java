package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    public UserEntity getUser(final String userUuid, final String authorizationToken) throws UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUserByName(userUuid);
        if(userEntity == null)
        {
            throw new UserNotFoundException("USR-001","User not found");
        }
        return  userEntity;
        //throw  new ("ATH-002","you are not authorized to fetch user details");
    }

    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException
    {
        if(userDao.getUserByName(userEntity.getUsername()).getUuid() != null)
        {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken.");
        }
        else if(userDao.getUserByEmail(userEntity.getEmail()).getUuid() != null)
        {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailid.");
        }
        else
        {

            String password = userEntity.getPassword();

            if(password == null)
            {
                password = "quora@123";
            }
            String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
            userEntity.setSalt(encryptedText[0]);
            userEntity.setPassword(encryptedText[1]);
            return userDao.createUser(userEntity);
        }
    }
}
