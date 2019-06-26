function getLocations() {
    $.ajax({
        type: "GET",
        url: "location/all",
        success: function (response) {
            var json = JSON.stringify(response, null, 4);
            var jsonparsed = JSON.parse(json);

            var list = $('#locations');
            list.empty().append(new Option('', '', null, null));
            for (var i = 0; i < jsonparsed.length; i++) {
                var option = new Option(jsonparsed[i].name + ' (' + jsonparsed[i].frostId + ')', jsonparsed[i].name + ' (' + jsonparsed[i].frostId + ')', null, null);
                option.setAttribute('data-value', JSON.stringify(jsonparsed[i], null, 4));
                list.append(option);
            }
            list.select2({
                placeholder: 'Choose a location',
                width: 'style',
                dropdownAutoWidth: true
            }).trigger('change');
        },
        error: function (e) {
            addToLog(e.responseText);
        }

    });

}

function createThing() {
    var $rows = $('#properties').find('tbody tr');
    var props = {},
        loc;
    for (var i = 0; i < $rows.length; i++) {
        props[$rows.eq(i).find('td:eq(0) input').val()] = $rows.eq(i).find('td:eq(1) input').val();
    }

    loc = $('#locations option:selected').attr('data-value');
    /*
    var options = document.getElementById('locations').childNodes;
    for (i = 0; i < options.length; i++) {
        if (options[i].value === $('#location').val()) {
            loc = options[i].getAttribute('data-value');
            break;
        }
    }
    */
    if (loc == null) {
        addToLog("Invalid Location (Must exist on the server)");
        alert("Invalid Location (Must exist on the server)");
        return;
    }

    var mydata = {
        name: $('#name').val(),
        description: $('#desc').val(),
        properties: props,
        location: JSON.parse(loc)
    };

    $.ajax({
        type: "POST",
        url: "thing/create",
        datatype: 'json',
        contentType: 'application/json',
        data: JSON.stringify(mydata),
        error: function (e) {
            $.notify({
                message: 'Thing could not be created, check the Log for errors'
            }, {
                allow_dismiss: true,
                type: 'danger',
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: 'animated fadeInDown',
                    exit: 'animated fadeOutUp'
                },
                z_index: 9000
            });
            addToLog(e.responseText);
        },
        success: function (e) {
            $.notify({
                message: 'Thing created.'
            },{
                allow_dismiss:true,
                type: 'info',
                placement: {
                    from: "top",
                    align: "left"
                },
                animate: {
                    enter: 'animated fadeInDown',
                    exit: 'animated fadeOutUp'
                }
            });
            addToLog('Thing created');
            closeModal('dialog');

            var text = e.name + ' (' + e.frostId + ')';
            var option = new Option(text, text, null, null);
            option.setAttribute('data-value', JSON.stringify(e, null, 4));

            $('#things').append(option).trigger('change');
            $('#things').val(text).trigger('select2:select');
        }
    });
}

function showLocationModal() {
    var $modal = $('#thingfooter').find('button:eq(0)');
    $modal.attr('onclick', 'createLocation()');
    $modal.html('Create Location');
    modal('thingdialog', 'location.html', null, 'Create a Location')
}