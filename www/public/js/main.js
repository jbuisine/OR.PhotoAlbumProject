/**
 * Created by jbuisine on 08/02/17.
 */
var socket_local = io.connect("http://"+window.location.host);

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
socket_local.on('generationFinished', genrationFinished);

/* Listen on generationProgress canal to get progress of generation and update information into DOM elements */
socket_local.on('generationProgress', generationProgress);

/* Listen on generationFile canal to get information about generation finished for a template */
socket_local.on('templateGeneration', function (templateName) {
    generateNotification("Template generation file finished, you can now use this template !", templateName);
});


$('button[id^="link_"]').on("click", function () {
   var action_url = $(this).attr("data-link");

    $.ajax({
        type: "POST",
        url: action_url,
        success: function (data) {
            generateNotification("Application information", data);
        }
    });

});

/**
 * Function which used for show notification when generation is finished
 *
 * @param data
 */
function genrationFinished(data){
    generateNotification("Generation finished", data.solFile);
}

/**
 * Function which is used for update information when data are sent on socket port
 *
 * @param data
 */
function generationProgress(data){

    if(data.percent){

        var formattedPercent = data.percent;

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

    $('#process-running span').text($('div[id^="solution-generation"]').length);
}


/**
 * Function uses as callback when process running is finished to shown information about it
 *
 * @param elem
 */
function removeProcessInformation(elem){

    elem.remove();
    var nbProcess =  $('div[id^="solution-generation"]').length;

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

/**
 * Function which used for send notification
 * @param title
 * @param content
 */
function generateNotification(title, content){
    if(Notification.permission !== 'granted'){
        Notification.requestPermission();
    }

    n = new Notification( title, {
        body: content,
        icon : "/img/generation-finished.png"
    });
}