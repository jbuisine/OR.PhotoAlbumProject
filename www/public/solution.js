/**
 * Created by jbuisine on 09/02/17.
 */

var socket = io.connect('http://localhost:3000');

var bar = $('#progress-bar');

bar.hide();

/* Listen on uploadProgress canal to get */
socket.on('uploadProgress' , function (percent){
    if(!bar.is(":visible")){
        bar.show('1000');
    }

    if(percent.indexOf('%') !== -1){
        var formattedPercent = parseFloat(percent.split('>')[1].replace('%', '')).toFixed(2);

        var progressBarItem = bar.find('.progress-bar')
        progressBarItem.text('');

        progressBarItem.width(formattedPercent + '%');
        progressBarItem.attr('aria-valuenow', formattedPercent);

        if(formattedPercent > 5){
            progressBarItem.text(formattedPercent + '%');
        }
    }
});

$('#content').hide();
$('button[id^="createSolution"]').hide();

$(document).ready(function () {
    $('#algorithmChoice input').attr('disabled', true);
});

$('#criteriaChoice input').change(function(){

    $('#content').hide();
    var nbChecked = $('#criteriaChoice input:checked').length;
    showOrHideCriteria(nbChecked);
});

$('#HC').change(function () {

    updateContent();

    $('#numberPermutation').show();
});

$('#EA').change(function () {

    updateContent();

    $('#HillClimber').show();
    $('#EAAlgorithm').show();
    $('#numberPermutation').show();
});

$('#ILS').change(function () {

    updateContent();

    $('#HillClimber').show();
    $('#numberPermutation').show();
});

$('#PLS').change(function () {

    updateContent();

    $('#createSolutionPLS').show();
});

$('#MOEAD').change(function () {

    updateContent();

    $('#MOEADAlgorithm').show();
    $('#numberPermutation').show();
});

$('#createSolution').click(function (e) {
    e.preventDefault();

    var model_data = $("#create-solution-form").serializeObject();

    $.ajax({
        type: "POST",
        url: '/create-solution',
        contentType: 'application/json',
        data: JSON.stringify(model_data),
        success: function (data) {

            console.log(data);
        }
    });
});

function showOrHideCriteria(nbElem) {
    console.log(nbElem);
    if(nbElem > 1 && nbElem <= 2){
        //One objective hidden
        $('#HC').attr('disabled',true);
        $('#ILS').attr('disabled',true);
        $('#EA').attr('disabled',true);

        //Multiple objectives shown
        $('#PLS').attr('disabled',false);
        $('#MOEAD').attr('disabled',false);
    }else if(nbElem > 2 && nbElem <= 3){
        //Hidden option
        $('#HC').attr('disabled',true);
        $('#ILS').attr('disabled',true);
        $('#EA').attr('disabled',true);
        $('#MOEAD').attr('disabled',true);

        //Three objectives
        $('#PLS').attr('disabled',false);

        $('#criteriaChoice input:not(:checked)').attr('disabled', true);
    }
    else if(nbElem == 1){
        //One objective shown
        $('#HC').attr('disabled',false);
        $('#ILS').attr('disabled',false);
        $('#EA').attr('disabled',false);

        //Multiple objectives hidden
        $('#PLS').attr('disabled',true);
        $('#MOEAD').attr('disabled',true);
    }else {
        $('#algorithmChoice input').attr('disabled', true);
    }

    if(nbElem <= 2){
        $('#criteriaChoice input:not(:checked)').attr('disabled', false);
    }

    $('#algorithmChoice input').prop('checked', false);
}


function updateContent() {

    $('#content').show();

    $('#EAAlgorithm').hide();
    $('#createSolutionILS').show();
    $('#HillClimber').hide();
    $('#EAAlgorithm').hide();
    $('#MOEADAlgorithm').hide();
    $('#numberPermutation').hide();
    $('button[id^="createSolution"]').show();
}