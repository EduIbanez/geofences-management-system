var mapOptions = {
			zoom : 2,
			center : new google.maps.LatLng(40.46366700000001,
					-3.7492200000000366),
			mapTypeControlOptions : {
				style : google.maps.MapTypeControlStyle.DROPDOWN_MENU
			},
			mapTypeId : google.maps.MapTypeId.ROADMAP,
			zoomControlOptions : {
				style : google.maps.ZoomControlStyle.SMALL
			}
		};

var map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);

$("#messageForm").submit(function(event) {
	event.preventDefault();
	$("#messageForm").mask("Sending Message ...");
	findUserCurrentLocation(sendLocation);
});

function findUserCurrentLocation(callback) {

	navigator.geolocation.getCurrentPosition(function(position){
        var longitude = position.coords.longitude;
        var latitude = position.coords.latitude;
        console.log('longitude .. '+longitude);
        console.log('latitude .. '+latitude);

        var location = new Array(longitude , latitude);
        callback(location);
    }, function(e){
        $("#messageForm").unmask();
        switch (e.code) {
            case e.PERMISSION_DENIED:
                alert('You have denied access to your position. You will ' +
                        'not get the most out of the application now.');
                break;
            case e.POSITION_UNAVAILABLE:
                alert('There was a problem getting your position.');
                break;
            case e.TIMEOUT:
                alert('The application has timed out attempting to get ' +
                        'your location.');
                break;
            default:
                alert('There was a horrible Geolocation error that has ' +
                        'not been defined.');
        }
    },
    { timeout: 45000 }

    );

}

function renderMessageOnMap(data) {
	var latLng = new google.maps.LatLng(data.location[1], data.location[0]);
	map.setCenter(latLng);
	var marker = new google.maps.Marker({
		position : latLng,
		animation : google.maps.Animation.DROP,
		html : "<p>" + data.message + "</p>"
	});

	var infoWindow = new google.maps.InfoWindow();
	google.maps.event.addListener(marker, 'click', function() {
		map.setCenter(marker.getPosition());
		infoWindow.setContent(this.html);
		infoWindow.open(map, this);
	});

	marker.setMap(map);
	map.setCenter(latLng);
	map.setZoom(4);
}

var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
}

function connect() {
    var socket = new SockJS('http://localhost:8080/api/locations');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/positions', function(positions) {
            showPosition(JSON.parse(positions.body).content);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendLocation(location) {
    stompClient.send("/api/locations", {}, JSON.stringify({ "geometry": {"type": "Point", "coordinates": location } }), function(location) {
        renderMessageOnMap(location);
        });
}

function showPosition(message) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    response.appendChild(p);
}