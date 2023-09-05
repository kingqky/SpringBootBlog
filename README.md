# king Blog
## 个人博客系统
king Blog是由SpringBoot1.5 + MyBatis + Thymeleaf等技术实现的个人网站，如果觉得项目不错，请帮忙`Star`支持一下。
### 适用对象
* Spring Boot 初学者。该博客系统综合运用了作者发表的 《Spring Boot 入门》 系列的文章提及的知识内容，初学者可以阅读文章以及结合该项目学习。
* 与作者一样，使用 hexo 但苦于没有后台管理工具（界面）管理文章的写作者。该博客系统模仿 hexo 生成的访问路径，并支持 markdown 文件导入功能。
* 懵懂者。初次接触博客系统的人。
### 技术栈
#### 后端
* 核心框架：SpringBoot
* 持久层框架：MyBatis
* 模板框架：Thymeleaf
* 分页插件：PageHelper
* 缓存框架：Ehcache
* Markdown：Commonmark

#### 前端
* JS框架：Jquery
* CSS框架：Bootstrap
* 富文本编辑器：editor.md
* 文件上传：dropzone
* 弹框插件：sweetalert

#### 第三方
* Minio（文件上传）
* 百度统计

### 安装
下载源码，执行sql文件，然后修改application-dev.yml文件中连接数据库的用户名、密码。运行项目即可。</br>
前端访问地址：http://localhost:8888 </br>
后台访问地址：http://localhost:8888/adminlogin_king 用户名：admin 密码：123456

