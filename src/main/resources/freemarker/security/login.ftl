[#ftl]
<!DOCTYPE html>
<html id="ng-app" xmlns:ng="http://angularjs.org" data-ng-app="security" >
<head>
    <title>Login</title>
	[#include "../frame-head.ftl" /]
</head>
<body style="background-color: #f3f3f4;">

	<div id="unauth">
	<div id="main" class="container" data-ng-controller="SecurityController as securityCtrl" >
	<div class="container-small" data-ng-cloak >
	<script >
		var email = [#if email??]'${email}' [#else]''[/#if];
	</script>
	[#include "../config/login-splash.html" /]
	<div class="panel panel-default">
	
		<div class="panel-heading">
			<div class="row">
				<div class="col-md-12">
					<h3 class="panel-title"><span class="glyphicon  glyphicon-lock"></span> Entrar</h3>
				</div>
			</div>
		</div>
		
		<div class="panel-body">
		<form  name="form"  id="login" action="/login" method="POST">
		
		
			[#--
			 # Context id
			 #--]
			<input type="hidden" name="contextId" value="${contextId!1}" />
				
			[#if error?? ]
				[#if user?? && !user.accountNonExpired  ]		
					<!--
					 ! Inactive user
					 !-->
					<div class="alert alert-danger" role="alert">
						<i class="fa fa-lock"></i> Usuário inativo 
					</div>
				[#else]	
					<!--
					 ! Login error
					 !-->
					<div class="alert alert-danger" role="alert">
						<i class="fa fa-lock"></i> Erro ao fazer login
					</div>		
				[/#if]
			[/#if]
			[#if userConfirmed?? ]
					<div class="alert alert-success" role="alert"><i class="fa fa-unlock"></i> Seu usuário foi ativado, faça o login para entrar.</div>		
			[/#if]
			[#if emailRecoverySent?? ]
					<div class="alert alert-warning" role="alert">Consulte seu email para recuperar sua senha. </div>		
			[/#if]
			[#if emailRecoveryFailed?? ]
					<div class="alert alert-warning" role="alert">Falha ao enviar o email para recuperar sua senha. </div>		
			[/#if]
			[#if recoverFail?? && recoverFail="true" ]<div class="alert alert-danger" role="alert">Falha ao mudar a senha. </div>[/#if]
			
			[#if recoverFail?? && recoverFail="false"]<div class="alert alert-success" role="alert">Senha modificada com sucesso. </div>[/#if]

			<div id="form-group-email" class="form-group">
				<input type="email"  required="" name="username"  data-ng-model="username" placeholder="E-mail" class="form-control">
			</div>
			<div id="form-group-password" class="form-group">
				<input type="password"  required="" name="password" data-ng-model="password" placeholder="Senha" class="form-control">
			</div>
			
			<!--
			 ! Forgot password
			 !-->
			<div class="row">
				<div class="col-md-6">
					<p><input type="checkbox" name="remember-me" > Lembrar senha</p>
				</div>
			</div>
			
			<!--
			 ! Submit
			 !-->
			<hr/>
			<button type="submit" class="btn btn-primary" style="width: 100%" >Enviar</button>
			
			
		</form>
	</div>
	
	<footer class="footer">
		<hr>
        <p>© i-serv Consultoria Empresarial Ltda. 2015</p>
  
  		<div class="row">
    	<div class="col-md-12">
	 		<small> 
		 			<a class="text-muted" target="_new" data-ng-href="/signup/privacy/" href="/signup/privacy/"> Politica de Privacidade</a>  |
		    		<a target="_new" class="text-muted" data-ng-href="/signup/license/" href="/signup/license/"> Termos de Uso</a>
			</small>
     	</div>
		</div>
	</footer>

	</div><!-- .container-small -->
	</div><!-- #main -->
	</div><!-- #unauth -->

	[#include "../_js.html" /]

</body>
</html>