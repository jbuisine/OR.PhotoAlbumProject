/**
 * Created by jbuisine on 09/02/17.
 */

//Get all files of current album type
$( "#albumType" ).change(function() {

    $.ajax({
        type: "POST",
        url: '/load-solutions',
        data: {
            albumType: $("#albumType").val()
        },
        success: loadSelectSol
    });
});

function loadSelectSol(albumsType) {
    $( "#solutionFile" ).empty();

    $.each(albumsType, function( key, albumType ) {
        $("#solutionFile").append('<option value="' + albumType + '">' + albumType + '</option>');
    });
}