/**
 * Created by jbuisine on 09/02/17.
 */

var socket = io.connect('http://localhost:3000');
var LIMIT_PROCESS = 4;
var modal_error = $('#create-solution-dialog');
var modal_error_content = modal_error.find('.modal-body p');
$('#content').hide();
$('button[id^="createSolution"]').show();
$('button[id^="createSolution"]').attr('disabled', true);

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
});

$('#EA').change(function () {

    updateContent();

    $('#HillClimber').show('200');
    $('#EAAlgorithm').show('200');
    $('#numberPermutation').show('200');
});

$('#ILS').change(function () {

    updateContent();

    $('#HillClimber').show('200');
    $('#numberPermutation').show('200');
});

$('#PLS').change(function () {

    updateContent();
});

$('#MOEAD').change(function () {

    updateContent();

    $('#MOEADAlgorithm').show('200');
    $('#numberPermutation').show('200');
});

$('#templateName').change(function(){

    $.ajax({
        type: "POST",
        url: '/get-template-size',
        data: {
            templateName: $(this).val()
        },
        contentType: 'application/json',
        success: function(size){
            $('#numberPermutation input').attr('max', size);
            $('#templateSize').val(size);
        }
    });
});

$('#createSolution').click(function (e) {
    e.preventDefault();
    e.stopPropagation();

    var model_data = $("#create-solution-form").serializeObject();
    var circleID = 'solution-generation-' + model_data.solutionFile.replace('.', '-').replace(' ', '') + "-circle";

    if($("div[id^='solution-generation']").length >= LIMIT_PROCESS){
        modal_error_content.text("You cannot run more generation currently. Please wait a moment while one of solution is generated.");
        modal_error.modal('show');
        return;
    }

    if($("#"+circleID).length > 0){
        modal_error_content.text("A solution with same name is already running. Please select another name or wait the end of the generation to replace it later.");
        modal_error.modal('show');
        return;
    }

    if(!checkInputFilled()){
        modal_error_content.text("Please filled all required input.");
        modal_error.modal('show');
        return;
    }

    init();

    $.ajax({
        type: "POST",
        url: '/create-solution',
        contentType: 'application/json',
        data: JSON.stringify(model_data)
    });

    return true;
});

function init(){
    $("#createSolution").attr('disabled', true).hide();
    $("#criteriaChoice input, #algorithmChoice input").prop("checked", false);
    $("#algorithmChoice input").attr("disabled", true);
    $('#content').hide();
    $('#solutionFile').val('');
}

function showOrHideCriteria(nbElem) {

    $('button[id^="createSolution"]').attr('disabled', true);

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

    $('#content').hide();
    $('#EAAlgorithm').hide();
    $('#HillClimber').hide();
    $('#EAAlgorithm').hide();
    $('#MOEADAlgorithm').hide();
    $('#numberPermutation').hide();
    $('button[id^="createSolution"]').attr('disabled', false);
    $('#content').show('200');
    $('#createSolution').show('200');
}

function checkInputFilled(){

    var check = true;
    $('input[type="text"]:visible, input[type="number"]:visible').each(function(){
       if($(this).val().length <= 0){
           check = false;
       }
    });
    return check;
}