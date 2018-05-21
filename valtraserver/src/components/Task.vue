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
                    <div class="brdcast">
                            <b-input id="username" class="usernameTask" placeholder="type username" v-model="name"></b-input>                         
                    <!-- p style="white-space: pre-line;">{{ msgText }}</p-->
                    <br>
                    <textarea id="message" v-model="msgText" placeholder="Message to broadcast"></textarea>
                    <br><br/>
                    <button class="sendButton" v-on:click="sendMessage">Add Task</button>
                    </div>
                </div>
            </section>
        </div>
    </div>
</template>

<script>
import firebase from 'firebase'

const database = firebase.database()
export default {
  name: "Task", 
  data() {
    return {
        msgText: ''
    };
  },

  methods:{
    sendMessage: function() {
       var msg = document.getElementById("message").value
       var usrname = document.getElementById("username").value
       document.getElementById("message").value = "";
       document.getElementById("username").value = "";
       msg = msg.trim()
       if(msg.length > 0){
           database.ref('/Tasks/Name').set(usrname);
           database.ref('/Tasks/Task').set(msg);
          // database.ref('Broadcast/Message/Current').set(msg);
          // database.ref('Broadcast/Message/Check').set("true");          
            //alert(msg);
                    this.$toast.open({
                    duration: 3000,    
                    message: 'Task added for user !',
                    type: 'is-success'
                })
             }
        else{
            this.$toast.open({
                duration: 2000,
                message: 'Oops. cannot add empty task !',
                type: 'is-danger'
            })
        }     
        
    },
    logout: function() {
        firebase.auth().signOut().then(() => {
        this.$router.replace('login')
            })
    },
    gotoHome: function() {
        this.$router.replace('admin')
    }
  }
};
</script>