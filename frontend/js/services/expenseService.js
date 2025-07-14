(function() {
    'use strict';

    angular.module('expenseTrackerApp')
        .service('ExpenseService', ExpenseService);

    ExpenseService.$inject = ['$http', 'API_BASE_URL'];

    function ExpenseService($http, API_BASE_URL) {
        var service = {
            getAllExpenses: getAllExpenses,
            getExpenseById: getExpenseById,
            createExpense: createExpense,
            updateExpense: updateExpense,
            deleteExpense: deleteExpense,
            getExpensesByDateRange: getExpensesByDateRange,
            getExpensesByMonth: getExpensesByMonth,
            getRecentExpenses: getRecentExpenses,
            getSortedExpenses: getSortedExpenses,
            getExpenseSummary: getExpenseSummary,
            getExpensesByCategory: getExpensesByCategory,
            getMonthlyTrend: getMonthlyTrend
        };

        return service;

        function handleError(error) {
            console.error('API Error:', error);
            return Promise.reject(error);
        }

        function getAllExpenses() {
            return $http.get(API_BASE_URL + '/expenses')
                .catch(handleError);
        }

        function getExpenseById(id) {
            return $http.get(API_BASE_URL + '/expenses/' + id)
                .catch(handleError);
        }

        function createExpense(expense) {
           
            var formattedExpense = angular.copy(expense);
            if (formattedExpense.date) {
                formattedExpense.date = new Date(formattedExpense.date).toISOString();
            }
            return $http.post(API_BASE_URL + '/expenses', formattedExpense)
                .catch(handleError);
        }

        function updateExpense(id, expense) {
            
            var formattedExpense = angular.copy(expense);
            if (formattedExpense.date) {
                formattedExpense.date = new Date(formattedExpense.date).toISOString();
            }
            return $http.put(API_BASE_URL + '/expenses/' + id, formattedExpense)
                .catch(handleError);
        }

        function deleteExpense(id) {
            return $http.delete(API_BASE_URL + '/expenses/' + id)
                .catch(handleError);
        }

        function getExpensesByDateRange(startDate, endDate) {
            return $http.get(API_BASE_URL + '/expenses/date-range', {
                params: {
                    startDate: startDate,
                    endDate: endDate
                }
            }).catch(handleError);
        }

        function getExpensesByMonth(year, month) {
            return $http.get(API_BASE_URL + '/expenses/month/' + year + '/' + month)
                .catch(handleError);
        }

        function getRecentExpenses(limit) {
            return $http.get(API_BASE_URL + '/expenses/recent', {
                params: {
                    limit: limit || 50
                }
            }).catch(handleError);
        }

        function getSortedExpenses(sortBy, ascending) {
            return $http.get(API_BASE_URL + '/expenses/sorted', {
                params: {
                    sortBy: sortBy || 'date',
                    ascending: ascending !== false
                }
            }).catch(handleError);
        }

        function getExpenseSummary() {
            return $http.get(API_BASE_URL + '/expenses/summary')
                .catch(handleError);
        }

        function getExpensesByCategory() {
            return $http.get(API_BASE_URL + '/expenses/by-category')
                .catch(handleError);
        }

        function getMonthlyTrend(year) {
            return $http.get(API_BASE_URL + '/expenses/trend/' + year)
                .catch(handleError);
        }
    }
})();
