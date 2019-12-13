## django 的 migration 问题

### 正确的 migration step

```shell
# 生成迁移文件
python manage.py makemigrations
# 同步数据库
python manage.py migrate
```

### 重置 migration [No changes detected]

```shell
python manage.py makemigrations # No changes detected
# 查看迁移
python mange.py showmigrations
# 取消迁移
python manage.py migrate --fake (app名字) zero
# 删除 app下的migrations模块中 除 init.py 之外的所有文件
# –fake-inital 会在数据库中的 migrations表中记录当前这个app 执行到 0001_initial.py ，但是它不会真的执行该文件中的 代码
python manage.py migrate  --fake-initial
```

### 常见的错误

    1. django.db.utils.InternalError: (1050, "Table 'django_content_type' already exists")
        删除数据库
    2. No migrations to apply.
    3. Dependency on app with no migrations: users
    4. No changes detected
    5. django.core.exceptions.ImproperlyConfigured: Error loading MySQLdb module: No module named 'MySQLdb'
      方案:     # __init__.py 添加
                import pymysql
                pymysql.install_as_MySQLdb()
