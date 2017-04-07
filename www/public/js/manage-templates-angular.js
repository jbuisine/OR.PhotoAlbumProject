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

app.controller('MainController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

    var manageCtrl              = this;

    manageCtrl.photoTemplate    = [];
    manageCtrl.templateName     = "";
    manageCtrl.selectedTemplate = "no";

    /**
     * Method which load books available into database
     */
    /*getDataApi.getBooks().then(function(data) {
        $timeout(function(){
            manageCtrl.books = data;
            manageCtrl.dataLoad = true;
        },2000);
    });*/

    manageCtrl.loadPhoto = function () {
        manageService.displayTemplate(manageCtrl.selectedTemplate).then(function (data) {
            console.log(data);
        });
    };
}]);

app.controller('HomeController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

}]);

app.controller('UploadController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

}]);

app.controller('DisplayController', ['$scope', '$timeout', '$http', 'ManageTemplate', function ($scope, $timeout, $http, manageService, address) {

}]);

app.config(function($routeProvider) {

    $routeProvider
        .when("/", {
            templateUrl : "./../../views/pages/manage-templates-views/home.html"
        })
        .when("/upload", {
            controller  : 'UploadController',
            templateUrl : "./www/pages/manage-templates-views/upload.htm"
        })
        .when("/display", {
            controller  : 'DisplayController',
            templateUrl : "./www/pages/manage-templates-views/display.htm"
        })
        .otherwise({ redirectTo: '/' });
});