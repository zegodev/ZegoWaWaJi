const path = require('path');

let webpackModule = {
    rules: [
        {
            test: /\.css$/,
            use: ['style-loader', 'css-loader', 'postcss-loader']
        }, 
        {
            test: /\.scss$/,
            // use: ['style-loader', 'css-loader', 'sass-loader', 'postcss-loader']
            use: [
                {
                    'loader': 'style-loader'
                }, 
                {
                    'loader': 'css-loader',
                    options: {
                        importLoaders: 2
                    }
                }, 
                {
                    'loader': 'sass-loader'
                }, 
                {
                    'loader': 'postcss-loader'
                }
            ]
            // exclude: [path.resolve(__dirname, "./src/scss/product/")]
        }, 
        {
            test: /\.js$/,
            use: 'babel-loader',
            include: [path.resolve(__dirname, '../../src'), path.resolve(__dirname, '../../test')], // src目录下的js文件才被loader加载解析
            exclude: [path.resolve(__dirname, '../../src/static')] // src目录下的js文件才被loader加载解析
        }, 
        {
            test: /\.(png|jpe?g|gif|svg)(\?.*)?$/,
            use: [
                {
                    loader: 'url-loader',
                    options: {
                        limit: 8192, // 小于8192 ／ 1024 kb的图片会被url-loader压缩成base64格式
                        name: 'static/images/[name].[hash:7].[ext]' // 图片名字，7位哈希值，扩展名
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
                        name: 'static/font/[name].[hash:7].[ext]' // 字体名字，7位哈希值，扩展名
                    }
                }
            ]
        }
    ]
};

module.exports = webpackModule;