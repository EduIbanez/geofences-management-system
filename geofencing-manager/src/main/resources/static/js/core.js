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
            window.location.replace("/user");
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

function postUser(userData) {
    $.ajax({
        url: "http://localhost:8080/api/users",
        type: "POST",
        data: JSON.stringify(userData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (data, textStatus, jqXHR) {
            setJwtToken(data.token);
            window.location.replace("/user");
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

$("#signUpForm").submit(function (event) {
    event.preventDefault();

    var date = new Date();
    var strDate = date.getFullYear() + "-" + (date.getMonth()+1) + "-" + date.getDate();
    
    var $form = $(this);
    var userData = {
        email: $form.find('input[name="username"]').val(),
        password: $form.find('input[name="password"]').val(),
        first_name: $form.find('input[name="first_name"]').val(),
        last_name: $form.find('input[name="last_name"]').val(),
        birthday: $form.find('input[name="birthday"]').val(),
        imei: "",
        geofences: [],
        enabled: true,
        role: "ROLE_USER",
        last_password_reset_date: strDate,
        notifications: []
    };

    postUser(userData);
});

$("#logout").click(doLogout);