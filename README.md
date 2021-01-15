基于 JavaFX 的即时通讯客户端程序
----------

### 基本配置

> JDK版本为15，如果需要在命令行执行```gradlew.bat run```，请设置环境变量`JAVA_HOME`指向jdk15根目录，也可以设置临时环境变量```set JAVA_HOME=C:\CommandLineTools\Java\jdk-15```，注意有些版本需要添加引号。

> 配置Emoji资源，[提取码：1sau](https://pan.baidu.com/s/1NgBu9n-cA6D8zfXtN8Cg-Q) ，解压后将文件夹 `/emojipedia/` 放到项目根目录下，注意不要保留压缩包名称所对应的文件夹。

### TODO

- JFoenix 其他的组件的复制和测试
- 登录页面（密码、短信和二维码登录方式切换）
- 登录用户的名称和头像属性绑定
- 会话中，正确显示用户的头像和名称（目前测试时每个会话的消息的发送方名称和头像都与会话数据绑定，还未更正）
- 各类消息组件的编写
- 通讯录（一个好友分组列表，可以添加、删除或更新分组或好友，同时支持拖拽更换分组或排序）
- 托盘图标（图标闪烁、鼠标悬浮时显示提示窗口）
- 主题皮肤
- 消除重复代码（后期）

### 未在多个平台测试过的功能（不包括Linux）

- 系统托盘功能（目前只在Windows 10上测试过）

### 其他说明

> 目前功能和页面都很残缺，后面慢慢补。

> 由于JFoenix的许多类使用了在JavaFX新版本以及模块化中不允许使用的类，所以要使用```jpackage```命令打包会有些麻烦，目前是fork了一份openjfx15的源码，把源码改了再构建，使用自己构建的SDK来操作就可以，不过目前本项目还没有添加打包相关的配置项。

> 关于模块化有些东西还没弄清楚，所以我目前只能用上面这种方法，后面如果弄清楚了，或许可以不必这样麻烦。

### 界面预览

- 用户状态
  ![用户状态](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/UserStatus.png)
- 会话页面  
  ![会话页面](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/ConversationPage.png)
- 头像修改  
  ![头像修改](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/AvatarModify.png)
- 消息发送方式  
  ![消息发送方式](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/MessageSendType.png)