
# 前端

## 9-9 前端集成vod sdk实现文件上传

### [使用JavaScript SDK上传文件](https://help.aliyun.com/zh/vod/developer-reference/upload-sdk-for-javascript?spm=a2c4g.11186623.help-menu-29932.d_4_1_6_1_1.3a6b485cEgJijk#34b59711afp0q)

* [下载SDK代码：V1.5.6 JavaScript上传SDK](https://alivc-demo-cms.alicdn.com/versionProduct/sourceCode/upload/JS/aliyun-upload-sdk-1.5.6.zip?spm=a2c4g.11186623.0.0.75604c7eagoFQg&file=aliyun-upload-sdk-1.5.6.zip)
* 使用安装命令：npm install aliyun-upload-sdk


| 特性                 | 直接复制 SDK 文件                          | 使用 npm install aliyun-upload-sdk          |
|----------------------|-------------------------------------------|---------------------------------------------|
| 引入方式             | 通过 `<script>` 标签引入                  | 通过 `import` 引入                          |
| 依赖管理             | 手动管理文件，容易遗漏或版本不一致        | 通过 `package.json` 管理，版本一致          |
| 构建工具支持         | 需要手动配置构建工具（如 Vite/Webpack）   | 自动支持，无需额外配置                      |
| 代码提示和类型检查   | 无代码提示和类型检查                      | 支持代码提示和类型检查（如果有类型定义）    |
| 适用场景             | 简单项目或快速原型开发                    | 现代前端项目，尤其是使用 TypeScript 的项目  |

`总结: 直接复制 SDK 文件：适合简单项目, 如果项目较复杂或使用 TypeScript，推荐使用 npm install aliyun-upload-sdk。`


暂时项目使用直接复制SDK方式
1. 下载 aliyun-upload-sdk.js 并放入 public/js 目录：
```text
/public
  /js
    /aliyun-oss-sdk-6.17.1.min.js
    /aliyun-upload-sdk-1.5.6.min.js
```
2. 在根目录下 index.html 中引入：
```text
<html>
  <head>
      <script src="/js/aliyun-oss-sdk-6.17.1.min.js"></script>
      <script src="/js/aliyun-upload-sdk-1.5.6.min.js"></script>
      <script src="/js/es6-promise.min.js"></script>
  </head>
</html>
```

### 目录结构说明 （截止到 9.9）

```text
plus-ui-ts
│
│ index.html ## 通过这里引入阿里云Vod.SDK
├─public
│  │  favicon.ico
│  │  
│  └─js  ## 阿里云Vod.SDK
│          aliyun-oss-sdk-6.17.1.min.js
│          aliyun-upload-sdk-1.5.6.min.js
│          es6-promise.min.js
├─src
│  │  
│  ├─components ## 组件目录
│  │  ├─Alibaba
│  │  │  └─Vod
│  │  │          FileUploader.vue ## 所以关于文件上传、阿里云SDK调用、请求后端API接口的逻辑
│  │  │
│  ├─api ## 调用后端API目录
│  │  │  
│  │  ├─audio
│  │  │  └─voiceRecognition
│  │  │          filetrans-upload.ts ## 请求API地址：/web/vod/get-upload-auth
│  │  │          types.ts ## 实体参数
│  │  │
│  └─views ## 展示页面
│      │  
│      ├─audio
│      │  └─voiceRecognition
│      │          filetrans-upload.vue  ## 点击按钮后文件弹框的实现, 里面调用 FileUploader.vue组件
│      │          index.vue  ## 语音识别的主窗口
```

`按照上面调用顺序： views ==> components ==> api`