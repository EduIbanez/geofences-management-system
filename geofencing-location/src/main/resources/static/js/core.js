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

function doLogin(loginData) {
    $.ajax({
        url: "http://localhost:8080/api/users/auth",
        type: "POST",
        data: JSON.stringify(loginData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data, textStatus, jqXHR) {
            setJwtToken(data.token);
            window.location.replace("/home");
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

function getData() {
    getGeofences();
    getNotifications();
}

function getGeofences() {
    $.ajax({
        url: "http://localhost:8080/api/geofences",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: createAuthorizationTokenHeader(),
        success: function (data, textStatus, jqXHR) {
            var len = data.length;
            for (var i = 0; i < len; i++) {
               var message = data[i].geometry;
               console.log(data);
               console.log(data[i].id);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getNotifications() {
    $.ajax({
        url: "http://localhost:8080/api/notifications",
        type: "GET",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        headers: createAuthorizationTokenHeader(),
        success: function (data, textStatus, jqXHR) {
            var len = data.length;
            for (var i = 0; i < len; i++) {
               var message = data[i].rule.message;
               $('#table').append('<tr><td>'+message+'</td></tr>');
               console.log(message);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function createAuthorizationTokenHeader() {
    var token = getJwtToken();
    if (token) {
        return {"Authorization": token};
    } else {
        return {};
    }
}

// REGISTER EVENT LISTENERS =============================================================
$("#loginForm").submit(function (event) {
    event.preventDefault();

    var $form = $(this);
    var formData = {
        username: $form.find('input[name="username"]').val(),
        password: $form.find('input[name="password"]').val()
    };

    doLogin(formData);
});

$("#logoutButton").click(doLogout);