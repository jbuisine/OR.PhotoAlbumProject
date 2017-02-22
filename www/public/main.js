/**
 * Created by jbuisine on 08/02/17.
 */
var socket = io.connect('http://localhost:3000');


$(document).ready(function () {

    //Change modal visibility to let possible add elements to it
    $('#process-running-modal').show().css('visibility', 'hidden');

    //When li item for process running information get mouse enter event, show the modal
    $('#process-running').hover(function(){
        if($('div[id^="solution-generation"]').length !== 0) {
            $('#process-running-modal').css('visibility', 'visible');
            $('#process-running-modal').modal('show');
        }
    });
});

/* Listen on generationFinished canal to get information of finished generation */
socket.on('generationFinished' , function (data){

    if(Notification.permission !== 'granted'){
        Notification.requestPermission();
    }

    n = new Notification( "Generation finished", {
        body: data.solFile,
        icon : "/images/generation-finished.png"
    });
});

/* Listen on generationProgress canal to get progress of generation and update information into DOM elements */
socket.on('generationProgress' , function (data){

    if(data.percent.indexOf('%') !== -1){

        var formattedPercent = parseInt(data.percent.split('>')[1].replace('%', ''));

        var circleID;

        circleID = data.solFile.replace('.', '-') + "-circle";

        var circle = $('#'+circleID);

        if(!circle.length){
            generateProgressCircle(circleID, formattedPercent);
            return;
        }

        updateProgressCircle(circle, formattedPercent);

        if(formattedPercent >= 100){
            $('#solution-generation-' + circleID).hide('2000', function () {
                removeProcessInformation($(this));
            });
        }
    }

    console.log($('div[id^="solution-generation"]').length);
    $('#process-running span').text($('div[id^="solution-generation"]').length);
});


/**
 * Function uses as callback when process running is finished to shown information about it
 *
 * @param elem
 */
function removeProcessInformation(elem){
    console.log(elem);
    elem.remove();
    var nbProcess =  $('div[id^="solution-generation"]').length;
    console.log(nbProcess);
    if(nbProcess === 0){
        $('#process-running-modal').modal('hide').css('visibility', 'hidden');
    }

    $('#process-running span').text(nbProcess);

}
/**
 * Function which generate a new information div of an running process
 *
 * @param id
 * @param percent
 */
function generateProgressCircle(id, percent){
    var filename;
    var tempID = id.replace("-circle", "");
    if(tempID.lastIndexOf('-') !== -1){
        var pos = tempID.lastIndexOf('-');
        filename = tempID.substring(0,pos)+'.'+tempID.substring(pos+1);
    }else{
        filename = tempID;
    }

    var content = '<div id="solution-generation-' + id + '" class="row">';

    if($('div[id^="solution-generation"]').length !== 0){
        content += '<hr />';
    }

    content += '<div class="col-md-9" style="padding-top:15px;"><h5>' + filename + '</h5></div>';
    content += '<div class="col-md-3">';
    content += '<div id="' + id + '" class="c100 p' + percent + ' small green">';
    content += '<span>' + percent + '%</span>';
    content += '<div class="slice">';
    content += '<div class="bar"></div>';
    content += '<div class="fill"></div>';
    content += '</div>';
    content += '</div>';
    content += '</div>';
    content += '</div>';


    $('#progress-content').append(content);
    $('#solution-generation-' + id).hide().show('2000');
}

/**
 * Update the information of the running process
 *
 * @param circle
 * @param percent
 */
function updateProgressCircle(circle, percent){
    circle.removeClass('p'+ percent -1).addClass('p' + percent);
    circle.find('span').text(percent + '%');
}
