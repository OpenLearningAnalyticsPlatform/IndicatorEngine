/*
 * Open Learning Analytics Platform (OpenLAP) : Indicator Engine
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Created by Tanmaya Mahapatra on 16-03-2015.
 */

function createRequest() {
    var request;
    if (window.XMLHttpRequest) {
        request = new XMLHttpRequest();
    }
    else {
        request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return request;
}

function checkPreExistingUserName(){
    var request = createRequest();
    var userNameEntered = document . getElementById("usernamesignup").value;
    var url ="/checkusername?username="+escape(userNameEntered);
    request.open("GET",url,true);
    request.onreadystatechange=function(){alert_duplicateUserName(request)};
    request.send(null);

}

function alert_duplicateUserName(request) {
    if (request.readyState == 4) {
        if (request.status == 200) {
            if (request.responseText == "true")
            {
                alert("Duplicate User Name");
                document.getElementById("usernamesignup").value=null;
            }

        }
    }
}

function checkPreExistingEmailID(){
    var request = createRequest();
    var emailEntered = document . getElementById("emailsignup").value;
    var url ="/checkemail?email="+escape(emailEntered);
    request.open("GET",url,true);
    request.onreadystatechange=function(){alert_duplicateEmail(request)};
    request.send(null);

}

function alert_duplicateEmail(request) {
    if (request.readyState == 4) {
        if (request.status == 200) {
            if (request.responseText == "true")
            {
                alert("Duplicate Email ID");
                document.getElementById("emailsignup").value=null;
            }

        }
    }
}

function checkPassword() {
    var password = document . getElementById("passwordsignup").value;
    var confirmpassword = document . getElementById("passwordsignup_confirm").value;
    if (!(password === confirmpassword)){
        alert("Passwords Do not Match");
        document.getElementById("passwordsignup").value=null;
        document.getElementById("passwordsignup_confirm").value=null;

    }
}

// function isValidDate(date)
// {
//     var matches = /^(\d{2})[-](\d{2})[-](\d{4})$/.exec(date);
//     if (matches == null) return false;
//     var m = matches[2];
//     var d = matches[1] - 1;
//     var y = matches[3];
//     var composedDate = new Date(y, m, d);
//     return composedDate.getDate() == d &&
//         composedDate.getMonth() == m &&
//         composedDate.getFullYear() == y;
// }

// function checkDate() {
//     var dateEntered = document . getElementById("dob").value;
//     if(!isValidDate(dateEntered)){
//         alert("Invalid Date Format. Please Use DD-MM-YYYY format");
//         document . getElementById("dob").value = null;
//     }
// }