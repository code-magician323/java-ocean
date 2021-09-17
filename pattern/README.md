## 六大设计原则

1. 单一原则: `高内聚`

   - 定义: 软件变化的角度, 应该仅有一个让类变化的原因, 即每个类应该只有一个职责{每个功能只负责一件事情}[把功能细化]
   - 解释: 某个类有 F1, F2 两个职责的话, 当需要修改 F1 时可能会影响 F2{使得原本正常的 F2 发生故障}}
   - feature:
     1. 降低类的复杂度, 职责单一, 易扩展
     2. 提高了代码的可读性, 提高系统的可维护性
     3. 实际编码的过程中很难将它恰当地运用, 需要结合实际情况进行运用: 改动会很大
   - 举例:
     1. 假定所有 annimal 都呼吸空气, 则 animal 类 breathe 可以写死呼吸的是空气
     2. 但是发现有鱼不是呼吸空气的, 则有两种方案
        - 在 breathe 方法内判断是不是鱼, 或者为鱼写一个呼吸方法: 常用的但是不符合单一职责原则
        - 单一职责改法: 将之前的 animal 拆分成 陆生类{Terrestrial}和水生动物类{Aquatic}, 但是改动是很大的

2. 开闭原则: **抽象**

   - 对修改关闭: 不要修改原有代码
   - 对扩展开放: 借助抽象和多态, 把可能变化的内容抽象出来, 而具体的实现则是可以改变和扩展的.
   - 当有新的需求时, 不要修改原来的代码[关闭], 可以基于抽象类做新的实现[开放]
   - feature
     1. 可扩展性, 可维护性
     2. 尽可能地不要修改已经写好的代码, 已有的功能, 而是去扩展它
   - 举例: 多途径支付{策略模式}, 新增一个只会新增代码, 热不会对已有功能进行修改

3. 依赖倒转原则: **面向接口|抽象编程**

   - 要依赖于抽象, 不要依赖于具体实现.
   - 抽象不应该依赖细节 + 高层模块不应该依赖低层模块
   - 举例: 组装电脑时的内存条类型{内存条向上抽象一层而不是依赖某一种指定的内存条[金士顿/**]}

4. 接口分离原则:

   - **不应该强迫程序依赖它们不需要使用的方法**: 一个类对另一个类的依赖应该建立在最小的接口上
   - 尽可能地去细化接口, 接口中的方法尽可能少
   - 如果接口设计过小, 则会造成接口数量过多, 使设计复杂化, 所以一定要适度
     ![avatar](/static/image/pattern/prin-interface.png)
   - 举例: 安全门{防盗防水拆分成 2 个接口}

5. 里氏替换原则: **尽量不要重写父类的方法** || `模板方法模式只是定义方法{有具体类实现}`

   - 在任何父类出现的地方都可以用它子类来替代: **子类可以扩展父类的功能, 但不能改变父类原有的功能**
   - 使用**继承**{_增加了耦合, 优先使用组合/依赖_}时, 在子类中**尽量不要重写父类的方法**: 在复杂多态下复用性就差
   - 类 ClassA#F1, F1 -> ClassB#F={F1+F2}: classB extends classA 实现 P 时需要注意是新的方法, 且不要覆盖之前的 F1 方法
   - 继承包含这样一层含义
     1. 父类中凡是已经实现好的方法[相对于抽象方法而言], 实际上是在设定一系列的规范和契约
     2. 虽然它不强制要求所有的子类必须遵从这些契约
     3. 但是如果子类对这些非抽象方法任意修改, 就会对整个继承体系造成破坏
   - 举例: 正方形不是长方形{扩宽的方法}

6. 迪米特原则: **低耦合**

   - 最少知识原则: 如果两个软件实体无须直接通信, 那么就不应当发生直接的相互调用, 可以通过第三方转发该调用
   - 在模块之间应该只通过接口通信, **而不理会模块内部工作原理**
   - 其目的是降低类之间的耦合度, 提高模块的相对独立性
   - 举例: 中介模式, 软件外包, 明星/粉丝/公司 = 经纪人

7. 合成复用原则

   - 优先使用组合和聚合来实现, 之后才考虑使用继承实现
   - 继承复用: 简单&易实现
     1. 破坏了类的封装性: 父类对子类是透明的
     2. 子类与父类的耦合度高: 父类的任何改变都会引起子类的改变
     3. 限制了复用的灵活性: 从父类继承而来的实现是编译时已经定义, 所以在运行时不可能发生变化
     4. 不能多继承
   - 合成/聚合复用
     1. 类的封装性: 成员对象的内部细节是新对象看不见的
     2. 对象间的耦合度低: 声明成抽象成员
     3. 复用的灵活性高: 可以在运行时动态进行, 新对象可以动态地引用与成分对象类型相同的对象
   - 举例: 汽车分类管理{颜色+能源}

     ![avatar](/static/image/pattern/prin-crp.png)

## 分类: **观模中策 状命备解 迭责访**

1. 创建者模式[5]: 将对象的创建与使用分离{解耦}

   - **单例模式**
   - **工厂模式**
   - 抽象工厂模式
   - **建造者模式**
   - 原型模式

2. 结构型模式[7]: 将类和对象按某种布局组成更大的结构

   - **代理模式**
   - _适配器模式_
   - 桥接模式
   - 装饰者模式
   - 外观模式: facade
   - 组合模式
   - 享元模式

3. 行为模式[11]: 描述类和对象怎样协同完成单个对象无法完成的任务 + 职责分配

   - **模板方法模式**
   - **策略模式**
   - 迭代器模式
   - 状态模式
   - 责任链模式
   - 命令模式
   - 观察者模式
   - 解释器模式
   - 中介模式
   - 备忘录模式
   - 访问者模式

## UML 图

1. 关联关系

   - 单向关联: →
   - 双向关联: -
   - 自关联

2. 聚合关系: 带空心菱形的实线

   - 老师与学校

3. 组合关系: 带实心菱形的实线

   - 接口和实现类: 头和身体

4. 依赖关系: 带箭头的虚线

   - 局部变量 / 方法的参数/ 对静态方法的调用

5. 继承关系: 带空心三角箭头的实线
6. 实现关系: 带空心三角箭头的虚线