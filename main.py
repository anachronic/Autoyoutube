from songs import Song

promises = Song('https://www.youtube.com/watch?v=yExPBSDnbU8')

print(promises.get_title())
print(promises.get_id())
promises.download()
