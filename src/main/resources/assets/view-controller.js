angular.module('app.services')

/**
 * Language filter.
 */
.filter('i18n', ['lang', function (lang) {
	return function (key, p) {
		if (typeof lang[key] != 'undefined' && lang[key] != '') {
			return (typeof p === "undefined") ? lang[key] : lang[key].replace('@{}@', p);
		}
		return key;
	}
}])
.directive("slimScroll",[function(){
	return{
		restrict:"A"
		,link:function(scope,ele,attrs) {
			return ele.slimScroll({height:attrs.scrollHeight||"100%"})
		}
	}
}])

/**
 * View controller
 */
.controller('ViewController', ['$rootScope', '$http', 'lang'
                               , function($rootScope, $http, lang) {
		
//	$scope.categoryMapList = [];
//	$scope.showMenuItem = function(menuCode){
//		console.log("Map list "+$scope.categoryMapList);
//		var result = false;
//		$scope.categoryMapList.forEach(function(entry) {
//			if(entry.qualifierValue == menuCode && entry.countItems>0){
//				result = true;
//			}	
//		});
//		return result;
//	}
	$rootScope.logout = function() {
		return $http.post('/logout');
    }
	/**
	 * Abas
	 */
	$rootScope.sectionTab = 1;
	$rootScope.setSectionTab = function(value) {
		$rootScope.sectionTab = value;
   };
   $rootScope.isSectionTabSet = function(value) {
     return $rootScope.sectionTab === value;
   };



//	$scope.localizationKeys = lang._getLocalizationKeys();
//
//	securityServices.getAuthorizedRoles('/api/entity/auth');
////	$scope.isAuthorized = function(role, ext) {
////		return securityServices.isAuthorized(role, ext);
////	}
////	/**
////	 * Autorização
////	 */
////	$scope.authList =[];
////	defineAuthorities();
////	function defineAuthorities(){
////		securityServices.getAuthorizedRoles(null).success(function(data, status, headers, config) {
////			$scope.authList = data.content;
////		});
////	}
////	$scope.isAuthorized =function(role, ext){
////		return securityServices.isAuthorized($scope.authList, role, ext);
////	}
		 
}]);
