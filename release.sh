#!/bin/sh

boot release

mkdir dist
cp target/picada.css dist/picada.css
cp target/picada.js dist/picada.js
cp resources-dev/index.html dist/index.html

# https://github.com/X1011/git-directory-deploy
./deploy.sh
