/**
 * Created by jbuisine on 06/02/17.
 */

var express = require('express');
var router = express.Router();

var utilities = require('./utilities');
const albumsPath = './albums';

router.get('/', function (req, res) {
    res.render('index', {
        page: "home",
        albums: utilities.getDirectories(albumsPath)
    });
});

router.get('/about', function (req, res) {
    res.render('index', {
        page: "about",
        albums: utilities.getDirectories(albumsPath)
    });
});

router.get('/contact', function (req, res) {
    res.render('index', {
        page: "contact",
        albums: utilities.getDirectories(albumsPath)
    });
});

module.exports = router;