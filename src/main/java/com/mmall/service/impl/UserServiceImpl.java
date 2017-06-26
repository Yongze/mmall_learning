package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by yw850 on 6/22/2017.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0){
            return ServerResponse.CreateByErrorMessage("The username doesn't exist.");
        }

        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5password);
        if (user == null){
            return ServerResponse.CreateByErrorMessage("Password error");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.CreateBySuccess("Login Success",user);
    }

    @Override
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5 encrypt
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0){
            return ServerResponse.CreateByErrorMessage("Registration Failure");
        }
        return ServerResponse.CreateBySuccessMessage("Registration Success");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type){
        if (StringUtils.isNoneBlank(type)){
            //校验
            if (Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0){
                    return ServerResponse.CreateByErrorMessage("The username has already exist.");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0){
                    return ServerResponse.CreateByErrorMessage("The email has already exist.");
                }
            }
        }else{
            return ServerResponse.CreateByErrorMessage("Wrong parameters");
        }
        return ServerResponse.CreateBySuccessMessage("Validation Success");
    }

    @Override
    public ServerResponse forgetGetQuestion(String username){
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            //not exist
            return ServerResponse.CreateByErrorMessage("The username doesn't exist.");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)){
            return ServerResponse.CreateBySuccessMessage(question);
        }
        return ServerResponse.CreateByErrorMessage("Question is not found.");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0 ){
            //说明问题和问题的答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.CreateBySuccess(forgetToken);
        }
        return ServerResponse.CreateByErrorMessage("The answer of this question in incorrect.");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.CreateByErrorMessage("Wrong parameter, need to pass token");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.CreateByErrorMessage("The username doesn't exist.");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.CreateByErrorMessage("token is expired or invalid");
        }
        if (StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount > 0){
                return ServerResponse.CreateBySuccessMessage("Password reset success");
            }
        }else{
            return ServerResponse.CreateByErrorMessage("Please get a new token for resetting password");
        }
        return ServerResponse.CreateByErrorMessage("fail to reset password");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        //防止横向越权，校验一下当前用户的旧密码一定要指定是这个用户，因为要查询一个count（1），如果不指定id，count（）一定大于零
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            return ServerResponse.CreateByErrorMessage("The old password is incorrect.");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.CreateBySuccessMessage("Password reset success");
        }
        return ServerResponse.CreateByErrorMessage("Fail to reset password");
    }

    public ServerResponse<User> updateInformation(User user){
        //username cannot be updated
        //check new email exist or not, and if the email exist, the email cannot be the current user
        int resultCount = userMapper.checkEmailByuserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.CreateByErrorMessage("The email has existed. Please change a new email address");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.CreateBySuccess("Succeed to update personal information",updateUser);
        }
        return ServerResponse.CreateByErrorMessage("Fail to update update personal information");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            ServerResponse.CreateByErrorMessage("Can't find the current user");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.CreateBySuccess(user);
    }

    //backend

    /**
     * To check the role of current user is an admin
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.CreateBySuccess();
        }
        return ServerResponse.CreateByError();
    }
}
