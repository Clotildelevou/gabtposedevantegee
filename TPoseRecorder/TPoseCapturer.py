import io
import random
import picamera
from PIL import Image

prior_image = None
prior_buffer = None
changedPixels = 0
filename = "capture-%04d%02d%02d-%02d%02d%02d.h264" % (time.year, time.month, time.day, time.hour, time.minute, time.second)
threshold = 10
sensitivity = 20
width = 1280
height = 720

def TPoseDetector(camera):
    global prior_image
    stream = io.BytesIO()
    camera.capture(stream, format='jpeg', use_video_port=True)
    stream.seek(0)
    if prior_image is None:
        prior_image = Image.open(stream)
        prior_buffer = prior_image.load()
        return False
    else:
        current_image = Image.open(stream)
        current_buffer = current_image.load()

        for x in xrange(0, width):
            for y in xrange(0, height):
                # Just check green channel as it's the highest quality channel
                pixdiff = abs(buffer1[x,y][1] - buffer2[x,y][1])
                if pixdiff > threshold:
                    changedPixels += 1
        # Once motion detection is done, make the prior image the current
        prior_image = current_image
        prior_buffer = prior_image.load()
        return return changedPixels > sensitivity
