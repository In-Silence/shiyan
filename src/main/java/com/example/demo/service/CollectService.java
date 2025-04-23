package com.example.demo.service;

import com.example.demo.domain.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectService {

    boolean addCollection(Collect collect);

    boolean existSongId(Integer userId, Integer songId);

    boolean updateCollectMsg(Collect collect);

    boolean deleteCollect(Integer userId, Integer songId);

    List<Collect> allCollect();

    List<Collect> collectionOfUser(Integer userId);

    boolean updatePlayNum(Integer userId,Integer songId);
}
