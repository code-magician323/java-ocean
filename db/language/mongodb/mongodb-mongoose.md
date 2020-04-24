**Table of Contents**

- [mongoose introducation](#mongoose-introducation)
  - [相关认知](#%E7%9B%B8%E5%85%B3%E8%AE%A4%E7%9F%A5)
  - [mongoose 对象](#mongoose-%E5%AF%B9%E8%B1%A1)
    - [Schema(definition, option)](#schemadefinition-option)
    - [Model](#model)
    - [Document](#document)
- [mongoose 基本使用](#mongoose-%E5%9F%BA%E6%9C%AC%E4%BD%BF%E7%94%A8)
  - [基础](#%E5%9F%BA%E7%A1%80)
  - [demo](#demo)
- [mongoose 模块化](#mongoose-%E6%A8%A1%E5%9D%97%E5%8C%96)

## mongoose introducation

### 相关认知

- mongoose 就是一个让我们可以通过 node 来操作 mongodb 的模块
- mongoose 是一个对象文档模型(odm)库，它对 node 原生的 mongodb 模块进行了进一步的优化封装，并提供了更多的功能
- mongoose 优点
  - 可以为文档创建一个模式结构(Schema)
  - 可以对模型中的对象/文档进行验证
  - 数据可以通过类型转换转换为对象模型
  - 可以使用中间件来应用业务逻辑挂钩
  - 比 node 原生的 mongodb 驱动更容易

### mongoose 对象

#### Schema(definition, option)

- Schema 模式对象定义约束了数据库中的文档结构, 为创建 Model 而存在
- Schema 支持的数据类型

  ```js
  > String
  > Number
  > Boolean
  > Array
  > Buffer
  > Date
  > ObjectId 或 Oid
  > Mixed
  ```

- options 配置对象，定义与数据库中集合的交互
  - autoIndex: 布尔值，开启自动索引，默认 true
  - bufferCommands: 布尔值，缓存由于连接问题无法执行的语句，默认 true
  - capped: 集合中最大文档数量
  - collection: 指定应用 Schema 的集合名称
  - id: 布尔值，是否有应用于\_id 的 id 处理器，默认 true
  - \_id: 布尔值，是否自动分配 id 字段，默认 true
  - strict:布尔值，不符合 Schema 的对象不会被插入进数据库，默认 true

#### Model

- Model 对象作为集合中的所有文档的表示，相当于 MongoDB 数据库中的集合 collection

- model(name, [schema], [collection] , [skipInit])
  - name 参数相当于模型的名字，以后可以同过 name 找到模型。
  - schema 是创建好的模式对象。
  - collection 是要连接的集合名。
  - skipInit 是否跳过初始化，默认是 false。
- methods
  - remove(conditions, callback)
  - deleteOne(conditions, callback)
  - deleteMany(conditions, callback)
  - find(conditions, projection, options, callback)
  - findById(id, projection, options, callback)
  - findOne(conditions, projection, options, callback)
  - count(conditions, callback)
  - create(doc, callback)
  - update(conditions, doc, options, callback)

#### Document

- Document 表示集合中的具体文档，相当于集合中的一个具体的文档

- methods
  - equals(doc)
  - id
  - get(path,[type])
  - set(path,value,[type])
  - update(update,[options],[callback])
  - save([callback])
  - remove([callback])
  - isNew
  - isInit(path)
  - toJSON()
  - toObject()

## mongoose 基本使用

### 基础

```js
// 1. 下载安装 Mongoose
cnpm install mongoose -g
// 2. 在项目中引入mongoose
ar mongoose = require("mongoose");
// 3. 连接MongoDB数据库
mongoose.connect('mongodb://数据库的ip地址:端口号/数据库名', {
  useMongoClient: true
}); // - 如果端口号是默认端口号（27017） 则可以省略不写

// 4. 断开数据库连接(一般不需要调用)
mongoose.disconnect(); //MongoDB数据库，一般情况下，只需要连接一次，连接一次以后，除非项目停止服务器关闭，否则连接一般不会断开

// 5. 在mongoose对象中，有一个属性叫做 connection，该对象表示的就是数据库连接
// 数据库连接成功的事件
mongoose.connection.once('open', function() {});
// 数据库断开的事件
mongoose.connection.once('close', function() {});
```

### demo

```js
var mongoose = require('mongoose');
// 1. 指定数据库名称、链接
mongoose.connect('mongodb://127.0.0.1:27017/mongoose_test', {
  useNewUrlParser: true
});

// 2. 数据库连接的监听事件
mongoose.connection.once('open', function() {
  console.log('数据库连接成功~~~');
});
mongoose.connection.once('close', function() {
  console.log('数据库连接已经断开~~~');
});

// 3. 创建 Schema(为创建 Model 而存在)
var Schema = mongoose.Schema;
var stuSchema = new Schema({
  name: {
    type: String
  },
  age: Number,
  // type、default 是关键字
  gender: {
    type: String,
    default: 'female'
  },
  address: String
});

// 4. Model: 通过 Schema
// modelName 就是要映射的集合名 mongoose 会自动将集合名变成复数
var StuModel = mongoose.model('student', stuSchema);

/**
 * 4.1 insert
 *      Model.create(doc(s), [callback(err, docs)])
 **/
StuModel.create(
  {
    name: '素悟空',
    age: 16,
    address: '花果山'
  },
  {
    name: '书八戒',
    age: 16,
    address: '高老庄'
  },
  function(err) {
    if (!err) {
      console.log('插入成功~~~');
    }
  }
);

/**
 *  4.2 find
 *      Model.find(conditions, [projection], [options], [callback]) 返回数组
 *      Model.findOne([conditions], [projection], [options], [callback])
 *      Model.findById(id, [projection], [options], [callback])
 *          conditions 查询的条件
 *          projection 投影 需要获取到的字段
 *              - 两种方式
 *                  {name:1,_id:0}
 *                  "name -_id"
 *          options  查询选项（skip limit）
 *          {skip:3 , limit:1}
 *          callback 回调函数，查询结果会通过回调函数返回, 回调函数必须传，如果不传回调函数，压根不会查询
 **/

StuModel.find({}, { name: 1, _id: 0 }, function(err, docs) {
  if (!err) {
    console.log(docs);
  }
});

StuModel.find({}, 'name -_id', { skip: 3, limit: 1 }, function(err, docs) {
  if (!err) {
    console.log(docs);
  }
});

/**
 * 4.3 update
 *   Model.update(conditions, doc, [options], [callback])
 *   Model.updateMany(conditions, doc, [options], [callback])
 *   Model.updateOne(conditions, doc, [options], [callback])
 *   Model.replaceOne(conditions, doc, [options], [callback])
 *      conditions 查询条件
 *      doc 修改后的对象
 *      options 配置参数
 *      callback 回调函数
 **/

StuModel.updateOne({ name: '唐僧' }, { $set: { age: 20 } }, function(err) {
  if (!err) {
    console.log('修改成功');
  }
});

/**
 *  4.4 delete
 *      Model.remove(conditions, [callback])
 *      Model.deleteOne(conditions, [callback])
 *      Model.deleteMany(conditions, [callback])
 **/
StuModel.remove({ name: '白骨精' }, function(err) {
  if (!err) {
    console.log('删除成功~~');
  }
});

/**
 * 4.5 count
 *      Model.count(conditions, [callback])
 **/
StuModel.count({}, function(err, count) {
  if (!err) {
    console.log(count);
  }
});

// 5. Document 和 集合中的文档一一对应, Document是Model的实例
//create the document
var stu = new StuModel({
  name: '奔波霸',
  age: 48,
  gender: 'male',
  address: '碧波潭'
});

/**
 * 5.1 save
 *      Model#save([options], [fn])
 * */
stu.save(function(err) {
  if (!err) {
    console.log('保存成功~~~');
  }
});

/**
 * 5.2 update
 *      update(update,[options],[callback])
 *      remove([callback])
 **/
StuModel.findOne({}, function(err, doc) {
  if (!err) {
    // update1
    doc.update({ $set: { age: 28 } }, function(err) {
      if (!err) {
        console.log('修改成功~~~');
      }
    });
    // update2
    doc.age = 18;
    doc.save();
    // remove
    doc.remove(function(err) {
      if (!err) {
        console.log('大师兄再见~~~');
      }
    });

    /**
     * 5.3 doc 的基本方法
     *   get(name): 获取文档中的指定属性值
     *   set(name , value): 设置文档的指定的属性值
     *   id: 获取文档的_id属性值
     *   toObject(): 将 Document 对象转换为一个普通的 JS 对象
     *               转换为普通的js对象以后，注意所有的 Document 对象的方法或属性都不能使用了
     **/

    console.log(doc.get('age')); // console.log(doc.age);
    doc.set('name', '猪小小'); // doc.name = 'hahaha';
    console.log(doc._id);

    var j = doc.toJSON();
    console.log(j);

    var o = doc.toObject();
    console.log(o);
    doc = doc.toObject();
    delete doc.address;
    console.log(doc._id);
  }
});
```

## mongoose 模块化

- 将连接 mongoose 部分代码模块化, 创建 conn_mongo.js 文件, 内容如下:

  ```js
  var mongoose = require('mongoose');
  // 1. 指定数据库名称、链接
  mongoose.connect('mongodb://127.0.0.1:27017/mongoose_test', {
    useNewUrlParser: true
  });

  // 2. 数据库连接的监听事件
  mongoose.connection.once('open', function() {
    console.log('数据库连接成功~~~');
  });
  mongoose.connection.once('close', function() {
    console.log('数据库连接已经断开~~~');
  });
  ```

- 将连接 model 部分代码模块化, 创建 student.js 文件, 内容如下:

  ```js
  requrie('mongoose');
  var Schema = mongoose.Schema;
  var stuSchema = new Schema({
    name: {
      type: String
    },
    age: Number,
    // type、default 是关键字
    gender: {
      type: String,
      default: 'female'
    },
    address: String
  });
  // 向外暴露属性
  module.exports = stuSchema;
  // exports.model = stuSchema;  ok
  // exports = stuSchema; 这种方式改变了 exports 对象, 是不对的
  ```

- 使用以上模块

  ```js
  // 引入连接模块
  require('..../conn_mongo.js');
  // 引入 model
  var student = require('..../student');
  // var student = require('..../student').model; 对于 exports.model = stuSchema;
  ```
