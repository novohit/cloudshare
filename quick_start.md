## Quick Start

## 环境

- JDK 17
- MySQL 5.7/8

1. 下载源码
2. 创建数据库 `cloudshare`
3. 配置启动环境变量

![image-20231008172236929](https://zwx-images-1305338888.cos.ap-guangzhou.myqcloud.com/img/2023/10/08image-20231008172236929.png)

根据需要更改对应的环境变量，例如

`MYSQL_HOST`  默认值 `localhost`

`MYSQL_PORT ` 默认值 `3306`

`MYSQL_USERNAME`  默认值 `root`

`MYSQL_PASSWORD`

`REDIS_xxx`

...



4. 启动项目，数据库表自动生成
5. 访问 `http://localhost:8080` ，响应 `helloworld!` 即启动成功
