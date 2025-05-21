
# 目录

## 10-2 在阿里云智能语音交互服务中设置支持识别多国语言.mp4

### [阿里云-智能语音交互](https://nls-portal.console.aliyun.com/overview)

首次打开页面会提示，开通

#### 创建项目

路径：全部项目 -》 创建项目 -》 项目名称（中文） -》 其他默认

## [关于登录调试步骤](https://plus-doc.dromara.org/#/questions/login_step?id=%e5%85%b3%e4%ba%8e%e7%99%bb%e5%bd%95%e8%b0%83%e8%af%95%e6%ad%a5%e9%aa%a4)

### 1. 关闭 api 接口加密 （方便查看F12的请求参数）

#### 1.1 后端

修改后端配置文件 application.yml

```yaml
# api接口加密
api-decrypt:
  # 是否开启全局接口加密
  #enabled: true
  enabled: false  ## 设置 false
## ... 
```

#### 1.2 前端

修改前端配置文件 .env.development | .env.production

```text
# 接口加密功能开关(如需关闭 后端也必须对应关闭)
VITE_APP_ENCRYPT = true
```

### 1.3 屏蔽登录验证码

src/main/resources/application.yml

```yaml
captcha:
#  enable: true
  enable: false  ## 设置 false
## ... 
```

### 2. 登录参数

#### 2.1 对照表
```text
参数名 	说明
tenantId 	租户id
username 	用户名
password 	密码
rememberMe 	记住密码
uuid 	-
code 	验证码结果
clientId 	客户端id（表 sys_client）
grantType 	授权类型（表 sys_client）
```

### 2.2 上述已经屏蔽验证码，`故此缺少 code`

```json
{
    "tenantId": "000000",
    "username": "admin",
    "password": "admin123",
    "rememberMe": true,
    "clientId": "e5cd7e4891bf95d1d19206ce24a7b32e",
    // "code": "12",
    "grantType": "password"
}
```

### 2.3 没有屏蔽验证码，也没有传入会提示下面错误

```json
{
    "code": 403,
    "msg": "没有访问权限，请联系管理员授权",
    "data": null
}
```