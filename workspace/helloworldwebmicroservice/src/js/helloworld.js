var rid = 0;
var type = "";

function displayTable(data) {
	$("#tabledata").empty();
	var table = $("#tabledata");
	$.each(data, function(rowIndex, r) {
		var row = $("<tr/>");
		$.each(r, function(colIndex, c) { 
			if (rowIndex == 0) {
				if (colIndex == 'rid' || colIndex == 'message') {
					row.append($("<td width=\"33%\" />").text(c));
				} else {
					row.append($("<td width=\"34%\" />").text(c));
				}
			} else {
				row.append($("<td/>").text(c));				
			}
		});
		table.append(row);
	});

}

function reloadData() {
	$.ajax({
		url: restHostUrl,
		type: "GET",
		cache: false,
		success: function(dataraw) { 
			if (null == dataraw) {
				displayNotWorking();
				return;
			}

			displayWorking();
			// Display into a table.
			displayTable(dataraw);		
			$(helloworldform).validator('validate');	
		},
		error: function() {
			displayNotWorking();
		}
	});	
}

$(document).ready(function() {	
	displayLoading();
	reloadData();		
});

function displayLoading() {
	$("#loading").show();
	$("#notworking").hide();
	$("#working").hide();			
}

function displayWorking() {
	$("#loading").hide();
	$("#notworking").hide();
	$("#working").show();	
}

function displayNotWorking() {
	$("#loading").hide();
	$("#notworking").show();
	$("#working").hide();			
}

function displayError() {
	$(id).html("<div class='alert alert-danger'>");
	$(id + " > .alert-danger").html("<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;").append( "</button>");
	$(id + " > .alert-danger").append("<strong>Désolé, il semble que le serveur ne répond pas. </strong> Vérifier que le serveur est bien actif.");
	$(id + " > .alert-danger").append('</div>');
}

$('#helloworldform').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {	
		e.preventDefault();
	} else {
		var helloworldmessage = $("input#forhelloworldmessage").val();

		var helloworldjson = {message:helloworldmessage};

		$.ajax({
			url: restHostUrl,
			type: "POST",
			data : JSON.stringify(helloworldjson),	
			cache: false,
			contentType: "application/json;",
			success: function() {  
				$("input#forhelloworldmessage").val("");
				reloadData();
			},
			error: function(jqXHR, textStatus, errorThrown) {
				displayError();
			}
		});

		e.preventDefault();
	}
})

$("#refresh").click( function() {
	reloadData();
}
);

