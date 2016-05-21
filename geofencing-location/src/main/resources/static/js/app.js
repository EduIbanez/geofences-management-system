// VARIABLES =============================================================
var TOKEN_KEY = "jwtToken"

// FUNCTIONS =============================================================
function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setJwtToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function removeJwtToken() {
    localStorage.removeItem(TOKEN_KEY);
}

var stompClient = null;

function connect() {
    var options = {
        headers: {
            "Authorization" : getJwtToken()
        }
    }
    var socket = new SockJS('http://localhost:8080/api/locations', options);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/positions', function(positions) {
            renderMessageOnMap(JSON.parse(positions.body));
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendLocation(location) {
    stompClient.send("/api/locations", {'content-type':'application/json'}, JSON.stringify({ type : "Point", coordinates : location}));
}

function starting() {
    disconnect();
    connect();
}

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

function findUserCurrentLocation(callback) {

	navigator.geolocation.getCurrentPosition(function(position){
        var longitude = position.coords.longitude;
        var latitude = position.coords.latitude;
        console.log('longitude .. '+longitude);
        console.log('latitude .. '+latitude);

        var location = new Array(longitude , latitude);
        callback(location);
    }, function(e){
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
	var latLng = new google.maps.LatLng(data.coordinates.coordinates[1], data.coordinates.coordinates[0]);
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

var timer = setTimeout(startingTimer, 5000);

function startingTimer() {
    findUserCurrentLocation(sendLocation);
    timer = setTimeout(startingTimer, 30000);
}

function abortTimer() {
    clearTimeout(timer);
}