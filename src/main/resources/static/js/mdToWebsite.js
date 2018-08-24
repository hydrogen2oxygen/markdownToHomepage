// Copyright by Pietro Lusso 2018

var md = {};

md.init = function () {
    console.log('Init MD Functionality');
    // Init buttons
    $('#buttonCreateNewWebsite').click(md.createNewWebsite);
    $('#buttonDeleteWebsite').click(md.deleteWebsite);

    // Init selectBox projects
    md.reloadWebsitesSelectBox();

    console.log('End of init');
    console.log('========================');
};

md.reloadWebsitesSelectBox = function () {

    $("#selectProjects").html("<option selected>Choose a project ...</option>");

    $.getJSON("website", function (data) {

        $.each(data, function () {
            $("#selectProjects").append('<option value="' + this.name + '">' + this.name + '</option>');
        });
    });
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
            md.reloadWebsitesSelectBox();
            $("#createNewWebsiteForm").trigger("reset");
            $(".collapse").collapse('hide');
            md.message("success","Project saved!");
        },
        dataType: "json"
    });

    return false;
};

md.deleteWebsite = function () {
    console.log('delete selected website');
    var selectedWebsiteName = $("#selectProjects option:selected").text();
    console.log(selectedWebsiteName);

    var webSite = {name:selectedWebsiteName};
    console.log(webSite);
    var data = JSON.stringify(webSite);

    $.ajax({
        type: "DELETE",
        url: "website/"+ encodeURI(selectedWebsiteName),
        async: true,
        success: function (response) {
            console.log(response);
            md.reloadWebsitesSelectBox();
            md.message("warning","Project '" + selectedWebsiteName + "' was deleted!");
        }
    });
};

md.message = function (messageType, messageText) {
    $('#messageDialog').html('<span class="badge badge-' + messageType +'">' + messageText + '</span>');
    $('#messageDialog').css('visibility', 'visible');
    $("html, body").animate({ scrollTop: 0 }, "slow");
    setTimeout(function () {
        $("#messageDialog").animate({
            opacity: 0.25,
            left: "+=50",
            height: "toggle"
        }, 3000, function () {
            $('#messageDialog').css('visibility', 'hidden');
        });
    },5000);
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