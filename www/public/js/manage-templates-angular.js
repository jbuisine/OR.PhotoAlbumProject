/**
 * Created by jbuisine on 07/04/17.
 */

var app = angular.module('photoAlbum.manage', ['photoAlbum']);

app.service('ManageServiceURL', ['serverURL', function(serverURL) {
    return {
        CREATE_URL  : serverURL + "create-template/",
        GENERATE_URL: serverURL + "generate-template-file/",
        REMOVE_URL  : serverURL + "template-remove/",
        DISPLAY_URL : serverURL + "template-images-info/"
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
        return $http.delete(manageURL.REMOVE_URL + name).then(function(res){
            return res.data;
        });
    };

    manageTemplate.displayTemplate = function (name) {
        console.log(manageURL.DISPLAY_URL + name);
        return $http.get(manageURL.DISPLAY_URL + name).then(function(res){
            return res.data;
        });
    };

    return manageTemplate;
}]);

app.controller('MainController', ['$scope', '$timeout', '$http', '$location', 'ManageTemplate', function ($scope, $timeout, $http, $location, manageService) {

    var manageCtrl              = this;

    manageCtrl.templateName     = "";
    manageCtrl.selectedTemplate = "no";
    manageCtrl.photoTemplate    = [];
    manageCtrl.photoLoaded      = false;

    manageCtrl.init = function () {
        //Redirect to home if page is reload
        if(manageCtrl.selectedTemplate === "no"){
            $location.path("/");
        }
    };

    manageCtrl.init();

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
                var gridDiv = $('.gridly');
                gridDiv.gridly('layout');
                gridDiv.gridly('layout');
            }, 1000);
        });
    };

    manageCtrl.removeTemplate = function () {
        manageService.removeTemplate(manageCtrl.selectedTemplate).then(function () {
            location.reload();
        });
    }
}]);

app.controller('HomeController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

}]);

app.controller('UploadController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

}]);

app.config(function($routeProvider) {

    $routeProvider
        .when("/", {
            templateUrl : "/manage-display/home.html"
        })
        .when("/upload", {
            templateUrl : "/manage-display/upload.html"
        })
        .when("/display", {
            templateUrl : "/manage-display/display.html"
        })
        .otherwise({ redirectTo: '/' });
});