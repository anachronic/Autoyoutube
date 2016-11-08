from __future__ import unicode_literals
import os
import youtube_dl

from mutagen.easyid3 import EasyID3

YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v="


class DontShowLogger(object):
    def debug(self, msg):
        pass

    def warning(self, msg):
        pass

    def error(self, msg):
        pass


class Song(object):
    def __init__(self, url):
        self.url = url
        with youtube_dl.YoutubeDL({'quiet': True, 'verbose': False}) as ydl:
            info = ydl.extract_info(self.url, download=False)

            self.info = info

        self.location = None

    def get_title(self):
        return self.info['title']

    def get_id(self):
        return self.info['id']

    def get_url(self):
        return YOUTUBE_VIDEO_URL + self.info['id']

    def download(self):
        ydl_opts = {
            'format': 'bestaudio/best',
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }],
            'logger': DontShowLogger(),
            'outtmpl': '%(id)s.%(ext)s',
        }

        with youtube_dl.YoutubeDL(ydl_opts) as ydl:
            ydl.download([self.url])

        self.location = self.info['id'] + '.mp3'

    def put_tags(self, artistfirst=True, separator='-'):
        file = self.location

        striped = [x.strip() for x in self.get_title().split(separator)]

        if (artistfirst):
            artist = striped[0]
            name = striped[1]
        else:
            artist = striped[1]
            name = striped[0]

        audio = EasyID3(file)
        audio['title'] = name
        audio['artist'] = artist

        audio.save()

        self.rename(artist + ' - ' + name + '.mp3')

    def rename(self, desiredname):
        # This will raise an exception if the file doesn't exist anyway
        # TODO: Maybe get absolute path from song and desired name? (or at
        # least save current directory...)
        newloc = str(desiredname)
        os.rename(self.location, newloc)
        self.location = newloc
