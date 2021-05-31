基于 JavaFX 的即时通讯客户端程序
----------

### 基本配置

> JDK版本为16，如果需要在命令行执行```gradlew.bat run```，请设置环境变量`JAVA_HOME`指向jdk16根目录，也可以设置临时环境变量```set JAVA_HOME=C:\CommandLineTools\Java\jdk-15```，注意有些版本需要添加引号。

> 配置Emoji资源，[提取码：1sau](https://pan.baidu.com/s/1NgBu9n-cA6D8zfXtN8Cg-Q) ，解压后将文件夹 `/emojipedia/` 放到项目根目录下，注意不要保留压缩包名称所对应的文件夹。

### TODO

- JFoenix 其他的组件的复制和测试
- 托盘图标（实现一个任务队列处理收到新消息和好友请求时的通知操作）
- 主题皮肤
- 消除重复代码（后期）

### 未在多个平台测试过的功能（不包括Linux）

- 系统托盘功能（目前只在Windows 10上测试过）

### 构建说明，（使用了插件[Badass-Runtime plugin](https://badass-runtime-plugin.beryx.org/releases/latest/)）

- `gradle runtime`
  > 构建bat脚本，`build.gradle`中通过配置`runtime > launcher > noConsole`可以关闭掉执行时的cmd黑框口

- `gradle jpackageImage`
  > 构建exe程序

- `gradle jpackage`（Windows平台下，Java15版本此命令存在bug，Java16已经解决）
  > Windows上通过[WIX TOOLSET（需要将其bin目录添加到环境变量）](https://wixtoolset.org/) 来构建对应的安装程序

- 其他说明：
    - 此文档都基于Windows平台的实现来写，其他平台理论上相关命令执行效果一致。
    - `runtime > modules`的配置内容等于`gradle suggestModules`命令的结果。
    - 由于此项目emoji表情通过本地的`emojipedia`文件夹来加载，此构建命令是不会打包此文件夹，所以构建exe时，可以将`emojipedia`
      文件夹直接拷贝到exe所在目录，但构建安装包时，就没办法了，实际上emoji资源应该在程序运行时通过网络加载，所以这个问题也不是个问题。
    - 可以构建exe后，将`emojipedia`拷贝后使用`NSIS`等其他安装包制作工具来多加一步解决这个问题。
    - 非模块化的打包方式制作出来的安装包相比模块化的方式大一些，所以如果项目比较新，并且所用到的第三方库支持模块化的话，
      可以按照[JavaFX-Package-Sample](https://github.com/icuxika/JavaFX-Package-Sample) ， 其中`gradle`
      方式的插件可以自动模块化一些非模块化的第三方库，大部分情况下能解决问题。
    - 使用`Kotlin`来写`JavaFX`项目，支持模块化打包的项目模板[KtFX-Package-Sample](https://github.com/icuxika/KtFX-Package-Sample) 。
    - 使用`Kotlin`来写`JavaFX`项目，非模块化构建的方式，可以查看项目[KtFX-Lets-Plot](https://github.com/icuxika/KtFX-Lets-Plot) 的`non-modular`
      分支实现。
    - 项目[KtFX-Lets-Plot](https://github.com/icuxika/KtFX-Lets-Plot) 的`master`
      分支演示了模块化项目中使用未模块化的库的时候一个解决思路（目前只能执行，还没有解决构建对应的问题，Kotlin本身相关的库就挺混乱），相当麻烦。
    - 不想为模块化折腾的话，就使用非模块化的方式来构建项目。
    - 目前`jpackage`默认使用`WIX TOOLSET`，难以灵活配置，特殊需求，最好只构建出exe，然后使用其他安装包制作工具。

### 文档教程

- [JavaFX 16](https://openjfx.io/index.html)
- [JavaFX中文视频](https://space.bilibili.com/5096022/channel/detail?cid=16953)
- [JavaFX CSS Reference Guide](https://openjfx.cn/javadoc/15/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [GraalVM](https://www.graalvm.org/docs/getting-started/)
- [Gluon Documentation](https://docs.gluonhq.com/)

### 一些功能实现的来源

- [JFoenix](https://github.com/jfoenixadmin/JFoenix)
- [EasyFx（可选择可复制Label）](https://github.com/xizi110/easyfx)
- [JavaFX-ImageCropper（图片裁切）](https://github.com/imgeself/JavaFX-ImageCropper)
- [DataFX（AppView类的实现思路）](https://github.com/guigarage/DataFX)

### 界面预览

- 用户状态
  ![用户状态](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/UserStatus.png)
- 会话页面  
  ![会话页面](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/ConversationPage.png)
- 头像修改  
  ![头像修改](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/AvatarModify.png)
- 消息发送方式  
  ![消息发送方式](https://github.com/icuxika/IMFrameworkFX/raw/master/preview/MessageSendType.png)