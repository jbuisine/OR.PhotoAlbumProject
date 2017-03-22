Dropzone.autoDiscover = false;

$(document).ready(function() {

    var errorColor = "#e0552f";
    var acceptColor = "#67b276";

    $(".dz-hidden-input").prop("disabled",true);

    $('#templateName').on('change paste keyup', function (){
        var name = $(this).val();
        $("#form-templateName .form-group").removeClass("has-warning");
        $('#inputIconStatus').removeClass("glyphicon-warning-sign");

        //Set disable by default
        $(".dz-hidden-input").prop("disabled",true);
        $('nav li.template-name').each(function(){
            if(name == $(this).text()){
                $('#fileUpload').css('border-color', errorColor);
                $("#form-templateName.form-group").removeClass("has-success").addClass("has-success");
                return;
            }
        });

        if(name.length >= 6){
            $('#fileUpload').css('border-color', acceptColor);
            $(".dz-hidden-input").prop("disabled", false);
            $("#form-templateName .form-group").removeClass("has-error").addClass("has-success");
            $('#inputIconStatus').removeClass("glyphicon-remove").addClass("glyphicon-ok");
            $("#fileUpload input[type='hidden']").prop("value", name);
        }else{
            $('#fileUpload').css('border-color', errorColor);
            $("#form-templateName .form-group").removeClass("has-success").addClass("has-error");
            $('#inputIconStatus').removeClass("glyphicon-ok").addClass("glyphicon-remove");
        }
    });

    $("form#fileUpload").dropzone({
        //previewTemplate: document.querySelector('#preview-template').innerHTML,
        acceptedFiles: ".jpg",
        init: function() {
            this.on("success", function(file) {
                console.log("success done");
            });
            this.on("addedfile", function(file) {
                console.log("added");
            });
        },
        dictDefaultMessage: "Drop your image file here"
    });
});