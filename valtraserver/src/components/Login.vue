<template>
    <div class="login">
        <section class="hero is-bold animated fadeIn bgcolor">
            <div class="hero-body">
                <div class="container">
                    <h1 class="title textcolor">
                        VALTRA
                    </h1>
                    <h2 class="subtitle textcolor">
                        <br>Admin Panel
                    </h2>
                </div>
            </div>
        </section>
        <br><br>
        <div class="padding">
            <b-field label="Email">
                <b-input placeholder="Email" v-model="email"
                    type="email"
                    icon="email">
                </b-input>
            </b-field>
            <b-field label="Password">
                <b-input type="password" 
                    value="admin123"
                    password-reveal
                    v-model="password"
                    placeholder="Password">
                </b-input>
            </b-field>
            <button class="button is-primary is-outlined" v-on:click="signIn">LogIn</button>
        </div>
    </div>
</template>

<script>

import firebase from 'firebase'

export default {
    name: "Login",
    data: function() {
        return {
            email: '',
            password: ''
        }
    },
    methods: {
        signIn: function() {
        firebase.auth().signInWithEmailAndPassword(this.email, this.password).then(
          (user) => {
            this.$router.replace('admin')
            this.$toast.open({
                    message: 'you are now login',
                    type: 'is-success'
                })
          },
          (err) => {
            this.$toast.open({
                    duration: 5000,
                    message: 'Oops. Email or Password incorrect',
                    position: 'is-bottom',
                    type: 'is-danger'
                })
          }
        );
      }
    }
}
</script>