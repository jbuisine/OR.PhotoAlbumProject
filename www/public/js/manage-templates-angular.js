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

app.service('ManageTemplate', ['ManageServiceURL' , '$http', function (manageURL, $http) {

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

app.controller('MainController', ['$scope', '$timeout', '$route', '$location', 'ManageTemplate', function ($scope, $timeout, $route, $location, manageService) {

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
        $location.path(pathView);
    };

    manageCtrl.addTemplate = function () {
        manageService.createTemplate(manageCtrl.templateName).then(function () {
           location.reload();
        });
    };

    manageCtrl.loadPhoto = function () {
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

app.config(function($routeProvider) {

    $routeProvider
        .when("/", {
            templateUrl : "/manage-display/home.html",
            activeLiNav : "home"
        })
        .when("/upload", {
            templateUrl : "/manage-display/upload.html",
            activeLiNav : "upload"
        })
        .when("/display", {
            templateUrl : "/manage-display/display.html",
            activeLiNav : "display"
        })
        .otherwise({ redirectTo: '/' });
});