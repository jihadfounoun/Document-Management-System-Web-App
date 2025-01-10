function makeCall(method, url,form,callback,reset=true){
	
	//creo le request
	var req=new XMLHttpRequest();
	
	//istanzio la funzione che viene chiamata ad ogni cambiamento di stato della req
	req.onreadystatechange= function (){
		if(req.readyState === XMLHttpRequest.DONE){//gestendo solo il caso di chiamata finita
			callback(req);//chiamando cosi la call back function
		}
	}
	//Initializes a method request to the specified URL
	req.open(method,url,true);//il buleano indica se la chiamata deve essere asincrona(in parallelo all'esecuzione js)
	if (form == null) {
        req.send(); 
    } else if (form instanceof FormData){
        req.send(form); 
    } else {
        req.send(new FormData(form)); 
    }

    if (form !== null && !(form instanceof FormData)&&reset==true) {
        form.reset();
    }
	
}
