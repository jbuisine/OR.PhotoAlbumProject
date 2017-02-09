/**
 * Created by jbuisine on 09/02/17.
 */

$(document).ready(function () {
    $('#algorithmChoice input').attr('disabled', true);
    $('#content').hide();
    $('button[id^="createSolution"]').hide();
});

$('#criteriaChoice input').change(function(){

    $('#content').hide();
    var nbChecked = $('#criteriaChoice input:checked').length;
    showOrHideCriteria(nbChecked);
});

$('#HC').change(function () {

    updateContent();

    $('#createSolutionHC').show();

    $('#numberPermutation').show();
});

$('#EA').change(function () {

    updateContent();

    $('#createSolutionEA').show();

    $('#HillClimber').show();
    $('#EAAlgorithm').show();
    $('#numberPermutation').show();
});

$('#ILS').change(function () {

    updateContent();

    $('#createSolutionILS').show();

    $('#HillClimber').show();
    $('#numberPermutation').show();
});

$('#PLS').change(function () {

    updateContent();

    $('#createSolutionPLS').show();
});

$('#MOEAD').change(function () {

    updateContent();

    $('#createSolutionMOEAD').show();

    $('#MOEADAlgorithm').show();
    $('#numberPermutation').show();
});

$('#createSolutionHC').click(function (e) {
    e.preventDefault();

    /*
    var criterias = new Array($('#criteriaChoice input:checked').length);

    $('#criteriaChoice input:checked').each(function(key){
       criterias[key] = $(this).val();
    });

    console.log(criterias);*/

    $.ajax({
        type: "POST",
        url: '/solution-HC',
        data: {
            solutionFile: $("#solutionFile").val(),
            albumType: $("#albumType").val(),
            criteria: $('#criteriaChoice input:checked').val(),
            iterationAlgo: $("#iterationAlgorithm").val(),
            permutation: $("#numberPermutation").val()
        },
        success: function (data) {
            alert(data);
        }
    });
});

$('#createSolutionILS').click(function (e) {
    e.preventDefault();
    alert('ILS');
});

$('#createSolutionEA').click(function (e) {
    e.preventDefault();
    alert('EA');
});

$('#createSolutionPLS').click(function (e) {
    e.preventDefault();
    alert('PLS');
});

$('#createSolutionMOEAD').click(function (e) {
    e.preventDefault();
    alert('MOEAD');
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
    $('button[id^="createSolution"]').hide();
}