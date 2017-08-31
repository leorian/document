#!/usr/bin/env bash

cd /Applications/OpenOffice.app/Contents/program
./soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard &

 
