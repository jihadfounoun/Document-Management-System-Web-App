(function() {

	var login_button = document.getElementById("login_button");
	var register_button = document.getElementById("register_button");
	var register_div = document.getElementById("register_div");
	var login_div = document.getElementById("login_div");
	var goToLogin_button = document.getElementById("goToLogin_button");
	var goToRegister_button = document.getElementById("goToRegister_button");
	var registration_form = document.getElementById("registration_form");
	var login_form = document.getElementById("login_form");

	var login_error = document.getElementById("login_error");
	var register_error = document.getElementById("register_error");

	goToLogin_button.style.display = "none";
	goToRegister_button.style.display = "block";

	register_div.style.display = "none";
	login_div.style.display = "block";

	goToRegister_button.addEventListener("click", (e) => {
		login_form.reset();
		register_div.style.display = "block";
		login_div.style.display = "none";
		goToLogin_button.style.display = "block";
		goToRegister_button.style.display = "none";
	});
	goToLogin_button.addEventListener("click", (e) => {
		registration_form.reset();
		register_div.style.display = "none";
		login_div.style.display = "block";
		goToLogin_button.style.display = "none";
		goToRegister_button.style.display = "block";
	});
	register_button.addEventListener("click", () => {
		if (registration_form.checkValidity()) {
			if (checkRegistrationForm()) {

				makeCall("POST", "Register", registration_form, function(request) {
					if (request.status == 200) {
						var user = JSON.parse(request.responseText);
						console.log(user);
						sessionStorage.setItem('id', user.id);
						sessionStorage.setItem('name', user.name);
						sessionStorage.setItem('email', user.email);
						sessionStorage.setItem('username', user.username);
						window.location.href = "home.html";
					} else {
						var message = request.responseText;
						document.getElementById("register_error").textContent = message;
					}

				});
			}
		} else {
			registration_form.reportValidity();
		}


	});
	login_button.addEventListener("click", (e) => {
		e.preventDefault();
		if (login_form.checkValidity()) {
			if (checkLoginForm()) {
				makeCall("POST", "Login", login_form, function(request) {
					if (request.status == 200) {
						console.log(request.responseText);
						var user = JSON.parse(request.responseText); //se tutto ok, metto nella sessione
						sessionStorage.setItem('id', user.id);//l' id dell'utemnte correnet
						sessionStorage.setItem('name', user.name);
						sessionStorage.setItem('username', user.username);
						sessionStorage.setItem('email', user.email);
						window.location.href = "home.html";
					} else {
						var message = request.responseText;
						login_error.textContent = message;
					}

				});
			} else {
				login_error.textContent = "Data non valid!";
			}
		} else {
			login_form.reportValidity();
		}
	});

	function checkLoginForm() {
		var form = new FormData(login_form);
		var username = form.get("username");
		var password = form.get("password");
		console.log(username);
		console.log(password);
		if (isEmpty(username) || isEmpty(password)) {
			return false;
		}
		return true;
	}
	function checkRegistrationForm() {
		var form = new FormData(registration_form);
		var name = form.get("name");
		var email = form.get("email");
		var username = form.get("username");
		var pass1 = form.get("password");
		var pass2 = form.get("repeated_password");
		for (var pair of form.entries()) {
			console.log(pair[0] + ": " + pair[1]);
		}
		if (isEmpty(name) || isEmpty(username) || isEmpty(email) || isEmpty(pass1) || isEmpty(pass2)) {

			register_error.textContent = "please insert all the data";
			return false;
		}
		if (!checkEmail(email)) {
			register_error.textContent = "please insert correct email";

			return false;
		}
		if (pass1 != pass2) {
			register_error.textContent = "the passwords do not match";
			return false;
		}
		return true;
	}

	function checkEmail(email) {

		return email.includes("@") && email.indexOf("@") > 0 && email.length > 2 && email.indexOf("@") < (email.length - 1);
	}
	function isEmpty(string) {
		return string == null || string.trim().length == 0;
	}

})();












/**
 * 
 
(function() {
	var login_button = document.getElementById("login_button");
	login_button.addEventListener("click", (e) => {
		makeCall("POST","Login",form,function(request){
			if(form.checkValidity()){
			makeCall("POST","Login",form,function(request){
				//var msg=request.responseText;
				if(request.status===200){//Login successful
					//sessionStorage.setItem("username", message);
					//sessionStorage.setItem("name",message);
					//sessionStorage.setItem("id",message);
					window.location.href = "home.html";
				}else{
					//document.getElementById("login_error").textContent=message;
				}
				
			});
		}else{
			form.reportValidity();
		}
	});
})();

 * 
 * 

if(form.checkValidity()){
			makeCall("POST","Login",form,function(request){
				var msg=request.responseText;
				if(request.status===200){//Login successful
					sessionStorage.setItem("username", message);
					sessionStorage.setItem("name",message);
					sessionStorage.setItem("id",message);
					window.location.href = "home.html";
				}else{
					document.getElementById("login_error").textContent=message;
				}
				
			});
		}else{
			form.reportValidity();
		}

 * 
 */