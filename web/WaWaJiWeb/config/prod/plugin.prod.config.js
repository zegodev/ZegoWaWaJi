const path = require('path');
const HtmlwebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ImageminPlugin = require('imagemin-webpack-plugin').default;
const webpack = require('webpack');




// 动态入口文件
const dynamicWebpackEntry = require('./entry.prod.config.js').dynamicEntry;



// 获取模版的部分路径数组，即在src／html下的路径
let dynamicPageArr = Object.keys(dynamicWebpackEntry);
// console.log('dynamicPageArr = ', dynamicPageArr)




let webpackPlugin = [
    new ExtractTextPlugin({
        filename: 'static/css/[name].[contenthash].css',
    }),

    // split vendor js into its own file
    // new webpack.optimize.CommonsChunkPlugin({
    //     name: 'vendor',
    //     minChunks: function(module, count) {
    //         // any required modules inside node_modules are extracted to vendor
    //         // console.log('module =', module.resource);
    //         // console.log('count =', count);
    //         console.log('module = ', module);
    //         // return (
    //         //     module.resource &&
    //         //     /\.js$/.test(module.resource) &&
    //         //     module.resource.indexOf(
    //         //         path.join(__dirname, '../node_modules')
    //         //     ) === 0
    //         // );
    //     }
    // }),

    /* 抽取出动态入口文件中所有通用的部分 */
    new webpack.optimize.CommonsChunkPlugin({
        name: 'common', // 需要注意的是，chunk的name不能相同！！！
        minChunks: 10,
        chunks: dynamicPageArr
    }),

    // extract webpack runtime and module manifest to its own file in order to
    // prevent vendor hash from being updated whenever app bundle is updated
    new webpack.optimize.CommonsChunkPlugin({
        name: 'manifest',
        chunks: ['common']
    }),

    new CopyWebpackPlugin([
        {
            from: path.resolve(__dirname, '../../src/static/css'),
            to: path.resolve(__dirname, '../../dist/static/css'),
        }, 
        {
            from: path.resolve(__dirname, '../../src/static/js'),
            to: path.resolve(__dirname, '../../dist/static/js'),
        },
        {
            from: path.resolve(__dirname, '../../src/static/media'),
            to: path.resolve(__dirname, '../../dist/static/media'),
        },
        {
            from: path.resolve(__dirname, '../../src/static/ico'),
            to: path.resolve(__dirname, '../../dist'),
        }
    ]),


    // new ImageminPlugin({
    //     pngquant: {
    //         quality: '95-100'
    //     }
    // }),


    // new webpack.optimize.UglifyJsPlugin({ //webpack内置插件，压缩代码
    //     compress: {
    //         warnings: false
    //     },
    //     sourceMap: false,
    //     except: ['$super', '$', 'exports', 'require'] //排除关键字，使之不被压缩或者去除
    // }),
    // new webpack.optimize.UglifyJsPlugin({ //webpack内置插件，压缩代码
    //     compress: false,
    //     output: {
    //         comments: true,
    //         beautify: true,
    //     },
    //     sourceMap: false,
    //     except: ['$super', '$', 'exports', 'require'] //排除关键字，使之不被压缩或者去除
    // }),
    new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'window.jQuery': 'jquery',
        'window.$': 'jquery',
        'process.env': {
            NODE_ENV: JSON.stringify('production')
        }
    })
];



dynamicPageArr.forEach((page) => {
    let htmlPlugin;
    let pageHead = page.split('/')[0];
    if (pageHead === 'index') {
        htmlPlugin = new HtmlwebpackPlugin({
            filename: './index.html',
            template: path.resolve(__dirname, '../../', 'src', './index.html'),
            chunks: ['manifest', 'common', page],
            chunksSortMode: function(chunk1, chunk2){
                var order = ['manifest', 'common', page];
                var order1 = order.indexOf(chunk1.names[0]);
                var order2 = order.indexOf(chunk2.names[0]);
                return order1 - order2; 
            },
            // minify: {
            //     removeComments: true, // 移除html 注释
            //     collapseWhitespace: true, // 移除空白符和换行符
            //     // removeAttributeQuotes: true  //删除html元素中属性的引号
            // },
            // inject: false, // 默认为true
            // hash: true // 为静态资源生成hash值 默认为false
        });
    } else {
        htmlPlugin = new HtmlwebpackPlugin({
            filename: `${page}.html`,
            template: path.resolve(__dirname, '../../', 'src/html', `./${page}.html`),
            chunks: ['manifest', 'common', page],
            chunksSortMode: function(chunk1, chunk2){
                var order = ['manifest', 'common', page];
                var order1 = order.indexOf(chunk1.names[0]);
                var order2 = order.indexOf(chunk2.names[0]);
                return order1 - order2; 
            },
            // minify: {
            //     removeComments: true, // 移除html 注释
            //     collapseWhitespace: true, // 移除空白符和换行符
            //     // removeAttributeQuotes: true  //删除html元素中属性的引号
            // },
            // inject: false, // 默认为true
            // hash: true // 为静态资源生成hash值 默认为false
        });
    }
    webpackPlugin.push(htmlPlugin);
});


module.exports = webpackPlugin;



