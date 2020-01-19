## android-environment

### 搭建开发环境

1. 解压 sdk.zip 文件(不要有中文)2); 配置 path
2. 创建第一个 Android 项目: HelloAndroid
   - 指定 sdk 的版本都为 182). 修改清单文件: minSdk="8"
3. 四个文件目录结构:

   - 1).应用项目的

     - src(源码文本夹): **MainActivity**.java: 主界面类
     - gen(自动生成的源码文本夹): R.java: 对应 res 文件夹
       - drawble: 图片
       - layout: 布局
       - string: 字符串
     - res(资源文件夹)
       - drawable-xxx: 图片文件夹: 为了适配不同分辨率的手机
       - layout: 界面的布局文件: 功能类似于 HTML
       - values: 常量文件夹: strings.xml : 包含固定的字符串, 在布局中引用: @string/name
     - **AndroidManifest.xml(功能清单文件)**

     ![avatar](https://img-blog.csdnimg.cn/20190309191219716.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

   - 2).应用 APK 的

   ![avatar](https://img-blog.csdnimg.cn/20190309191439658.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

   ![avatar](https://img-blog.csdnimg.cn/20190309191452963.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

   - 3).Android 系统的

   ![avatar](https://img-blog.csdnimg.cn/20190309191507735.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

   - 4).SDK 的

   ![avatar](https://img-blog.csdnimg.cn/20190309191519922.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3NzA0MzY0,size_16,color_FFFFFF,t_70)

4. 三个应用开发工具

   - ADB: 调试工具

   ```json
     adb shell
     adb install -r apkPath
     ls
     cd
     cls
     ctrl+C
   ```

   - DDMS: eclipse 中的 debug 调试工具:

     - LogCat: 查看日志输出
     - File Explorer: 查看系统内部文件
     - Devices: 显示关联的 Android 设备
     - Emulator Control: 操作控制控制的 Android 设备

   - Log: 日志工具类
     - Log 提供了多个级别的输出打印方法，在 Logcat 中显示不同的颜色
     - Log 打印时必须指定 TAG，在 Logcat 中可以通过添加 TAG 进行过滤查看
     - Logcat 可以通过两种方式过滤:
       - TAG 名: 显示所有的 TAG 标签的输出
       - 应用包名: 显示指定包名应用的所有输出

5. 尺寸

   - dp 与 px 的比较:
     - 以 px 为长度单位，再好的手机上会变小， 在坏手机上会变大
     - dp 为单位是比较好的
   - dp 与 sp 的比较:

   * 用户 可以在系统设置中设置文本的大小:
   * dp 为单位是没有效果的；所以 sp 为单位是比较好的

   - 注意:
     1. 在布局文件视图的宽高尽量使用 match_parent/wrap_content
     2. 如果必须指定特定值，使用 dp/dip 做单位
     3. 文本大小使用 sp 做单位

6. 相关 API:
   - **Activity:** 四大应用组件之一
     - onCreate() 自动调用的回调函数，在其中加载布局显示
     - setContentView(int layoutId) 加载布局
     - View findViewById(int id) 根据 id 找到对应的视图对象
   - **R.java:** 应用的资源类，对应 res 资源
     - R.drawable: 包含所有图片资源标识的内部类
     - R.layout: 标识所有布局资源的内部类
     - R.id: 包含所有视图 id 标识的内部类
     - R.string: 包含所有字符串标识的内部类
   - View/Button: 视图/按钮
     - setOnClickListener(listener): 给视图设置点击监听
   - View.OnClickListener: 内部接口
     - void onClick(View v) 点击事件的回调方法
   - Toast: 用来显示短时间提示文本的类
     - static Toast make Text(...) 创建一个 Toast 对象
     - show() 显示提示

7) 总结思路:
   - 定义界面布局
   - 实现 Activity
     1. 在 onCreate()中加载布局
     2. 根据 id 查询所有需要操作的视图对象, 并保存为成员变量
     3. 给视图对象设置监听(点击)
     4. 在监听器的回调方法中实现响应逻辑
