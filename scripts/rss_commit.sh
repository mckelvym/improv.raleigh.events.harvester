#!/bin/bash

cd ~/Documents/improv.raleigh.events.rss
date=$(date +%Y-%m-%d_%H.%M.%S)
size=$(du -h events.xml | cut -f1)
items=$(grep -c '<item>' events.xml)
git add events.xml && git commit -m "$date $size bytes, $items items" && git push
exit
