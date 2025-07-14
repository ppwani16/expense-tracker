(function() {
    'use strict';

    angular.module('expenseTrackerApp', [])
        .config(['$httpProvider', function($httpProvider) {
            $httpProvider.defaults.headers.common['Content-Type'] = 'application/json';
        }])
        .constant('API_BASE_URL', 'http://localhost:8080/api');
})();
