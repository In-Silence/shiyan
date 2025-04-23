package com.example.demo.dao;

import com.example.demo.domain.Rank;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankMapper {
//    插入评分记录
    int insert(Rank record);

//    插入评分记录（不为空）
    int insertSelective(Rank record);

//     查总分
    int selectScoreSum(Long songListId);

//    查总评分人数
    int selectRankNum(Long songListId);

//查询所有评分记录
    List<Rank> selectRanks();

//    根据歌单ID和用户Id查询评分记录
    Rank selectRankByUserIdAndSongListId(Long consumerId, Long songListId);


//更新现有记录
    int updateByPrimaryKeySelective(Rank record);

}
