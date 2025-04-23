package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Consumer;
import com.example.demo.domain.Result;
import com.example.demo.service.impl.ConsumerServiceImpl;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Controller
public class ConsumerController {

    @Autowired
    private ConsumerServiceImpl consumerService;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;


//    注册
    @ResponseBody
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Result addUser(HttpServletRequest req){
//        JSONObject jsonObject = new JSONObject();
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String phone_num = req.getParameter("phone_num").trim();
        String email = req.getParameter("email").trim();
        String birth = req.getParameter("birth").trim();
        String introduction = req.getParameter("introduction").trim();
        String location = req.getParameter("location").trim();
        String avator = req.getParameter("avator").trim();

        if (username.equals("") || username == null){
            return Result.fail("用户名或密码错误");
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        } catch (Exception e){
            e.printStackTrace();
        }
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        if (phone_num == "") {
            consumer.setPhoneNum(null);
        } else{
            consumer.setPhoneNum(phone_num);
        }

        if (email == "") {
            consumer.setEmail(null);
        } else{
            consumer.setEmail(email);
        }
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
        consumer.setAvator(avator);
        consumer.setCreateTime(new Date());
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.addUser(consumer);
        if (res) {

            return Result.success(200, "注册成功");
        } else {

            return Result.fail("注册失败");
        }
    }

//    登录
    @ResponseBody
    @RequestMapping(value = "/user/login/status", method = RequestMethod.POST)
    public Result loginStatus(HttpServletRequest req){

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean res = consumerService.veritypasswd(username, password);

        if (res){
            String token = jwtUtil.generateToken(username);

            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(token,token);

            List<Consumer> userMsg = consumerService.loginStatus(username);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("userMsg", userMsg);
            return Result.success(200, "登陆成功", responseData);
        }else {
            return Result.fail("用户名或密码错误");
        }

    }

//    返回所有用户
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result allUser(){
        List<Consumer> consumers = consumerService.allUser();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("consumers", consumers);
        return Result.success(200, "查询成功",responseData );
    }

//    返回指定ID的用户
    @RequestMapping(value = "/user/detail", method = RequestMethod.GET)
    public Result userOfId(HttpServletRequest req){
        String id = req.getParameter("id");
        List<Consumer> consumers = consumerService.userOfId(Integer.parseInt(id));
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("consumers", consumers);
        return Result.success(200, "查询成功", responseData);
    }

//    删除用户
    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    public Result deleteUser(HttpServletRequest req){
        String id = req.getParameter("id");
        boolean bool = consumerService.deleteUser(Integer.parseInt(id));
        if (!bool){
            return Result.fail("删除失败");
        }
        return Result.success(200, "删除成功");
    }

//    更新用户信息
    @ResponseBody
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public Result updateUserMsg(HttpServletRequest req){
//        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String phone_num = req.getParameter("phone_num").trim();
        String email = req.getParameter("email").trim();
        String birth = req.getParameter("birth").trim();
        String introduction = req.getParameter("introduction").trim();
        String location = req.getParameter("location").trim();

        if (username.equals("") || username == null){
//            jsonObject.put("code", 0);
//            jsonObject.put("msg", "用户名或密码错误");
            return Result.fail("用户名或密码不能为空");
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        consumer.setId(Integer.parseInt(id));
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        consumer.setPhoneNum(phone_num);
        consumer.setEmail(email);
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
//        consumer.setAvator(avator);
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.updateUserMsg(consumer);
        if (res){
            return Result.success(200, "修改成功");
        }else {
            return Result.fail("修改失败");
        }
    }

//    更新用户头像
    @ResponseBody
    @RequestMapping(value = "/user/avatar/update", method = RequestMethod.POST)
    public Object updateUserPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id")int id){
//        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            return Result.fail("文件上传失败！");
        }
        String fileName = System.currentTimeMillis()+avatorFile.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "avatorImages" ;
        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String storeAvatorPath = "/avatorImages/"+fileName;
        try {
            avatorFile.transferTo(dest);
            Consumer consumer = new Consumer();
            consumer.setId(id);
            consumer.setAvator(storeAvatorPath);
            boolean res = consumerService.updateUserAvator(consumer);
            if (res){
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("avator", storeAvatorPath);
                return Result.success(200, "上传成功", responseData);
            }else {
//
                return Result.fail("上传失败");
            }
        }catch (IOException e){
            return Result.fail("上传失败");
        }finally {
            return Result.success();
        }
    }
}
