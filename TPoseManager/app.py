from flask import Flask, jsonify, send_from_directory, request
import os
import ffmpeg
import json

app = Flask("T-Pose Manager")

VIDEOS_DIR = "/home/pi/gabtposedevantegee/tposes"
SAVED_DIR = "/home/pi/gabtposedevantegee/savedtposes"
SUFFIX = ".h264"


@app.route("/videos", methods=['GET'])
def videos():
    res = []
    filenames = os.listdir(VIDEOS_DIR)
    for name in filenames:
        path = os.path.join(VIDEOS_DIR, name)
        if not os.path.isfile(path):
            continue
        metadata = ffmpeg.probe(path)["streams"][0]
        res.append({
            'duration': metadata["duration"],
            'date': os.path.getmtime(path),
            'id': name[:name.index('.')]
        })
    return jsonify(res)


@app.route('/video/<id>', methods=['GET'])
def send_video(id):
    return send_from_directory(VIDEOS_DIR, id + SUFFIX)


@app.route('/videos/upload', methods=['POST'])
def upload_videos():
    for i in request.json:
        name = str(i) + SUFFIX
        os.rename(os.path.join(VIDEOS_DIR, name), os.path.join(SAVED_DIR, name))
    return "ok"


@app.route('/videos/delete', methods=['POST'])
def delete_video():
    for i in request.json:
        name = str(i) + SUFFIX
        os.remove(os.path.join(VIDEOS_DIR, name))
    return "ok"


if __name__ == "__main__":
    app.run(host="0.0.0.0")
