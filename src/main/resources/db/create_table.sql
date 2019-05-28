drop table if exists miaosha_order;
drop table if exists order_info;
drop table if exists goods_category;
drop table if exists miaosha_goods;
drop table if exists goods_picture;
drop table if exists category;
drop table if exists goods;
drop table if exists user;
drop table if exists picture;


#图片表（商品图片，用户头像）
CREATE TABLE picture(
picture_id INT PRIMARY KEY auto_increment,
picture_address VARCHAR(120) NOT NULL
);

insert into picture (picture_address) values('http://47.106.244.103:8080/group1/M00/00/00/rBIN-1uWmECAFGj8AAAL6CFOlLk676.jpg');

#普通用户表
CREATE TABLE user(
user_id INT PRIMARY KEY auto_increment,
mobile VARCHAR(11) NOT NULL,
nick_name VARCHAR(20) NOT NULL,
password VARCHAR(64) NOT NULL,
icon_id INT, #用户默认头像
permission INT NOT NULL DEFAULT 1,   #1:普通用户    2:商家    3:管理员
FOREIGN KEY fk_us_incon_id (icon_id) REFERENCES picture(picture_id)
);

INSERT INTO user (mobile,nick_name,password) VALUES ('18895368350','18895368350','donggua123');

#商品表
CREATE TABLE goods(
goods_id int PRIMARY KEY auto_increment,
goods_name VARCHAR(100) NOT NULL,
goods_info VARCHAR(10000) NOT NULL,
stock INT NOT NULL,
price BIGINT NOT NULL,
creator_id INT NOT NULL,
creator_name VARCHAR(20),
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ,
FOREIGN KEY fk_gd_creator_id (creator_id) REFERENCES user(user_id)
);

insert into goods (goods_name,goods_info,stock,price,creator_id,creator_name) values('电脑','很好用的电脑',5,100,1,'18895368350');

#商品图片列表
CREATE TABLE goods_picture(
goods_picture_id int PRIMARY KEY auto_increment,
goods_id INT NOT NULL,
picture_id INT NOT NULL,
FOREIGN KEY fk_gp_goods_id (goods_id) REFERENCES goods(goods_id),
FOREIGN KEY fk_gp_picture_id (picture_id) REFERENCES picture(picture_id)
);

insert into goods_picture (goods_id,picture_id) values(1,1);

#秒杀商品表
CREATE TABLE miaosha_goods(
miaosha_goods_id INT PRIMARY KEY auto_increment,
goods_id INT NOT NULL,
miaosha_stock INT NOT NULL,       #秒杀限量库存
miaosha_price BIGINT NOT NULL,    #特价
start_time DATETIME NOT NULL,         #秒杀开始时间
end_time DATETIME NOT NULL,            #秒杀结束时间
FOREIGN KEY fk_mg_goods_id (goods_id) REFERENCES goods(goods_id)
);

#商品类别表
CREATE TABLE category(
category_id INT PRIMARY KEY auto_increment,
category_name VARCHAR(30) NOT NULL,  #分类名称
is_parent INT NOT NULL DEFAULT 0,   #是否为父节点（便于树状展开）  --0:非父节点  1：父节点  默认为非父节点
parent_id INT,      #若为非父节点则用来标识其直接父节点
FOREIGN KEY fk_cg_parent_id (parent_id) REFERENCES category(category_id)
);

INSERT INTO category (category_name,is_parent,parent_id)  VALUES ("电子",1,NULL);


#商品类别映射
CREATE TABLE goods_category(
goods_category_id INT PRIMARY KEY auto_increment,
goods_id INT NOT NULL,
category_id INT NOT NULL,
FOREIGN KEY fk_gc_goods_id (goods_id) REFERENCES goods(goods_id),
FOREIGN KEY fk_gc_category_id (category_id) REFERENCES category(category_id)
);

insert into goods_category (goods_id,category_id) values (1,1);


#普通订单
CREATE TABLE order_info(
order_id INT PRIMARY KEY auto_increment,
user_id INT NOT NULL,
goods_id INT NOT NULL,
goods_name VARCHAR(100) NOT NULL,
goods_count INT NOT NULL,
goods_price BIGINT NOT NULL,
status INT NOT NULL DEFAULT 0,  #订单状态 --0：创建订单，未付款  --1：付款  --2：订单取消   --（后期还可以拓展发货状态）
created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
pay_time DATETIME ,
FOREIGN KEY fk_oi_user_id (user_id) REFERENCES user(user_id),
FOREIGN KEY fk_oi_goods_id (goods_id) REFERENCES goods(goods_id)
);

#秒杀订单
CREATE TABLE miaosha_order(
miaosha_order_id INT PRIMARY KEY auto_increment,
user_id INT NOT NULL,
goods_id INT NOT NULL,
order_id INT NOT NULL,
FOREIGN KEY fk_mo_user_id (user_id) REFERENCES user(user_id),
FOREIGN KEY fk_mo_goods_id (goods_id) REFERENCES goods(goods_id),
FOREIGN KEY fk_mo_order_id (order_id) REFERENCES order_info(order_id)
);