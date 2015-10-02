
var myMod = angular.module('app.services', ['ngResource'])
.config(function (datepickerConfig, datepickerPopupConfig) {
    datepickerConfig.showWeeks = false;
    // datepickerPopupConfig.toggleWeeksText = null;
    datepickerPopupConfig.showButtonBar = false;

})
.factory("securityServices", ['$http', function($http) {
	var categoryMapList =  {};
	var getCategoryMap = function() {
		return $http.get('/api/category/qualifier')
		.success(function(data, status, headers, config) {
			categoryMapList = data;
		});
	}
	var roleList = [];
	var getAuthorizedRoles = function(userId) {
		return $http.get('/api/entity/auth'+((userId!=null && userId!='null')?'?userId='+userId:''))
		.success(function(data, status, headers, config) {
			roleList = data.content;
		});
	}
	return {
		getAuthorizedRoles : (getAuthorizedRoles) ,
		isAuthorized: function(role, extension) {
			var result = false;
			roleList.forEach(function(entry) {
				if(entry.serviceName == (role) && entry.serviceExtension.indexOf(extension)>-1){
					result = true;
				}
			});
			return result;
		
		}
		, getCategoryMap:(getCategoryMap)
		, showMenuItem : function(menuCode){
			var result = false;
			categoryMapList.forEach(function(entry) {
				if(entry.qualifierValue == menuCode && entry.countItems>0){
					result = true;
				}	
			});
			return result;
		}
		, logout: function() {return $http.post('/logout')}
		}
}])
.factory("genericServices", function() {                                                                                                                                                   
	return {                                                                                                                                                                                                              
		getNextAndPreviousLinkByList: function(list) {   
			var page = {next:0, previous:0, hasNext:false, hasPrevious:false}; 
			if(list.number!=0){
				page.previous = list.number-1;
				page.hasPrevious = true;
			}
			if(list.number+1<list.totalPages){				
				page.next = list.number+1;
				if(list.numberOfElements==list.size && list.totalElements > list.size ){
					page.hasNext = true;	
				}				
			}
			return page;
		},
		/**
		 * Transforma um Form num object pronto para ser Transformado em Json
		 * @param form
		 */
		serializeObject : function(form)
		{ //from : http://jsfiddle.net/sxGtM/3/
			var json = {};
			var formData = form.serializeArray();
			$.each(formData, function() {
				if (json[this.name] !== undefined) {
					if (!json[this.name].push) {
						json[this.name] = [json[this.name]];
					}
					json[this.name].push(this.value || '');
				} else {
					json[this.name] = this.value || '';
				}
			});
			return json;
		},
		verifyPassword: function(pass, cpass){
			if(pass==null || pass=='undefined' || cpass==null || cpass=='undefined'){
				return false;
			}
			return pass === cpass;
		}
	}
})
//filters
//================================================= 
.filter('pad', function() {
	return function(num) {
		return (num < 10 ? '0' + num : num); // coloca o zero na frente
	};
})
/**
 * Directiva lista qualificadores 
 */
//TODO colocar atributos variáveis para a lista ex: img,class icons ,etc..
.directive('listQualifier', [ '$http', function($http) {
	return {
		restrict: 'E',				  	    
		scope: {
			ngClickFn: '& onclick'
		},				    
		link:function(scope, element, attrs){
			$http.get(attrs.href)
			.success(function(data, status, headers, config) {
				scope.qualifiers = data;
				
				scope.$parent.categoryId = data[0].id;
				scope.$parent.qualifierValue = data[0].id;
			});		
			scope.setCategoryId=scope.$parent.setCategoryId;
			scope.$parent.$watch('categoryId', function() {
				scope.categoryId = scope.$parent.categoryId;
				scope.qualifierValue = scope.$parent.categoryId;
			}); 
			if (!attrs.countlabel) { 
				attrs.countlabel = 'Item(s)'; 
			}			
			scope.countLabel=attrs.countlabel;
		},	
		templateUrl: '/assets/_template/list-qualifier.html'
	};
}])
.directive('iservportMain', function(){
		return {
			restrict: 'EA',			
			template :'<div ng-include="iservportMainPath"></div>',
			controller: function($scope) {
				$scope.iservportMainPath = "/assets/"+$scope.baseName+"/selection-main.html";
			}

		}

})
.directive('iservportFilter', function(){
		return {
			restrict: 'EA',			
			template :'<div ng-include="iservportFilterPath"></div>',
			controller: function($scope) {
				$scope.iservportFilterPath = "/assets/"+$scope.baseName+"/selection-filter.html";
			}

		}

})
.directive('iservportProperties', function(){
		return {
			restrict: 'EA',			
			template :'<div ng-include="iservportPropertiesPath"></div>',
			controller: function($scope) {				
				$scope.iservportPropertiesPath = "/assets/"+$scope.baseName+"/selection-properties.html";
			}
		}

})
.directive('iservportInfo', function(){
	return {
		restrict: 'EA',			
		template :'<div ng-include="iservportInfoPath"></div>',
		controller: function($scope) {				
			$scope.iservportInfoPath = "/assets/"+$scope.baseName+"/info.html";
		}
	}

})

/**
 * Diretiva para recuperar entidade autorizada.
 */
.directive('authorizedEntity', [ '$http', function($http) {
		return {
			restrict: 'A',			
			link:function(scope, element, attrs) {
				$http.get(attrs.href)
				.success(function(data, status, headers, config) {
					scope.authorizedEntity = data;
				});		
			},
			template :'<div id="authorizedEntity">{{authorizedEntity.entityAlias.length>0?authorizedEntity.entityAlias:"..."}}</div>'
		}

}])
/**
 * Diretiva para recuperar usuário autorizado.
 * 
 * Default : userKey 
 * 
 */
.directive('authorizedUser', [ '$http', function($http) {
		return {
			restrict: 'EA',
			link:function(scope, element, attrs) {
				$http.get('/api/entity/user')
				.success(function(data, status, headers, config) {
					
					scope.userLabel = data.userKey; 
					if(typeof attrs.typeName != 'undefined' && attrs.typeName.indexOf('name')>-1 ){
						scope.userLabel = data.userName;
					}else if(typeof attrs.typeName != 'undefined' && attrs.typeName.indexOf('display')>-1){
						scope.userLabel = data.displayName;
					}
					console.log(scope.userLabel);
				});		
			},
			template :'<div id="authorizedUser">{{userLabel}}</div>'
		}

}])
/**
 * Directiva para tratar erro de imagens.
 * from: http://plnkr.co/edit/KGvqfvKA5n979mu6BJT2?p=preview
 * Usage:<img src="URL" err-src="URL_DEFAULT">
 * 
 */
.directive('errSrc', function() {
	return {
		link: function(scope, element, attrs) {
			element.bind('error', function() {
				if (attrs.src != attrs.errSrc) {
					attrs.$set('src', attrs.errSrc);
				}
			});

			attrs.$observe('ngSrc', function(value) {
				if (!value && attrs.errSrc) {
					attrs.$set('src', attrs.errSrc);
				}
			});
		}
	}
})
.directive("toggleNavCollapsedMin", ["$rootScope", function($rootScope) {
            return {
                restrict: "A",
                link: function(scope, ele) {
                    var app;
                    return app = $("#app"), ele.on("click", function(e) {
                        return app.hasClass("nav-collapsed-min") ? app.removeClass("nav-collapsed-min") : (app.addClass("nav-collapsed-min"), $rootScope.$broadcast("nav:reset")), e.preventDefault()
                    })
                }
            }
        }])
.directive("collapseNav", [function() {
            return {
                restrict: "A",
                link: function(scope, ele) {
                    var $a, $aRest, $app, $lists, $listsRest, $nav, $window, Timer, prevWidth, updateClass;
                    return $window = $(window), $lists = ele.find("ul").parent("li"), $lists.append('<i class="ti-angle-down icon-has-ul-h"></i><i class="ti-angle-double-right icon-has-ul"></i>'), $a = $lists.children("a"), $listsRest = ele.children("li").not($lists), $aRest = $listsRest.children("a"), $app = $("#app"), $nav = $("#nav-container"), $a.on("click", function(event) {
                        var $parent, $this;
                        return $app.hasClass("nav-collapsed-min") || $nav.hasClass("nav-horizontal") && $window.width() >= 768 ? !1 : ($this = $(this), $parent = $this.parent("li"), $lists.not($parent).removeClass("open").find("ul").slideUp(), $parent.toggleClass("open").find("ul").stop().slideToggle(), event.preventDefault())
                    }), $aRest.on("click", function() {
                        return $lists.removeClass("open").find("ul").slideUp()
                    }), scope.$on("nav:reset", function() {
                        return $lists.removeClass("open").find("ul").slideUp()
                    }), Timer = void 0, prevWidth = $window.width(), updateClass = function() {
                        var currentWidth;
                        return currentWidth = $window.width(), 768 > currentWidth && $app.removeClass("nav-collapsed-min"), 768 > prevWidth && currentWidth >= 768 && $nav.hasClass("nav-horizontal") && $lists.removeClass("open").find("ul").slideUp(), prevWidth = currentWidth
                    }, $window.resize(function() {
                        var t;
                        return clearTimeout(t), t = setTimeout(updateClass, 300)
                    })
                }
            }
        }])
	/**
	 * Directiva lista qualificadores (segunda versão)
	 */
	.directive('qualifierPanel', function($compile) {
		return {
			restrict: 'A',
			terminal: true,
			scope: { qualifiers: '=qualifierPanel', setQualifier: '&', isQualifierActive: '&' },
			link:function(scope, element, attrs){
				element.addClass("panel panel-default");
				scope.countLabel=attrs.countlabel;
				if (!attrs.countlabel) { 
					scope.countLabel = 'Item(s)'; 
				}
				$compile(element.contents())(scope.$new());
			},
			template:
				'<ul class="list-group">' +
				'<a href="" class="list-group-item" data-ng-repeat="qualifierItem in qualifiers" ' + 
				'   data-ng-click="setQualifier({value: qualifierItem.qualifierValue})" >' +
				'<div data-ng-class="{h4: isQualifierActive({value: qualifierItem.qualifierValue}) }">' +
				'<i class="{{qualifierItem.fontIcon}}" data-ng-if="qualifierItem.fontIcon.length>0"></i>' +
				'{{qualifierItem.qualifierName}}' +
			    '</div>' +
			    '<span style="font-size: 70%; color: #aaa;">{{qualifierItem.countItems}} {{countLabel}}</span>' +
			    '</a></ul>'
		};
	})
	/**
	 * Directiva lista qualificadores (segunda versão)
	 */
	.directive('qualifierNav', function($compile) {
		return {
			restrict: 'A',
			terminal: true,
			scope: { qualifiers: '=qualifierNav', setQualifier: '&', isQualifierActive: '&' },
			link:function(scope, element, attrs){
				element.addClass("panel panel-default");
				scope.countLabel=attrs.countlabel;
				if (!attrs.countlabel) { 
					scope.countLabel = 'Item(s)'; 
				}
				$compile(element.contents())(scope.$new());
			},
			template:
				'<ul class="list-group">' +
				'<a href="" class="list-group-item" data-ng-repeat="qualifierItem in qualifiers" ' + 
				'   data-ng-click="setQualifier({value: qualifierItem.qualifierValue})" >' +
				'<div data-ng-class="{h4: isQualifierActive({value: qualifierItem.qualifierValue}) }">' +
				'<i class="{{qualifierItem.fontIcon}}" data-ng-if="qualifierItem.fontIcon.length>0"></i>' +
				'{{qualifierItem.qualifierName}}' +
			    '</div>' +
			    '<span style="font-size: 70%; color: #aaa;">{{qualifierItem.countItems}} {{countLabel}}</span>' +
			    '</a></ul>'
		};
	})
	/**
	 * Qualifier service.
	 */
	.factory('qualifierService', function() {
		var qualifierService = {};
		var value = 0;
		
		/**
		 * Run function
		 */
		qualifierService.run = function(resource, callBack, externalId) {
			var list = resource.query();
			list.$promise.then(function(data) {
				if (value === 0 && data.length>0 && externalId==0) {
					value = data[0].qualifierValue;
				}
				callBack(value, data);
			})
		};

		/**
		 * Is active function
		 */
		qualifierService.isActive = function(qualifierValue) {
			return value==qualifierValue;
		}
		
		return qualifierService;
	})
	.controller('ViewController', ['$scope', '$http', 'securityServices', function($scope, $http, securityServices) {
		
		/**
		 * Abas
		 */
		$scope.sectionTab = 1;
		$scope.setSectionTab = function(value) {
			this.sectionTab = value;
	    };
	    $scope.isSectionTabSet = function(value) {
	      return this.sectionTab === value;
	    };

		/**
		 * Autorização
		 */
		$scope.authList =[];
		defineAuthorities();
		function defineAuthorities(){
			securityServices.getAuthorizedRoles(null).success(function(data, status, headers, config) {
				$scope.authList = data.content;
			});
		}
		$scope.isAuthorized =function(role, ext){
			return securityServices.isAuthorized($scope.authList, role, ext);
		}
		 
	}]);
	

