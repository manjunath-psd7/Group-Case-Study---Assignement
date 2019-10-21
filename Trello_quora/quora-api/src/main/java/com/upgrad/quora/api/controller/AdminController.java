package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    //End point for deleting the particular user
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getUser(@PathVariable("userId") final String userUuid,
                                                      @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        try {
            final UserEntity userEntity = userAdminBusinessService.deleteUser(userUuid, authorization);
            UserDeleteResponse userResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
            return new ResponseEntity<UserDeleteResponse>(userResponse, HttpStatus.CREATED);
        } catch (AuthorizationFailedException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        } catch (UserNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse().code(e.getCode()).message(e.getErrorMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
    }
}

