## [Ubuntu install SQLServer](https://docs.microsoft.com/zh-cn/sql/linux/quickstart-install-connect-ubuntu?view=sql-server-2017)

```shell
# 1. 导入公共存储库 GPG 密钥
wget -qO- https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -

# 2. 注册 Microsoft SQL Server Ubuntu 存储库[version]
sudo add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/16.04/mssql-server-2017.list)"

# 3. 安装 SQL Server
sudo apt-get update
sudo apt-get install -y mssql-server

# 4. 初始化实例, 配置密码
sudo /opt/mssql/bin/mssql-conf setup

# 5. 查看服务状态
systemctl status mssql-server

# 6. 开放端口号, Ubuntu
sudo ufw allow 1433

# 7. 数据存储位置
/var/opt/mssql/data/
```

## Ubuntu uninstall SQLServer

```shell
# 1. 删除 SQL Server
apt-get remove mssql-server
# 2. 删除数据
sudo rm -rf /var/opt/mssql/
```
