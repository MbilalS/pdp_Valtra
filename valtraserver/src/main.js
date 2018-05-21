// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import firebase from 'firebase'
import animated from 'animate.css'
import Buefy from 'buefy'
import 'buefy/lib/buefy.css'
import './assets/css/custom.css'
import moment from "moment"
import * as VueGoogleMaps from 'vue2-google-maps'
 
Vue.use(Buefy)

Vue.use(VueGoogleMaps, {
  load: {
    key: 'AIzaSyBXklEJWn5H5ldJx-EVTbAnkHT_7RruaQw'
  }
})

Vue.config.productionTip = false

new Vue({
      el: '#app',
      template: '<App/>',
      components: { App },
      router
    })