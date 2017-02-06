/**
 * Created by jbuisine on 06/02/17.
 */

var fs = require('fs');
var path = require('path');

var utilities = {
    getDirectories: function (srcpath) {
        return fs.readdirSync(srcpath)
                .filter(file => fs.statSync(path.join(srcpath, file)).isDirectory());
    }
};

module.exports = utilities;