from __future__ import unicode_literals

import json
import os
import re

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
        self.artist = None
        self.name = None

    def get_title(self):
        return self.info['title']

    def get_id(self):
        return self.info['id']

    def get_artist(self):
        return self.artist

    def get_name(self):
        return self.name

    def set_name(self, name):
        self.name = name

    def set_artist(self, artist):
        self.artist = artist

    def get_url(self):
        return YOUTUBE_VIDEO_URL + self.info['id']

    def download(self, with_auto_tags=True, auto_rename=True):
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

        if with_auto_tags:
            self.put_tags()

        if auto_rename:
            self.rename(self.artist + ' - ' + self.name + '.mp3')

    # This gets a string like
    # (HQ) Gramatik - While I Was Playin' Fair [Beatz & Pieces Vol. 1]
    # To Gramatik - While I Was Playin' Fair
    # Of course, this returns a string, so don't use it outside this class
    def strip_bad_words(self, strings_file):
        strings = json.load(open(str(strings_file)))
        title = self.get_title()

        for badpair in strings['delete_inside']:
            start = badpair[0]
            end = badpair[1]

            i = title.find(start)
            k = title.find(end)
            if i == -1 or k == -1:
                continue

            # we have the indexes for the parenthesis: i and k.
            inside = title[i + 1:k].lower()

            delete = True
            for goodword in strings['good_words']:
                delete = delete and inside.find(goodword) == -1

            if delete:
                title = title[0:i] + title[k + 1:]
                title = title.strip()

        return title

    def fill_song_metadata(self, separator='-+', artistfirst=True):
        title = self.strip_bad_words('strings.json')

        self.title = title

        striped = [x.strip() for x in re.split(separator, title)]

        if (artistfirst):
            self.artist = striped[0]
            self.name = striped[1]
        else:
            self.artist = striped[1]
            self.name = striped[0]

    def put_tags(self):
        file = self.location
        audio = EasyID3(file)

        if self.artist is None or self.name is None:
            self.fill_song_metadata()

        audio['title'] = self.name
        audio['artist'] = self.artist

        audio.save()

    def rename(self, desiredname):
        # This will raise an exception if the file doesn't exist anyway
        # TODO: Maybe get absolute path from song and desired name? (or at
        # least save current directory...)
        newloc = str(desiredname).replace("/", "-")
        os.rename(self.location, newloc)
        self.location = newloc
