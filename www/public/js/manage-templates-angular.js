/**
 * Created by jbuisine on 07/04/17.
 */

var app = angular.module('photoAlbum.manage', ['photoAlbum']);

app.service('ManageServiceURL', ['serverURL', function(serverURL) {
    return {
        CREATE_URL                : serverURL + "templates/create/",
        GENERATE_URL              : serverURL + "templates/generate-file/",
        REMOVE_TEMPLATE_URL       : serverURL + "templates/remove/",
        DISPLAY_URL               : serverURL + "templates/images-info/",
        REMOVE_PHOTO_URL          : serverURL + "templates/remove-image/",
        GET_NUMBER_PHOTO          : serverURL + "templates/number-photo/",
        CREATE_DISPOSITION        : serverURL + "templates/create-disposition/"
    }
}]);

app.factory('ManageTemplateService', ['ManageServiceURL' , '$http', function (manageURL, $http) {

    var manageService = this;

    manageService.createTemplate = function (name) {
        return $http.post(manageURL.CREATE_URL, {templateName: name}).then(function(res){
            return res.data;
        });
    };

    manageService.generateTemplate = function (name) {
        return $http.post(manageURL.GENERATE_URL, {templateName: name}).then(function(res){
            return res.data;
        });
    };

    manageService.removeTemplate = function (name) {
        return $http.delete(manageURL.REMOVE_TEMPLATE_URL + name).then(function(res){
            return res.data;
        });
    };

    manageService.displayTemplate = function (name) {
        return $http.get(manageURL.DISPLAY_URL + name).then(function(res){
            return res.data;
        });
    };

    manageService.removePhotoTemplate = function (name, photo) {
        return $http.delete(manageURL.REMOVE_PHOTO_URL + name + "/" + photo).then(function(res){
            return res.data;
        });
    };

    manageService.getNbPhotoTemplate = function (name) {
        return $http.get(manageURL.GET_NUMBER_PHOTO + name).then(function(res){
            return res.data;
        });
    };

    manageService.createDisposition = function (data) {
      return $http.post(manageURL.CREATE_DISPOSITION, data).then(function (res) {
         return res.data;
      });
    };

    return manageService;
}]);

/**
 * Factory used for getting current template selected
 */
app.factory('ManageTemplateInfo', function () {
   
    manageTempInfo = this;

    var templateSelected = undefined;
    
    manageTempInfo.setSelectedTemplate = function (val) {
        templateSelected = val;
    };

    manageTempInfo.getSelectedTemplate = function() {
        return templateSelected;
    };

    return manageTempInfo;
});

app.controller('MainController', ['$scope', '$timeout', '$route', '$location', 'ManageTemplateService', 'ManageTemplateInfo', function ($scope, $timeout, $route, $location, manageService, manageTemplateInfo) {

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
            if (Notification.permission !== 'granted') {
                Notification.requestPermission();
            }

            new Notification(manageCtrl.selectedTemplate + " generation file", {
                body: "Your template may be unavailable for a moment. You will be notify when it's finished.",
                icon: "img/generation-finished.png"
            });

            manageService.generateTemplate(manageCtrl.selectedTemplate);
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

                if (uploadCtrl.selectedTemplate === "no") {
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

app.controller('DispositionController', ['$scope', 'ManageTemplateService', 'ManageTemplateInfo', function ($scope, manageService, manageTemplateInfo) {
    var dispositionCtrl = this;

    dispositionCtrl.template    = manageTemplateInfo.getSelectedTemplate();
    dispositionCtrl.nbPhoto     = 0;
    dispositionCtrl.fileName    = "";
    dispositionCtrl.xElem       = 1;
    dispositionCtrl.yElem       = 1;
    dispositionCtrl.nbPage      = 1;
    dispositionCtrl.formError   = false;


    manageService.getNbPhotoTemplate(dispositionCtrl.template).then(function (data) {
       dispositionCtrl.nbPhoto = data;
       console.log(dispositionCtrl.nbPhoto);
    });

    dispositionCtrl.checkValidation = function () {
        var sum = dispositionCtrl.xElem * dispositionCtrl.yElem * dispositionCtrl.nbPage;

        if(sum > dispositionCtrl.nbPhoto)
            dispositionCtrl.formError = true;
        else
            dispositionCtrl.formError = false;
    };

    dispositionCtrl.createDispositionSubmit = function () {
        var data = {
            templateName: dispositionCtrl.template,
            fileName    : dispositionCtrl.fileName,
            xElem       : dispositionCtrl.xElem,
            yElem       : dispositionCtrl.yElem,
            nbPage      : dispositionCtrl.nbPage
        };

        manageService.createDisposition(data).then(function (data) {
            if (Notification.permission !== 'granted') {
                Notification.requestPermission();
            }

            new Notification(dispositionCtrl.template+ " disposition generated", {
                body: "Your generation of new disposition is finished. You can now use it !",
                icon: "img/template-file-finished.png"
            });

           if(data === "success")
               document.location.href = "/templates/manage";
        });
    };
}]);

app.config(function($routeProvider) {

    $routeProvider
        .when("/", {
            templateUrl : "/templates/display/home.html",
            activeLiNav : "home"
        })
        .when("/upload", {
            templateUrl : "/templates/display/upload.html",
            activeLiNav : "upload",
            controller: "UploadController as uploadCtrl"
        })
        .when("/display", {
            templateUrl : "/templates/display/display.html",
            activeLiNav : "display"
        })
        .when("/disposition", {
            templateUrl : "/templates/display/disposition.html",
            activeLiNav : "disposition",
            controller  : "DispositionController as dispositionCtrl"
        })
        .otherwise({ redirectTo: '/' });

    /*$locationProvider.html5Mode({
        enabled: true,
        requireBase: false
    });*/
});