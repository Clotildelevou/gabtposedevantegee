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

def TPoseRecorder(time):
    with picamera.PiCamera() as camera:
        camera.resolution = (width, height)
        stream = picamera.PiCameraCircularIO(camera, seconds=time)
        camera.start_recording(stream, format='h264')
        try:
            while True:
                camera.wait_recording(1)
                if detect_motion(camera):
                    print('Motion detected!')
                    # As soon as we detect motion, split the recording to
                    # record the frames "after" motion
                    camera.split_recording(filename + '.h264')
                    stream.clear()
                    # Wait until motion is no longer detected, then split
                    # recording back to the in-memory circular buffer
                    while detect_motion(camera):
                        camera.wait_recording(1)
                    print('Motion stopped!')
                    camera.split_recording(stream)
        finally:
            camera.stop_recording()
