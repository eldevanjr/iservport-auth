var appLayout = angular.module('app.layout', [])

	/**
	 * Directiva lista qualificadores (segunda versão)
	 */
	.directive('alertWrapper', function() {
		return {
			restrict: 'C',
			scope: { alert: '@' },
			replace: true,
			template: '<span class="badge alert-danger" data-ng-show="alert>0 ">{{alert}}</span>'
		}
	})
	
	/**
	 * Menu
	 */
	
	/**
	 * Menu main wrapper
	 * 
	 * Example:
	 * <div class="panel-body" data-menu-main-wrapper data-labels="a, b"></div>
	 */
	.directive('menuMainWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    scope: { labels: '@' },
			link:function(scope, element, attrs){
				scope.labelList = attrs.labels.split(",");
				scope.shift = -1;
				if (!attrs.allowHidden) {
					scope.shift = 0;
				}
			},	
		    template: '<ul class="nav nav-pills nav-justified" >' +
		              '<li data-ng-class="{active:isSectionTab($index) }" ng-repeat="label in labelList track by $index">' +
		              '<a class="list-group-item" data-ng-click="setSectionTab($index)">{{label}}</a>' +
		              '</li>' +
		              '</ul>'
		  }
	})
	/**
	 * Menu line wrapper
	 * 
	 * Example:
	 * <li data-base-ref="x" data-menu-line-wrapper data-icon="y">Name</li>
	 */
	.directive('menuLineWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    replace: true,
		    scope: { baseRef:'@', icon:'@' },
		    template: '<li role="presentation" >' +
		    		  '<a href="/{{baseRef}}/" >' +
		              '<i class="{{icon}}"></i>' +
		              '<span data-ng-transclude></span>' +
		              '</a></li>'
		  }
	})
	/**
	 * Menu side wrapper
	 * 
	 * Example:
	 * <div data-menu-side-wrapper>...</div>
	 */
	.directive('menuSideWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    scope: { },
		    template: '<div class="nav-wrapper">' +
		              '<div class="slimScrollDiv" style="position: relative; overflow: hidden; width: auto; height: 100%;">' +
		              '<ul id="nav" class="nav" data-slim-scroll data-collapse-nav data-highlight-active ng-transclude' +
		              ' style="overflow: hidden; width: auto; height: 100%;">' +
		              '</div>' +
		              '</div>'
		  }
	})
	/**
	 * Form group wrapper
	 * 
	 * Example:
	 * <div data-control-id="x" data-form-group-wrapper data-label="y">...</div>
	 */
	.directive('formGroupWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    scope: { controlId:'@', label:'@' },
		    template: '<div class="form-group">'+
		              '<label for="{{controlId}}" class="col-sm-3 control-label">{{label}}</label>' +
		              '<div class="col-sm-9">' +
		              '<ng-transclude></ng-transclude>' +
		              '</div>' +
		              '</div>'
		  }
	})
	
	/**
	 * Modal
	 */
	
	/**
	 * Modal header wrapper
	 * 
	 * Example:
	 * <div data-modal-header-wrapper>...</div>
	 */
	.directive('modalHeaderWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    template: '<div class="modal-header">'+
		              '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
		              '<span class="h4 modal-title" id="modalLabel" >' +
		              '<ng-transclude></ng-transclude>' +
		              '</span>' +
		              '</div>'
		  }
	})

	/**
	 * Panel
	 */
	
	/**
	 * Panel wrapper
	 * 
	 * Example:
	 * <div data-panel-wrapper data-label="x">...</div>
	 */
	.directive('panelWrapper', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    scope: { label:'@' },
		    template: '<div class="panel panel-default">'+
		              '<div class="panel-heading" >{{label}}</div>' +
		              '<div class="ng-transclude" >' +
		              '</div>' +
		              '</div>'
		  }
	})
	/**
	 * Panel wrapper with panel body
	 * 
	 * Example:
	 * <div data-panel-wrapper-body data-label="x">...</div>
	 */
	.directive('panelWrapperBody', function() {
		  return {
		    restrict: "A", 
		    transclude: true,
		    scope: { label:'@' },
		    template: '<div class="panel panel-default">'+
		              '<div class="panel-heading" >{{label}}</div>' +
		              '<div class="panel-body ng-transclude" >' +
		              '</div>' +
		              '</div>'
		  }
	})
	
	;

;
