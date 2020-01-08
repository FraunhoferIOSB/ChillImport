/*global addToLog, closeModal*/

function createLocation() {

	var name = $("#locname").val();
	var desc = $("#locdescription").val();
	var loc = "[" + $("#loclocation").val() + "]";
	var url = document.getElementById("serverurlbox").innerText;

	if (url === "") {
		addToLog("FROST-URL can't be empty");
		alert("FROST-URL can't be empty");

	} else {

		var myloc = {
			name : name,
			description : desc,
			encoding_TYPE : "application/vnd.geo+json",
			location : "{\"type\": \"Point\", \"coordinates\": " + loc + "}"
		};

		var mydata = {
			entity : myloc,
			string : url
		};

		$
				.ajax({
					type : "POST",
					url : "location/create",
					datatype : "json",
					contentType : "application/json",
					data : JSON.stringify(mydata),
					error : function(e) {
						$
								.notify(
										{
											message : "Location could not be created, check the Log for errors"
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
							message : "Location created."
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
							},
							z_index : 9000
						});
						addToLog("Location created.");
						closeModal("thingdialog");

						var text = e.name + " (" + e.frostId + ")";
						var option = new Option(text, text, null, null);
						option.setAttribute("data-value", JSON.stringify(e,
								null, 4));

						$("#locations").append(option).trigger("change");
						$("#locations").val(text);
					}
				});
	}

}