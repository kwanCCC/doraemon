#!/usr/bin/python
import urllib2
import json
import sys

rs=0


for i in range(2, len(sys.argv)):
    
    html = urllib2.urlopen('http://'+sys.argv[1]+':'+sys.argv[i]+'/health')

    hjson = json.loads(html.read())
    status=hjson.get("status")
    if status=="UP":
        rs+=1

print rs
