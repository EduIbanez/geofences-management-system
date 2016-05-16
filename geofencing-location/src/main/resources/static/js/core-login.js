var Controller = angular.module('LoginController', []);

function mainController($scope, $http, $window) {
    $scope.formData = {};

    $scope.loginUser = function () {
        $http({
                  method: 'POST',
                  url: 'http://localhost:8080/login',
                  headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj) {
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      }
                      return str.join("&");
                  },
                  data: {username: $scope.formData.email, password: $scope.formData.password}

              })
            .success(function (data) {
                $scope.formData = {};
                $window.location.href = '/home';
            })
            .error(function (data) {
                console.log('Error: ' + data);
            });
    };
}