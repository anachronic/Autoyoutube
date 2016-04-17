from songs import Song

promises = Song('https://www.youtube.com/watch?v=yExPBSDnbU8')

print(promises.gettitle())
print(promises.getid())
promises.download()
promises.put_tags()
