from gi.repository import GLib

from songs import Song


# This function downloads every song in the `songs' array. The process
# isn't actually async, but it is intended to be a function called by
# a thread. That's why it's in this package.
# songs <- array of Song
# label <- Gtk.Label
def async_download(songs, label):
    for song in songs:
        msg = 'Downloading ' + song.get_name() + ' by ' + song.get_artist()

        GLib.idle_add(update_label, label, msg)
        song.download()

    update_label(label, 'Done downloading.')


def update_label(label, message):
    label.set_text(message)


def async_search(url, window):
    # only for song right now
    song = Song(url)

    song.fill_song_metadata()
    row = (song.get_artist(), song.get_name(), song.get_id())
    window.list_store.append(row)
    window.result_label.set_text(song.get_title())
    window.songs.append(song)

    show_download_button(window)


def show_download_button(window):
    window.vbox.pack_start(window.download_button, True, True, 0)
    window.download_button.set_visible(True)
