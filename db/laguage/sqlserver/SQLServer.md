## [Ubuntu install SQLServer](https://docs.microsoft.com/zh-cn/sql/linux/quickstart-install-connect-ubuntu?view=sql-server-2017)

- notice: require ubuntu 16.04[only]

```shell
# 1. 导入公共存储库 GPG 密钥
wget -qO- https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -

# 2. 注册 Microsoft SQL Server Ubuntu 存储库[version]
# python 2
sudo add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/16.04/mssql-server-2017.list)"

# 3. 安装 SQL Server
sudo apt-get update
sudo apt-get install -y mssql-server

# 4. 初始化实例, 配置密码, 选择 developver
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

## Common Command

```sql
-- get timezone
DECLARE @TimeZone VARCHAR(50)
EXEC MASTER.dbo.xp_regread 'HKEY_LOCAL_MACHINE',
'SYSTEM\CurrentControlSet\Control\TimeZoneInformation',
'TimeZoneKeyName',@TimeZone OUT
SELECT @TimeZone
```

---

## Common Issue

- 1. `JPA SQL Server No Dialect mapping for JDBC type: -9`

  - default configuration

  ```xml
  <property name="hibernate.dialect" value="org.hibernate.dialect.SQLServerDialect"/>
  ```

  - solution: donot use built-in dialect and define own dialect as below

  ```java
  import java.sql.Types;
  import org.hibernate.dialect.SQLServerDialect;
  import org.hibernate.type.StandardBasicTypes;

  public class SQlServerDBDialect extends SQLServerDialect {
      public SQlServerDBDialect() {
          super();
          registerHibernateType(Types.NCHAR, StandardBasicTypes.CHARACTER.getName());
          registerHibernateType(Types.NCHAR, 1, StandardBasicTypes.CHARACTER.getName());
          registerHibernateType(Types.NCHAR, 255, StandardBasicTypes.STRING.getName());
          registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
          registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.TEXT.getName());
          registerHibernateType(Types.NCLOB, StandardBasicTypes.CLOB.getName());
      }
  }
  ```
