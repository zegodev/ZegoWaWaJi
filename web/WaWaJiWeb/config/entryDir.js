var fs = require('fs');

// 获取webpack入口文件
function getDirectory(dir, ignoreFileArr) {
    var children = [];
    fs.readdirSync(dir).forEach(function (filename) {
        if (filename === 'node_modules' || filename === '.DS_Store') {
            return;
        }
        var path = dir + '/' + filename;
        var stat = fs.statSync(path);
        // console.log('\n', '读取路径path=',path)
        if (ignoreFileArr) {
            for (var i = 0; i < ignoreFileArr.length; i++) {
                // console.log('忽略路径ignoreFileArr'+ i +'=',ignoreFileArr[i])
                if (path === ignoreFileArr[i]) {
                    return;
                }
            }
        }
        if (stat && stat.isDirectory()) {
            children = children.concat(getDirectory(path, ignoreFileArr) && getDirectory(path, ignoreFileArr));
        } else {
            var splitPath = path.split('/');
            // console.log(splitPath)
            var len = splitPath.length;
            if (splitPath[len - 1].slice(0,1) === '_') {
                return;
            }
            children.push(path.replace(/^\.\//, ''));
        }
    });
    return children;
}

module.exports = getDirectory;