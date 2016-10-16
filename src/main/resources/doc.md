
<h1>B2C搜索接口文档</h1>

### 说明：
   > 本文档提供的接口都是Http 请求的接口

##1.B2C商铺搜索
###接口调用请求如下：
	http请求方式: POST
	http://127.0.0.1:8080/seo-server/seo/shop
###POST数据示例如下：
	{
		"hotwords":"跨境测试",
		"channel":"2000",
		"sort":"builddatetime",
		"rule":"asc",
		"start":"0",
		"rows":"100"
	} 
####请求参数解析
-|-
参数名	|  类型  	|    是否必须   |   备注
hotwords|  string   |      是      |   搜索内容
channel |  string   |      是      |   渠道  
sort    |  string   |      是      |   排序字段
rule    |  string   |      是      |   排序
start   |  string   |      是      |   第几条数开始
rows    |  string   |      是      |   多少行
 
####返回说明（正确时的JSON返回结果）：
	{
		"time": null,
		"code": null,
		"message": null,
		"serialNum": null,
		"page": 1,
		"totalNum": 2,
		"seoGoods": [
			{
			"shopId": "6002",
			"shopName": "跨境测试",
			"trueName": null,
			"shopLevelId": null,
			"source": "3",
			"logo": "o2o/image/pc/2016/03/23/7407/shop/201603232346529787Gana8b3.jpg",
			"address": "江苏省 常州市 戚墅堰区城关区",
			"businessarea": null,
			"totalSales": null,
			"goodsList": [
				{
				"hotwords": null,
				"prices": "200.0",
				"picture": "o2o/image/pc/2016/05/09/5211/goods/20160509232024518sVPC75fT.jpg",
				"sales": null,
				"comment": null,
				"shopName": null,
				"source": null,
				"goodsId": "15824",
				"activityLabel": null,
				"goodsName": "保税直发商品测试运费模板保税直发商品测试运费模板"
				},
				...
				]
			}
		]
	}
###返回参数解析
-|-
参数名	|  类型  	|    是否可为空   |   备注
time    |  string   |   是           |   请求时间
code    |  string   |   是           |   业务码
message |  string   |   是           |   业务状态码描述
serialNum| string   |   是           |   序列号
page    |  string   |   是           |   当前页
totalNum|  string   |   是           |   总条数
seoGoods|  string（json格式）|是      |  业务数据
shopId  |  string   |   否           |  商铺id
shopName|  string   |   否           |  商铺名
trueName|  string   || 商户昵称
shopLevelId|string  || 商户等级
source| string ||商铺来源
logo|string|| 商铺logo
address|string ||地址
businessarea|string||经营范围
totalSales|string|| 店铺总销量
prices|string||商品价格
picture|string||商品主图
goodsId|string||商品id
goodsName|string||商品名称
activityLabel|string||商品活动标识


##2.B2C商品搜索
###接口调用请求如下：
	http请求方式: POST
	http://127.0.0.1:8080/seo-server/seo/goods
###POST数据示例如下：
	{
		"hotwords":"test",
		"channel":"0000",
		"sort":"sales",
		"rule":"asc",
		"attribute":"hello",
		"other":"index"
	} 
####请求参数解析
-|-
参数名	|  类型  	|    是否必须   |   备注
hotwords|  string   |      是      |   搜索内容
channel |  string   |      是      |   渠道  
brand   |  string   |      是      |   品牌  
category |  string  |      是      |   类目  
sort    |  string   |      是      |   排序字段
rule    |  string   |      是      |   排序
start   |  string   |      是      |   第几条数开始
rows    |  string   |      是      |   多少行
 
####返回说明（正确时的JSON返回结果）：
	{
		"time": null,
		"code": null,
		"message": null,
		"serialNum": null,
		"page": null,
		"totalNum": null,
		"seoGoods": [
		{
		"hotwords": "test",
		"prices": "11.0",
		"picture": "o2o/image/pc/2016/08/26/17515/goods/20160826030707142eqQzEdiF.png",
		"sales": null,
		"comment": null,
		"shopName": "15085",
		"source": "8",
		"goodsId": "18698",
		"activityLabel": null,
		"goodsName": null
		},
		...
		]
	}

###返回参数解析
-|-
参数名	|  类型  	|    是否可为空   |   备注
time    |  string   |   是           |   请求时间
code    |  string   |   是           |   业务码
message |  string   |   是           |   业务状态码描述
serialNum| string   |   是           |   序列号
page    |  string   |   是           |   当前页
totalNum|  string   |   是           |   总条数
seoGoods|  string（json格式）|是      |  业务数据
hotwords|string||搜索内容
prices|string||商品价格
picture|string||商品主图
goodsId|string||商品id
goodsName|string||商品名称
source|string||商品名称
activityLabel|string||商品活动标识

##3.B2C商品热词搜索
###接口调用请求如下：
	http请求方式: POST
	http://127.0.0.1:8080/seo/gdhotwd
###POST数据示例如下：
	{
		"hotwords":"牛肉",
		"channel":"1000"
	} 
####请求参数解析
-|-
参数名	|  类型  	|    是否必须   |   备注
hotwords|  string   |      是      |   搜索内容
channel |  string   |      是      |   渠道  
 
####返回说明（正确时的JSON返回结果）：
	{
	     "time": null,
  		 "code": null,
  		 "message": null,
  		 "serialNum": null,
  		 "hotWords": [
	    {
	      "hotWord": "牛肉",
	      "frequency": 239
	    },
	    {
	      "hotWord": "牛肉面",
	      "frequency": 41
	    },
	    {
	      "hotWord": "牛肉味核桃",
	      "frequency": 23
	    },
	    {
	      "hotWord": "牛肉干",
	      "frequency": 16
	    },
	    {
	      "hotWord": "牛肉拉面",
	      "frequency": 14
	    },
	    {
	      "hotWord": "陕西牛肉",
	      "frequency": 144
	    }
	  ]
	}

###返回参数解析
-|-
参数名	|  类型  	|    是否可为空   |   备注
time    |  string   |   是           |   请求时间
code    |  string   |   是           |   业务码
message |  string   |   是           |   业务状态码描述
serialNum| string   |   是           |   序列号
hotWords|  string（json格式）|是      |  业务数据
hotWord |string||搜索内容
frequency|string||搜索的次数

##4.B2C店铺热词搜索
###接口调用请求如下：
	http请求方式: POST
	http://127.0.0.1:8080/seo/sphotwd
###POST数据示例如下：
	{
		"hotwords":"牛肉面馆",
		"channel":"1001"
	} 
####请求参数解析
-|-
参数名	|  类型  	|    是否必须   |   备注
hotwords|  string   |      是      |   搜索内容
channel |  string   |      是      |   渠道  
 
####返回说明（正确时的JSON返回结果）：
	{
		  "time": null,
		  "code": null,
		  "message": null,
		  "serialNum": null,
		  "hotWords": [
		    {
		      "hotWord": "东方宫牛肉面馆",
		      "frequency": 2
		    },
		    {
		      "hotWord": "马老六牛肉面馆",
		      "frequency": 2
		    },
		    {
		      "hotWord": "马友卜牛肉面馆",
		      "frequency": 2
		    },
		    {
		      "hotWord": "萨达姆牛肉面馆",
		      "frequency": 2
		    },
		    {
		      "hotWord": "金鼎牛肉面馆",
		      "frequency": 2
		    },
		    {
		      "hotWord": "希嘛香牛肉面馆",
		      "frequency": 1
		    },
		    {
		      "hotWord": "宇宙牛肉面馆",
		      "frequency": 1
		    }
		  ]
	}

###返回参数解析
-|-
参数名	|  类型  	|    是否可为空   |   备注
time    |  string   |   是           |   请求时间
code    |  string   |   是           |   业务码
message |  string   |   是           |   业务状态码描述
serialNum| string   |   是           |   序列号
hotWords|  string（json格式）|是      |  业务数据
hotWord |string||搜索内容
frequency|string||搜索的次数

##5.B2C商品热词推荐
###接口调用请求如下：
	http请求方式: POST
	http://127.0.0.1:8080/seo/gdhotwd
###POST数据示例如下：
	{
		"channel":"1010"
	} 
####请求参数解析
-|-
参数名	|  类型  	|    是否必须   |   备注
channel |  string   |      是      |   渠道  
 
####返回说明（正确时的JSON返回结果）：
	{
	  "time": null,
	  "code": null,
	  "message": null,
	  "serialNum": null,
	  "hotWords": [
	    {
	      "hotWord": "陕西牛肉",
	      "frequency": 852
	    },
	    {
	      "hotWord": "西北牛肉",
	      "frequency": 787
	    },
	    {
	      "hotWord": "牛肉面",
	      "frequency": 703
	    },
	    {
	      "hotWord": "核桃",
	      "frequency": 324
	    },
	    {
	      "hotWord": "枸杞",
	      "frequency": 324
	    }
	  ]
	}

###返回参数解析
-|-
参数名	|  类型  	|    是否可为空   |   备注
time    |  string   |   是           |   请求时间
code    |  string   |   是           |   业务码
message |  string   |   是           |   业务状态码描述
serialNum| string   |   是           |   序列号
hotWords|  string（json格式）|是      |  业务数据
hotWord |string||搜索内容
frequency|string||搜索的次数




