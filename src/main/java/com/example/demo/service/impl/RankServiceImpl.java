package com.example.demo.service.impl;

import com.example.demo.dao.RankMapper;
import com.example.demo.domain.Rank;
import com.example.demo.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private RankMapper rankMapper;

    @Override
    public int rankOfSongListId(Long songListId) {
        int rank =rankMapper.selectScoreSum(songListId);
        int num = rankMapper.selectRankNum(songListId);
        if(num == 0)
            return 0;
        return rank / num;
    }

    @Override
    public boolean addRank(Rank rank) {
        Rank rank1 = rankMapper.selectRankByUserIdAndSongListId(rank.getConsumerId(), rank.getSongListId());
        if (rank1 != null)
            return rankMapper.updateByPrimaryKeySelective(rank) > 0;
        return rankMapper.insertSelective(rank) > 0;
    }
}
