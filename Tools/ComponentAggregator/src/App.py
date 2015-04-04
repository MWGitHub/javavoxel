__author__ = 'MW'

import os
import re
import json

root = "../../../"

componentFiles = []
for path, dirs, files in os.walk(root):
    for file in files:
        if file.endswith("Component.java"):
            out = os.path.join(path, file)
            if not out.endswith("\Component.java"):
                componentFiles.append(os.path.join(path, file))


def find_between(s, first, last):
    try:
        start = s.index(first) + len(first)
        end = s.index(last, start)
        return s[start:end]
    except ValueError:
        return ""


fieldType = re.compile('public (.*?) ', re.DOTALL)
varName = re.compile('public (.*?) (.*?)', re.DOTALL)

components = {}
for file in componentFiles:
    with open(file, "r") as data:
        output = data.read().replace('\n', '')
        start = "public class "
        end = " implements Component"

        name = find_between(output, start, end)
        components[name] = {}

        '''
        match = fieldType.findall(output)
        for data in match:
            if data != 'class':
                components[name]['types'] = data
        match = varName.findall(output)
        for data in match:
            print data
        '''

print json.dumps(components)