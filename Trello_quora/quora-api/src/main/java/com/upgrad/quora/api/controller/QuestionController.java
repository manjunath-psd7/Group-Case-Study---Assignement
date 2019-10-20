package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserDao userDao;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createQuestion(final QuestionRequest questionrequest, @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        try{
            QuestionEntity respquestionEntity = questionService.createQuestion(questionrequest.getContent(), accessToken);
            QuestionResponse questionResponse = new QuestionResponse().id(respquestionEntity.getUuid()).status("QUESTION CREATED");
            return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
        }catch(AuthorizationFailedException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getallQuestions(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        List<QuestionDetailsResponse> RespDetailsQuestionsList = null;
        try {
            List<QuestionEntity> RespQuestionsList = questionService.getAllQuestions(accessToken);
            JSONArray respArray = new JSONArray();
            for (QuestionEntity questionEntity : RespQuestionsList) {
                JSONObject jo=new JSONObject();
                jo.put("uuid", questionEntity.getUuid());
                jo.put("content", questionEntity.getQuestion());
                respArray.appendElement(jo);
            }

            return new ResponseEntity<JSONArray>(respArray, HttpStatus.OK);
        }catch(AuthorizationFailedException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.FORBIDDEN);
        }

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getQuestionbyUserId(@PathVariable("userId") final String userId,
                                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionDetailsResponse> RespDetailsQuestionsList = null;
        try {
            List<QuestionEntity> RespQuestionsList = questionService.getQuestionsByUserUUID(userId, accessToken);

            JSONArray respArray = new JSONArray();
            for (QuestionEntity questionEntity : RespQuestionsList) {
                JSONObject jo=new JSONObject();
                jo.put("uuid", questionEntity.getUuid());
                jo.put("content", questionEntity.getQuestion());
                respArray.appendElement(jo);
            }

            return new ResponseEntity<JSONArray>(respArray, HttpStatus.OK);
        }catch(AuthorizationFailedException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.FORBIDDEN);
        }catch (UserNotFoundException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path="/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity editQuestion(@PathVariable("questionId") final String questionId,
                                                            QuestionEditRequest questionEditRequest,
                                                            @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        try {
            QuestionEntity respquestionEntity = questionService.editQuestionContent(questionEditRequest.getContent(), questionId, accessToken);
            QuestionEditResponse questionResponse = new QuestionEditResponse().id(respquestionEntity.getUuid()).status("QUESTION EDITED");
            return new ResponseEntity<QuestionEditResponse>(questionResponse, HttpStatus.OK);
        }catch(AuthorizationFailedException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.FORBIDDEN);
        }catch (InvalidQuestionException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path="/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteQuestion(@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {
        try{
            QuestionEntity respquestionEntity = questionService.deleteQuestion(questionId, accessToken);
            QuestionDeleteResponse questionResponse = new QuestionDeleteResponse().id(respquestionEntity.getUuid()).status("QUESTION DELETED");
            return new ResponseEntity<QuestionDeleteResponse>(questionResponse, HttpStatus.OK);
        }catch(AuthorizationFailedException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.FORBIDDEN);
        }catch (InvalidQuestionException e){
            JSONObject respObj = new JSONObject();
            return new ResponseEntity<>(respObj.appendField("code", e.getCode()), HttpStatus.NOT_FOUND);
        }
    }



}
