package com.example.model

enum class SortOption {
    YEAR_RELEASED_DESC,
    YEAR_RELEASED_ASC,
    LATEST_ADDED,
    TITLE_ASC,
    ARTIST_ASC
}

enum class LibraryTab {
    SONGS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS
}

enum class AppThemeMode {
    AMOLED_DARK,
    MIDNIGHT_DARK,
    STUDIO_LIGHT,
    SYSTEM
}

enum class RepeatMode {
    OFF,
    REPEAT_ALL,
    REPEAT_ONE
}
