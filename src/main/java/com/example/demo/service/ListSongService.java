package com.example.demo.service;

import com.example.demo.domain.ListSong;

import java.util.List;

public interface ListSongService {

    boolean addListSong(ListSong listSong);

    boolean updateListSongMsg(ListSong listSong);

    boolean deleteListSong(Integer songId);

    boolean deleteBySongIdAndSongListId(Integer songId, Integer songListId);


    List<ListSong> allListSong();

    List<ListSong> listSongOfSongId(Integer songListId);
}
