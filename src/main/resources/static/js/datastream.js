/*global modal, initSensor, addToLog, closeModal*/
function initDatastream() {
    var thing = getThing();
    getSensors();
    getOprops();
    var label = $("#strlabel");
    label.text(thing.name);
    label.attr("data-value", JSON.stringify(thing, null, 4));
}

function getSensors() {
	var url = document.getElementById("serverurlbox").innerText;
	var mydata = {
		frostUrlString: url	
	};
    $.ajax({
        type: "GET",
        url: "sensor/all",
        data: mydata,
        success: function (response) {
            var json = JSON.stringify(response, null, 4);
            var jsonparsed = JSON.parse(json);


            var list = $("#streamsensors");
            list.empty().append(new Option("", "", null, null));
            for (var i = 0; i < jsonparsed.length; i++) {
                var option = new Option(jsonparsed[i].name + " (" + jsonparsed[i].frostId + ")", jsonparsed[i].name + " (" + jsonparsed[i].frostId + ")", null, null);
                option.setAttribute("data-value", JSON.stringify(jsonparsed[i], null, 4));
                list.append(option);
            }
            list.select2({
                placeholder: "Choose a sensor",
                width: "style",
                dropdownAutoWidth: true
            }).trigger("change");
        },
        error: function (e) {
            addToLog(e.responseText);
        }
    });

}


function getOprops() {
	var url = document.getElementById("serverurlbox").innerText;
	var mydata = {
		frostUrlString: url	
	};
	
    $.ajax({
        type: "GET",
        url: "observedProperty/all",
        data: mydata,
        success: function (response) {
            var json = JSON.stringify(response, null, 4);
            var jsonparsed = JSON.parse(json);

            streamProperties = [];

            var stream = {};
            stream["id"] = "";
            stream["text"] = "";
            streamProperties.push(stream);

            for (var i = 0; i < jsonparsed.length; i++) {
                stream = {};
                stream["id"] = jsonparsed[i].name + " (" + jsonparsed[i].frostId + ")";
                stream["text"] = jsonparsed[i].name + " (" + jsonparsed[i].frostId + ")";
                stream["data-value"] = JSON.stringify(jsonparsed[i], null, 4);
                streamProperties.push(stream);
            }

            $("#streamUnits tbody").empty();
            addUnit();
        },
        error: function (e) {
            addToLog(e.responseText);
        }

    });

}

function getSensor() {
    return JSON.parse($("#streamsensors option:selected").attr("data-value"));
}

function createDS() {

    var $rows = $("#streamUnits").find("tbody tr");
    var types = [],
        units = [],
        props = [],
        obj;
    for (var i = 0; i < $rows.length; i++) {
        types.push($rows.eq(i).find(".selectStreamType option:selected").text());
        props.push(JSON.parse($rows.eq(i).find(".selectProperties option:selected").attr("data-value")));
        obj = {};
        obj["definition"] = $rows.eq(i).find("td:eq(4) input").val();
        obj["name"] = $rows.eq(i).find("td:eq(2) input").val();
        obj["symbol"] = $rows.eq(i).find("td:eq(3) input").val();
        units.push(obj);
    }

    var thing = JSON.parse($("#strlabel").attr("data-value"));
    var myDS = {
        name: $("#streamname").val(),
        description: $("#streamdesc").val(),
        observation_types: types,
        observedProperties: props,
        units_of_measurement: units,
        sensor: getSensor(),
        thing: thing
    };
    
    var url = document.getElementById("serverurlbox").innerText;
    
    var mydata = {
    	entity: myDS,
    	string: url
    };
    
    
    $.ajax({
        type: "POST",
        url: "datastream/create",
        datatype: "json",
        contentType: "application/json",
        data: JSON.stringify(mydata),
        error: function (response) {
            $.notify({
                message: "Datastream could not be created, check the Log for errors"
            }, {
                allow_dismiss: true,
                type: "danger",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                }
            });
            addToLog(response.responseText);
        },
        success: function (e) {
            $.notify({
                message: "Datastream created."
            },{
                allow_dismiss:true,
                type: "info",
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: "animated fadeInDown",
                    exit: "animated fadeOutUp"
                }
            });
            addToLog("Datastream created.");
            closeModal("dialog");

            var text = e.name + " (" + e.frostId + ")";
            var stream = {};
            stream["id"] = text;
            stream["text"] = text;
            stream["data-value"] = JSON.stringify(e, null, 4);
            streamData.push(stream);

            var option = new Option(text, text, null, null);
            option.setAttribute("data-value", JSON.stringify(e, null, 4));
            $("#datastreams").find("select").each(function () {
                $(this).append(option).trigger("change");
            });
        }
    });
}

var streamProperties = [];
var streamTypes = [{
    id: "",
    text: ""
},
    {
        id: "0",
        text: "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement (Double)"
    },
    {
        id: "1",
        text: "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation (URL)"
    },
    {
        id: "2",
        text: "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation (Integer)"
    },
    {
        id: "3",
        text: "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation (Boolean)"
    },
    {
        id: "4",
        text: "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation (Any)"
    }];

function addUnit() {
    $("#streamUnits tbody")
        .append($("<tr>")
            .append($("<td>")
                .append($("<select>")
                    .attr("class", "selectProperties")
                    .attr("style", "width:150px")
                )
            )
            .append($("<td>")
                .append($("<select>")
                    .attr("class", "selectStreamType")
                    .attr("style", "width:150px")
                )
            )
            .append($("<td>")
                .append($("<input>")
                    .attr("size", 15)
                    .attr("type", "text")
                )
            )
            .append($("<td>")
                .append($("<input>")
                    .attr("size", 15)
                    .attr("type", "text")
                )
            )
            .append($("<td>")
                .append($("<input>")
                    .attr("size", 15)
                    .attr("type", "text")
                )
            )
            .append($("<td>")
                .append($("<button>")
                    .attr("class", "btn btn-secondary")
                    .attr("onclick", "delRow($(this))")
                    .attr("style", "width: auto;")
                    .html("<span class='fas fa-minus'></span>")
                )
            )
        );

    $(".selectProperties").last().select2({
        data: streamProperties,
        placeholder: "Choose a Property",
        width: "style",
        dropdownAutoWidth: true
    }).trigger("change").on("select2:select", function (e) {
        $(this).find("option:selected").attr("data-value", e.params.data["data-value"]);
    });

    $(".selectStreamType").last().select2({
        data: streamTypes,
        placeholder: "Choose a datatype",
        width: "style",
        dropdownAutoWidth: true
    }).trigger("change").on("select2:select", function (e) {
        $(this).find("option:selected").attr("data-value", e.params.data["data-value"]);
    });
}

function showSensorModal(url) {
    var $modal = $("#dsfooter").find("button:eq(0)");
    $modal.attr("onclick", "createSensor()");
    $modal.html("Create Sensor");
    modal("dsdialog", url, initSensor, "Create a Sensor");
}

function showObsPropModal(url) {
    var $modal = $("#dsfooter").find("button:eq(0)");
    $modal.attr("onclick", "createObsprop()");
    $modal.html("Create Property");
    modal("dsdialog", url, null, "Create an Observed Property");
}