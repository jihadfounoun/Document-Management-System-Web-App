(function() {


	var folderTreeContainer = document.getElementById("folderTreeContainer");
	var items;
	var folder_modal = document.getElementById("addFolderModal");
	var document_modal = document.getElementById("addDocumentModal");
	var errorMessage_modal = document.getElementById("errorMessageModal");
	var confirmation_modal = document.getElementById("confirmationModal");
	var addFolder_button = document.getElementById("addFolder_button");
	var addDocument_button = document.getElementById("addDocument_button");
	var addMainFolder_button = document.getElementById("addMainFolder_button");
	var home_page = document.getElementById("home_page");
	var content_page = document.getElementById("content_page");

    var home_userInfo= document.getElementById("user_info_home");
    var content_userInfo= document.getElementById("user_info_content");    
	var dustbin_home = document.getElementById("dustbin_home");
	var dustbin_content = document.getElementById("dustbin_content");	
	var items_container=document.getElementById("left-container");	
	var user_name=document.getElementById("user_name");
	
	content_page.style.display = "none";
	
	user_name.textContent=sessionStorage.getItem('name');
	
	dustbin_home.addEventListener("dragover", (e) => { e.preventDefault(); });
    dustbin_content.addEventListener("dragover", (e) => { e.preventDefault(); });
    
	dustbin_home.addEventListener("drop", (e) => {
		e.preventDefault();
		var data = JSON.parse(e.dataTransfer.getData("application/json"));
		if (data.type == "folder") {
			deleteFolder(data.id, data.name);
		} else if (data.type == "document") {
			deleteDocument(data.id, data.name);
		}
	}
	);
	dustbin_content.addEventListener("drop", (e) => {
		e.preventDefault();
		var data = JSON.parse(e.dataTransfer.getData("application/json"));
		if (data.type == "folder") {
			deleteFolder(data.id, data.name);
		} else if (data.type == "document") {
			deleteDocument(data.id, data.name);
		}
	}
	);
	
		
	addMainFolder_button.className="addMainFolder-button";
	addMainFolder_button.addEventListener("click", (e) => {
		handleFolderFormModal("0");
	});


	addDocument_button.addEventListener("click", (e) => {
		e.preventDefault();
		document_modal.style.display = "none";
		var form = document.getElementById("addDocument_form");
		var formData = new FormData(form);
		form.reset();
		makeCall("POST", "AddDocument", formData, (request) => {
			if (request.status == 200) {
				var message = JSON.parse(request.responseText);
				showContentPage(message.id, message.name,message.date);
			} else {
				handleErrorMessageModal(request.responseText);
			}
		});

	});

	addFolder_button.addEventListener("click", (e) => {
		e.preventDefault();
		folder_modal.style.display = "none";
		var form = document.getElementById("addFolder_form");
		var formData = new FormData(form);
		form.reset();
		makeCall("POST", "AddFolder", formData, (request) => {
			if (request.status == 200) {
				showHomePage();
				//refreshFolderTree();
			} else {
				handleErrorMessageModal(request.responseText);
			}
		});

	});

	showHomePage();
	
	function checkLogin() {
		if (sessionStorage.getItem('id') == null)
			window.location.href = "login.html";
	}
	
	function setupContentUserInfo(){
		content_userInfo.innerHTML="";
		var span_username=document.createElement("span");
		span_username.textContent="Username: "+sessionStorage.getItem('username');
		var span_email=document.createElement("span");
		span_email.textContent="Email: "+sessionStorage.getItem('email');
		var div_logout=document.createElement("div");
		div_logout.textContent="logout";
		div_logout.className="logout";
		div_logout.addEventListener("click", (e) => {
		handleConfirmationModal("press ok to logout", function() {
			sessionStorage.clear();
			makeCall("GET", "Logout", null, (request) => {
				checkLogin();
			});
		}, null)
	    });
	    var div_goHome=document.createElement("div");
	    div_goHome.className="goHome";
	    div_goHome.textContent="home"
		div_goHome.addEventListener("click", (e) => {
			e.preventDefault();
			showHomePage()
		});
	    content_userInfo.append(span_username);
	    content_userInfo.append(span_email);
	    content_userInfo.append(div_logout);
	    content_userInfo.append(div_goHome);
		
	}
	
	function setupHomeUserInfo(){
		home_userInfo.innerHTML="";
		var span_username=document.createElement("span");
		span_username.textContent="Username: "+sessionStorage.getItem('username');
		var span_email=document.createElement("span");
		span_email.textContent="Email: "+sessionStorage.getItem('email');
		var div_logout=document.createElement("div");
		div_logout.textContent="logout";
		div_logout.className="logout";
		div_logout.addEventListener("click", (e) => {
		handleConfirmationModal("press ok to logout", function() {
			sessionStorage.clear();
			makeCall("GET", "Logout", null, (request) => {
				checkLogin();
			});
		}, null)
	    });
	    home_userInfo.append(span_username);
	    home_userInfo.append(span_email);
	    home_userInfo.append(div_logout);
		
	}
	
	
	function showHomePage() {
		setupHomeUserInfo();
		content_page.style.display = "none";
		refreshFolderTree();
		
		home_page.style.display = "block";
	}


	

	function fillFoldersContainer(items) {
		folderTreeContainer.innerHTML = "";
		const folderTree = createFolderTree(items.folders, 0, false, null);
		folderTreeContainer.appendChild(folderTree);
	};

	function createFolderTree(folders, parentId, move_mode, fatherId) {
		const ul = document.createElement("ul");

		folders
			.filter(folder => folder.parentFolderId === parentId)
			.forEach(folder => {

				const li = document.createElement("li");
				const a_folder = document.createElement("a");
				const a_add_folder = document.createElement("a");
				const a_add_document = document.createElement("a");
				const span = document.createElement("span");

				span.append(a_folder);
				a_folder.textContent = folder.name;
                a_folder.className="folder";
				if (move_mode == false) {
					a_add_folder.className="addFolder-button";
					a_add_folder.id = "addChildFolder_button";

					a_add_document.className="addDocument-button";
					a_add_document.id = "addDocument_button";


					a_add_document.addEventListener("click", (e) => {
						handleDocumentFormModal(folder.id);
					});

					a_add_folder.addEventListener("click", (e) => {
						handleFolderFormModal(folder.id);
					});

					a_folder.setAttribute("href", "#");

					a_folder.draggable = true;
					a_folder.setAttribute("type", "folder");


					a_folder.addEventListener("click", () => {
						showContentPage(folder.id, folder.name,folder.date);
					});

					a_folder.addEventListener("dragstart", (e) => {
						var data = {
							id: folder.id,
							name: folder.name,
							type: "folder"
						}
						e.dataTransfer.setData("application/json", JSON.stringify(data));
					});
					span.append(a_add_folder);
					span.append(a_add_document);
				}
				li.appendChild(span);


				if (move_mode == true) {
					if (folder.id == fatherId) {
						a_folder.style.color = "grey";
					} else {
						a_folder.addEventListener("dragover", (e) => {
							e.preventDefault();
						});
						a_folder.addEventListener("drop", (e) => {
							e.preventDefault();
							console.log("ci siamo");
							var data = JSON.parse(e.dataTransfer.getData("application/json"));
							console.log(data);
							if (data != null) {
								if (data.type == "folder") {
									handleErrorMessageModal("Unable to move Folder");
								} else if (data.parent_id == folder.id) {
									handleErrorMessageModal("Unable to move folder: The document is already located in the selected folder ");
								} else if (data.type == "document" && data.parent_id != folder.id) {
									//confirmation = confirm("click ok if you wnat to move the document " + data.name + " to the folder " + item.folder.name);

									var form = new FormData();
									form.append("document", data.id);
									form.append("folderId", folder.id);
									handleConfirmationModal("click ok if you wnat to move the document " + data.name + " to the folder " + folder.name, function(form) {
										makeCall("POST", "MoveDocument", form, (request) => {
											if (request.status == 200) {//show content della cartella padre
												var message = JSON.parse(request.responseText);
												showContentPage(message.id, message.name,message.date);
											} else {
												handleErrorMessageModal(request.responseText);
											}
										});
									}, form);


								}
							}
						});
					}
				}


				const childTree = createFolderTree(folders, folder.id, move_mode, fatherId);
				if (childTree.children.length > 0) {
					li.appendChild(childTree);
				}

				ul.appendChild(li);
			});

		return ul;
	}

	function showContentPage(folderId, folderName,folderDate) {
		home_page.style.display = "none";
		content_page.style.display = "block";
		setupContentUserInfo();
		var name = document.getElementById("folder_name");
		name.textContent = folderName;
		var folder_date=document.getElementById("folder_date");
		folder_date.textContent="folder created on "+folderDate;
		var droppableFolders_container=document.getElementById("droppableFolders_container");
		droppableFolders_container.innerHTML="";
		var form = new FormData();
		form.append("folderId", folderId);

		makeCall("POST", "GetItems", form, function(req) {
			if (req.status == 200) {
				folder_items = JSON.parse(req.responseText);
				fillContentPage(folder_items);
			} else {
				handleErrorMessageModal(req.responseText);
			}
		});
	}


	function fillContentPage(folder_items) {
		var documents = folder_items.documents;
		var folders = folder_items.folders;
		var folders_container = document.getElementById("folders");
		var documents_container = document.getElementById("documents");
		var moveDocument_button =document.getElementById("moveDocument_button");
		var droppableFolders_container = document.getElementById("droppableFolders_container");
		droppableFolders_container.style.display="none";
		if(moveDocument_button!=null){
			moveDocument_button.remove();
		}
		
		folders_container.innerHTML = "";
		documents_container.innerHTML = "";
		documents.forEach(doc => {
			var li = document.createElement("li");
			var span = document.createElement("span");
			span.textContent = doc.name;
			span.id = doc.id;
			span.className="document";
			span.setAttribute("type", "document");
			span.draggable = true;
			span.addEventListener("dragstart", (e) => {
				var data = {
					id: doc.id,
					name: doc.name,
					parent_id: doc.folderId,
					type: "document"
				};
				e.dataTransfer.setData("application/json", JSON.stringify(data));
			});
			span.addEventListener("click", (e) => {
				handleDocumentContentModal(doc);
			});
			li.append(span);
			documents_container.append(li);
		});
		folders.forEach(folder => {
			var li = document.createElement("li");
			var span = document.createElement("span");
			span.textContent = folder.name;
			span.setAttribute("type", "folder");
			span.className="folder";
			li.append(span);
			span.addEventListener("click",(e)=>{
				handleFolderModal(folder)
			});
			folders_container.append(li);
		});
		//show droppable folders

		if (documents.length > 0) {
			var id = documents[0].folderId;
			moveDocument_button=document.createElement("div");
			moveDocument_button.textContent="click to move document";
			moveDocument_button.setAttribute("id","moveDocument_button");
			items_container.append(moveDocument_button);
			moveDocument_button.addEventListener("click", (e) => {
				e.preventDefault();
				moveDocument_button.style.display="none";
				showDroppableFolders(id);
			});
			
		}

	}

	function showDroppableFolders(id) {
		makeCall("GET", "GetItems", null, function(request) {
			if (request.status == 200) {
				items = JSON.parse(request.responseText);
			} else {
				handleErrorMessageModal(request.responseText);
				items = null;
			}
		});
		if (items != null) {
			var droppableFolders_container = document.getElementById("droppableFolders_container");
			droppableFolders_container.innerHTML = "";

			droppableFolders_container.style.display="block";
			var close_move_window=document.createElement("span");
			close_move_window.className="close";
			close_move_window.textContent="x";
			droppableFolders_container.append(close_move_window);
			
			close_move_window.addEventListener("click",(e)=>{
				droppableFolders_container.style.display="none";
				document.getElementById("moveDocument_button").style.display="block";
			});
			
			droppableFolders_container.append(createFolderTree(items.folders, 0, true, id));
			

			
	
		}
		
	}

	function deleteFolder(id, name) {
		var form = new FormData();
		form.append("folderId", id);
		handleConfirmationModal("Press ok to delete " + name + " folder", function(form) {
			makeCall("POST", "DeleteFolder", form, function(request) {
				if (request.status == 200) {
					showHomePage();
				} else {
					handleErrorMessageModal(request.responseText);
				}

			});
		}, form);
	}
	function deleteDocument(id, name) {
		var form = new FormData();
		form.append("documentId", id);
		handleConfirmationModal("Press ok to delete " + name + " document", function(form) {
			makeCall("POST", "DeleteDocument", form, function(request) {
				if (request.status == 200) {
					var message = JSON.parse(request.responseText);
					showContentPage(message.id, message.name,message.date);
				} else {
					handleErrorMessageModal(request.responseText);
				}

			});
		}, form);
	}



	function refreshFolderTree() {
		folderTreeContainer.innerHTML = "";
		makeCall("GET", "GetItems", null, function(request) {
			if (request.status == 200) {
				items = JSON.parse(request.responseText);
				fillFoldersContainer(items);

			} else {
				handleErrorMessageModal(request.responseText);
			}
		});
	}

	function handleFolderFormModal(parentFolderId) {
		const span = document.getElementById("close_addFolderModal");
		const parentFolder = document.getElementById("folderId");
		parentFolder.value = parentFolderId;
		folder_modal.style.display = "block";
		span.addEventListener("click", () => {
			folder_modal.style.display = "none";
		});
		window.onclick = function(event) {
			if (event.target == folder_modal) {
				folder_modal.style.display = "none";
			}
		}
	}
	function handleDocumentFormModal(parentFolderId) {
		const span = document.getElementById("close_addDocumentModal");
		const parentFolder = document.getElementById("documentFolderId");
		parentFolder.value = parentFolderId;
		document_modal.style.display = "block";
		span.addEventListener("click", () => {
			document_modal.style.display = "none";
		});
		window.onclick = function(event) {
			if (event.target == document_modal) {
				document_modal.style.display = "none";
			}
		}
	}

	function handleErrorMessageModal(message) {
		const span = document.getElementById("close_errorMessageModal");
		const div = document.getElementById("error_message");
		div.textContent = message;
		errorMessage_modal.style.display = "block";
		span.addEventListener("click", () => {
			errorMessage_modal.style.display = "none";
			div.textContent = "";

		});
		window.onclick = function(event) {
			if (event.target == errorMessage_modal) {
				errorMessage_modal.style.display = "none";
				div.textContent = "";
			}
		}
	}
	
	function handleFolderModal(folder) {
		const span = document.getElementById("close_folderModal");
		var div = document.getElementById("folderDetailModal");
		var name_p = document.getElementById("folderName");
		var date_p = document.getElementById("folderDate");
		name_p.textContent = folder.name;
		date_p.textContent = folder.date;
		div.style.display = "block";
		span.addEventListener("click", (e) => {
			e.preventDefault();
			div.style.display = "none";
		});
		window.onclick = function(e) {
			if (e.target == div) {
				div.style.display = "none";
			}
		};

	}
	
	
	function handleDocumentContentModal(doc) {
		const span = document.getElementById("close_documentModal");
		var div = document.getElementById("documentContentModal");
		var name_p = document.getElementById("documentName");
		var type_p = document.getElementById("documentType");
		var description_p = document.getElementById("documentDescription");
		var date_p = document.getElementById("documentDate");
		name_p.textContent = doc.name;
		type_p.textContent = doc.type;
		description_p.textContent = doc.description;
		date_p.textContent = doc.date;
		div.style.display = "block";
		span.addEventListener("click", (e) => {
			e.preventDefault();
			div.style.display = "none";
		});
		window.onclick = function(e) {
			if (e.target == div) {
				div.style.display = "none";
			}
		};

	}

	function handleConfirmationModal(message, callBack, form) {
		const span = document.getElementById("close_confirmationModal");
		const div = document.getElementById("confirmation_message");
		var confirm = document.getElementById("confirm_button");
		var cancel = document.getElementById("cancel_button");

		span.removeEventListener("click", span._clickHandler);
		confirm.removeEventListener("click", confirm._clickHandler);
		cancel.removeEventListener("click", cancel._clickHandler);
		window.removeEventListener("click", window._clickHandler);

		div.textContent = message;

		confirmation_modal.style.display = "block";
		span._clickHandler = (e) => {
			e.preventDefault();
			confirmation_modal.style.display = "none";
			div.textContent = "";
		};
		confirm._clickHandler = (e) => {
			e.preventDefault();
			confirmation_modal.style.display = "none";
			div.textContent = "";
			callBack(form);
		};
		cancel._clickHandler = (e) => {
			e.preventDefault();
			confirmation_modal.style.display = "none";
			div.textContent = "";
		};
		window._clickHandler = (e) => {
			e.preventDefault();
			if (e.target == confirmation_modal) {
				confirmation_modal.style.display = "none";
				div.textContent = "";
			}
		};

		span.addEventListener("click", span._clickHandler);
		window.addEventListener("click", window._clickHandler)
		confirm.addEventListener("click", confirm._clickHandler);
		cancel.addEventListener("click", cancel._clickHandler);
	}






})();