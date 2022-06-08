from flask import Flask
import os
import ffmpeg

app = Flask("T-Pose Manager")
app.config.from_pyfile('config.py')

@app.route("/videos")
def videos():
    videosdir = app.config['videos_dir']
    res = []

    filenames = os.listdir(videosdir)
    for name in filenames:
        path = os.path.join(videosdir, name)
        if not os.path.isfile(path):
            continue
        metadata = ffmpeg.probe(path)["streams"][0]
        res.append({
            'duration':  metadata["duration"],
            'date': os.path.getmtime(path)
        })

    return res