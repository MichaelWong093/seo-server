                        ESB 项目介绍


一 : 项目介绍


@  Collection 解释

brand 品牌索引库

category 后台类目索引库

categorys 前台类目索引库         category  id 关联  caterev revid

caterev 前台类目与后台类目关联索引库

gdhotwd 商品联想索引库

sphotwd 店铺联想索引库

goods 商品索引库

shop  店铺索引库

sku  商品属性索引库


打包构建跳过测试类

mvn install -Dmaven.test.skip=true

-DskipTests=true package