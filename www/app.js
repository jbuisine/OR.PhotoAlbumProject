/**
 * Created by jbuisine on 06/02/17.
 */

var express = require('express');
var path = require('path');
var fs = require('fs');

var routes = require('./modules/routes');

var app = express();


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

//Use css components
app.use(express.static(path.join(__dirname, 'public')));

//Use routes defined in other module
app.use('/', routes);

app.listen(3000, function () {
    console.log('Photo album app listening on port 3000!');
});