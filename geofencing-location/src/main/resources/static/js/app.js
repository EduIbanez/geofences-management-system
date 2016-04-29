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

var map = new google.maps.Map(document.getElementById('map-canvas'),mapOptions);

$("#messageForm").submit(function(event){
    event.preventDefault();
    $("#messageForm").mask("Sending Message ...");
    findUserCurrentLocation(saveMessage);
});

function findUserCurrentLocation(callback){

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

function saveMessage(location){

    var message = this.$('textarea#message').val();

    var url = "api/messages";
    var data = {message : message , location : location};
    $.ajax(url , {
        data : JSON.stringify(data),
        contentType : 'application/json',
        type : 'POST',
        success :function(){
            alert("Message Sent Successfully");
            renderMessageOnMap(data);
            $('#messageForm')[0].reset();
        },
        error :function(){
            alert("Message Delivery Failed");
        }
    });

    $("#messageForm").unmask();

}

function renderMessageOnMap(data){
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
    map.setZoom(6);
}