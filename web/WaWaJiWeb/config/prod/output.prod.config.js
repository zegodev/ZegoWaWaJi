const path = require('path');

let filename;
// 判断是构建后给github上demo用的，还是发布到wwj.zego.im上用的
// demo上不需要hash值，wwj.zego.im上需要来解决缓存问题
console.log('buildType = ', process.env.buildType);
if (process.env.buildType === 'demo') {
    filename = 'static/js/assets/[name].js';
} else {
    filename = 'static/js/assets/[name].[chunkhash].js';
}
const webpackOutput = {
    path: path.resolve(__dirname, '../../dist'), // 打包输出目录
    filename: filename, // 打包合并之后的js的命名，默认是入口文件的文件名
    chunkFilename: 'common/[id].js',  // 非入口文件的 其他的被入口文件require的文件的命名规则
    publicPath: '/' // 网站运行时的静态资源目录   该项不会影响webpack对文件的打包生成路径，而是会在html-webpack-plugin在html页面上插入js、css时候影响
};

module.exports = webpackOutput;