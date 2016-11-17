import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

import songs
from playlists import Playlist
from songs import Song


class AytWindow(Gtk.Window):
    def __init__(self):
        Gtk.Window.__init__(self, title="Autoyoutube")
        self.set_border_width(10)

        self.set_default_size(800, 100)

        hbox = Gtk.Box(spacing=6)

        self.url = Gtk.Entry()
        hbox.pack_start(self.url, True, True, 0)

        button = Gtk.Button(label="Buscar")
        button.connect("clicked", self.on_search)
        hbox.pack_start(button, True, True, 0)

        self.result_label = Gtk.Label()

        self.vbox = Gtk.Box(spacing=6, orientation=Gtk.Orientation.VERTICAL)
        self.vbox.pack_start(hbox, True, True, 0)
        self.vbox.pack_start(self.result_label, True, True, 0)

        # we begin with no candidates whatsoever
        self.candidates = []

        columns = ("Artist", "Song")

        self.list_store = Gtk.ListStore(str, str)
        self.treeview = Gtk.TreeView(self.list_store)

        for i, column in enumerate(columns):
            renderer = Gtk.CellRendererText()
            col = Gtk.TreeViewColumn(column, renderer, text=i)
            self.treeview.append_column(col)

        self.vbox.pack_start(self.treeview, True, True, 0)

        self.add(self.vbox)

    def on_search(self, widget):
        url = self.url.get_text()

        try:
            is_playlist = self.url_is_playlist(url)
        except ValueError:
            self.result_label.set_text("That is absolutely not an URL.")
            return

        if not is_playlist:
            song = Song(url)
            song.fill_song_metadata()
            # Append song's artist and title into self.candidates
            # then add the items to the list_store. that should suffice

            row = (song.get_artist(), song.get_name())
            self.list_store.append(row)
            self.result_label.set_text(song.get_title())
        else:
            # This doesn't really look good. But let's do it anyway
            # The playlist should hold the song ids and the process
            # should be async since its SLOOOOOOOOOOOOOOOW
            plist = Playlist(url)

            ids = plist.get_ids()
            for sid in ids:
                song = Song(songs.YOUTUBE_VIDEO_URL + sid)
                song.fill_song_metadata()

                row = (song.get_artist(), song.get_name())
                self.list_store.append(row)
                self.result_label.set_text("It's a playlist!")


    def url_is_playlist(self, url):
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
