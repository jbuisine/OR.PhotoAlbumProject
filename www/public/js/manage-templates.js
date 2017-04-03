Dropzone.autoDiscover       = false;


var formTemplateName        = $('#form-templateName');
var buttonValidate          = $('.template-name-validate');
var buttonBuild             = $('.build-template');
var buttonManage            = $('.manage-template');
var buttonRemove            = $('.remove-template');
var selectedTemplate        = $('select[name="templateSelected"]');
var manageContainer         = $('.manage-template-container');
var uploadedImagesContainer = $('.template-images-uploaded');

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
                buttonBuild.css('visibility', 'visible');
            });
        },
        dictDefaultMessage: "Drop your image file here"
    });

    //Only put class selector later with BEM methodology
    var inputUploadFile  = $('.dz-hidden-input');
    var iconStatus       = $('#inputIconStatus');
    var navTemplateName  = $('nav li.template-name');

    //Set property disabled by default for button
    buttonValidate.prop('disabled', true);
    buttonBuild.prop('disabled', true);
    buttonManage.prop('disabled', true);
    buttonRemove.prop('disabled', true);

    formTemplateName.find('input').on('change paste keyup', function (){
        var name = $(this).val();
        formTemplateName.find('.form-group').first().removeClass("has-warning");
        iconStatus.removeClass("glyphicon-warning-sign");

        var checkExists = false;

        navTemplateName.each(function(){
            if(name.trim() == $(this).text().trim()){
                formTemplateName.find('.form-group').first().removeClass("has-success").addClass("has-error");
                buttonValidate.prop('disabled', true);
                iconStatus.removeClass("glyphicon-ok").addClass("glyphicon-remove");
                checkExists = true;
                return;
            }
        });

        if(checkExists)
            return;

        if(name.length >= 6){
            buttonValidate.prop('disabled', false);
            formTemplateName.find('.form-group').first().removeClass("has-error").addClass("has-success");
            iconStatus.removeClass("glyphicon-remove").addClass("glyphicon-ok");
        }else{
            buttonValidate.prop('disabled', true);
            formTemplateName.find('.form-group').first().removeClass("has-success").addClass("has-error");
            iconStatus.removeClass("glyphicon-ok").addClass("glyphicon-remove");
        }
    });

    //Hide upload component by default
    buttonValidate.click(createTemplate);

    //Gridly initialisation
    $('.gridly').gridly({
        base: 60, // px
        gutter: 20, // px
        columns: 12
    });

    //Build template json info file
    buttonBuild.click(function (e) {

        var templateName = selectedTemplate.val();

        //Ensure that template name exists
        if(templateName != "no"){
            //Send request
            $.ajax({
                url: "/generate-template-file",
                method: "POST",
                data: {templateName: templateName}
            });

            if(Notification.permission !== 'granted'){
                Notification.requestPermission();
            }

            n = new Notification(templateName + " generation file", {
                body: "You template may be unavailable for a moment. You will be notify when it's finished.",
                icon : "/img/template-file-finished.png"
            });
        }
    });

    selectedTemplate.change(function(){
       if($(this).val() == "no"){
           buttonBuild.prop('disabled', true);
           buttonManage.prop('disabled', true);
           buttonRemove.prop('disabled', true);
       } else {
           buttonBuild.prop('disabled', false);
           buttonManage.prop('disabled', false);
           buttonRemove.prop('disabled', false);
       }
    });

    buttonManage.click(function (e) {
        e.preventDefault();

        var templateName = selectedTemplate.val();

        //Remove all icons upload and load images already added
        $.ajax({
            url: "template-images-info",
            method: "POST",
            data: {templateName: templateName},
            success: function (data) {
                console.log(data);

                manageContainer.show();
                uploadedImagesContainer.empty();
                $.each(data, function(img){
                   uploadedImagesContainer.append('<div class="brick small"><img src="/'+templateName+'/img/rIMG_1978.jpg" alt="Image ' + templateName + '"/></div>');
                });
            }
        });
    });
});

//Function which used for activate the uploading files
function activateUpload() {
    navTemplateName.parent().append('<li><a href="/templates/'+formTemplateName.find('input').val()+'"></a></li>')
    formFileUpload.css('border-color', acceptColor);
    inputUploadFile.prop("disabled", false);
    formTemplateName.find('input').prop('disabled', true);
    $(this).prop('disabled', true);
}

function createTemplate() {
    var templateName = selectedTemplate.val();
    console.log(templateName);
    $.ajax({
        url: "/create-template",
        method: "POST",
        data: {
            templateName: templateName
        },
        success:function () {
            location.reload();
        }
    });
}