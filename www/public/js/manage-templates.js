Dropzone.autoDiscover       = false;

var formTemplateName        = $('#form-templateName');
var buttonValidate          = $('.template-name-validate');
var buttonBuild             = $('.build-template');
var buttonRemove            = $('.remove-template');

var navTemplatePage         = $('.nav-template-management');

var homeLink                = navTemplatePage.find('li[data-page="home"]');
var manageLink              = navTemplatePage.find('li[data-page="manage"]');
var uploadLink              = navTemplatePage.find('li[data-page="add"]');
var selectedTemplate        = $('select[name="templateSelected"]');

/* Different container */
var homeContainer           = $('.home-description-container');
var uploadContainer         = $('.images-uploaded-container');
var manageContainer         = $('.manage-template-container');


uploadContainer.hide();
manageContainer.hide();

$(document).ready(function() {

    //Define form file upload to init dropzone
    var formFileUpload   = $('#fileUpload');

    //Initialize drop zone
    formFileUpload.dropzone({
        acceptedFiles: ".jpg",
        paramName: "photo",
        accept: function(file, done) {
            console.log(selectedTemplate.val())
            if (selectedTemplate.val() == "no") {
                done('No template name defined');
            }else{
                done();
            }
        },
        init: function() {
            this.on("success", function(file) {
                buttonBuild.css('visibility', 'visible');
            });
            this.on("sending", function(file, xhr, data) {
                data.append("templateName", selectedTemplate.val());
            });
        },

        dictDefaultMessage: "Drop your image file here"
    });

    //Only put class selector later with BEM methodology
    var iconStatus       = $('#inputIconStatus');
    var navTemplateName  = $('nav li.template-name');

    //Set property disabled by default for buttons & links
    buttonValidate.prop('disabled', true);
    buttonBuild.prop('disabled', true);
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
           buttonRemove.prop('disabled', true);
           initNavTemplate();
       } else {
           buttonBuild.prop('disabled', false);
           buttonRemove.prop('disabled', false);
           uploadLink.removeClass('disabled');
           manageLink.removeClass('disabled');
           displayPhoto($(this));
       }
    });

    navTemplatePage.find('li[data-page="manage"]').click(function (e) {
        e.preventDefault();

        displayPhoto($(this));
    });

    navTemplatePage.find('li').click(function (e) {
        e.preventDefault();

        if($(this).hasClass('disabled'))
            return false;

        var linkData = $(this).attr('data-page');

        //By default hide all pages
        homeContainer.hide();
        uploadContainer.hide();
        manageContainer.hide();

        navTemplatePage.find('li').removeClass('active');
        $(this).addClass('active');

        switch(linkData) {
            case "home":
                homeContainer.show('500');
                break;
            case "add":
                uploadContainer.show('500');
                break;
            case "manage":
                manageContainer.show('500');
                break;
        }
    });

    buttonRemove.click(function (e) {
        var templateName = selectedTemplate.val();

        $.ajax({
            url: "/template-remove",
            method: "POST",
            data: {
                templateName: templateName
            },
            success: function () {
                location.reload();
            }
        })
    });
});

function createTemplate() {
    var templateName = formTemplateName.find('input').val();

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

function initNavTemplate() {
    navTemplatePage.find('li').removeClass('active');
    uploadLink.addClass('disabled');
    manageLink.addClass('disabled');
    homeLink.addClass('active');
    uploadContainer.hide();
    manageContainer.hide();
    homeContainer.show('1000');
}

function displayPhoto(elem) {
    if(elem.hasClass('disabled'))
        return false;
    var templateName = selectedTemplate.val();

    //Remove all icons upload and load images already added
    $.ajax({
        url: "template-images-info",
        method: "POST",
        data: {templateName: templateName},
        success: function (data) {

            homeContainer.hide();
            uploadContainer.hide();
            manageContainer.empty();
            manageContainer.show();

            navTemplatePage.find('li').removeClass('active');
            navTemplatePage.find('li[data-page="manage"]').addClass('active');

            if(data.length === 0){
                manageContainer.append("<div class='jumbotron'><p>No photo found !</p></div>")
            }
            else{
                $.each(data, function(index, img){

                    //Gridly initialisation
                    $('.gridly').gridly({
                        base: 60, // px
                        gutter: 20, // px
                        columns: 14
                    });

                    manageContainer.append('<div class="brick small">' +
                        '<img src="/'+templateName+'/img/'+img+'" alt="Image ' + templateName + '"/>' +
                        '<a href="#" data-id-image="'+img+'" class="image-delete-btn">' +
                        '<span class="glyphicon glyphicon-trash"></span>' +
                        '</a>' +
                        '<span class="image-info-icon">' + (index+1) +
                        '</span>' +
                        '</div>');
                });

                if(data.length % 2 === 0){
                    buttonBuild.prop('disabled', false);
                    buttonBuild.prop('title', '');
                }else{
                    buttonBuild.prop('disabled', true);
                    buttonBuild.prop('title', 'You don\'t have a correct number of photo to build your template');
                }
                //Set brick well placed
                $('.gridly').gridly('layout');
            }

            //Generate event for delete image to bind with DOM elements added
            $('.image-delete-btn').click(function (e) {
                e.stopPropagation();

                var photoName = $(this).attr('data-id-image');
                var templateName = selectedTemplate.val();
                var brick = $(this).parent();

                $(this).prop('disabled', true);
                $.ajax({
                    url: "/template-remove-image",
                    method: "POST",
                    data:{
                        templateName: templateName,
                        photoName: photoName
                    },
                    success:function(){
                        //Remove selected item and resize
                        brick.remove();
                        $('.gridly').gridly('layout');
                    }
                })

            })
        }
    });
}

