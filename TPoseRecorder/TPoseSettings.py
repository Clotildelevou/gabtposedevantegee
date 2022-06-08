import os
import json


def TPoseSize():
    with open("settings.json") as file:
        settings = json.load(file)
        width = settings["video-settings"]["width"]
        height = settings["video-settings"]["height"]
        return int(width), int(height)

def TPoseMotion():
    with open("settings.json") as file:
        settings = json.load(file)
        threshold = settings["motion-sensors"]["threshold"]
        sensitivity = settings["motion-sensors"]["sensitivity"]
        return int(threshold), int(sensitivity)
