$(document).ready(function(){

	$("a#botao-login").click(function () {
	    $("div#login").fadeOut("slow");
	});

	$("a#botao-login").click(function () {
	    $("div#login-escolha").fadeIn("slow");
	});
	
	$("a#login-link-empresa").click(function () {
	    $("div#login-escolha").fadeOut("slow");
	});
	
	$("a#login-link-empresa").click(function () {
	    $("div#login-form").fadeIn("slow");
	});


});