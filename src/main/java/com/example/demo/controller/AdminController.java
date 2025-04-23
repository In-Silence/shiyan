package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Result;
import com.example.demo.service.impl.AdminServiceImpl;
import com.example.demo.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class AdminController {
    @Autowired
    private AdminServiceImpl adminService;
    @Autowired
    private JWTUtil jwtUtil;

    //    判断是否登录成功
    @ResponseBody
    @RequestMapping(value = "/admin/login/status", method = RequestMethod.POST)
    public Result loginStatus(HttpServletRequest req) {
//        JSONObject jsonObject = new JSONObject();

        String name = req.getParameter("name");
        String password = req.getParameter("password");

        boolean res = adminService.veritypasswd(name, password);
        if (res) {
//            jsonObject.put("code", 1);
//            jsonObject.put("msg", "登录成功");
//            session.setAttribute("name", name);
            String token = jwtUtil.generateToken(name);
            return Result.success(200, "登陆成功", token);
        } else {
            return Result.fail("用户名或密码错误");
        }

    }
}
