/**
 * Created by jbuisine on 06/02/17.
 */
var express = require('express');
var app = module.exports = express();

var server = require('http').createServer(app);
var io = require('socket.io')(server);

//Set connection
io.on('connection', function () {
    console.log('connection');
});

//Export module io for routes
module.exports.io = io;

var path = require('path');
var bodyParser = require('body-parser');

var homeRoutes = require('./modules/routes/index');
var templatesRoutes = require('./modules/routes/template');
var solutionRoutes = require('./modules/routes/solution');

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

//Use css components
app.use(express.static(path.join(__dirname, 'public')));

app.use(express.static(path.join(__dirname, 'views/templates')));

app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

//Use routes defined in other module
app.use('/', homeRoutes);
app.use('/', solutionRoutes);
app.use('/', templatesRoutes);

//Catch error page
app.get('*', function(req, res, next) {
    var err = new Error();
    err.status = 404;
    next(err);
});

// handling 404 & 500 errors
app.use(function(err, req, res, next) {
    if(err.status !== 404 && err.status !== 500) {
        return next();
    }
    res.status(err.status || 500);
    res.redirect('/error');
});

server.listen(3000, function () {
    console.log('Photo album app listening on port 3000!');
});