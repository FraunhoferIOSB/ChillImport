/*global addToLog, modal, closeModal*/
function getLocations() {
	var url = document.getElementById("serverurlbox").innerText;

	var valid = false;
	var mydata = {
		frostUrlString : url
	};
	if (url === "") {
		addToLog("FROST-URL can't be empty");
		alert("FROST-URL can't be empty");
		valid = false;
	} else {
		$.ajax({
			type : "GET",
			url : "location/all",
			data : mydata,
			success : function(response) {
				var json = JSON.stringify(response, null, 4);
				var jsonparsed = JSON.parse(json);

				var list = $("#locations");
				list.empty().append(new Option("", "", null, null));
				
				jsonparsed.forEach(function(val) {
					var option = new Option(val.name + " ("
							+ val.frostId + ")", val.name
							+ " (" + val.frostId + ")", null, null);
					option.setAttribute("data-value", JSON.stringify(
							val, null, 4));
					list.append(option);
				});
				/*
				for (const val of jsonparsed) {
					var option = new Option(val.name + " ("
							+ val.frostId + ")", val.name
							+ " (" + val.frostId + ")", null, null);
					option.setAttribute("data-value", JSON.stringify(
							val, null, 4));
					list.append(option);
				}
				*/
				
				list.select2({
					placeholder : "Choose a location",
					width : "style",
					dropdownAutoWidth : true
				}).trigger("change");
			},
			error : function(e) {
				addToLog(e.responseText);
			}
		});
	}

}

function createThing() {
	var $rows = $("#properties").find("tbody tr");
	var props = {}, loc;
	for (var i = 0; i < $rows.length; i++) {
		props[$rows.eq(i).find("td:eq(0) input").val()] = $rows.eq(i).find(
				"td:eq(1) input").val();
	}

	loc = $("#locations option:selected").attr("data-value");

	if (loc === null) {
		addToLog("Invalid Location (Must exist on the server)");
		alert("Invalid Location (Must exist on the server)");
		return;
	}
	var url = document.getElementById("serverurlbox").innerText;

	var thing = {
		name : $("#name").val(),
		description : $("#desc").val(),
		properties : props,
		location : JSON.parse(loc)
	};

	var mydata = {
		entity : thing,
		string : url
	};

	$
			.ajax({
				type : "POST",
				url : "thing/create",
				datatype : "json",
				contentType : "application/json",
				data : JSON.stringify(mydata),
				error : function(e) {
					$
							.notify(
									{
										message : "Thing could not be created, check the Log for errors"
									}, {
										allow_dismiss : true,
										type : "danger",
										placement : {
											from : "top",
											align : "left"
										},
										animate : {
											enter : "animated fadeInDown",
											exit : "animated fadeOutUp"
										},
										z_index : 9000
									});
					addToLog(e.responseText);
				},
				success : function(e) {
					$.notify({
						message : "Thing created."
					}, {
						allow_dismiss : true,
						type : "info",
						placement : {
							from : "top",
							align : "left"
						},
						animate : {
							enter : "animated fadeInDown",
							exit : "animated fadeOutUp"
						}
					});
					addToLog("Thing created");
					closeModal("dialog");

					var text = e.name + " (" + e.frostId + ")";
					var option = new Option(text, text, null, null);
					option.setAttribute("data-value", JSON
							.stringify(e, null, 4));

					$("#things").append(option).trigger("change");
					$("#things").val(text).trigger("select2:select");
				}
			});
}

function showLocationModal() {
	var $modal = $("#thingfooter").find("button:eq(0)");
	$modal.attr("onclick", "createLocation()");
	$modal.html("Create Location");
	modal("thingdialog", "location.html", null, "Create a Location");
}