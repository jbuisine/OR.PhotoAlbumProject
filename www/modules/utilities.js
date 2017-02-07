/**
 * Created by jbuisine on 06/02/17.
 */

var fs = require('fs');
var path = require('path');

var utilities = {
    getDirectories: function (srcpath) {
        return fs.readdirSync(srcpath)
                .filter(file => fs.statSync(path.join(srcpath, file)).isDirectory());
    },
    getFiles: function (srcpath) {
        return fs.readdirSync(srcpath)
                .filter(file => fs.statSync(path.join(srcpath, file)).isFile());
    },
    filePathExists: function(filePath) {
        return new Promise((resolve, reject) => {
            fs.stat(filePath, (err, stats) => {
                if (err && err.code === 'ENOENT') {
                    return resolve(false);
                } else if (err) {
                    return reject(err);
                }
                if (stats.isFile() || stats.isDirectory()) {
                    return resolve(true);
                }
            });
        });
    },
    readFileContent: function(filePath){
        fs.readFile(filePath, function (err,data) {
            if (err) {
                return console.log(err);
            }
            return data;
        });
    }
};

module.exports = utilities;