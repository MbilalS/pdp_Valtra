<template>
  <div class="google-map" :id="mapName"></div>
</template>
<script>
export default {
  name: 'Button',
  //props: ['name'],
  data: function () {
    return {
      mapName: this.name + "-map",
      markerCoordinates: [{
        latitude: 60.177456,
        longitude: 24.8127839
      },{
        latitude: 60.177456,
        longitude: 24.8137839
      },{
        latitude: 60.177156,
        longitude: 24.8117839
      },{
        latitude: 60.177132,
        longitude: 24.8337839
      },{
        latitude: 60.175182,
        longitude: 24.8317839
      },{
        latitude: 60.177182,
        longitude: 24.8317839
      },{
        latitude: 60.1855372,
        longitude: 24.822267  //purple
      },{
        latitude: 60.1850372,
        longitude: 24.812267
      }, {
        latitude: 60.1816559,
        longitude: 24.8317836
      }, {
        latitude: 60.1851182,
        longitude: 24.8367839
      }],
       map: null,
      bounds: null,
      markers: []
    }
  },
  mounted: function () {
    this.bounds = new google.maps.LatLngBounds();
    const element = document.getElementById(this.mapName)
    const mapCentre = this.markerCoordinates[0]
    const options = {
      center: new google.maps.LatLng(mapCentre.latitude, mapCentre.longitude)
    }
    this.map = new google.maps.Map(element, options);
    // Setup the different icons and shadows
    var iconURLPrefix = 'http://maps.google.com/mapfiles/ms/icons/';
    var icons = [
      iconURLPrefix + 'red-dot.png',
      iconURLPrefix + 'green-dot.png',
      iconURLPrefix + 'blue-dot.png',
      iconURLPrefix + 'orange-dot.png',
      iconURLPrefix + 'purple-dot.png',
      iconURLPrefix + 'pink-dot.png',      
      iconURLPrefix + 'yellow-dot.png',
      iconURLPrefix + 'red-dot.png',
      iconURLPrefix + 'blue-dot.png',
      iconURLPrefix + 'pink-dot.png' 
    ]
    var iconCounter = 0;
    this.markerCoordinates.forEach((coord) => {
      const position = new google.maps.LatLng(coord.latitude, coord.longitude);
      const marker = new google.maps.Marker({ 
        position,
        map: this.map,
        icon: icons[iconCounter ++]   //trying to show icon color
      });
    this.markers.push(marker)
      this.map.fitBounds(this.bounds.extend(position))
    });
  }
};
</script>