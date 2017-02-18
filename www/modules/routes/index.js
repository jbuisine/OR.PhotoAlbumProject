/**
 * Created by jbuisine on 06/02/17.
 */
var express = require('express');
var router = express.Router();
var utilities = require('./../utilities');

const templatesPath = './views/templates/';

router.get('/', function (req, res) {
    res.render('index', {
        page: "home",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get('/about', function (req, res) {
    res.render('index', {
        page: "about",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get('/contact', function (req, res) {
    res.render('index', {
        page: "contact",
        templates: utilities.getDirectories(templatesPath)
    });
});

router.get("/error", function(req, res){
    res.render('index', {
        page: 'error',
        message: "Page not found",
        templates: utilities.getDirectories(templatesPath)
    });
});

module.exports = router;