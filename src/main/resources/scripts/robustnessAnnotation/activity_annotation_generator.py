#!/bin/env python3
# This script is supposed to reside in resources/scripts/robustnessAnnotation
# It can be used to generate XML files used by the robustnessAnnotation package.
# The script reads in all XML files in storyboard1/{processes,fragments} and pulls out all concrete activities.
# The files are parsed and the results are put in storyboard1/robustness
# Note that the demonstrator runs fine without anything present in robustness.
# How to use: just run the main method of this script from anywhere like
# "from activity_annotation_generator import main"
# The first run will generate the map, edit it and run the script again to generate the XML files

import re
import os
import random
from collections import defaultdict


def write_xml(files, activities, activity_types, robustness_dir='out'):
    for f in files:
        xmlFile = open(os.path.join(robustness_dir, f), 'w')
        xmlFile.write('<?xml version="1.0" encoding="UTF-8"?>\n')
        xmlFile.write('<robustnessAnnotation>\n')

        for activity in files[f]:
            if activity_types[activity] == 'process':
                xmlFile.write('\t<process name="{}">\n'.format(f))
            else:
                xmlFile.write('\t<fragment name="{}">\n'.format(f))

            activity_type = activities[activity] 
            xmlFile.write('\t\t<activity name="{}" compensable="{}" deterministic="{}" write="{}" robustness="{}"/>\n'.format(activity, activity_type[0], activity_type[1], activity_type[2], activity_type[3]))

            if activity_types[activity] == 'process':
                xmlFile.write('\t</process>\n')
            else:
                xmlFile.write('\t</fragment>\n')
        xmlFile.write('</robustnessAnnotation>\n')

        xmlFile.close()

def read_activity_map():
    activities = dict()
    try:
        f = open('activity_map.dat', 'r')
    except FileNotFoundError:
        return activities
    for line in f:
        if not line.startswith('#'):
            m = re.match("^(?P<action_name>.+) C:(?P<compensable>\w+) D:(?P<deterministic>\w+) W:(?P<write>\w+) R:(?P<robustness>\d\d).*", line)
            if m != None:
                activities[m.group('action_name')] = (m.group('compensable'), m.group('deterministic'), m.group('write'), m.group('robustness'))
    f.close()
    return activities


def print_activities(activities):
    for key in activities.keys():
        print('{}: {}'.format(key, activities[key]))

def main():
    activities = read_activity_map()
    current_dir = os.path.dirname(os.path.realpath(__file__))
    resources_dir = os.path.dirname(os.path.dirname(current_dir))
    fragments_dir = os.path.join(resources_dir, 'storyboard1', 'fragments')
    processes_dir = os.path.join(resources_dir, 'storyboard1', 'processes')
    robustness_dir = os.path.join(resources_dir, 'storyboard1', 'robustness')
    new_activities = dict()
    files = defaultdict(list)
    activity_types = dict()
    for process_file in os.listdir(processes_dir):
        if os.path.isfile(os.path.join(processes_dir, process_file)):
            with open(os.path.join(processes_dir, process_file), 'r') as f:
                for line in f:
                    m = re.match(".*<tns:concrete *name=\"(?P<action_name>.+)\".*", line)
                    if m != None:
                        files[process_file].append(m.group('action_name'))
                        activity_types[m.group('action_name')] = 'process'
                        if m.group('action_name') not in activities:
                            new_activities[m.group('action_name')] = 'process'
                    m = re.match(".*<tns:abstract *name=\"(?P<action_name>.+)\".*", line)
                    if m != None:
                        files[process_file].append(m.group('action_name'))
                        activity_types[m.group('action_name')] = 'process'
                        if m.group('action_name') not in activities:
                            new_activities[m.group('action_name')] = 'process'

    for fragment_file in os.listdir(fragments_dir):
        if os.path.isfile(os.path.join(fragments_dir, fragment_file)):
            with open(os.path.join(fragments_dir, fragment_file), 'r') as f:
                for line in f:
                    m = re.match(".*<tns:action *name=\"(?P<action_name>.+)\" *actionType=\"(?P<action_type>.+)\">", line)
                    if m != None:
                        files[fragment_file].append(m.group('action_name'))
                        if m.group('action_name') in activity_types:
                            print('name clash in {}!'.format(m.group('action_name')))
                        activity_types[m.group('action_name')] = 'fragment'
                        if m.group('action_name') not in activities:
                            new_activities[m.group('action_name')] = m.group('action_type')
    
    try:
        activity_map = open('activity_map.dat', 'a')
    except FileNotFoundError:
        activity_map = open('activity_map.dat', 'w')

    if len(new_activities) > 0:
        print('Adding new Activities')
        activity_map.write('### New Activities added below this line ###\n')
    else:
        print('No new activities were found')
    for key in new_activities.keys():
        robustness = random.randrange(90,100)
        new_activities[key] = robustness
        activity_map.write('{} C:{} D:{} W:{} R:{}\n'.format(key, 'true', 'false', 'true', robustness))
    activity_map.close()


    ## add new activities with default values
    for key in new_activities.keys():
        activities[key] = ('true', 'false', 'true', new_activities[key])

    write_xml(files, activities, activity_types, robustness_dir)


if __name__ == '__main__':
    main()
