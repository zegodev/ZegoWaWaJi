const ExtractTextPlugin = require('extract-text-webpack-plugin');
const path = require('path');

let webpackModule = {
    rules: [
        {
            test: /\.css$/,
            use: ExtractTextPlugin.extract({
                use: [
                    {
                        'loader': 'css-loader',
                        'options': {
                            minimize: true
                        }
                    }
                ]
            })
        },
        {
            test: /\.scss$/,
            use: ExtractTextPlugin.extract({
                use: [
                    {
                        'loader': 'css-loader',
                        options: {
                            importLoaders: 2,
                            minimize: true,
                            sourceMap: false
                        }
                    },
                    { 'loader': 'sass-loader', options: { sourceMap: false } },
                    { 'loader': 'postcss-loader' }
                ]
            }),
            // exclude: [path.resolve(__dirname, "./src/scss/product/")]
            include: [path.resolve(__dirname, '../../src')],
        },
        {
            test: /\.js$/,
            use: 'babel-loader',
            include: [path.resolve(__dirname, '../../src')], // src目录下的js文件才被loader加载解析
            exclude: [path.resolve(__dirname, '../../src/static')] // src目录下的js文件才被loader加载解析
        },
        {
            test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
            use: [
                {
                    loader: 'url-loader',
                    options: {
                        limit: 8192, // 小于8192 ／ 1024 kb的图片会被url-loader压缩成base64格式
                        name: 'static/images/[name].[hash:7].[ext]', // 图片名字，7位哈希值，扩展名
                        // outputPath: '/static/images/',
                        // publicPath: function(url) {
                        //     return url.replace(/static/, '..');
                        // }
                    }
                }
            ]
        },
        {
            test: /\.(woff2?|eot|ttf|otf)(\?.*)?$/,
            use: [
                {
                    loader: 'url-loader',
                    options: {
                        limit: 10000, // 小于10000 ／ 1024 kb的字体会被url-loader压缩成base64格式
                        name: 'static/font/[name].[hash:7].[ext]', // 字体名字，7位哈希值，扩展名
                        // outputPath: '/static/font/',
                        // publicPath: function(url) {
                        //     return url.replace(/static/, '..');
                        // }
                    }
                }
            ]
        }
        // ,
        // {
        //通过该loader，可以把test匹配到的模块设置为全局变量，
        //比如例子中，将jQuery设置为全局变量，之后再通过管道进一步声明为全局变量$
        //      test: /\.\/src\/js\/jquery-1\.11\.3\.min\.js$/,
        //      use: 'expose-loader?$!expose?jQuery'
        // }
    ]
};

module.exports = webpackModule;