(function() {
    'use strict';

    angular.module('expenseTrackerApp')
        .controller('ExpenseController', ExpenseController);

    ExpenseController.$inject = ['ExpenseService', '$timeout'];

    function ExpenseController(ExpenseService, $timeout) {
        var vm = this;
        
        vm.expenses = [];
        vm.currentExpense = {};
        vm.isEditing = false;
        vm.loading = false;
        vm.filterType = 'all';
        vm.dateRange = {
            start: null,
            end: null
        };
        vm.sortBy = 'date';
        vm.sortAscending = false;
        vm.summary = {
            totalExpenses: 0,
            monthlyExpenses: 0,
            yearlyExpenses: 0,
            expensesByCategory: {},
            highestSpendCategory: 'None',
            lowestSpendCategory: 'None'
        };
        vm.notification = {
            show: false,
            message: '',
            type: 'success'
        };

        
        vm.selectedYear = new Date().getFullYear();
        vm.selectedMonth = new Date().getMonth() + 1;
        vm.availableYears = generateYearOptions();
        vm.availableMonths = [
            {value: 1, label: 'January'}, {value: 2, label: 'February'}, {value: 3, label: 'March'},
            {value: 4, label: 'April'}, {value: 5, label: 'May'}, {value: 6, label: 'June'},
            {value: 7, label: 'July'}, {value: 8, label: 'August'}, {value: 9, label: 'September'},
            {value: 10, label: 'October'}, {value: 11, label: 'November'}, {value: 12, label: 'December'}
        ];

        
        vm.submitExpense = submitExpense;
        vm.editExpense = editExpense;
        vm.deleteExpense = deleteExpense;
        vm.cancelEdit = cancelEdit;
        vm.applyFilter = applyFilter;
        vm.applySorting = applySorting;
        vm.toggleSortOrder = toggleSortOrder;
        vm.handleKeyPress = handleKeyPress;
        vm.updateChartFilters = updateChartFilters;
        vm.updateCategoryChart = updateCategoryChart;
        vm.updateTrendChart = updateTrendChart;

        
        init();

        function init() {
            loadExpenses();
            loadSummary();
            resetForm();
            
            
            $timeout(function() {
                updateCharts();
            }, 500);
        }

        function generateYearOptions() {
            var years = [];
            var currentYear = new Date().getFullYear();
            for (var i = currentYear - 5; i <= currentYear + 1; i++) {
                years.push(i);
            }
            return years;
        }

        function loadExpenses() {
            vm.loading = true;
            
            var loadPromise;
            
            switch (vm.filterType) {
                case 'recent':
                    loadPromise = ExpenseService.getRecentExpenses(50);
                    break;
                case 'month':
                    var now = new Date();
                    loadPromise = ExpenseService.getExpensesByMonth(now.getFullYear(), now.getMonth() + 1);
                    break;
                case 'dateRange':
                    if (vm.dateRange.start && vm.dateRange.end) {
                        loadPromise = ExpenseService.getExpensesByDateRange(
                            vm.dateRange.start + 'T00:00:00',
                            vm.dateRange.end + 'T23:59:59'
                        );
                    } else {
                        loadPromise = ExpenseService.getAllExpenses();
                    }
                    break;
                default:
                    loadPromise = ExpenseService.getAllExpenses();
            }

            loadPromise.then(function(response) {
                vm.expenses = response.data;
                applySorting();
            }).catch(function(error) {
                console.error('Error loading expenses:', error);
                showNotification('Error loading expenses', 'error');
            }).finally(function() {
                vm.loading = false;
            });
        }

        function loadSummary() {
            ExpenseService.getExpenseSummary().then(function(response) {
                vm.summary = response.data;
                updateCharts();
            }).catch(function(error) {
                console.error('Error loading summary:', error);
                showNotification('Error loading summary', 'error');
            });
        }

        function submitExpense() {
            
            if (!vm.currentExpense.description || vm.currentExpense.description.trim() === '') {
                showNotification('Description is required', 'error');
                return;
            }
            
            if (!vm.currentExpense.amount || vm.currentExpense.amount <= 0) {
                showNotification('Amount must be greater than 0', 'error');
                return;
            }
            
            if (!vm.currentExpense.category) {
                showNotification('Category is required', 'error');
                return;
            }
            
            if (!vm.currentExpense.date) {
                showNotification('Date is required', 'error');
                return;
            }

            vm.loading = true;

            var promise;
            if (vm.isEditing) {
                promise = ExpenseService.updateExpense(vm.currentExpense.id, vm.currentExpense);
            } else {
                promise = ExpenseService.createExpense(vm.currentExpense);
            }

            promise.then(function(response) {
                showNotification(vm.isEditing ? 'Expense updated successfully' : 'Expense added successfully', 'success');
                resetForm();
                loadExpenses();
                loadSummary();
            }).catch(function(error) {
                console.error('Error saving expense:', error);
                showNotification('Error saving expense', 'error');
            }).finally(function() {
                vm.loading = false;
            });
        }

        function editExpense(expense) {
            vm.currentExpense = angular.copy(expense);
            vm.currentExpense.date = new Date(expense.date).toISOString().slice(0, 16);
            vm.isEditing = true;
        }

        function deleteExpense(id) {
            if (!confirm('Are you sure you want to delete this expense?')) {
                return;
            }

            vm.loading = true;

            ExpenseService.deleteExpense(id).then(function() {
                showNotification('Expense deleted successfully', 'success');
                loadExpenses();
                loadSummary();
            }).catch(function(error) {
                console.error('Error deleting expense:', error);
                showNotification('Error deleting expense', 'error');
            }).finally(function() {
                vm.loading = false;
            });
        }

        function cancelEdit() {
            resetForm();
        }

        function resetForm() {
            
            var now = new Date();
            var year = now.getFullYear();
            var month = String(now.getMonth() + 1).padStart(2, '0');
            var day = String(now.getDate()).padStart(2, '0');
            var hours = String(now.getHours()).padStart(2, '0');
            var minutes = String(now.getMinutes()).padStart(2, '0');
            
            vm.currentExpense = {
                description: '',
                amount: null,
                category: '',
                date: `${year}-${month}-${day}T${hours}:${minutes}`
            };
            vm.isEditing = false;
        }

        function applyFilter() {
            loadExpenses();
        }

        function applySorting() {
            if (!vm.expenses || vm.expenses.length === 0) {
                return;
            }

            vm.expenses.sort(function(a, b) {
                var valueA, valueB;
                
                switch (vm.sortBy) {
                    case 'amount':
                        valueA = parseFloat(a.amount);
                        valueB = parseFloat(b.amount);
                        break;
                    case 'category':
                        valueA = a.category.toLowerCase();
                        valueB = b.category.toLowerCase();
                        break;
                    case 'date':
                    default:
                        valueA = new Date(a.date);
                        valueB = new Date(b.date);
                        break;
                }

                var comparison = 0;
                if (valueA > valueB) {
                    comparison = 1;
                } else if (valueA < valueB) {
                    comparison = -1;
                }

                return vm.sortAscending ? comparison : -comparison;
            });
        }

        function toggleSortOrder() {
            vm.sortAscending = !vm.sortAscending;
            applySorting();
        }

        function handleKeyPress(event, fieldName) {
            if (event.which === 13) { 
                event.preventDefault();
                
                
                $timeout(function() {
                    if (fieldName === 'description') {
                        vm.currentExpense.description = '';
                    } else if (fieldName === 'amount') {
                        vm.currentExpense.amount = null;
                    } else if (fieldName === 'category') {
                        vm.currentExpense.category = '';
                    } else if (fieldName === 'date') {
                        var now = new Date();
                        var year = now.getFullYear();
                        var month = String(now.getMonth() + 1).padStart(2, '0');
                        var day = String(now.getDate()).padStart(2, '0');
                        var hours = String(now.getHours()).padStart(2, '0');
                        var minutes = String(now.getMinutes()).padStart(2, '0');
                        vm.currentExpense.date = `${year}-${month}-${day}T${hours}:${minutes}`;
                    }
                }, 100);
            }
        }

        function showNotification(message, type) {
            vm.notification.message = message;
            vm.notification.type = type;
            vm.notification.show = true;

            $timeout(function() {
                vm.notification.show = false;
            }, 3000);
        }

        function init() {
    loadExpenses();
    loadSummary();
    resetForm();
    
    
    $timeout(function() {
        try {
            updateCharts();
        } catch (error) {
            console.error('Chart initialization error:', error);
            showNotification('Error initializing charts', 'error');
        }
    }, 1000); 
}

function updateCharts() {
    $timeout(function() {
        updateCategoryChart();
        updateTrendChart();
    }, 100);
}


        function updateChartFilters() {
            updateCategoryChart();
            updateTrendChart();
        }

        function updateCategoryChart() {
            var ctx = document.getElementById('categoryChart');
            if (!ctx) return;

            
            ExpenseService.getExpensesByMonth(vm.selectedYear, vm.selectedMonth)
                .then(function(response) {
                    var expenses = response.data;
                    var categoryData = {};
                    
                    expenses.forEach(function(expense) {
                        if (categoryData[expense.category]) {
                            categoryData[expense.category] += parseFloat(expense.amount);
                        } else {
                            categoryData[expense.category] = parseFloat(expense.amount);
                        }
                    });

                    var categories = Object.keys(categoryData);
                    var amounts = Object.values(categoryData);

                    if (vm.categoryChart) {
                        vm.categoryChart.destroy();
                    }

                    vm.categoryChart = new Chart(ctx, {
                        type: 'doughnut',
                        data: {
                            labels: categories,
                            datasets: [{
                                data: amounts,
                                backgroundColor: [
                                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
                                    '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
                                ]
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                },
                                title: {
                                    display: true,
                                    text: 'Expenses by Category - ' + getMonthName(vm.selectedMonth) + ' ' + vm.selectedYear
                                }
                            }
                        }
                    });
                }).catch(function(error) {
                    console.error('Error updating category chart:', error);
                });
        }

        function updateTrendChart() {
    var ctx = document.getElementById('trendChart');
    if (!ctx) return;

    ExpenseService.getMonthlyTrend(vm.selectedYear)
        .then(function(response) {
            var monthlyData = response.data;
            
            
            var monthOrder = ['January', 'February', 'March', 'April', 'May', 'June',
                             'July', 'August', 'September', 'October', 'November', 'December'];
            
            var orderedMonths = [];
            var orderedAmounts = [];
            
            monthOrder.forEach(function(month) {
                orderedMonths.push(month.substring(0, 3)); 
                var amount = monthlyData[month];
                orderedAmounts.push(amount ? parseFloat(amount) : 0);
            });

            if (vm.trendChart) {
                vm.trendChart.destroy();
            }

            vm.trendChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: orderedMonths,
                    datasets: [{
                        label: 'Monthly Expenses ($)',
                        data: orderedAmounts,
                        borderColor: '#4facfe',
                        backgroundColor: 'rgba(79, 172, 254, 0.1)',
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: '#4facfe',
                        pointBorderColor: '#ffffff',
                        pointBorderWidth: 3,
                        pointRadius: 6,
                        pointHoverRadius: 8,
                        pointHoverBackgroundColor: '#2d89ef',
                        pointHoverBorderColor: '#ffffff',
                        pointHoverBorderWidth: 3
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    interaction: {
                        intersect: false,
                        mode: 'index'
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return '$' + value.toFixed(2);
                                }
                            }
                        }
                    },
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top'
                        },
                        title: {
                            display: true,
                            text: 'Monthly Expense Trend - ' + vm.selectedYear
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    return 'Expenses: $' + context.parsed.y.toFixed(2);
                                }
                            }
                        }
                    }
                }
            });
        }).catch(function(error) {
            console.error('Error updating trend chart:', error);
            showNotification('Error loading trend chart', 'error');
        });
}


function updateCategoryChart() {
    var ctx = document.getElementById('categoryChart');
    if (!ctx) return;

    ExpenseService.getExpensesByMonth(vm.selectedYear, vm.selectedMonth)
        .then(function(response) {
            var expenses = response.data;
            var categoryData = {};
            
            expenses.forEach(function(expense) {
                var category = expense.category || 'Uncategorized';
                if (categoryData[category]) {
                    categoryData[category] += parseFloat(expense.amount);
                } else {
                    categoryData[category] = parseFloat(expense.amount);
                }
            });

            var categories = Object.keys(categoryData);
            var amounts = Object.values(categoryData);
            
            
            if (categories.length === 0) {
                categories = ['No Data'];
                amounts = [0];
            }

            if (vm.categoryChart) {
                vm.categoryChart.destroy();
            }

            vm.categoryChart = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: categories,
                    datasets: [{
                        data: amounts,
                        backgroundColor: [
                            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
                            '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
                        ],
                        borderWidth: 2,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: {
                                padding: 20,
                                usePointStyle: true
                            }
                        },
                        title: {
                            display: true,
                            text: 'Expenses by Category - ' + getMonthName(vm.selectedMonth) + ' ' + vm.selectedYear
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    var label = context.label || '';
                                    var value = context.parsed || 0;
                                    return label + ': $' + value.toFixed(2);
                                }
                            }
                        }
                    }
                }
            });
        }).catch(function(error) {
            console.error('Error updating category chart:', error);
            showNotification('Error loading category chart', 'error');
        });
}


        function getMonthName(monthNumber) {
            var monthNames = ['January', 'February', 'March', 'April', 'May', 'June',
                             'July', 'August', 'September', 'October', 'November', 'December'];
            return monthNames[monthNumber - 1];
        }
    }
})();
