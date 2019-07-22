/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function GoogleMaps() {

    var Instance = this;

    Instance.init = function () {
        Instance.DEFAULT_LAT= 4.66891;
        Instance.DEFAULT_LON= -74.08287;
    };
    
    this.load= function(fieldName, value){
        var mapId= fieldName+"Map";
        var latitude= Instance.DEFAULT_LAT;
        var longitude= Instance.DEFAULT_LON;
        if(value!==undefined && value.indexOf(",")!==-1){
            latitude= parseFloat(value.split(",")[0]);
            longitude= parseFloat(value.split(",")[1]);
        }
        if (GBrowserIsCompatible()) {
            var map = new GMap2(document.getElementById(mapId));
            map.addControl(new GSmallMapControl());
            map.addControl(new GMapTypeControl());
            var center;

            center = new GLatLng(latitude, longitude);
            var zoom= (latitude+","+longitude !== Instance.DEFAULT_LAT+","+Instance.DEFAULT_LON)?16:13;
            map.setCenter(center, zoom);
            Instance.geocoder = new GClientGeocoder();
            var marker = new GMarker(center, {draggable: true});
            map.addOverlay(marker);
            //Instance.addMapEvents(fieldName, map, marker);
        }
    };
    
    this.showAddress= function(fieldName){
        var mapId= fieldName+"Map";
        var address= document.getElementById(fieldName+"Address").value;
        var map = new GMap2(document.getElementById(mapId));
        map.addControl(new GSmallMapControl());
        map.addControl(new GMapTypeControl());
        if (Instance.geocoder) {
            Instance.geocoder.getLatLng(address, function (point) {
                if (!point) {
                    alert(address + " not found");
                } else {
                    Instance.setLatLng(fieldName, point);
                    map.clearOverlays();
                    map.setCenter(point, 14);
                    var marker = new GMarker(point, {draggable: true});
                    map.addOverlay(marker);
                    Instance.addMapEvents(fieldName, map, marker);
                }
            });
        }
    };
    
    this.addMapEvents= function(fieldName, map, marker){
        GEvent.addListener(marker, "dragend", function () {
            var point = marker.getPoint();
            map.panTo(point);
            Instance.setLatLng(fieldName, point);
        });

        GEvent.addListener(map, "moveend", function () {
            map.clearOverlays();
            var center = map.getCenter();
            var marker = new GMarker(center, {draggable: true});
            map.addOverlay(marker);
            Instance.setLatLng(fieldName, center);

            GEvent.addListener(marker, "dragend", function () {
                var point = marker.getPoint();
                map.panTo(point);
                Instance.setLatLng(fieldName, point);
            });
        });
    };
    
    this.setLatLng= function(fieldName, point){
        if(point!==undefined){
            var coordinates= point.lat().toFixed(5)+","+point.lng().toFixed(5);
            if(coordinates !== Instance.DEFAULT_LAT+","+Instance.DEFAULT_LON){
                var fields= document.getElementsByName(fieldName);
                fields[fields.length-1].value= point.lat().toFixed(5)+","+point.lng().toFixed(5);
            }
        }
    };

    Instance.init();
}
var googleMaps= new GoogleMaps();