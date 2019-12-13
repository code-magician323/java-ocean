
## android-activity
### 1. Activity的理解:
    1). 活动: 四大应用组件之一
    2). 作用: 提供能让用户操作并与之交互的界面
    3). 组件的特点:
        它的类必须实现特定接口或继承特定类
        需要在配置文件中配置其全类名
        它的对象不是通过new来创建的, 而是系统自动创建的
        它的对象具有一定的生命周期, 它的类中有对应的生命周期回调方法
    4). 哪些地方用到反射技术:(Android)
        a. 配置文本中配置全类名
        b. 布局文件定义标签
        c. 显式意图: Intent(Context context, Class c)


### 2. Intent的理解
    1). 意图: 信使(Activity, Service, BroadcastReceiver三个组件间通信的信使)
    2). 分类:
        显式: 操作当前应用自己的组件
        隐式: 操作其它应用自己的组件

### 3. Intent的使用
    1). 创建:
        显式: Intent(Context context, Class activityClass)
        隐式: Intent(String action) //与Activity与<intent-filter>的action匹配
    2). 携带数据
        额外: putExtra(String key, Xxx value) 内部用map容器保存
        有特定前缀: setData(Uri data)  //tel:123123, smsto:123123
    3). 读取数据:
        额外: Xxx getXxxExtra(String key)
        有特定前缀: Uri getData()

### 4. Activity的使用
    1). 定义
        a. 定义一个类 extends Activity, 并重写生命周期方法
        b. 在功能清单文件中使用<activity>注册
    2). 启动
        a. 一般: startActivity(Intent intent)
        b. 带回调启动: startActivityForResult(Intent intent, int requestCode)
           重写: onActivityResult(int requestCode, int resultCode, Intent data)
    3). 结束
        a. 一般: finish()
        b. 带结果的: setResult(int resultCode, Intent data)

### 5. Activity的生命周期
```
     1). Activity界面的状态:
        运行状态: 可见也可操作
        暂停状态: 可见但不可操作
        停止状态: 不可见，但对象存在
        死亡状态: 对象不存在
     2). Activity的生命周期流程:
        onCreate() : 加载布局和初始化的工作
        onResume() : 只有经历此方法, 才进入运行状态
        onDestroy() : 在对象死亡之前, 做一些收尾或清理的工作
```

![avatar](https://img-blog.csdnimg.cn/20190309203904166.png)

### 6. TaskStack和lauchMode
    1). TaskStack
        在Android中，系统用Task Stack (Back Stack)结构来存储管理启动的Activity对象
        一个应用启动,系统就会为其创建一个对应的Task Stack来存储并管理该应用的Activity对象
        只有最上面的任务栈的栈顶的Activity才能显示在窗口中
    2). lauchMode:
        standard: 标准模式，每次调用startActivity()方法就会产生一个新的实例。
        singleTop: 如果已经有一个实例位于Activity栈的顶部时，就不产生新的实例；如果不位于栈顶，会产生一个新的实例。
        singleTask: 只有一个实例, 默认在当前Task中
        singleInstance: 只有一个实例, 创建时会新建一个栈, 且此栈中不能有其它对象

    3). 设置监听的四种方式:
        1. layout中: android:onclick=“方法名”
            Activity中: public void 方法名(View v) {   }
        2. view.setOnclickListener(new View.OnclickListener(){})
        3. view.setOnclickListener(this)
        4. view.setOnclickListener(onclickListener成员变量)

### 7. 应用练习:  打电话与发短信
    1). 功能描述:
        1). 输入电话号, 点击"打电话", 进入拨号界面, 且已输入的指定的号码
        2). 输入电话号, 长按"打电话", 直接打电话(进入拨打界面)
        3). 输入电话和短信内容, 点击"发短信", 进入短信编辑界面, 且已有号码和内容
        4). 输入电话和短信内容, 长按"发短信", 直接将短信发送给指定的手机号
    2). 技术点:
        1). 布局的设计
        2). 点击事件和长按事件监听的添加
        3). 使用隐式意图拨打电话,进入拨号界面, 进入发短信界面: 借助系统应用源码  action
        4). 使用SmsMessager发送短信
        5). 权限的声明(如打电话, 发短信)
    3). 总结:
        1). 实现一个简单功能的应用的步骤:
            a. 外观: 分析界面组成, 定义布局文件
            b. 行为: 编写Activity的实现
                1). 在onCreate()中加载布局文件: setContentView(layoutId)
                2). 调用findViewById得到需要操作的所有视图对象并保存为成员变量
                3). 给视图对象设置监听器(点击/长按), 在回调方法实现响应逻辑
        2). 使用隐式意图启动系统应用的界面
            如何找到对应的Action字符串: 添加ActivityManager的Log日志, 利用系统应用源码找到对应的Activity的配置
        3). 权限: 当调用一些系统比较重要的功能时需要声明
