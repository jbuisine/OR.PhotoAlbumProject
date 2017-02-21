/**
 * Created by jbuisine on 09/02/17.
 */

//Get all files of current template type
$( "#albumType" ).change(function() {

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
        $("#solutionFile").attr();
        $.each(solutions, function( key, sol ) {
            $("#solutionFile").append('<option value="' + sol + '">' + sol + '</option>');
        });
    }
    else{
        $("#solutionFile").append('<option> No solution found</option>');
    }
}

$("#solutionFile").change(function () {
    $.ajax({
        type: "POST",
        url: '/load-solution-content',
        data: {
            templateName: $("#templateName").val(),
            albumType: $("#albumType").val(),
            solutionFile: $(this).val()
        },
        success: loadContentSol
    });
});

function loadContentSol(data) {
    console.log(data);
}