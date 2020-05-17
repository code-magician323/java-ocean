**Table of Contents**

- [crud](#crud)
  - [insert](#insert)
  - [find](#find)
  - [remove](#remove)
  - [update](#update)
  - [常见函数与投影: limit skip sort](#%E5%B8%B8%E8%A7%81%E5%87%BD%E6%95%B0%E4%B8%8E%E6%8A%95%E5%BD%B1-limit-skip-sort)

## crud

### insert

- 相关知识点

  ```
  1. db.collection.insert()
      向集合中插入一个或多个文档[]
      当我们向集合中插入文档时, 如果没有给文档指定_id属性, 则数据库会自动为文档添加_id, 该属性用来作为文档的唯一标识
      _id我们可以自己指定, 如果我们指定了数据库就不会在添加了, 如果自己指定_id 也必须确保它的唯一性
  2. db.collection.insertOne()
      插入一个文档对象
  3. db.collection.insertMany()
      插入多个文档对象
  ```

- 示例

  ```js
  db.stus.insert({ name: '猪八戒', age: 28, gender: '男' });

  db.stus.insert([
    { name: '沙和尚', age: 38, gender: '男' },
    { name: '白骨精', age: 16, gender: '女' },
    { name: '蜘蛛精', age: 14, gender: '女' }
  ]);

  db.stus.insert({ _id: 'hello', name: '猪八戒', age: 28, gender: '男' });

  db.stus.find();

  // 唯一标识
  ObjectId();
  ```

### find

- 相关知识点

  ```
  1. db.collection.find()
      find() 返回的是一个数组
      find()/find({}) 用来查询集合中所有符合条件的文档
      find(doc) 可以接收一个对象作为条件参数
  2. db.collection.findOne()
      用来查询集合中符合条件的第一个文档
      findOne() 返回的是一个文档对象
  3. db.collection.find({}).count()
      查询所有结果的数量
  ```

- 示例

  ```js
  db.stus.find({ _id: 'hello' });
  db.stus.find({ age: 16, name: '白骨精' });
  db.stus.find({ age: 28 });
  db.stus.findOne({ age: 28 });

  db.stus.find({}).count();
  ```

### remove

- 相关知识点

  ```
  1. db.collection.remove(doc[, flag])
      删除一个或多个, 可以第二个参数传递一个true, 则只会删除一个
      如果传递一个空对象作为参数, 则会删除所有的
  2. db.collection.deleteOne()
  3. db.collection.deleteMany()
  4. db.collection.drop() 删除集合
  5. db.dropDatabase() 删除数据库
      一般数据库中的数据都不会删除, 所以删除的方法很少调用
      一般会在数据中添加一个字段, 来表示数据是否被删除
  ```

- 示例

  ```js
  db.stus.find({ _id: 'hello' });
  db.stus.find({ age: 16, name: '白骨精' });
  db.stus.find({ age: 28 });
  db.stus.findOne({ age: 28 });

  db.stus.find({}).count();
  ```

### update

- 相关知识点

  ```
  1. db.collection.update(查询条件, 新对象)
      update() 默认情况下会使用新对象来替换旧的对象
      update() 默认只会修改一个
      如果需要修改指定的属性, 而不是替换需要使用 '修改操作符' 来完成修改
          $set 可以用来修改文档中的指定属性
          $unset 可以用来删除文档的指定属性
  2. db.collection.updateMany()
      同时修改多个符合条件的文档
  3. db.collection.updateOne()
      修改一个符合条件的文档
  4. db.collection.replaceOne()
      替换一个文档
  ```

- 示例

  ```js
  db.stus.find({});

  //替换
  db.stus.update({ name: '沙和尚' }, { age: 28 });

  // 添加属性
  db.stus.update(
    { _id: ObjectId('59c219689410bc1dbecc0709') },
    {
      $set: {
        gender: '男',
        address: '流沙河'
      }
    }
  );
  // 删除属性
  db.stus.update(
    { _id: ObjectId('59c219689410bc1dbecc0709') },
    {
      $unset: {
        address: 1
      }
    }
  );
  // 给所有 name 为 '猪八戒' 添加属性
  db.stus.updateMany(
    { name: '猪八戒' },
    {
      $set: {
        address: '猪老庄'
      }
    }
  );
  // 给所有 name 为 '猪八戒' 添加属性
  db.stus.update(
    { name: '猪八戒' },
    {
      $set: {
        address: '呵呵呵'
      }
    },
    {
      multi: true
    }
  );

  db.stus.find();
  ```

### 常见函数与投影: limit skip sort

    ```js
    //limit skip sort 可以以任意的顺序进行调用
    //sort() 默认情况是按照 _id 升序排列, 可以用来指定文档的排序的规则, sort()需要传递一个对象来指定排序规则 1表示升序 -1表示降序
    db.emp.find({}).sort({ a: 1, b: -1 });
    // 分页  skip((页码-1) * 每页显示的条数).limit(每页显示的条数);
    db.emp
      .find()
      .skip(10)
      .limit(10);

    //在查询时，可以在第二个参数的位置来设置查询结果的投影
    db.emp.find({}, { ename: 1, _id: 0, sal: 1 });
    ```
