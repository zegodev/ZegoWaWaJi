const path = require('path');

let util = {
    resolve: function(dir){
        return path.join(__dirname, '..', dir);
    }
};

module.exports = util;