Dropzone.autoDiscover = false;


var formTemplateName = $('#form-templateName');
var buttonValidate   = $('.template-name-validate');
var buttonShow   = $('.template-show');

$(document).ready(function() {

    var errorColor = "#e0552f";
    var acceptColor = "#67b276";

    //Define form file upload to init dropzone
    var formFileUpload   = $('#fileUpload');

    //Initialize drop zone
    formFileUpload.dropzone({
        acceptedFiles: ".jpg",
        paramName: "photo",
        accept: function(file, done) {
            if (formTemplateName.find("input").val() < 6) {
                done('No template name defined');
            }else{
                done();
            }
        },
        init: function() {
            this.on("success", function(file) {
                buttonShow.css('visibility', 'visible');
            });
        },
        dictDefaultMessage: "Drop your image file here"
    });

    //Only put class selector later with BEM methodology
    var inputUploadFile  = $('.dz-hidden-input');
    var iconStatus       = $('#inputIconStatus');
    var navTemplateName  = $('nav li.template-name');

    //Set property disabled by default for button and dropzone
    inputUploadFile.prop('disabled',true);
    buttonValidate.prop('disabled', true);

    formTemplateName.find('input').on('change paste keyup', function (){
        var name = $(this).val();
        formTemplateName.find('.form-group').first().removeClass("has-warning");
        iconStatus.removeClass("glyphicon-warning-sign");

        var checkExists = false;

        navTemplateName.each(function(){
            if(name.trim() == $(this).text().trim()){
                formTemplateName.find('.form-group').first().removeClass("has-success").addClass("has-error");
                buttonValidate.prop('disabled', true);
                inputUploadFile.prop('disabled',true);
                iconStatus.removeClass("glyphicon-ok").addClass("glyphicon-remove");
                checkExists = true;
                return;
            }
        });

        if(checkExists)
            return;

        if(name.length >= 6){
            formFileUpload.find("input[type='hidden']").prop("value", name);
            buttonValidate.prop('disabled', false);
            formTemplateName.find('.form-group').first().removeClass("has-error").addClass("has-success");
            iconStatus.removeClass("glyphicon-remove").addClass("glyphicon-ok");
        }else{
            buttonValidate.prop('disabled', true);
            formTemplateName.find('.form-group').first().removeClass("has-success").addClass("has-error");
            iconStatus.removeClass("glyphicon-ok").addClass("glyphicon-remove");
        }
    });

    buttonValidate.click(activateUpload);

    //Function which used for activate the uploading files
    function activateUpload() {
        console.log('inside');
        formFileUpload.css('border-color', acceptColor);
        inputUploadFile.prop("disabled", false);
        formTemplateName.find('input').prop('disabled', true);
        $(this).prop('disabled', true);
    }

    //Redirect to template created
    buttonShow.click(function (e) {
        e.stopPropagation();
        window.location.href = "/templates/"+formTemplateName.find('input').val();
    });


    //Throw build for generate template required json file
    window.unload = function () {
        var modal_id = "#modal-template-information";

        $(modal_id).modal();
        console.log("TEST");
        setTimeout(function () {
            $.ajax({
                method: "POST",
                url: "/generate-template-file",
                data: {templateName: formTemplateName.find('input').val()}
            });
        }, 5000);
    };
});