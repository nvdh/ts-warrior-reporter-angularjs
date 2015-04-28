angular
.module('ts-warrior', [])
.controller('Controller', ['$scope', '$http', function($scope, $http) {
		
	$scope.fetchResult = "";
	
	$scope.setJSON = function(data){
		$scope.fetchResult = data;
	}
	
	$scope.date = function(dateString){
		return Date.parse(dateString);
	}
	
	$scope.duration = function(duration){
		var sign = duration < 0 ? "-" : "";
		var abs = Math.abs(duration);
		var seconds=Math.floor((abs/1000)%60);
		var minutes=Math.floor((abs/(1000*60))%60);
		var hours=Math.floor((abs/(1000*60*60)));
		return sign + hours + "h" + minutes;
	}

	$scope.nonZeroDuration = function(duration){
		var sign = duration < 0 ? "-" : "";
		var abs = Math.abs(duration);
		var seconds=Math.floor((abs/1000)%60);
		var minutes=Math.floor((abs/(1000*60))%60);
		var hours=Math.floor((abs/(1000*60*60)));
		if (hours == 0 && minutes == 0){
			return "";
		} else {
			return hours + "h" + minutes;
		}
	}
	
	$scope.timeToGoHomeLabel = function(overtime, timeToGoHome){
		if (overtime > 8 * 60 * 60 * 1000){
			return "You have more than 8 hours overtime."
		} else if (!timeToGoHome){
			return "It's unclear when you can leave.";
		} else if ($scope.timeToDate(timeToGoHome) < new Date()){
			return "You can go home.";
		} else {
			var parts = timeToGoHome.split(":");
			return "You can checkout at: " + parts[0] + "h" + parts[1];
		}
	}
	
	$scope.timeToDate = function(time){
		var parts = time.split(":");
		var hours = parts[0];
		var minutes = parts[1];
		var date = new Date();
		date.setHours(hours);
		date.setMinutes(minutes);
		return date;
	}

	$scope.loadData = function(){
		console.log("working with data: " + $scope.fetchResult)
	};
	
}]);