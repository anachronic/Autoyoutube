import youtube_dl.YoutubeDL

import songs
from songs import Song


class Playlist(object):
    """A youtube playlist object

    """

    def __init__(self, url):
        self.url = url

        with youtube_dl.YoutubeDL({'quiet': True, 'verbose': False}) as ydl:
            info = ydl.extract_info(self.url, download=False)

            self.entries = info['entries']
            self.name = info['title']

            self.ids = []
            for entry in info['entries']:
                self.ids.append(entry['id'])

    def get_ids(self):
        return self.ids

    def get_name(self):
        return self.name

    def download(self):
        # for every id construct a song object and download it.

        for sid in self.ids:
            song = Song(songs.YOUTUBE_VIDEO_URL + sid)

            song.download()
