const ora = require('ora');
const rm = require('rimraf');
const chalk = require('chalk');
const opn = require('opn');
const path = require('path');



const express = require('express');
let app = express();
const webpack = require('webpack');
const webpackDevMiddleware = require('webpack-dev-middleware');
const webpackHotMiddleware = require('webpack-hot-middleware');
// const proxyMiddleware = require('http-proxy-middleware');




const webpackEntry     = require('../config/dev/entry.dev.config.js');
const webpackOutput    = require('../config/dev/output.dev.config.js');
const webpackModule    = require('../config/dev/module.dev.config.js');
const webpackResolve   = require('../config/resolve.config.js');
const webpackPlugin    = require('../config/dev/plugin.dev.config.js');
const webpackExternals = require('../config/externals.config');




let webpackConfig = module.exports = {
    // cheap-module-eval-source-map is faster for development
    devtool: '#cheap-module-eval-source-map',
    entry: webpackEntry,
    output: webpackOutput,
    module: webpackModule,
    resolve: webpackResolve,
    plugins: webpackPlugin,
    externals: webpackExternals
};




// add hot-reload related code to entry chunks
Object.keys(webpackConfig.entry).forEach(function(name) {
    webpackConfig.entry[name] = ['webpack/hot/dev-server', 'webpack-hot-middleware/client'].concat(webpackConfig.entry[name]);
});



let port = 80;
let host = 'localhost';
let uri = 'http://' + host + ':' + port;
let autoOpenBrowser = true;



let spinner = ora('building for develop...');
spinner.start();


// const options = {
//     target: 'http://www.zego.im/test',
//     changeOrigin: true,
// };




rm(path.resolve(__dirname, '../test'), err => {
    if (err) { throw err; }
    let compiler = webpack(webpackConfig, function(err, stats) {
        spinner.stop();
        if (err) throw err;
        console.log(chalk.cyan('  devBuild complete.\n'));
    });

    // apiRoutes.get('/', function(req, res){
    //     console.log('req.query=', req.query);
    //     console.log(22222222222);
    // });

    // app.use(proxyMiddleware('http://localhost:8080/html/tmp/guide/video-show.html', options));

    app.all('*', function(req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "X-Requested-With");
        res.header("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
        res.header("X-Powered-By",' 3.2.1')
        res.header("Content-Type", "application/json;charset=utf-8");
        next();
    });

    app.use(webpackDevMiddleware(compiler, {
        publicPath: '/',
        stats: {
            colors: true
        }
    }));

    app.use(webpackHotMiddleware(compiler, {
        log: console.log(1)
    }));

    

    app.listen(port, function() {
        console.log('listening on ' + uri + '!');
    });

    if (autoOpenBrowser) {
        opn(uri);
    }

   

});