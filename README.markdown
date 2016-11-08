# Autoyoutube

A tool to download music from youtube. It's intended to do these steps

1. Download a song
2. Infer the artist and song names
3. Put those in ID3v2 tags

## Future

Up next the application should:

* Be able to download a playlist
* Have a GUI
* Give the user the option to change inferred song/artist names
* Maybe organize the collection.

### How and what

Well, downloading a playlist is already written in the **Java** section. Next step is to take that and migrate it to Python. When it gets done we'll erase the java part of this repository.

The user interface will be written using PyGobject or any flavour of Gtk development for Python. I still have a long way to go to get this done, but I'm sure I'll find some way to do it. This should get the change artist/song names part done easily.

About organizing the collection I don't really know right now, I'll investigate if any GNU/Linux players already do that. I suspect Rhythmbox does, so we'll have to see about that.

## Dependencies

Look at `requirements.txt`. Suggested working environment is `ayt`.
