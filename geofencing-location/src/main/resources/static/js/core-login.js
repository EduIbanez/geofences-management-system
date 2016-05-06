var Controller = angular.module('LoginController', []);

function mainController($scope, $http) {
    $scope.formData = {};

    $scope.loginUser = function() {
        var formData = new FormData();
        formData.append("email", $scope.formData.email);
        formData.append("password", $scope.formData.password);
        $http.post('http://localhost:8080/api/users/authenticate', formData)
            .success(function(data) {
                $scope.formData = {};
                $scope.user = data.message;
                console.log(data);
                $window.location.href = '/home.html';
            })
            .error(function(data) {
                console.log('Error: ' + data);
            });
        };
}