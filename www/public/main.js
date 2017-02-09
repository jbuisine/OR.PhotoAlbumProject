/**
 * Created by jbuisine on 08/02/17.
 */

var socket = io.connect('http://localhost:3000');

socket.on('uploadProgress' , function (percent){
    alert(percent);
});