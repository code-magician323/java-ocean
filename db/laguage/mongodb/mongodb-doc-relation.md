**Table of Contents**

- [document relations](#document-relations)
  - [relations](#relations)
  - [demo](#demo)

## document relations

### relations

- 一对一(one to one)
  在 MongoDB, 可以通过内嵌文档的形式来体现出一对一的关系
- 一对多(one to many)/多对一(many to one)
  也可以通过内嵌文档来映射一对多的关系
- 多对多(many to many)

### demo

```js
// one to one
db.wifeAndHusband.insert([
  {
    name: '黄蓉',
    husband: {
      name: '郭靖'
    }
  },
  {
    name: '潘金莲',
    husband: {
      name: '武大郎'
    }
  }
]);

db.wifeAndHusband.find();

// one to many  users -- orders
db.users.insert([
  {
    username: 'swk'
  },
  {
    username: 'zbj'
  }
]);

db.order.insert({
  list: ['牛肉', '漫画'],
  user_id: ObjectId('59c47e35241d8d36a1d50de0')
});

db.users.find();
db.order.find();

// 查找用户 swk 的订单
var user_id = db.users.findOne({ username: 'zbj' })._id;
db.order.find({ user_id: user_id });

// many to many
db.teachers.insert([
  { name: '洪七公' },
  { name: '黄药师' },
  { name: '龟仙人' }
]);

db.stus.insert([
  {
    name: '郭靖',
    tech_ids: [
      ObjectId('59c4806d241d8d36a1d50de4'),
      ObjectId('59c4806d241d8d36a1d50de5')
    ]
  },
  {
    name: '孙悟空',
    tech_ids: [
      ObjectId('59c4806d241d8d36a1d50de4'),
      ObjectId('59c4806d241d8d36a1d50de5'),
      ObjectId('59c4806d241d8d36a1d50de6')
    ]
  }
]);

db.teachers.find();

db.stus.find();
```
