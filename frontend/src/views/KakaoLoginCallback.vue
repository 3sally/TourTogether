<template>
  <div class="py-4 container-fluid" ref="pdfarea">
    <div class="row">
      <div class="col-lg-7 mb-lg-0 mb-4">
        <div class="card">
          <div class="card-body p-3">
            <div class="row">
              <div class="d-flex flex-column h-100">
                <!-- 관광지 이미지 carousel 부분 start-->
                <div
                  id="carouselExampleSlidesOnly"
                  class="carousel slide"
                  data-bs-ride="carousel"
                >
                  <div class="carousel-inner">
                    <div class="carousel-item active">
                      <img
                        src="../assets/img/carousel_image1.jpeg"
                        class="d-block w-100 rounded"
                      />
                    </div>
                    <div class="carousel-item">
                      <img
                        src="../assets/img/carousel_image2.jpeg"
                        class="d-block w-100 rounded"
                      />
                    </div>
                    <div class="carousel-item">
                      <img
                        src="../assets/img/carousel_image3.jpeg"
                        class="d-block w-100 rounded"
                      />
                    </div>
                  </div>
                </div>
                <!-- 관광지 이미지 carousel 부분 end-->
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 일정 짜러 가기 -->
      <div class="col-lg-5">
        <div class="card h-100 p-3">
          <div
            class="overflow-hidden position-relative border-radius-lg bg-cover h-100"
          >
            <span class="mask bg-gradient-dark"></span>
            <div
              class="card-body position-relative z-index-1 d-flex flex-column h-100 p-3"
              style="background-color: rgb(171 200 231)"
            >
              <h1 class="text-white font-weight-bolder mb-4 pt-2">✈️</h1>
              <p class="text-white">
                친구들과 실시간으로 소통하며 여행 계획을 짜고 싶은 사람!<br />
                다른 사람들의 기깔나는 여행 계획을 추천받고 싶은 사람! <br />
                여행지는 정했지만 어디 가야할지 도저히 모르겠는 사람!
                <br /><br />
                Tour Together을 통해 실시간으로 소통하고,<br />
                인기있는 여행계획을 둘러보고, 관광지를 추천받으세요!
                <br />
                당신의 완벽한 여행을 위해 Tour Together가 도와줍니다.
              </p>
              <a
                class="text-white text-sm font-weight-bold mb-0 icon-move-right mt-auto"
                href="javascript:;"
              >
                <h4>
                  <button
                    type="button"
                    class="btn btn-primary"
                    data-bs-toggle="modal"
                    data-bs-target="#modal"
                  >
                    일정 짜기 START
                  </button>
                </h4>
              </a>
            </div>
          </div>
        </div>
      </div>
      <!-- Modal -->
      <div
        class="modal fade"
        id="modal"
        tabindex="-1"
        aria-labelledby="modalLabel"
        aria-hidden="true"
      >
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="modalLabel">
                당신의 일정에 제목을 정해주세요.
              </h5>
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Close"
              ></button>
            </div>
            <form @submit.prevent="createBoard">
              <div class="modal-body">
                <input
                  type="text"
                  class="form-control"
                  v-model="boardName"
                  placeholder="여행 제목을 입력해주세요"
                  required
                  @keyup.enter="createBoard"
                />
              </div>
              <div class="modal-footer">
                <button
                  type="button"
                  class="btn btn-secondary"
                  data-bs-dismiss="modal"
                >
                  Close
                </button>
                <button type="submit" class="btn btn-primary">START</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <!-- 유형 선택하기 -->
    <div class="mt-4 row">
      <div class="mb-4 col-lg-12 mb-lg-0">
        <h4>여행지 추천</h4>
        <div class="card z-index-2">
          <div class="p-3 card-body">
            <button
              type="button"
              class="btn btn-secondary"
              @click="exportToPDF"
            >
              PDF로 내보내기
            </button>
            <!-- <div data-html2canvas-ignore="true">
              출력하지 않고 싶은 영역은 태그에 'data-html2canvas-ignore'
              attribute를 넣어주면된다.
            </div> -->

            <div class="col-lg-4 col-sm-8">
              <board-category />
            </div>
            <br />
          </div>
          <div v-for="(board, index) in boards" :key="index">
            <div class="col-lg-6 col-sm-8">
              {{ board.boardId }} // {{ board.boardName }} //
              {{ board.boardLikesCount }}
              <img
                v-if="this.favoriteBoardId.includes(board.boardId)"
                src="@/assets/img/full_heart.png"
                width="30"
                @click="likeCancel(board.boardId)"
              />
              <img
                v-if="!this.favoriteBoardId.includes(board.boardId)"
                src="@/assets/img/empty_heart.png"
                width="30"
                @click="likeClick(board.boardId)"
              />
            </div>
            <div class="col-lg-6 col-sm-8"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import BoardCategory from "./components/BoardCategory.vue";
import { API_BASE_URL } from "@/config/index.js";
import html2pdf from "html2pdf.js";

import { mapMutations, mapState } from "vuex";
import { useStore } from "vuex";
import { reactive } from "vue";
// import { onBeforeMount } from "vue";
import { onMounted } from "vue";
import { onUpdated } from "vue";
import axios from "axios";
import router from "@/router";

const userStore = "userStore";
const boardStore = "boardStore";

export default {
  name: "dashboard",
  data() {
    return {
      boards: [],
      favoriteBoardId: [],
      boardName: "",
      boardRandom: "",
      // userId: "",
      propTitle: "mypdf",
      stats: {
        iconBackground: "bg-gradient-success",
      },
      sales: {},
    };
  },
  computed: {
    ...mapState(userStore, ["userId"]),
    ...mapState(boardStore, ["searchByCategoryBoards"]),
    ...mapState(boardStore, ["withWhom", "season", "area", "theme"]),
    // ...mapState(boardStore, ["boards"]),
  },
  watch: {
    searchByCategoryBoards() {
      this.boards = this.searchByCategoryBoards;
    },
  },
  created() {
    // this.getAllBoards();
    this.getUserFavoriteBoards();
  },
  methods: {
    ...mapMutations(boardStore, ["setAllBoards", "setSearchByCategoryBoards"]),
    // getAllBoards() {
    //   axios({
    //     method: "get",
    //     url: API_BASE_URL + "board",
    //   }).then((res) => {
    //     this.boards = res.data.boards;
    //     this.setAllBoards(res.data.boards);
    //   });
    // },
    getUserFavoriteBoards() {
      axios({
        method: "post",
        url: API_BASE_URL + "board/searchLikeBoardByUserId",
        data: {
          userId: this.userId,
        },
      }).then((res) => {
        for (var i = 0; i < res.data.myBoards.length; i++) {
          this.favoriteBoardId[i] = res.data.myBoards[i].boardId;
        }
      });
    },
    createBoard() {
      axios({
        method: "post",
        url: API_BASE_URL + "board/create",
        data: {
          boardName: this.boardName,
          userId: this.userId,
        },
      }).then((res) => {
        this.boardRandom = res.data.boardRandom;
        location.href = `/board/${this.boardRandom}`;
      });
    },
    likeClick(boardId) {
      axios({
        method: "patch",
        url: API_BASE_URL + "board/clickBoardLike",
        data: {
          boardId: boardId,
          userId: this.userId,
        },
      }).then((res) => {
        this.favoriteBoardId = res.data.favoriteBoardId;
        this.getListByCategory(
          this.withWhom,
          this.season,
          this.area,
          this.theme
        );
      });
    },
    likeCancel(boardId) {
      axios({
        method: "patch",
        url: API_BASE_URL + "board/cancelBoardLike",
        data: {
          boardId: boardId,
          userId: this.userId,
        },
      }).then((res) => {
        this.favoriteBoardId = res.data.favoriteBoardId;
        this.getListByCategory(
          this.withWhom,
          this.season,
          this.area,
          this.theme
        );
      });
    },
    getListByCategory(withWhom, season, area, theme) {
      axios({
        method: "post",
        url: API_BASE_URL + "board/searchByCategory",
        data: {
          categoryArea: area,
          categorySeason: season,
          categoryTheme: theme,
          categoryWithWhom: withWhom,
        },
      }).then((res) => {
        this.setSearchByCategoryBoards(res.data.boards);
      });
    },
    exportToPDF() {
      //window.scrollTo(0, 0);
      html2pdf(this.$refs.pdfarea, {
        margin: 0,
        filename: "document.pdf",
        image: { type: "jpg", quality: 0.95 },
        //	allowTaint 옵션추가
        html2canvas: {
          useCORS: true,
          scrollY: 0,
          scale: 1,
          dpi: 300,
          letterRendering: true,
          allowTaint: false, //useCORS를 true로 설정 시 반드시 allowTaint를 false처리 해주어야함
        },
        jsPDF: {
          orientation: "portrait",
          unit: "mm",
          format: "a4",
          compressPDF: true,
        },
      });
    },
  },

  setup() {
    const state = reactive({
      accessToken: window.Kakao.Auth.getAccessToken(),
    });
    const store = useStore();
    const getters = store.getters;

    const getParameterByName = (name) => {
      name = name.replace(/[[]/, "\\[").replace(/[\]]/, "\\]");
      var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
      return results == null
        ? ""
        : decodeURIComponent(results[1].replace(/\+/g, " "));
    };
    onUpdated(() => {});

    onMounted(() => {
      if (!getters["userStore/getUserId"]) {
        const code = getParameterByName("code");
        var details = {
          grant_type: "authorization_code",
          client_id: process.env.VUE_APP_KAKAO_RESTAPI_KEY,
          redirect_uri: "https://i6a105.p.ssafy.io/kakao-login-callback",
          // redirect_uri: "http://localhost:8080/kakao-login-callback",
          code: code,
        };
        var formBody = [];
        for (var property in details) {
          var encodedKey = encodeURIComponent(property);
          var encodedValue = encodeURIComponent(details[property]);
          formBody.push(encodedKey + "=" + encodedValue);
        }
        formBody = formBody.join("&");

        fetch("https://kauth.kakao.com/oauth/token", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=utf-8",
          },
          body: formBody,
        })
          .then((response) => response.json())
          .then((data) => {
            window.Kakao.Auth.setAccessToken(data.access_token);
            state.accessToken = window.Kakao.Auth.getAccessToken();
          })
          .then(() => {
            window.Kakao.API.request({
              url: "/v2/user/me",
              data: {
                property_keys: [
                  "properties.nickname",
                  "kakao_account.email",
                  "properties.profile_image",
                ],
              },
              success: function (response) {
                let email = null;
                if (
                  response.kakao_account.has_email &
                  !response.kakao_account.email_needs_agreement
                ) {
                  email = response.kakao_account.email;
                }
                let imageURL = null;
                if (response.properties.profile_image) {
                  imageURL = response.properties.profile_image;
                } else {
                  imageURL =
                    "https://tour-together--s3.s3.ap-northeast-2.amazonaws.com/%ED%81%AC%EB%AA%BD.png";
                }
                store.commit(
                  "userStore/setKakaoProfileImage",
                  response.properties.profile_image
                );
                axios({
                  method: "post",
                  url: API_BASE_URL + "user/login",
                  data: {
                    userLoginPlatform: "kakao",
                    userClientId: response.id,
                    userEmail: email,
                    userName: response.properties.nickname,
                    userProfileImage: imageURL,
                  },
                }).then((res) => {
                  let token = res.data.accessToken;
                  store.commit("userStore/setUserLoginPlatform", "kakao");
                  store.commit("userStore/setUserClientId", response.id);
                  store.commit("userStore/setAccessToken", token);
                  store.dispatch("userStore/getUserInfo");
                });
              },
              fail: function () {},
            });
          });
        router.push("/dashboard");
      }
    });
    return { state, getters };
  },
  components: {
    BoardCategory,
  },
};
</script>
