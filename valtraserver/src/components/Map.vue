<template>
    <div>
        <section class="hero is-bold animated slideInDown bgcolor">
            <div class="hero-body">
                <div class="container">
                    <h1 class="title textcolor">
                        VALTRA
                    </h1>
                </div>
            </div>
        </section>
        <div>
            <section class="hero is-bold animated slideInUp is-fullheight bodybgcolor">
                <!-- Hero head: will stick at the top -->
                <div class="hero-head">
                    <nav class="navbar">
                    <div class="container">
                        <div class="navbar-brand">
                        <span class="navbar-burger burger" data-target="navbarMenuHeroA">
                            <span></span>
                            <span></span>
                            <span></span>
                        </span>
                        </div>
                        <div id="navbarMenuHeroA" class="navbar-menu">
                        <div class="navbar">
                            <a class="navbar"> <!-- is-active -->
                            <span class="navbar-item bgcolor">
                                <a class="button size" v-on:click="gotoHome">Home</a>
                            </span>
                            </a>
                        </div>
                        <div class="navbar-end">
                            <span class="navbar-item bgcolor">
                            <button class="button" v-on:click="logout">Logout</button>
                            </span>
                        </div>
                        </div>
                    </div>
                    </nav>
                </div>

                <!-- Hero content: will be in the middle -->
                <div class="hero-body">
                        <gmap-map
                        :center="center"
                        :zoom="17"
                        style="width:100%;  height: 500px;"
                        >
                        <gmap-marker
                            :key="index"
                             v-for="(m, index) in markers"
                            :position="m.position"
                            @click="center=m.position"
                        ></gmap-marker>
                        </gmap-map>
                </div>
            </section>
        </div>
    </div>
</template>

<script>
import firebase from 'firebase'
export default {
  name: "Map",  //Check if this name is correct
  props: ["lati", "lon"],
  data() {
    return {
      // default to Montreal to keep it simple
      center: { lat: 45.508, lng: -73.587 },
      markers: [],
      currentPlace: null
    };
  },

  mounted() {
    this.geolocate();
    this.addMarker();
    //alert(this.lati)
  },

  methods:{

    logout: function() {
        firebase.auth().signOut().then(() => {
        this.$router.replace('login')
            })
    },
    gotoHome: function() {
        this.$router.replace('admin')
    },
    addMarker: function() {
        const marker = {
          lat: this.lati, 
          lng: this.lon
        };
        this.markers.push({ position: marker });
        this.center = marker;
        this.currentPlace = null;
    },
    geolocate: function() {
      navigator.geolocation.getCurrentPosition(position => {
        this.center = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };
      });
    }
  }
};
</script>