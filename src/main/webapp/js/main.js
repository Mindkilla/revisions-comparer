var checker = angular.module('checker', []);
checker.controller('main-controller', function ($scope, $http, $sce) {

    $scope.submitForm = function () {

        document.getElementById("submit").disabled = true;
        document.getElementById("branchName").disabled = true;
        document.getElementById("oldRevision").disabled = true;
        document.getElementById("newRevision").disabled = true;
        document.getElementById("result").disabled = true;
        $scope.resultText = "ВЫПОЛНЯЕТСЯ";

        $http.get('/CheckerMainServlet',
            { params: { branchName: $scope.branchName, oldRevision: $scope.oldRevision, newRevision: $scope.newRevision}})
            .then(function (result) {
                document.getElementById("result").disabled = false;
                $scope.resultText = result.data;
                document.getElementById("submit").disabled = false;
                document.getElementById("branchName").disabled = false;
                document.getElementById("oldRevision").disabled = false;
                document.getElementById("newRevision").disabled = false;
            }, function (error) {
                document.getElementById("result").disabled = false;
                $scope.resultText = "Ошибка выполнения";
                document.getElementById("submit").disabled = false;
                document.getElementById("branchName").disabled = false;
                document.getElementById("oldRevision").disabled = false;
                document.getElementById("newRevision").disabled = false;
                });
    };
});