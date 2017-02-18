/**
 * Created by jbuisine on 09/02/17.
 */

//Get all files of current template type
$( "#templateType" ).change(function() {

    $.ajax({
        type: "POST",
        url: '/load-solutions',
        data: {
            templateType: $("#templateType").val()
        },
        success: loadSelectSol
    });
});

function loadSelectSol(templatesType) {
    $( "#solutionFile" ).empty();

    $.each(templatesType, function( key, templateType ) {
        $("#solutionFile").append('<option value="' + templateType + '">' + templateType + '</option>');
    });
}