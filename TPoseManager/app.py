from flask import Flask, jsonify, send_from_directory
import os
import ffmpeg

app = Flask("T-Pose Manager")

VIDEOS_DIR = "/home/pi/gabtposedevantegee/tposes"

@app.route("/videos")
def videos():
    res = []
    filenames = os.listdir(VIDEOS_DIR)
    for name in filenames:
        path = os.path.join(VIDEOS_DIR, name)
        if not os.path.isfile(path):
            continue
        metadata = ffmpeg.probe(path)["streams"][0]
        res.append({
            'duration':  metadata["duration"],
            'date': os.path.getmtime(path),
            'id': name[:name.index('.')]
        })
    return jsonify(res)

@app.route('/video/<id>')
def send_video(id):
    return send_from_directory(VIDEOS_DIR, id + ".h264")

if __name__ == "__main__":
    app.run(host="0.0.0.0")
