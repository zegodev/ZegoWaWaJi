const path = require('path');

const webpackOutput = {
    path: path.resolve(__dirname ,'../../test'), // 打包输出目录
    filename: '[name].js', // 打包合并之后的js的命名，默认是入口文件的文件名
    publicPath: '/' // 网站运行时的静态资源目录   该项不会影响webpack对文件的打包生成路径，而是会在html-webpack-plugin在html页面上插入js、css时候影响
};

module.exports = webpackOutput;
