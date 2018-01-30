# H5-WaWaJi

ZegoWaWaJi is a delightful software solution for the Grab-Toy game. The content contains:

* web/WaWaJiWeb ——> H5 running on the Modern browser
* 项目运行于现代浏览器

## How To Get Started

* For H5, all you need to do is to clone or download this repository, run it on a server and set 'dist' as root dir
* 运行项目：1、对于dist目录，不分任何后端语言，可以任意启动一个服务器，将dist目录放置其中，并且设置dist为根目录，而后访问即可<br>
2、对于src目录，是未打包前的源代码<br>
（1）、在WaWaJiWeb目录下运行命令  cnpm install  安装依赖包<br>
（2）、接着执行命令  npm run dev  启动项目  运行开发环境<br>
（3）、构建项目执行命令  npm run build   改命令构建的项目没有压缩混淆，开发者有需要可以自行在webpack配置文件中配置<br>
## 关于版本的区别

娃娃机目前有多个版本


前两个版本使用Tag区分
开发者可以按照Tag标签定位版本，下载相应源码

> Tag H5-WaWaJi-1.0.1 <br>
对应安卓娃娃机版子第一版协议

> Tag H5-WaWaJi-1.0.2 <br>
对应安卓娃娃机版子第二版协议 <br>
1、sdk支持UMD加载 <br>
2、更新发送指令   由发送一次指令移动一次，转为发一次，一直移动，直到收到结束指令 <br>
3、增加poster <br>
4、demo源码添加注释 <br>

第三个版本从原来的分支 H5-WaWaJi-1.0.3转移到master分支，此后master作为H5 demo 的发布分支

>对应安卓娃娃机版子第三版协议 <br>
1、添加了未打包之前的源码 <br>
2、切换了正面和侧面流 <br>
3、长按时， seq不变 <br>
4、房间列表页添加了筛选条件 <br>
5、收到273指令信息，添加了逻辑判断