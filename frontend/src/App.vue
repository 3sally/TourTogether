<template>
  <!-- <sidenav
    :custom_class="this.$store.state.mcolor"
    :class="[
      this.$store.state.isTransparent,
      this.$store.state.isRTL ? 'fixed-end' : 'fixed-start',
    ]"
    v-if="this.$store.state.showSidenav"
  /> -->
  <main
    class="main-content position-relative max-height-vh-100 h-100 border-radius-lg"
    :style="this.$store.state.isRTL ? 'overflow-x: hidden' : ''"
  >
    <!-- nav -->

    <navbar
      :class="[navClasses]"
      :textWhite="this.$store.state.isAbsolute ? 'text-white opacity-8' : ''"
      :minNav="navbarMinimize"
      v-if="this.$store.state.showNavbar"
      style="height: 90px"
    />
    <router-view />
    <app-footer v-show="this.$store.state.showFooter" />
  </main>
  <!-- <router-view /> -->
</template>

<script>
// import Sidenav from "./examples/Sidenav";
import Navbar from "@/examples/Navbars/Navbar.vue";
import AppFooter from "@/examples/Footer.vue";
// import axios from "axios";
// import { API_BASE_URL } from "@/config/index.js";
// import { reactive } from "vue";
// import { watch, onMounted } from "vue";
import { mapMutations } from "vuex";

export default {
  components: {
    // Sidenav,
    Navbar,
    AppFooter,
  },
  methods: {
    ...mapMutations(["navbarMinimize"]),
  },
  computed: {
    navClasses() {
      return {
        "position-sticky blur shadow-blur mt-0 left-auto top-1 z-index-sticky":
          this.$store.state.isNavFixed,
        "position-absolute px-4 mx-0 w-100 z-index-2":
          this.$store.state.isAbsolute,
        "px-0 mx-4 mt-0": !this.$store.state.isAbsolute,
      };
    },
  },
  beforeMount() {
    this.$store.state.isTransparent = "bg-transparent";
  },
  setup() {},
  // watch: {
  //   accessToken: function () { // 토큰이 변경 확인
  //     console.log(this.accessToken);
  //   },
  //   $route(to) { // 라우트 변경 될때 마다 확인하여 (로그인체크)
  //     this.accessToken = window.Kakao.Auth.getAccessToken();
  //     if (to.name != "Login" && to.name != "KakaoLoginCallback") {
  //       if (!this.accessToken) {
  //         console.log("Not logged in.");
  //         location.href = "/login";
  //       }
  //     }
  //   },
  // },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;
  height: 90px;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#nav a.router-link-exact-active {
  color: #42b983;
}
</style>
