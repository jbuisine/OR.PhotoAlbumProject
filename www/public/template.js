/**
 * Created by jbuisine on 09/02/17.
 */

//Get all files of current template type
$( "#albumType" ).change(function() {

    console.log($("#templateName").val());
    $.ajax({
        type: "POST",
        url: '/load-solutions',
        data: {
            albumType: $("#albumType").val(),
            templateName: $("#templateName").val()
        },
        success: loadSelectSol
    });
});

function loadSelectSol(solutions) {
    $( "#solutionFile" ).empty();

    if(solutions.length > 0){
        $.each(solutions, function( key, sol ) {
            $("#solutionFile").append('<option value="' + sol + '">' + sol + '</option>');
        });
    }
    else{
        $("#solutionFile").append('<option> No solution found</option>');
    }
}