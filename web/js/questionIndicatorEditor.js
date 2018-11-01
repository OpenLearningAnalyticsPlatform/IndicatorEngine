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
 * Created by Tanmaya Mahapatra on 26-06-2015.
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

// function populateSPA() {
//
//     populateSources();
//     populatePlatform();
//     populateAction();
// }

function FilterInit() {
    var request = createRequest();
    var url ="/indicators/initFilters";
    request.open("GET",url,false);
    request.onreadystatechange=function(){processReceivedFilters(request)};
    request.send(null);
}

function processReceivedFilters(request) {
    if (request.readyState == 4) {
        if (request.status == 200) {
            var parsedJSON = JSON.parse(request.responseText);
            console.log(parsedJSON);
            var attributeSpecificationType = document.getElementById("specificationType");
            removeOptions(attributeSpecificationType);
            for (var i=0;i< parsedJSON.length;i++) {
                var newOption = new Option(parsedJSON[i], parsedJSON[i]);
                attributeSpecificationType.appendChild(newOption);
            }
        }
    }

}
// function populateSources() {
//     request = createRequest();
//     var url ="/indicators/initSources";
//     request.open("GET",url,false);
//     request.onreadystatechange=function(){processReceivedSources(request)};
//     request.send(null);
// }

function processReceivedSources(request) {
    if (request.readyState == 4) {
        if (request.status == 200) {
            var parsedJSON = JSON.parse(request.responseText);
            var sources = document.getElementById("sourceSelection");
            removeOptions(sources);
            for (var i=0;i< parsedJSON.length;i++) {
                var newOption = new Option(parsedJSON[i], parsedJSON[i]);
                sources.appendChild(newOption);
            }
        }
    }
}

// function populatePlatform() {
//     request = createRequest();
//     var url ="/indicators/initPlatform";
//     request.open("GET",url,false);
//     request.onreadystatechange=function(){processReceivedPlatforms(request)};
//     request.send(null);
// }

function processReceivedPlatforms(request) {
    if (request.readyState == 4) {
        if (request.status == 200) {
            var parsedJSON = JSON.parse(request.responseText);
            var platforms = document.getElementById("PlatformSelection");
            removeOptions(platforms);
            for (var i=0;i< parsedJSON.length;i++) {
                var newOption = new Option(parsedJSON[i], parsedJSON[i]);
                platforms.appendChild(newOption);
            }
        }
    }
}

// function populateAction() {
//     request = createRequest();
//     var url ="/indicators/initAction";
//     request.open("GET",url,false);
//     request.onreadystatechange=function(){processReceivedActions(request)};
//     request.send(null);
// }

function processReceivedActions() {
    if (request.readyState == 4) {
        if (request.status == 200) {
            var parsedJSON = JSON.parse(request.responseText);
            var actions = document.getElementById("actionSelection");
            removeOptions(actions);
            for (var i=0;i< parsedJSON.length;i++) {
                var newOption = new Option(parsedJSON[i], parsedJSON[i]);
                actions.appendChild(newOption);
            }
        }
    }
}
