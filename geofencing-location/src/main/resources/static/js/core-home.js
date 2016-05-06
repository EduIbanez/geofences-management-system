var Controller = angular.module('HomeController', []);

function mainController($scope, $http) {
    $scope.formData = {};

    $http.get('http://localhost:8080/api/notifications')
        .success(function(data) {
            $scope.notifications = data.message;
            console.log(data);
        })
        .error(function(data) {
            console.log('Error: ' + data);
        });
}