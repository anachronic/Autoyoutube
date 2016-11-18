import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

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

        button = Gtk.Button(label="Search")
        button.connect("clicked", self.on_search)
        hbox.pack_start(button, True, True, 0)

        self.result_label = Gtk.Label()

        self.vbox = Gtk.Box(spacing=6, orientation=Gtk.Orientation.VERTICAL)
        self.vbox.pack_start(hbox, True, True, 0)
        self.vbox.pack_start(self.result_label, True, True, 0)

        self.build_tree_view()

        self.vbox.pack_start(self.treeview, True, True, 0)

        # Add the download button at the bottom
        self.download_button = Gtk.Button(label="Download")

        self.add(self.vbox)

    # TODO: Maybe unify these two? They are so similar...
    def on_artist_edited(self, cellrenderer, path, new_text):
        model = self.treeview.get_model()
        iter = model.get_iter_from_string(path)

        model.set(iter, [0], [new_text])

        # change the song's artist
        # The 2 there is the id column
        song = self.get_song_by_id(model[path][2])
        song.set_artist(new_text)

    def on_songname_edited(self, cellrenderer, path, new_text):
        model = self.treeview.get_model()
        iter = model.get_iter_from_string(path)

        model.set(iter, [1], [new_text])

        # Same as def above: change song's name
        # The 2 below corresponds to the id column
        song = self.get_song_by_id(model[path][2])
        song.set_name(new_text)

    def on_search(self, widget):
        url = self.url.get_text()

        try:
            is_song = not self.url_is_playlist(url)
        except ValueError:
            self.result_label.set_text("That is absolutely not an URL.")
            return

        self.songs = []

        if is_song:
            song = Song(url)
            song.fill_song_metadata()
            # Append song's artist and title into self.candidates
            # then add the items to the list_store. that should suffice

            row = (song.get_artist(), song.get_name(), song.get_id())
            self.list_store.append(row)
            self.result_label.set_text(song.get_title())
            self.songs.append(song)
        else:
            plist = Playlist(url)
            self.result_label.set_text(plist.get_name())

            for song in plist.get_songs():
                song.fill_song_metadata()

                row = (song.get_artist(), song.get_name(), song.get_id())
                self.list_store.append(row)
                self.songs.append(song)

        self.vbox.pack_start(self.download_button, True, True, 0)
        self.download_button.set_visible(True)
        self.download_button.connect("clicked", self.on_download)

        self.treeview.show()

    def on_download(self, widget):
        # Need to extract them from the model, otherwise we would
        # download the songs in a chaotic order. Especially if the
        # user changed artist/song names and/or ordered the treeview
        model = self.treeview.get_model()

        for row in model:
            song = self.get_song_by_id(row[2])
            print('downloading ' + song.get_artist() + ' by ' + song.get_name(
            ))

    def build_tree_view(self):
        # Build the TreeView and Columns
        self.list_store = Gtk.ListStore(str, str, str)
        self.treeview = Gtk.TreeView(self.list_store)

        renderer = Gtk.CellRendererText()
        renderer.set_property("editable", True)
        renderer.connect("edited", self.on_artist_edited)

        col = Gtk.TreeViewColumn("Artist", renderer, text=0)
        col.set_sort_column_id(0)

        self.treeview.append_column(col)

        renderer = Gtk.CellRendererText()
        renderer.set_property("editable", True)
        renderer.connect("edited", self.on_songname_edited)

        col = Gtk.TreeViewColumn("Song", renderer, text=1)
        col.set_sort_column_id(1)

        self.treeview.append_column(col)

        renderer = Gtk.CellRendererText()
        idcol = Gtk.TreeViewColumn("id", renderer, text=2)
        idcol.set_visible(False)
        self.treeview.append_column(idcol)
        # End of TreeView stuff.

    # defuns from here to bottom should be moved to another file at
    # some point

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

    def get_song_by_id(self, id):
        if not self.songs:
            return None

        for s in self.songs:
            if s.get_id() == id:
                return s

        return None
