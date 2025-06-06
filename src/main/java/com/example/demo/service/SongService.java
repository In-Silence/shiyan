package com.example.demo.service;

import com.example.demo.domain.Song;

import java.util.List;

public interface SongService {

    boolean addSong (Song song);

    boolean updateSongMsg(Song song);

    boolean updateSongUrl(Song song);

    boolean updateSongPic(Song song);

    boolean deleteSong(Integer id);

    List<Song> allSong();

    List<Song> songOfSingerId(Integer singerId);

    Song songOfId(Integer id);

    List<Song> songOfSingerName(String name);

    List<Song> songOfName(String name);
}
