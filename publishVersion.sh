#!/usr/bin/env bash
# slightly different on mac:
# sed -i "" "1s/.*/\[\!\[Version\](https:\/\/img\.shields\.io\/badge\/style-$TRAVIS_BUILD_NUMBER-green\.svg\?style=flat\&label=version)\]()/" README.md
sed -i "1s/.*/\[\!\[Version\](https:\/\/img\.shields\.io\/badge\/style-$TRAVIS_BUILD_NUMBER-green\.svg\?style=flat\&label=version)\]()/" README.md
git checkout -b __temp
git add README.md
git commit -m "[ci skip] update version badge in README"
git push origin __temp:master
