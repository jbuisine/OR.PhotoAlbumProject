/**
 * Created by jbuisine on 07/04/17.
 */

var app = angular.module('photoAlbum.manage', ['photoAlbum']);

app.service('ManageServiceURL', ['serverURL', function(serverURL) {
    return {
        CREATE_URL           : serverURL + "create-template/",
        GENERATE_URL         : serverURL + "generate-template-file/",
        REMOVE_TEMPLATE_URL  : serverURL + "template-remove/",
        DISPLAY_URL          : serverURL + "template-images-info/",
        REMOVE_PHOTO_URL     : serverURL + "template-remove-image/"
    }
}]);

app.service('ManageTemplateApi', ['ManageServiceURL' , '$http', function (manageURL, $http) {

    var manageTemplate = this;

    manageTemplate.createTemplate = function (name) {
        return $http.post(manageURL.CREATE_URL, {templateName: name}).then(function(res){
            return res.data;
        });
    };

    manageTemplate.generateTemplate = function (name) {
        return $http.post(manageURL.GENERATE_URL, {templateName: name}).then(function(res){
            return res.data;
        });
    };

    manageTemplate.removeTemplate = function (name) {
        return $http.delete(manageURL.REMOVE_TEMPLATE_URL + name).then(function(res){
            return res.data;
        });
    };

    manageTemplate.displayTemplate = function (name) {
        return $http.get(manageURL.DISPLAY_URL + name).then(function(res){
            return res.data;
        });
    };

    manageTemplate.removePhotoTemplate = function (name, photo) {
        return $http.delete(manageURL.REMOVE_PHOTO_URL + name + "/" + photo).then(function(res){
            return res.data;
        });
    };

    return manageTemplate;
}]);

app.service('ManageTemplateInfo', function () {
   
    manageTempInfo = this;

    var templateSelected = undefined;
    
    manageTempInfo.setSelectedTemplate = function (val) {
        templateSelected = val;
    };

    manageTempInfo.getSelectedTemplate = function() {
        return templateSelected;
    }

    return manageTempInfo;
});

app.controller('MainController', ['$scope', '$timeout', '$route', '$location', 'ManageTemplateApi', 'ManageTemplateInfo', function ($scope, $timeout, $route, $location, manageService, manageTemplateInfo) {

    var manageCtrl              = this;

    manageCtrl.route            = $route;

    manageCtrl.templateName     = "";
    manageCtrl.selectedTemplate = "no";
    manageCtrl.photoTemplate    = [];
    manageCtrl.photoLoaded      = false;
    manageCtrl.activeLiNav      = "home";

    manageCtrl.init = function () {
        //Redirect to home if page is reload
        if(manageCtrl.selectedTemplate === "no"){
            $location.path("/");
        }
    };

    manageCtrl.init();

    manageCtrl.changeView = function (pathView) {
        if(manageCtrl.selectedTemplate !== 'no')
            $location.path(pathView);
        else
            $location.path('/');
    };

    manageCtrl.addTemplate = function () {
        manageService.createTemplate(manageCtrl.templateName).then(function () {
           location.reload();
        });
    };

    manageCtrl.loadPhoto = function () {

        manageTemplateInfo.setSelectedTemplate(manageCtrl.selectedTemplate);

        if(manageCtrl.selectedTemplate !== 'no')
        {
            manageCtrl.photoLoaded = false;
            manageService.displayTemplate(manageCtrl.selectedTemplate).then(function (data) {
                manageCtrl.photoTemplate = data;
                manageCtrl.photoLoaded = true;

                $location.path('/display');

                $timeout(function () {
                    //Need to set layout two times to have correct display
                    $('.gridly').gridly('layout');
                    $('.gridly').gridly('layout');
                }, 1000);

            });
        }
        else{
            $location.path('/');
        }
    };

    manageCtrl.removeTemplate = function () {
        manageService.removeTemplate(manageCtrl.selectedTemplate).then(function () {
            location.reload();
        });
    };

    manageCtrl.buildTemplate = function () {

        if(manageCtrl.selectedTemplate !== "no") {
            //Send request and notification if request was a success
            manageService.buildTemplate(manageCtrl.selectedTemplate).then(function () {
                if (Notification.permission !== 'granted') {
                    Notification.requestPermission();
                }

                new Notification(manageCtrl.selectedTemplate + " generation file", {
                    body: "You template may be unavailable for a moment. You will be notify when it's finished.",
                    icon: "/img/template-file-finished.png"
                });
            });
        }
    };

    manageCtrl.removePhoto = function (photo) {
        manageService.removePhotoTemplate(manageCtrl.selectedTemplate, photo).then(function () {
            var brick = $('a[data-id-photo="'+photo+'"]').parent();
            brick.remove();
            $('.gridly').gridly('layout');
        });
    };
}]);

app.controller('UploadController', ['$scope', 'ManageTemplateInfo', function ($scope, manageTemplateInfo) {

    var uploadCtrl = this;

    uploadCtrl.selectedTemplate = manageTemplateInfo.getSelectedTemplate();

    uploadCtrl.initUploadForm = function () {

        //Define form file upload to init dropzone
        var formFileUpload   = $('#fileUpload');
        //Initialize drop zone
        formFileUpload.dropzone({
            acceptedFiles: ".jpg",
            paramName: "photo",
            accept: function(file, done) {

                if (uploadCtrl.selectedTemplate == "no") {
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
                    data.append("templateName", uploadCtrl.selectedTemplate);
                });
            },

            dictDefaultMessage: "Drop your image file here"
        });
    };

    uploadCtrl.initUploadForm();
}]);

app.config(function($routeProvider) {

    $routeProvider
        .when("/", {
            templateUrl : "/manage-display/home.html",
            activeLiNav : "home"
        })
        .when("/upload", {
            templateUrl : "/manage-display/upload.html",
            activeLiNav : "upload",
            controller: "UploadController"
        })
        .when("/display", {
            templateUrl : "/manage-display/display.html",
            activeLiNav : "display"
        })
        .when("/disposition", {
            templateUrl : "/manage-display/disposition.html",
            activeLiNav : "disposition"
        })
        .otherwise({ redirectTo: '/' });
});