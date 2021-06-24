import Vue from "vue"
import Vuex from "vuex"
import {getJsonRequest} from "../utils/api";
import '../utils/stomp'
import SockJS from  'sockjs-client';
import '../utils/stomp'
import '../utils/sockjs'
import message from "../components/chat/message";
import { Notification } from 'element-ui';


Vue.use(Vuex)

const now = new Date();


/*vuex*/
const store = new Vuex.Store({
    state: {
        routes: [],
        sessions:{},
        hrs:[],
        currentSession: {
            id: -1
        },
        currentHr:JSON.parse(window.sessionStorage.getItem("user")),
        filterKey:'',
        stomp:null,
        isDot:{}
    },
    mutations: {
        initCurrentHr(state,hr){
            state.currentHr=hr
        },

        initRoutes (state,data) {
            state.routes=data
        },
        changeCurrentSession (state,currentSession) {
            state.currentSession = currentSession;
            Vue.set(state.isDot,state.currentHr.username+'#'+state.currentSession.username,false)
        },
        addMessage (state,msg) {
            let mss=state.sessions[state.currentHr.username+'#'+msg.to];
            if (!mss){
                //通过初始化数组的方式无法让sessions拥有计算属性的效果
                //state.sessions[state.currentHr.username+'#'+msg.to]=[]
                //采用set方式 绑定sessions属性
                Vue.set(state.sessions,state.currentHr.username+'#'+msg.to,[])
            }
            state.sessions[state.currentHr.username+'#'+msg.to].push({
                message:msg.content,
                date:new Date(),
                self:!msg.notSelf
            })
        },
        //刚加载数据时  从本地拿起sessions的值  聊天消息保存
        INIT_DATA (state) {
            let data = localStorage.getItem('vue-chat-session');
            //console.log(data)
            if (data) {
                state.sessions = JSON.parse(data);
            }
        },
        INIT_HRS(state,data){
            state.hrs=data
        }
    },
    actions:{
        connect(context){
            //创建SockJS连接点
            context.state.stomp=Stomp.over(new SockJS('/ws/ep'));
            //建立连接   success连接成功的回调函数
            context.state.stomp.connect({},success=>{
                //接收服务器消息
                //订阅服务器发送消息的URL:/user/queue/chat  一对一的模式必须加前缀/user
                context.state.stomp.subscribe('/user/queue/chat',msg=>{
                    let receiveMsg=JSON.parse(msg.body);
                    if (context.state.currentSession.id==-1 ||  context.state.currentSession.username!= receiveMsg.from) {
                        Notification.info({
                            title: '【' + receiveMsg.fromNickname + '】发来了一条消息',
                            message: receiveMsg.content.length >= 10 ? receiveMsg.content.substring(0, 10) + '...' : receiveMsg.content
                        })
                        Vue.set(context.state.isDot,context.state.currentHr.username+'#'+receiveMsg.from,true)
                    }
                    console.log("接收")
                    receiveMsg.notSelf=true;
                    receiveMsg.to=receiveMsg.from;
                    context.commit("addMessage",receiveMsg)
                })
            },error=>{

            })
        },
        initData (context) {
            context.commit('INIT_DATA')
            getJsonRequest("/chat/hrs").then(resp=>{
                context.commit("INIT_HRS",resp.obj)
            })
        }
    }
    }
)

//监听session的值是否发生变化
store.watch(function (state) {
    return state.sessions
},//一旦发生变化就在本地的localStorage存储sessions的值
    function (val) {
    //console.log('CHANGE: ', val);
    localStorage.setItem('vue-chat-session', JSON.stringify(val));
},{
    deep:true/*这个貌似是开启watch监测的判断,官方说明也比较模糊*/
})


export default store;