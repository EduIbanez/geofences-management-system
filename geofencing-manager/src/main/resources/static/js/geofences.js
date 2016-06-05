// VARIABLES =============================================================
var TOKEN_KEY = "jwtToken";

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

function createAuthorizationTokenHeader() {
    var token = getJwtToken();
    if (token) {
        return {"Authorization": token};
    } else {
        return {};
    }
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
            var latitude = position.coords.latitude;
            var longitude = position.coords.longitude;
            console.log('latitude .. '+latitude);
            console.log('longitude .. '+longitude);

            var location = new Array(latitude, longitude);
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

var marker;

function renderMessageOnMap(data) {
    if (typeof marker !== 'undefined') {
        marker.setMap(null);
    }
    var latLng = new google.maps.LatLng(data[0], data[1]);
    map.setCenter(latLng);
    marker = new google.maps.Marker({
        position : latLng,
        animation : google.maps.Animation.DROP,
        html : "<p>You are here</p>"
    });

    marker.setMap(map);
    map.setCenter(latLng);
    map.setZoom(14);
}

function drawing() {
    var drawingManager = new google.maps.drawing.DrawingManager({
        drawingControl: true,
        drawingControlOptions: {
            position: google.maps.ControlPosition.TOP_CENTER,
            drawingModes: [
                google.maps.drawing.OverlayType.CIRCLE,
                google.maps.drawing.OverlayType.POLYGON,
                google.maps.drawing.OverlayType.RECTANGLE
            ]
        },
        circleOptions: {
            strokeColor: '#20B2AA',
            strokeOpacity: 0.8,
            fillColor: '#20B2AA',
            fillOpacity: 0.35,
            strokeWeight: 2,
            clickable: true,
            editable: true,
            zIndex: 1
        },
        polygonOptions: {
            strokeColor: '#20B2AA',
            strokeOpacity: 0.8,
            fillColor: '#20B2AA',
            fillOpacity: 0.35,
            strokeWeight: 2,
            clickable: true,
            editable: true,
            zIndex: 1
        },
        rectangleOptions: {
            strokeColor: '#20B2AA',
            strokeOpacity: 0.8,
            fillColor: '#20B2AA',
            fillOpacity: 0.35,
            strokeWeight: 2,
            clickable: true,
            editable: true,
            zIndex: 1
        }
    });
    drawingManager.setMap(map);

    google.maps.event.addListener(drawingManager, "overlaycomplete", function(event){
        if (event.type == google.maps.drawing.OverlayType.POLYGON) {
            overlayClickListener(event.overlay);
            var len = event.overlay.getPath().getLength();
            var coordinates = [];
            var init = null;
            for (var i = 0; i < len; i++) {
                var lat = event.overlay.getPath().getAt(i).lat();
                var lng = event.overlay.getPath().getAt(i).lng();
                if(i == 0) {
                    init = new Array(lat, lng);
                }
                coordinates.push(new Array(lat, lng));
            }
            coordinates.push(init);
            $('#vertices').val(JSON.stringify([coordinates]));
        }
    });
}
function overlayClickListener(overlay) {
    google.maps.event.addListener(overlay, "click", function(event){
        var len = overlay.getPath().getLength();
        var coordinates = [];
        var init = null;
        for (var i = 0; i < len; i++) {
            var lat = event.overlay.getPath().getAt(i).lat();
            var lng = event.overlay.getPath().getAt(i).lng();
            if(i == 0) {
                init = new Array(lat, lng);
            }
            coordinates.push(new Array(lat, lng));
        }
        coordinates.push(init);
        $('#vertices').val(JSON.stringify([coordinates]));
    });
}

function getGeofences() {
    $.ajax({
        url: "http://localhost:8080/api/geofences",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: createAuthorizationTokenHeader(),
        success: function (data, textStatus, jqXHR) {
            var geofences = [];
            for (var i = 0; i < data.length; i++) {
                var geo = [];
                var coordinates = data[i].geometry.coordinates[0];
                console.log(data[i].geometry.coordinates);
                for (var j = 0; j < coordinates.length; j++) {
                    geo.push({lat: coordinates[j][0], lng: coordinates[j][1]});
                }
                geofences.push(geo);
            }
            for (var j = 0; j < geofences.length; j++) {
                // Construct the polygon.
                var polygon = new google.maps.Polygon({
                    paths: geofences[j],
                    strokeColor: '#20B2AA',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#20B2AA',
                    fillOpacity: 0.35  });
                polygon.setMap(map);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function starting() {
    drawing();
    findUserCurrentLocation(renderMessageOnMap);
    getGeofences();
}

$("#mapForm").submit(function (event) {
    event.preventDefault();

    var $form = $(this);
    var geofence = $form.find('input[name="vertices"]').val();
    var geofenceData = {
        id: null,
        type: "Feature",
        properties: {},
        geometry: { "type" : "Polygon", "coordinates" : JSON.parse(geofence) },
        user: null,
        rules: []
    };

    postGeofence(geofenceData);
});

function postGeofence(geofenceData) {
    $.ajax({
        url: "http://localhost:8080/api/geofences",
        type: "POST",
        data: JSON.stringify(geofenceData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: createAuthorizationTokenHeader(),
        success: function (data, textStatus, jqXHR) {
            window.location.replace("/geofences");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 401) {
                console.log("Unauthorized request");
            } else {
                throw new Error("an unexpected error occured: " + errorThrown);
            }
        }
    });
}


function doLogout() {
    removeJwtToken();
}

$("#logout").click(doLogout);