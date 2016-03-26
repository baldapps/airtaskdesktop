#!/bin/bash
cd "$(dirname "$0")"
for entry in `ls .`; do
   if [[ $entry =~ ^airtaskdesktop\-.*\-all\.jar ]]; then
       java -jar $entry
       exit
   fi
done

