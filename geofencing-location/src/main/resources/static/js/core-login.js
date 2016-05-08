var Controller = angular.module('LoginController', []);

function mainController($scope, $http, $window) {
    $scope.formData = {};

    $scope.loginUser = function () {
        $http.post('http://localhost:8080/api/users/authenticate', $scope.formData)
            .success(function (data) {
                $scope.formData = {};
                $scope.user = data.message;
                console.log(data);
                $window.location.href = '/home';
            })
            .error(function (data) {
                console.log('Error: ' + data);
            });
    };
}