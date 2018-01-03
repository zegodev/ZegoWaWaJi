const path = require('path');

let webpackResolve = { // 解析配置
    extensions: ['.js', '.css', '.scss'], // 自动扩展文件的后缀名，比如在require时，不写后缀名js
    alias: { //  定义模块的别名，方便后续直接用别名引用文件，无须写过长的地址
        'vue$': 'vue/dist/vue.esm.js',
        '@src': path.resolve(__dirname, '../src'),
        '@assets': path.resolve(__dirname, '../src/assets'),
        '@components': path.resolve(__dirname, '../src/components'),
        '@scss': path.resolve(__dirname, '../src/scss'),
        '@static': path.resolve(__dirname, '../src/static')
    },
    // modules: [ // webpack解析时，应该搜索的目录
    //     path.resolve(__dirname, 'src'),
    //     'node_modules'
    // ]
};

module.exports = webpackResolve;