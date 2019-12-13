## 创建及使用

### 过程

- 1. 下载后，解压，把 xadmin 文件夹拷问到 extra_apps 目录下，**并 Mark as 为 Sources Root**
- 2. **pip install django-crispy-forms**
- 3. 更改 setting.py 的配置

  ```python
  # 加入这一行
  sys.path.insert(0, os.path.join(BASE_DIR, 'extra_apps'))
  # 在INSTALLED_APPS 加入
  crispy_forms,
  xadmin,
  ```

- 4. 更改 urls.py 的配置

  ```python
  urlpatterns = [
      url(r'^xadmin/', xadmin.site.urls), # 注意结尾不能使用$
  ]
  ```

- 5. 同步下数据库

  ```shell
  # 生成迁移文件
  python manage.py makemigrations
  # 同步数据库
  python manage.py migrate
  ```

- 6. 运行 django

  - 访问的 URL: **127.0.0.1:8000/xadmin**

- 7. **创建 superuser**

  ```shell
  python manage.py createsuperuser --username=zack --email=zzhang_xz@163.com
  Password:
  Password (again):
  Superuser created successfully.
  ```

- 8. 创建 adminx.py

  ```python
    # 显示内容
    list_display = ['image', 'url', 'add_time']
    # 搜索字段
    search_fields = ['image', 'url']
    # 筛选字段
    list_filter = ['image', 'url', 'add_time']
  ```
