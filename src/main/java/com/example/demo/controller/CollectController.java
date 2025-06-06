package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Collect;
import com.example.demo.service.impl.CollectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@Controller
public class CollectController {

    @Autowired
    private CollectServiceImpl collectService;

//    添加收藏的歌曲
    @ResponseBody
    @RequestMapping(value = "/collection/add", method = RequestMethod.POST)
    public Object addCollection(HttpServletRequest req){

        JSONObject jsonObject = new JSONObject();
        String user_id = req.getParameter("userId");
        String song_id=req.getParameter("songId");
        String play_num=req.getParameter("songId");

        if (song_id == ""){
            jsonObject.put("code", 0);
            jsonObject.put("msg", "收藏歌曲为空");
            return jsonObject;
        } else if (collectService.existSongId(Integer.parseInt(user_id), Integer.parseInt(song_id))) {
            jsonObject.put("code", 2);
            jsonObject.put("msg", "已收藏");
            return jsonObject;
        }
        Collect collect = new Collect();
        collect.setUserId(Integer.parseInt(user_id));
        collect.setSongId(Integer.parseInt(song_id));
        collect.setPlayNum(Integer.parseInt(play_num));
        collect.setCreateTime(new Date());
        boolean res = collectService.addCollection(collect);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "收藏成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "收藏失败");
            return jsonObject;
        }
    }

//    返回所有用户收藏列表
    @RequestMapping(value = "/collection", method = RequestMethod.GET)
    public Object allCollection(){
        return collectService.allCollect();
    }

//    返回的指定用户ID收藏列表
    @RequestMapping(value = "/collection/detail", method = RequestMethod.GET)
    public Object collectionOfUser(HttpServletRequest req){
        String userId = req.getParameter("userId");
        return collectService.collectionOfUser(Integer.parseInt(userId));
    }

//    删除收藏的歌曲
    @RequestMapping(value = "/collection/delete", method = RequestMethod.GET)
    public Object deleteCollection(HttpServletRequest req){
        String user_id = req.getParameter("userId").trim();
        String song_id = req.getParameter("songId").trim();
        return collectService.deleteCollect(Integer.parseInt(user_id), Integer.parseInt(song_id));
    }

//    更新收藏
    @ResponseBody
    @RequestMapping(value = "/collection/update", method = RequestMethod.POST)
    public Object updateCollectMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = req.getParameter("id").trim();
        String user_id = req.getParameter("userId").trim();
        String song_id = req.getParameter("songId").trim();


        Collect collect = new Collect();
        collect.setId(Integer.parseInt(id));
        collect.setUserId(Integer.parseInt(user_id));
        collect.setSongId(Integer.parseInt(song_id));

        boolean res = collectService.updateCollectMsg(collect);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }
//    更新收藏歌曲的播放次数
    @ResponseBody
    @RequestMapping(value = "/collection/updatePlayNum", method = RequestMethod.GET)
    public Object updatePlayNum(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String user_id = req.getParameter("userId").trim();
        String song_id = req.getParameter("songId").trim();
        System.out.println("user_id:"+user_id);
        System.out.println("song_id:"+song_id);

        boolean res = collectService.updatePlayNum(Integer.parseInt(user_id),Integer.parseInt(song_id));
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }
}

