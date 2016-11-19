# This file is intended to provide misc utilities for the app
# development. At the time of it's creation, it can determine whether
# an URL is playlist or song, and get a song object from a song list
# based on the song's ID


def url_is_playlist(url):
    # For now we do this here
    # TODO: Need to move this somewhere else
    playlist_words = ("list=", "playlist?")
    assert_words = ("youtube.com")

    for word in assert_words:
        if url.find(word) == -1:
            raise ValueError

    for word in playlist_words:
        if url.find(word) >= 0:
            return True

    return False


def get_song_by_id(songs, id):
    if not songs:
        return None

    for s in songs:
        if s.get_id() == id:
            return s

    return None
