from gi.repository import GLib


# This function downloads every song in the `songs' array. The process
# isn't actually async, but it is intended to be a function called by
# a thread. That's why it's in this package.
# songs <- array of Song
# label <- Gtk.Label
def async_download(songs, label):
    for song in songs:
        msg = 'Downloading ' + song.get_name() + ' by ' + song.get_artist()
        GLib.idle_add(update_label, label, msg)

        label.set_text(msg)
        song.download()

    label.set_text('Done downloading')


def update_label(label, message):
    label.set_text(message)
