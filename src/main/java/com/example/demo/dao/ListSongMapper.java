package com.example.demo.dao;

import com.example.demo.domain.ListSong;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListSongMapper {

    int insertSelective(ListSong record);

    ListSong selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(ListSong record);

    int updateListSongMsg(ListSong record);

    int deleteListSong(Integer songId);

    int deleteBySongIdAndSongListId(@Param("songId") Integer songId,@Param("songListId") Integer songListId);

    List<ListSong> allListSong();

    List<ListSong> listSongOfSongId(Integer songListId);
}
