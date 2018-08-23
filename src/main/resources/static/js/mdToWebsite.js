// Copyright by Pietro Lusso 2018

var md = {};

md.init = function () {
    console.log('Init MD Functionality');
    // Init buttons
    $('#buttonCreateNewWebsite').click(md.createNewWebsite);

    // Init selectBox projects
    $.getJSON("website", function (data) {
        $.each(data, function () {
            $("#selectProjects").append('<option value="' + this.name + '">' + this.name + '</option>')
        });
    });

    console.log('End of init');
    console.log('========================');
};

md.createNewWebsite = function () {
    console.log('Create new website');
    var data = md.objectifyForm($("#createNewWebsiteForm").serializeArray());
    console.log(data);

    $.ajax({
        type: "POST",
        url: "website",
        data: data,
        async: true,
        success: function (response) {
            console.log(response);
        },
        dataType: "json"
    });

    return false;
};

/**
 * serialize data function
 * @param formArray
 */
md.objectifyForm = function (formArray) {

    var returnArray = {};
    for (var i = 0; i < formArray.length; i++) {
        returnArray[formArray[i]['name']] = formArray[i]['value'];
    }
    return returnArray;
};