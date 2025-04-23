package com.example.demo.service.impl;

import com.example.demo.dao.*;
import com.example.demo.domain.*;
import com.example.demo.recommend.core.CoreMath;
import com.example.demo.recommend.dto.ProductDTO;
import com.example.demo.recommend.dto.RelateDTO;
import com.example.demo.service.RankService;
import com.example.demo.service.RecommendSongListService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author root
 * @Description:
 * @Package com.example.demo.service.impl
 * @date 2021/5/6 17:07
 */
@Service
public class RecommendServiceImpl implements RecommendSongListService {
    @Autowired
    private RankMapper rankMapper;

    @Autowired
    private SongListMapper songListMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private ListSongMapper listSongMapper;


    @Override
    public List<RelateDTO> getCollectData(){
        List<RelateDTO> relateDTOList = Lists.newArrayList();
        List<Collect> collectList = collectMapper.allCollect();
        for (Collect collect : collectList) {
            RelateDTO relateDTO = new RelateDTO();
            relateDTO.setUserId(collect.getUserId());
            relateDTO.setProductId(collect.getSongId());

            relateDTO.setIndex(collect.getPlayNum());
            relateDTOList.add(relateDTO);
        }
        if(CollectionUtils.isEmpty(relateDTOList)){
            System.out.println("--------------------List<RelateDTO>为空！");
        }
        return relateDTOList;
    }
    @Override
    public List<ProductDTO> getSongData() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Song> songLists = songMapper.allSong();
        for (Song song : songLists) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(song.getId());
            productDTOList.add(productDTO);
        }
        if(CollectionUtils.isEmpty(productDTOList)){
            System.out.println("----------------------List<ProductDTO>为空！");
        }
        return productDTOList;
    }

    @Override
    public List<Song> recommendSongs(Integer userId){
        CoreMath coreMath = new CoreMath();
        List<RelateDTO> relateDTOList = getCollectData();
        //执行推荐算法
        List<Integer> recommendations = coreMath.recommend(userId, relateDTOList);
        List<ProductDTO> productDTOList= getSongData().stream().filter(e->recommendations.contains(e.getProductId())).collect(Collectors.toList());
        //如果推荐id为空
        if(CollectionUtils.isEmpty(productDTOList)){
            System.out.println("-----------推荐的歌曲id集为空！");
            //return songMapper.allSong();
        }
        List<Song> songList = new ArrayList<>();
        //获取歌曲id
        List<Integer> productIdList = productDTOList.stream().map(e -> e.getProductId()).collect(Collectors.toList());
        //根据歌曲id获取歌曲
        for (Integer productId : productIdList) {
            songList.add(songMapper.selectByPrimaryKey(productId));
        }
        return songList;
    }

    @Override
    public List<RelateDTO> getRankData() {
        List<RelateDTO> relateList = Lists.newArrayList();
        List<Rank> rankList = rankMapper.selectRanks();
        for (Rank rank : rankList) {
            RelateDTO relateDTO = new RelateDTO();
            relateDTO.setUserId(rank.getConsumerId().intValue());
            relateDTO.setProductId(rank.getSongListId().intValue());
            relateDTO.setIndex(rank.getScore());
            relateList.add(relateDTO);
        }
        if(CollectionUtils.isEmpty(relateList)){
            System.out.println("--------------------List<RelateDTO>为空！");
        }
        return relateList;
    }


    @Override
    public List<ProductDTO> getSongListData() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<SongList> songLists = songListMapper.allSongList();
        for (SongList songList : songLists) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(songList.getId());
            productDTOList.add(productDTO);
        }
        if(CollectionUtils.isEmpty(productDTOList)){
            System.out.println("----------------------List<ProductDTO>为空！");
        }
        return productDTOList;
    }


    @Override
    public List<SongList> recommendSongListByRank(Integer userId){
        CoreMath coreMath = new CoreMath();
        List<RelateDTO> data = getRankData();
        //执行推荐算法
        List<Integer> recommendations = coreMath.recommend(userId, data);
        //根据返回的商品ids 从getProductData()进行过滤出 所有ProductDTO
        List<ProductDTO> productDTOS = getSongListData();
        List<ProductDTO> productDTOList= productDTOS.stream().filter(e->recommendations.contains(e.getProductId())).collect(Collectors.toList());
        //如果推荐id为空
        if(CollectionUtils.isEmpty(productDTOList)){
            System.out.println("-----------推荐的歌单id集为空！");
            return songListMapper.allSongList();
        }
        List<SongList> songList = new ArrayList<>();
        List<Integer> productIdList = productDTOList.stream().map(e -> e.getProductId()).collect(Collectors.toList());
        for (Integer productId : productIdList) {
            SongList songList1 = songListMapper.selectByPrimaryKey(productId);
            songList.add(songList1);
        }
        if(CollectionUtils.isEmpty(songList)){

            return songListMapper.allSongList();
        }
        return songList;
    }
    @Override
    public List<SongList> recommendSongListByCollect(Integer userId){
        //获取推荐歌曲
        List<Song> songs = recommendSongs(userId);
        //如果推荐歌曲为空，就返回所有歌单
        if(CollectionUtils.isEmpty(songs)){
            return songListMapper.allSongList();
        }
        //歌曲和歌单对应表
        List<ListSong> listSongLists = listSongMapper.allListSong();
        List<Integer> songIds = songs.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<ListSong> listSongs = listSongLists.stream().filter(e -> songIds.contains(e.getSongId())).collect(Collectors.toList());
        List<SongList> songLists = new ArrayList<>();
        //获取歌单id集
        List<Integer> songListIds = listSongs.stream().map(e -> e.getSongListId()).collect(Collectors.toList());
        //根据歌单id获取歌单
        for (Integer songListId : songListIds) {
            songLists.add(songListMapper.selectByPrimaryKey(songListId));
        }
        //如果推荐歌单为空，返回所有歌单
        if(CollectionUtils.isEmpty(songLists)){
            return songListMapper.allSongList();
        }
        return songLists;
    }



}
