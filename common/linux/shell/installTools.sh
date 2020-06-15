#!/bin/bash -e

cd ~/Downloads

curl -O https://statics.maiscrm.com/maiscrm-os-init/google-chrome-stable_current_amd64.deb
sudo apt-get -y -f install ./google-chrome-stable_current_amd64.deb
rm ./google-chrome-stable_current_amd64.deb
echo "浏览器安装完毕"

curl -O https://statics.maiscrm.com/maiscrm-os-init/code_1.36.0-1562161087_amd64.deb
sudo apt-get -y -f install ./code_1.36.0-1562161087_amd64.deb
rm ./code_1.36.0-1562161087_amd64.deb
echo "IDE安装完毕"

curl -O https://statics.maiscrm.com/maiscrm-os-init/sogoupinyin_2.2.0.0108_amd64.deb
sudo apt-get -y -f install ./sogoupinyin_2.2.0.0108_amd64.deb
im-config -n fcitx
rm ./sogoupinyin_2.2.0.0108_amd64.deb
echo "输入法安装完毕，请自行选择合适的时机重启系统."
