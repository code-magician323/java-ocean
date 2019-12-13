## python --pip

### pip 安装

```shell
# 安装或重置
sudo apt-get install python3-pip
* wget https://bootstrap.pypa.io/get-pip.py
* sudo python3 get-pip.py
# 查看pip信息
pip --version
# 获取帮助
pip --help
# 安装指定版本的
pip install django==1.11.7
# 升级 pip
pip install -U pip
# 升级 pip2
python -m pip install --upgrade pip
# 安装包
pip install SomePackage=1.1.1
# 卸载包
pip uninstall SomePackage
# 升级指定的包
pip install -U SomePackage
# 搜索包
pip search SomePackage
# 查看指定包的详细信息
pip show -f SomePackage
# 列出已安装的包
pip freeze or pip list
# 查看可升级的包
pip list -o
```
