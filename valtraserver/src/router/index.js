import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Admin from '@/components/Admin'
import Login from '@/components/Login'
import Map from '@/components/Map'
import Broadcast from '@/components/Broadcast'
import Task from '@/components/Task'

import firebase from 'firebase'

Vue.use(Router)

let router = new Router({
  routes: [
    // {
    //   path: '*',
    //   redirect: '/login'
    // },
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/admin',
      name: 'Admin',
      component: Admin,
      // meta: {
      //   requiresAuth: true
      // }
    },
    {
      path: '/map',
      name: 'Map',
      component: Map,
      props: true,
      // meta: {
      //   requiresAuth: true
      // }
    },
   {
      path: '/broadcast',
      name: 'Broadcast',
      component: Broadcast,
      // meta: {
      //   requiresAuth: true
      // }
    },
   {
      path: '/task',
      name: 'Task',
      component: Task,
      // meta: {
      //   requiresAuth: true
      // }
    } 
  ]
})


// router.beforeEach((to, from, next) => {
//   let currentUser = firebase.auth().currentUser;
//   let requiresAuth = to.matched.some(record => record.meta.requiresAuth);

//   if (requiresAuth && !currentUser) next('login')
//   else if (!requiresAuth && currentUser) next('admin')
//   else next()
// })

export default router
