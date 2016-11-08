import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk

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

        self.add(self.vbox)

    def on_search(self, widget):
        song = Song(self.url.get_text())

        self.result_label.set_text(song.get_title())
