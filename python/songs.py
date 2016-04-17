from __future__ import unicode_literals
import youtube_dl


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

        # ydl_opts = {
        #     'format': "bestaudio/best",
        #     'extractaudio': True,
        #     'audioformat': 'mp3',
        #     'outtmpl': '/home/nsalas/%(id)s.%(ext)s',
        #     'noplaylist': True,
        # }

        with youtube_dl.YoutubeDL(ydl_opts) as ydl:
            ydl.download([self.url])
