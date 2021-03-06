<template>
  <div id="main-container" class="container" style="padding: 0; margin: 0">
    <button v-if="!session" class="c-btn w-btn-green2" @click="joinSession()">
      Join!
    </button>
    <!-- 비디오설정버튼 -->
    <div class="d-flex flex-row">
      <div v-if="session">
        <div>
          <button
            @click="toggleVideo()"
            class="video-ctr-btn"
            title="비디오 on/off"
          >
            📷
          </button>
        </div>
        <div>
          <button
            @click="toggleAudio()"
            class="video-ctr-btn"
            title="마이크 on/off"
          >
            🎙️
          </button>
        </div>
        <div>
          <button
            class="video-ctr-btn"
            @click="changeChatToggle()"
            styl="bottom:120px; left: 0"
          >
            💬
          </button>
        </div>
      </div>
    </div>
    <div>
      <div v-if="session" id="session">
        <!-- 비디오 -->
        <div id="video_wrapper">
          <user-video :stream-manager="mainStreamManager" class="box" />
          <user-video
            v-for="sub in subscribers"
            :key="sub.stream.connection.connectionId"
            :stream-manager="sub"
            @click="updateMainVideoStreamManager(sub)"
            class="box"
          >
            {{ sub.stream.connection.connectionId }}
          </user-video>
        </div>
      </div>
    </div>

    <draggable-div
      v-show="session && this.chatToggle"
      id="plan_wrap"
      class="chattingPart"
      style="overflow: auto"
    >
      <template v-slot:header> 채팅 </template>
      <template v-slot:main>
        <!-- 채팅화면 -->
        <div id="chatting-wrapper">
          <ul id="chatting"></ul>

          <!-- 채팅입력 -->
        </div>
        <input
          @keyup.enter="submitChatting()"
          placeholder="메시지 입력"
          v-model="message"
          id="chattingInput"
          style="position: relative; width: 250px; width: 100%"
        />
      </template>
    </draggable-div>
  </div>
</template>

<script>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/components/UserVideo";
import store from "@/store";
const getters = store.getters;
import DraggableDiv from "@/components/DraggableDiv.vue";

axios.defaults.headers.post["Content-Type"] = "application/json";

// const OPENVIDU_SERVER_URL = "https://" + location.hostname + ":4443";
// 서버 배포 시
const OPENVIDU_SERVER_URL = "https://i6a105.p.ssafy.io:443";
const OPENVIDU_SERVER_SECRET = "twist";

export default {
  name: "webRTC",

  components: {
    UserVideo,
    DraggableDiv,
  },

  data() {
    return {
      OV: undefined,
      session: undefined,
      mainStreamManager: undefined,
      publisher: undefined,
      subscribers: [],
      message: undefined,
      mySessionId: window.location.pathname.split("/")[2],
      myUserName: getters["userStore/getUserNickname"],
      // 토글버튼
      chatToggle: false,
    };
  },
  unmounted() {
    this.leaveSession();
  },
  methods: {
    joinSession() {
      // --- Get an OpenVidu object ---
      this.OV = new OpenVidu();

      // --- Init a session ---
      this.session = this.OV.initSession();

      // --- Specify the actions when events take place in the session ---

      // On every new Stream received...
      this.session.on("streamCreated", ({ stream }) => {
        const subscriber = this.session.subscribe(stream);
        this.subscribers.push(subscriber);
      });

      // On every Stream destroyed...
      this.session.on("streamDestroyed", ({ stream }) => {
        const index = this.subscribers.indexOf(stream.streamManager, 0);
        if (index >= 0) {
          this.subscribers.splice(index, 1);
        }
      });

      // On every asynchronous exception...
      this.session.on("exception", ({ exception }) => {
        console.warn(exception);
      });

      // --- Connect to the session with a valid user token ---

      // 'getToken' method is simulating what your server-side should do.
      // 'token' parameter should be retrieved and returned by your own backend
      this.getToken(this.mySessionId).then((token) => {
        this.session
          .connect(token, { clientData: this.myUserName })
          .then(() => {
            // --- Get your own camera stream with the desired properties ---

            let publisher = this.OV.initPublisher(undefined, {
              audioSource: undefined, // The source of audio. If undefined default microphone
              videoSource: undefined, // The source of video. If undefined default webcam
              publishAudio: false, // Whether you want to start publishing with your audio unmuted or not
              publishVideo: false, // Whether you want to start publishing with your video enabled or not
              resolution: "320x320", // The resolution of your video
              frameRate: 30, // The frame rate of your video
              insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
              mirror: false, // Whether to mirror your local video or not
            });

            this.mainStreamManager = publisher;
            this.publisher = publisher;

            // --- Publish your stream ---

            this.session.publish(this.publisher);
          })
          .catch((error) => {
            console.log(
              "There was an error connecting to the session:",
              error.code,
              error.message
            );
          });
      });

      window.addEventListener("beforeunload", this.leaveSession);
      this.session.on("signal", (event) => {
        // console.log(event.data); // Message/
        // console.log(event.from); // Connection object of the sender
        // console.log(event.type); // The type of message

        // 여기에 div 찾아서 내용 넣고 올리기
        const dest = document.getElementById("chatting");
        const el = document.createElement("li");
        const chattingDiv = document.createElement("div");
        el.classList.add("chattingLi");
        chattingDiv.classList.add("eaChatting");

        const wrapper = document.getElementById("chatting-wrapper");
        const nameTag = document.createElement("span");
        nameTag.classList.add("nameTag");
        const contentTag = document.createElement("span");

        const nameDiv = document.createElement("div");
        const contentDiv = document.createElement("div");

        nameDiv.classList.add("left");
        nameDiv.setAttribute("style", "text-align:left;");
        contentDiv.classList.add("right");
        contentDiv.classList.add("contentDiv");
        contentDiv.setAttribute("style", "text-align: right;");

        nameTag.innerText = event.from.data.split(":")[1].split('"')[1];
        contentTag.classList.add("contentTag");
        contentTag.innerText = event.data;
        nameDiv.append(nameTag);
        contentDiv.append(contentTag);
        chattingDiv.appendChild(nameDiv);
        chattingDiv.appendChild(contentDiv);
        el.appendChild(chattingDiv);
        dest.appendChild(el);
        wrapper.scrollTop = wrapper.scrollHeight;
      });
    },

    leaveSession() {
      // --- Leave the session by calling 'disconnect' method over the Session object ---
      if (this.session) this.session.disconnect();

      this.session = undefined;
      this.mainStreamManager = undefined;
      this.publisher = undefined;
      this.subscribers = [];
      this.OV = undefined;

      window.removeEventListener("beforeunload", this.leaveSession);
    },

    updateMainVideoStreamManager(stream) {
      if (this.mainStreamManager === stream) return;
      this.mainStreamManager = stream;
    },

    /**
     * --------------------------
     * SERVER-SIDE RESPONSIBILITY
     * --------------------------
     * These methods retrieve the mandatory user token from OpenVidu Server.
     * This behavior MUST BE IN YOUR SERVER-SIDE IN PRODUCTION (by using
     * the API REST, openvidu-java-client or openvidu-node-client):
     *   1) Initialize a Session in OpenVidu Server	(POST /openvidu/api/sessions)
     *   2) Create a Connection in OpenVidu Server (POST /openvidu/api/sessions/<SESSION_ID>/connection)
     *   3) The Connection.token must be consumed in Session.connect() method
     */

    getToken(mySessionId) {
      return this.createSession(mySessionId).then((sessionId) =>
        this.createToken(sessionId)
      );
    },

    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-openviduapisessions
    createSession(sessionId) {
      return new Promise((resolve, reject) => {
        axios
          .post(
            `${OPENVIDU_SERVER_URL}/openvidu/api/sessions`,
            JSON.stringify({
              customSessionId: sessionId,
            }),
            {
              auth: {
                username: "OPENVIDUAPP",
                password: OPENVIDU_SERVER_SECRET,
              },
            }
          )
          .then((response) => response.data)
          .then((data) => resolve(data.id))
          .catch((error) => {
            console.log("errorrororororo", error);
            if (error.response.status === 409) {
              resolve(sessionId);
            } else {
              console.warn(
                `No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}`
              );
              if (
                window.confirm(
                  `No connection to OpenVidu Server. This may be a certificate error at ${OPENVIDU_SERVER_URL}\n\nClick OK to navigate and accept it. If no certificate warning is shown, then check that your OpenVidu Server is up and running at "${OPENVIDU_SERVER_URL}"`
                )
              ) {
                location.assign(`${OPENVIDU_SERVER_URL}/accept-certificate`);
              }
              reject(error.response);
            }
          });
      });
    },

    // See https://docs.openvidu.io/en/stable/reference-docs/REST-API/#post-openviduapisessionsltsession_idgtconnection
    createToken(sessionId) {
      return new Promise((resolve, reject) => {
        axios
          .post(
            `${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`,
            {},
            {
              auth: {
                username: "OPENVIDUAPP",
                password: OPENVIDU_SERVER_SECRET,
              },
            }
          )
          .then((response) => response.data)
          .then((data) => resolve(data.token))
          .catch((error) => reject(error.response));
      });
    },

    toggleVideo() {
      if (this.publishVideo) {
        this.publishVideo = false;
        this.publisher.publishVideo(this.publishVideo);
      } else if (!this.publishVideo) {
        this.publishVideo = true;
        this.publisher.publishVideo(this.publishVideo);
      }
    },
    toggleAudio() {
      if (this.publishAudio) {
        this.publishAudio = false;
        this.publisher.publishAudio(this.publishAudio);
      } else if (!this.publishAudio) {
        this.publishAudio = true;
        this.publisher.publishAudio(this.publishAudio);
      }
    },
    submitChatting() {
      const content = this.message;
      if (this.message == "") return;
      this.message = undefined;
      this.session
        .signal({
          data: content, // Any string (optional)
          to: [], // Array of Connection objects (optional. Broadcast to everyone if empty)
        })
        .then(() => {
          // console.log("Message successfully sent");
        })
        .catch((error) => {
          console.error(error);
        });
    },
    changeChatToggle() {
      if (this.session) {
        if (this.chatToggle == false) {
          this.chatToggle = true;
        } else {
          this.chatToggle = false;
        }
      } else {
        alert("화상채팅을 켜주세요!");
      }
    },
  },
};
</script>

<style scoped>
.box {
  width: 120px;
  height: 120px;
  border-radius: 70%;
  overflow: hidden;
  float: left;
}
#chatting-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  border: none;
  background: #96b7e8ba;
  border-radius: 5px 5px 0px 0px;
  top: 130%;
  right: 10%;
  z-index: 3;
}

.c-btn {
  border: none;
  display: inline-block;
  padding: 5px 10px;
  margin: 5px;
  border-radius: 5px;
  font-family: "paybooc-Light", sans-serif;
  text-decoration: none;
  font-weight: 600;
  transition: 0.25s;
  font-size: 13px;
  width: 100px;
  height: 30px;
}

.w-btn-green2 {
  background-color: #80da52;
  color: white;
}

#chatting {
  list-style: none;
}

.video-ctr-btn {
  background: none;
  border-radius: 5px;
  border: none;
}

#session {
  /* width: 150px; */
  /* height: 150px; */
}

.video-ctr-btn-group {
  position: absolute;
  top: 727px;
  margin-left: 47px;
}

#chattingInput {
  width: 250px;
  background: #afafaf61;
  bottom: 0;
  width: 400px;
  height: 40px;
  border: none;
  border-radius: 0px 0px 5px 5px;
  padding-left: 10px;
  padding-right: 10px;
}

.box {
  margin: 0 20px;
}

.popup-btn {
  background-color: rgb(252, 160, 14);
  color: rgb(84, 52, 4);
}

.chattingPart {
  width: 250px;
  position: absolute;
  right: 0.5vw;
  /* top: 0.3vh; */
  top: 0;
  z-index: 5000;
  opacity: 1;
  background-color: white;
  border-radius: 5px;
  overflow: hidden;
  resize: both;
}
.nameTag {
  /* margin-left: 0px; */
  /* position: absolute; */
  text-align: right;
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: solid black 2px;
  padding-bottom: 2px;
  margin-left: 5px;
  font-size: 12px;
}
.contentTag {
  /* position: absolute;
  left: 50%; */
  text-align: right;
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: solid black 2px;
  padding-bottom: 2px;
  margin-right: 5px;
  font-size: 12px;
}
.eaChatting {
  margin-top: 5px;
  position: relative;
}
.chat-btn {
  position: absolute;
  left: 0;
}
.left {
  text-align: left;
}
.right {
  text-align: right;
}
.contentDiv {
  margin-right: 5px;
}
</style>
