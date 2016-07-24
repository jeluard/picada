#!/bin/sh

boot package

mkdir dist
cp target/picada.css dist/picada.css
cp target/main.js dist/main.js
cp resources-dev/index.html dist/index.html

# https://github.com/X1011/git-directory-deploy
#./deploy.sh
