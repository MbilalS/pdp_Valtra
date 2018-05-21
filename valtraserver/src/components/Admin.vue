<template>
  <div>
    <section class="hero is-bold animated fadeIn bgcolor">
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
            </div>
            <div class="navbar-menu">
              <div class="navbar">
                <a class="navbar"> <!-- is-active -->
                  <span class="navbar-item bgcolor">
                    <a class="button size" v-on:click="gotoHome">Home</a>
                  </span>
                </a>
                <a class="navbar">
                  <span class="navbar-item bgcolor">
                    <a class="button size" v-on:click="gotoBroadcast">Broadcast</a>
                  </span>
                </a>
                <a class="navbar">
                  <span class="navbar-item bgcolor">
                    <a class="button size" v-on:click="gotoTask">Task</a>
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
      <div class="hero-body" style="margin-top: -5px">
        <div class="container">
          <!-- <h2 class="subtitle">
            Subtitle
          </h2> -->
                <div class="imageContainer" v-for="(user, user_index) in users" :key="user_index">
                    <p class="center">
                      <a @click="loadMap(user.Latitude,user.Longitude)"> <img class="pic" :src=user.PhotoUrl> </a>  
                      <br/> <br/>              
                      <p>{{user.Name}}</p>
                      <!-- <p> <a @click="loadMap(user.Latitude,user.Longitude)"><button class="location">Location</button></a> </p> -->
                      <p >{{user.HeartRate}}</p>
                      <p :id=user.Status>{{user.Status}}</p>
                    </p>
                </div>
        </div>
      </div>
      <v-button></v-button>
      <br/>
    </section>
    </div>
  </div>
</template>
<script>

import firebase from 'firebase'
import Button from './Button'

var config = {
  apiKey: "AIzaSyAn6m6RfMQFhhPe3hHIAqz4yMySBeuvMC8",
  authDomain: "valtra-5c843.firebaseapp.com",
  databaseURL: "https://valtra-5c843.firebaseio.com",
  projectId: "valtra-5c843",
  storageBucket: "valtra-5c843.appspot.com",
  messagingSenderId: "460566192298"
};    

firebase.initializeApp(config);

const database = firebase.database()

export default {
  name: 'Admin',
  components: {
  'v-button': Button
  },
  data: function() {
    return {
      users: []
    }
  },
  mounted: function() {
    console.log("mounted")
    // database.ref('Users').on('child_added', snapshot => this.users.push({name: snapshot.val().Name, location= snapshot.val().Location, status: snapshot.val().Status}))
    database.ref('Users').on('child_added', snapshot => this.users.push(snapshot.val())) 
  },
  methods: {
    logout: function() {
      firebase.auth().signOut().then(() => {
        this.$router.replace('login')
      })
    },
    // setUsers: function() {
    //   console.log("setUsers")
    //   this.users.push({Location: this.location, Name: this.name, Status: this.status})
    // },
    created: function() {  
      database.ref('Users').on('child_added', snapshot => this.users.push(snapshot.val()))
    },
    loadMap: function(lati, lon){
      //alert(lati + ", " + lon)
      this.$router.push({name:'Map',params:{lati: lati, lon: lon}})
    },
    gotoHome: function() {
        //this.$router.replace('admin')
        this.$router.go(this.$router.currentRoute)
        this.$router.replace('admin')
    },
    gotoBroadcast: function() {
        this.$router.replace('broadcast')
    },
    gotoTask: function() {
        this.$router.replace('task')
    }
  }
}

</script>