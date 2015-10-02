[#ftl]
<!DOCTYPE html >
<html >

<head>
    <meta content="text/html; iso-8859-1" http-equiv="content-type">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
    [#include "/frame-head.ftl" /]

    <script type="text/javascript">var externalId = ${externalId!0};</script>
	<link type="image/x-icon" href="/images/favicon.ico" rel="shortcut icon">
	<link type="image/x-icon" href="/images/favicon.ico" rel="icon">

    <title>${title!''}</title>
	<meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

</head>
<body style="background-color: #f3f3f4;">
	<!-- 
	 ! Menu
	 ! -->
	[#if externalBrand??]
	<div class="header clearfix ng-scope">
	<nav>
		<ul class="nav nav-pills pull-right">
			<li role="presentation" class="active"><a href="/home">Login</a></li>
			<li role="presentation"><a href="/signup">Criar conta</a></li>
		</ul>
	</nav>
	<h3 class="text-muted">${externalBrand}</h3>
	</div>
	[/#if]
	
	<!-- 
	 ! Conteúdo principal
	 ! -->
	<div class="main-container">
	
		<!-- [#if staticContent??][#include "${staticContent}.html"/][/#if] -->
		
	</div><!--main-container-->
	
</body>

</html>
