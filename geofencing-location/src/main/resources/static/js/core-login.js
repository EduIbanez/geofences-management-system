var Controller = angular.module('LoginController', []);

function mainController($scope, $http, $window) {
    $scope.formData = {};

    $scope.loginUser = function () {
        var data = 'username=' + $scope.formData.email + '&password=' + $scope.formData.password;
        $http.post('http://localhost:8080/api/users/login', data)
            .success(function (data) {
                $scope.formData = {};
                $window.location.href = '/home';
            })
            .error(function (data) {
                console.log('Error: ' + data);
            });
    };
}