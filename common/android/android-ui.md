## android-ui

### 1.UI 的理解

    全称user interface, 意为: 用户界面
    UI由View和ViewGroup组成
    View类是所有视图(包括ViewGroup)的根基类
    View在屏幕上占据一片矩形区域, 并会在上面进行内容绘制
    ViewGroup包含一些View或ViewGroup, 用于控制子View的布局
        事件模型: 设置监听器: view.seton…Listener(listener)

![avatar](https://img-blog.csdnimg.cn/20190309205013360.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

### 2.常用的 UI 组件

- 1). 简单 UI 组件

  - TextView : 文本视图

  ![avatar](https://img-blog.csdnimg.cn/20190309205241981.png)

  - EditText : 可编辑的文本视图

  ![avatar](https://img-blog.csdnimg.cn/20190309205435703.png)

  - Button : 按钮
  - ImageView : 图片视图

  ![avatar](https://img-blog.csdnimg.cn/20190309205606768.png)

  - CheckBox : 多选框

  ![avatar](https://img-blog.csdnimg.cn/20190309205658116.png)

  - RadioGroup/RadioButton : 单选框

  ![avatar](https://img-blog.csdnimg.cn/20190309205741606.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

- 2). 菜单 Menu

  ```
  OptionMenu
      1. 如何触发Menu的显示?
          点击menu键
      2. 如何向Menu中添加MenuItem?
          重写onCreateOptionMenu()
          menu.add()或者加载菜单文件
      3. 选择某个MenuItem时如何响应?
          重写onOptionsItemSelected(), 根据itemId做响应
  ContextMenu
      1. 如何触发Menu的显示?
          长按某个视图
          view.setOnCreateContextMenuListener(this)
      2. 如何向Menu中添加MenuItem?
          重写onCreateContextMenu()
          menu.add()
      3. 选择某个MenuItem时如何响应?
          重写onContextItemSelected(), 根据itemId做响应
  ```

- 3). 进度条

  - a. ProgressBar

    - 圆形

    ![avatar](https://img-blog.csdnimg.cn/20190309210243695.png)

    - 水平

    ![avatar](https://img-blog.csdnimg.cn/20190309210415928.png)
    ![avatar](https://img-blog.csdnimg.cn/20190309210534779.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

  - b. SeekBar

  ![avatar](https://img-blog.csdnimg.cn/20190309210709302.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

- 4). 对话框

  - a.API 结构

  ![avatar](https://img-blog.csdnimg.cn/20190309210816580.png)

  - b.AlertDialog

    - 一般的

    ![avatar](https://img-blog.csdnimg.cn/20190309211209305.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

    - 自定义布局的

      - 动态加载布局文件得到对应的 View 对象

      ![avatar](https://img-blog.csdnimg.cn/20190309211310905.png)

      - 设置 View

      ![avatar](https://img-blog.csdnimg.cn/20190309211352722.png)

    - 带单选列表的

      ![avatar](https://img-blog.csdnimg.cn/2019030921145176.png)

  - c.ProgressDialog

    - 圆形进度

      ![avatar](https://img-blog.csdnimg.cn/20190309211520748.png)

    - 水平进度
      ![avatar](https://img-blog.csdnimg.cn/20190309211628674.png)

### 补充:

```
1). 启动分线程
```

![avatar](https://img-blog.csdnimg.cn/20190309211821841.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

```
2). 根据id查找View对象
    a. 查找当前界面中的View对象:  findViewById(id)
    b. 查找某个View对象的子View: view.findViewById(id)
3). 更新视图
    a. 不能在分线程直接更新UI:  toast不能在分线程显示, 但ProgressDialog可以在分线程更新
    b. 长时间的工作只能放在分线程执行
```

### 3.常用的 UI 布局

    1). LinearLayout: 线性布局
        用来控制其子View以水平或垂直方式展开显示
    2). RelativeLayout: 相对布局
        用来控制其子View以相对定位的方式进行布局显示
    3). FrameLayout: 帧布局
        每一个子View都代表一个画面, 后面出现的会覆盖前面的画面
        通过子View的android:layout_gravity 属性来指定子视图的位置

### 4.常用的视图标签的属性

- 1).视图的常用属性

![avatar](https://img-blog.csdnimg.cn/20190309212748451.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
![avatar](https://img-blog.csdnimg.cn/20190309212826920.png)

- 2). 只针对 RelativeLayout

![avatar](https://img-blog.csdnimg.cn/20190309212914308.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)
![avatar](https://img-blog.csdnimg.cn/20190309213032714.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

- 3). 只针对 LinearLayout

![avatar](https://img-blog.csdnimg.cn/20190309213119442.png)
![avatar](https://img-blog.csdnimg.cn/20190309213153976.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

### 5.ListView 的使用

- 1). 理解

  ![avatar](https://img-blog.csdnimg.cn/20190309213305839.png)
  ![avatar](https://img-blog.csdnimg.cn/20190309213336391.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

```java
  2). 使用'\n'
    a. ArrayAdapter : 显示简单文本列表
      ArrayAdapter(Context context, int resource, T[] objects)
    b. SimpleAdpater : 显示复杂列表

    c. BaseAdpater(抽象的):　显示复杂列表
      int getCount() : 得到集合数据的个数, 决定了能显示多少行
      Object getItem(int position) : 根据position得到对应的数据对象
      View getView(int position, View convertView, ViewGroup parent)
        //根据position返回对应的带数据的Item视图对象
        position : 下标
        convertView : 可复用的Item视图对象
            为null : 没有可复用的, 我们必须加载一个item的布局文件, 并赋值给convertView
            不为null: 直接使用此视图对象
            后面: 找到子View, 找到对应的数据, 设置数据
        parent : ListView
    d. 给ListView的Item设置监听
      item的点击监听: listView.setOnItemClickListener(listener)
      item的长按监听 : listView.setOnItemLongClickListener(listener)
  3). 优化'\n'

    a. 内存中最多存在n+1个convertView对象
    b. 只有当convertView为null时才去加载item的布局文件
```

### 6.style 和 Theme

- 1). style : 多个视图标签属性的集合
  - 好处: 复用标签属性
  - 目标: 布局文件中的视图标签
- 2). theme : 本质也是 style
  - 好处: 复用标签属性
  - 目标: 功能清单文件中整个应用/Activity

### 7.练习

- 1). 应用功能编码的基本流程
  - 外观: 布局文件, 读取数据, 定义 Adapter, 显示列表
  - 行为: 设置事件监听, 并在回调就去中做出对应的响应
- 2). 初始显示列表和更新列表
- 3). GridView 的基本使用
