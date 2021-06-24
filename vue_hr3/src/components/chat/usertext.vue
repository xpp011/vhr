<template>
  <div id="uesrtext">
    <textarea placeholder="按 Ctrl + Enter 发送" v-model="content" v-on:keyup="addMessage"></textarea>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
  name: 'uesrtext',
  data () {
    return {
      content:''
    }
  },
  computed: mapState([
    'currentSession'
  ]),
  methods: {
  	addMessage (e) {
  		if (e.ctrlKey && e.keyCode ===13 && this.content.length) {
  		  let chatMsg=new Object();
  		  //设置发送给谁
  		  chatMsg.to=this.currentSession.username
        //消息体
        chatMsg.content=this.content
        //向服务器/ws/chat接口发送消息                  chatMsg需要序列化传送
  		  this.$store.state.stomp.send('/ws/chat',{},JSON.stringify(chatMsg));
  			this.$store.commit('addMessage',chatMsg);
  			this.content='';
  		}
  	}
  }
}
</script>

<style lang="scss" scoped>
#uesrtext {
	position: absolute;
  bottom: 0;
  right: 0;
  width: 100%;
  height: 30%;
  border-top: solid 1px #DDD;
  > textarea {
  	padding: 10px;
  	width: 100%;
  	height: 100%;
  	border: none;
  	outline: none;
  }
}
</style>
