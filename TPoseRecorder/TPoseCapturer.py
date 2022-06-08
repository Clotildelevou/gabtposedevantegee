import io
from time import gmtime, strftime
import random
import picamera
import TPoseSettings as set
from PIL import Image

prior_image = None
prior_buffer = None
changedPixels = 0
filename = strftime("capture-%H:%S_%d_%m_%Y", gmtime())

threshold, sensitivity = set.TPoseMotion()
width, height = set.TPoseSize()

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

        for x in range(0, width):
            for y in range(0, height):
                # Just check green channel as it's the highest quality channel
                pixdiff = abs(current_buffer[x,y][1] - prior_buffer[x,y][1])
                if pixdiff > threshold:
                    changedPixels += 1
        # Once motion detection is done, make the prior image the current
        prior_image = current_image
        prior_buffer = prior_image.load()
        return changedPixels > sensitivity

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
