package com.topview.purejoy.home.data.source

import com.topview.purejoy.common.net.ServiceCreator
import com.topview.purejoy.common.net.awaitAsync
import com.topview.purejoy.home.data.api.HomeService
import com.topview.purejoy.home.data.bean.*
import com.topview.purejoy.home.entity.*

private const val BANNER_TYPE: Int = 1
private const val SEARCH_SONG_TYPE = 1
private const val SEARCH_PLAYLIST_TYPE = 1000

class HomeRemoteStore {
    private val homeService = ServiceCreator.create(HomeService::class.java)

    suspend fun getBanners(): List<HomeDiscoverBannerItem>? {
        val bannerJson: BannerJson? = homeService.getBanners(BANNER_TYPE).awaitAsync()
        if (bannerJson != null) {
            val banners = bannerJson.banners
            if (banners != null) {
                val list = mutableListOf<HomeDiscoverBannerItem>()
                banners.forEach {
                    list.add(HomeDiscoverBannerItem(it.pic, it.url))
                }
                return list
            }
        }
        return null
    }

    suspend fun getDailyRecommendPlayList(limit: Int): List<PlayList>? {
        val dailyRecommendPlayListJson: DailyRecommendPlayListJson? =
            homeService.getDailyRecommendPlayList(limit).awaitAsync()
        if (dailyRecommendPlayListJson != null) {
            val result = dailyRecommendPlayListJson.result
            if (result != null) {
                val list = mutableListOf<PlayList>()
                result.forEach {
                    val dailyRecommendPlayList =
                        PlayList(it.id, it.name, it.picUrl, it.playCount)
                    list.add(dailyRecommendPlayList)
                }
                return list
            }
        }
        return null
    }

    suspend fun getRecommendNewSong(limit: Int): List<Song>? {
        val recommendNewSongJson: RecommendNewSongJson? =
            homeService.getRecommendNewSong(limit).awaitAsync()
        if (recommendNewSongJson != null) {
            val result = recommendNewSongJson.result
            if (result != null) {
                val list = mutableListOf<Song>()
                result.forEach {
                    val artistNameBuilder = StringBuilder()

                    // 拼接歌手的名字
                    val artists: List<RecommendNewSongJson.Result.Song.Artist>? = it.song?.artists
                    artists?.forEach { artist ->
                        artistNameBuilder.append(artist.name + " ")
                    }
                    list.add(
                        Song(
                            id = it.id,
                            name = it.name,
                            picUrl = it.picUrl,
                            artistName = artistNameBuilder.toString()
                        )
                    )
                }
                return list
            }
        }
        return null
    }

    /**
     * 搜索歌曲，第一次请求，返回的实体类SongPagerWrapper中，包含总歌曲数量的信息，方便
     * 分页加载
     * */
    suspend fun getSearchSongByFirst(keyword: String, limit: Int): SongPagerWrapper? {
        val searchSongJson: SearchSongJson? =
            homeService.getSearchSongs(keyword, SEARCH_SONG_TYPE, 0, limit).awaitAsync()
        if (searchSongJson != null) {
            val result = searchSongJson.result
            if (result != null) {
                val songCount = result.songCount
                val songs = result.songs
                if (songs != null) {
                    return SongPagerWrapper(parseSearchSongs(songs), songCount)
                }
            }
        }
        return null
    }

    suspend fun loadMoreSongs(keyword: String, offset: Int, limit: Int): List<Song>? {
        val searchSongJson: SearchSongJson? =
            homeService.getSearchSongs(keyword, SEARCH_SONG_TYPE, offset, limit).awaitAsync()
        if (searchSongJson != null) {
            val result = searchSongJson.result
            if (result != null) {
                val songs = result.songs
                if (songs != null) {
                    return parseSearchSongs(songs)
                }
            }
        }
        return null
    }

    suspend fun getSearchPlayListByFirst(keyword: String, limit: Int): PlayListPagerWrapper? {
        val searchPlayListJson: SearchPlayListJson? =
            homeService.getSearchPlayLists(keyword, SEARCH_PLAYLIST_TYPE, 0, limit).awaitAsync()
        if (searchPlayListJson != null) {
            val result = searchPlayListJson.result
            if (result != null) {
                val playlistCounts = result.playlistCount
                val playlists = result.playlists
                if (playlists != null) {
                    return PlayListPagerWrapper(parseSearchPlayList(playlists), playlistCounts)
                }
            }
        }
        return null
    }

    suspend fun loadMorePlayLists(keyword: String, offset: Int, limit: Int): List<PlayList>? {
        val searchPlayListJson: SearchPlayListJson? =
            homeService.getSearchPlayLists(keyword, SEARCH_PLAYLIST_TYPE, offset, limit).awaitAsync()
        if (searchPlayListJson != null) {
            val result = searchPlayListJson.result
            if (result != null) {
                val playlists = result.playlists
                if (playlists != null) {
                    return parseSearchPlayList(playlists)
                }
            }
        }
        return null
    }

    private fun parseSearchSongs(songJson: MutableList<SearchSongJson.Result.Song>): List<Song> {
        val searchSongs = mutableListOf<Song>()
        songJson.forEach {
            val artistNameBuilder = StringBuilder()

            // 拼接歌手的名字
            val artists: List<SearchSongJson.Result.Song.Artist>? = it.artists
            artists?.forEach { artist ->
                artistNameBuilder.append(artist.name + " ")
            }

            searchSongs.add(
                Song(
                    id = it.id,
                    name = it.name,
                    artistName = artistNameBuilder.toString()
                )
            )
        }
        return searchSongs
    }

    private fun parseSearchPlayList(playlistJson: MutableList<SearchPlayListJson.Result.PlayList>):
            List<PlayList> {
        val searchPlayLists = mutableListOf<PlayList>()
        playlistJson.forEach {
            searchPlayLists.add(
                PlayList(
                    id = it.id,
                    name = it.name,
                    picUrl = it.picUrl,
                    playCount = it.playCount,
                    songCounts = it.songCounts
                )
            )
        }
        return searchPlayLists
    }

}