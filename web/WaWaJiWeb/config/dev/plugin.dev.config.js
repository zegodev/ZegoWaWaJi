const path = require('path');
// const util = require('util');
const webpackEntry = require('./entry.dev.config');
const HtmlwebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const FriendlyErrorsPlugin = require('friendly-errors-webpack-plugin');
const webpack = require('webpack');

let webpackPlugin = [
    new CopyWebpackPlugin([{
        from: path.resolve(__dirname, '../../src/static/'),
        to: path.resolve(__dirname, '../../test/static/'),
        ignore: ['.*']
    }]),
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoEmitOnErrorsPlugin(),
    new FriendlyErrorsPlugin(),
    new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'window.jQuery': 'jquery',
        'window.$': 'jquery',
        'process.env': {
            NODE_ENV: JSON.stringify('development')
        },
    })
];

// 获取模版的部分路径数组，即在src／html下的路径
let pageArr = Object.keys(webpackEntry);
// console.log('pageArr = ', pageArr);

pageArr.forEach((page) => {
    let htmlPlugin;
    if (page === 'index') {
        htmlPlugin = new HtmlwebpackPlugin({
            filename: './index.html',
            template: path.resolve(__dirname, '../../', 'src', './index.html'),
            chunks: [page],
            // inject: false, // 默认为true
            hash: true // 为静态资源生成hash值 默认为false  开发环境下hash必须打开，否js无法插入页面，未知原因，此时页面会报 cannot get／
        });
    } else {
        htmlPlugin = new HtmlwebpackPlugin({
            filename: `./${page}.html`,
            template: path.resolve(__dirname, '../../', 'src/html', `./${page}.html`),
            chunks: [page],
            // inject: false, // 默认为true
            hash: true // 为静态资源生成hash值 默认为false  开发环境下hash必须打开，否js无法插入页面，未知原因，此时页面会报 cannot get／
        });
    }
    webpackPlugin.push(htmlPlugin);
});
// console.log('webpackPlugin = ',util.inspect(webpackPlugin, true, 4, true));
module.exports = webpackPlugin;
