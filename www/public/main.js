/**
 * Created by jbuisine on 08/02/17.
 */
var socket = io.connect('http://localhost:3000');

/* Listen on generationFinished canal to get information of finished generation */
socket.on('generationFinished' , function (data){
    console.log('finished');

    if(Notification.permission !== 'granted'){
        Notification.requestPermission();
    }

    n = new Notification( "Generation finished", {
        body: data.solFile,
        icon : "/images/generation-finished.png"
    });
});