import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk


class AytWindow(Gtk.Window):
    def __init__(self):
        Gtk.Window.__init__(self, title="Autoyoutube")
        self.set_border_width(10)

        hbox = Gtk.Box(spacing=6)
        self.add(hbox)

        self.url = Gtk.Entry()
        hbox.pack_start(self.url, True, True, 0)

        button = Gtk.Button(label="Buscar")
        button.connect("clicked", self.on_search)
        hbox.pack_start(button, True, True, 0)

    def on_search(self, widget):
        print("Search bro")
