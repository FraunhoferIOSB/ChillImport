/*global addToLog, closeModal*/
function initSensor() {
	var data = [ {
		id : "",
		text : ""
	}, {
		id : "application/pdf",
		text : "application/pdf"
	}, {
		id : "application/json",
		text : "application/json"
	}, {
		id : "text",
		text : "text"
	} ];

	$("#senEncTypes").select2({
		data : data,
		placeholder : "Choose an encoding type",
		width : "style",
		dropdownAutoWidth : true
	});
}

function createSensor() {

	var name = $("#senname").val();
	var desc = $("#sendescription").val();
	var encType = $("#senEncTypes").val();
	if (!encType || encType === "") {
		alert("Choose an encryption type.");
		return false;
	}
	var meta = $("#senmeta").val();

	var mySensor = {
		name : name,
		description : desc,
		encoding_TYPE : encType,
		metadata : meta
	};
	var url = document.getElementById("serverurlbox").innerText;

	var mydata = {
		entity : mySensor,
		string : url
	};
	$
			.ajax({
				type : "POST",
				url : "sensor/create",
				datatype : "json",
				contentType : "application/json",
				data : JSON.stringify(mydata),
				error : function(e) {
					$
							.notify(
									{
										message : "Sensor could not be created, check the Log for errors"
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
						message : "Sensor created."
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
					addToLog("Sensor created.");
					closeModal("dsdialog");

					var text = e.name + " (" + e.frostId + ")";
					var option = new Option(text, text, null, null);
					option.setAttribute("data-value", JSON
							.stringify(e, null, 4));

					var sensors = $("#streamsensors");
					sensors.append(option).trigger("change");
					sensors.val(text);
				}
			});
}