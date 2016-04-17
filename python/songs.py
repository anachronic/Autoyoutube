from __future__ import unicode_literals
import os
import youtube_dl

from mutagen.easyid3 import EasyID3


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

    def gettitle(self):
        return self.info['title']

    def getid(self):
        return self.info['id']

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

    def put_tags(self, artistfirst=True, separator='-'):
        file = self.info['id'] + '.mp3'

        striped = [x.strip() for x in self.gettitle().split(separator)]

        if(artistfirst):
            artist = striped[0]
            name = striped[1]
        else:
            artist = striped[1]
            name = striped[0]

        audio = EasyID3(file)
        audio['title'] = name
        audio['artist'] = artist

        audio.save()

        self.rename(artist + ' - ' + name)

    def rename(self, desiredname):
        # TODO: Maybe get absolute path from song and desired name? (or at
        # least save current directory...)
        os.rename(str(self.info['id']) + '.mp3', str(desiredname) + '.mp3')
