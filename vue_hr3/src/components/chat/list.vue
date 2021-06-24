<template>
  <div id="list">
    <ul class="ulStyle">
      <li v-for="item in hrs"  :class="{ active: item.id === currentSession.id }"
          v-on:click="changeCurrentSession(item)"><!--   :class="[item.id === currentSessionId ? 'active':'']" -->
        <el-badge :is-dot="isDot[currentHr.username+'#'+item.username]"><img class="avatar" :src="item.userface" :alt="item.name"></el-badge>
       <p class="name">{{ item.name }}</p>
      </li>
    </ul>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
  name: 'list',
  data() {
    return {

    }
  },
  computed: mapState([
    'currentSession',
    'currentHr',
    'isDot',
    'hrs'
  ]),
  methods: {
    changeCurrentSession: function (item) {
      this.$store.commit('changeCurrentSession', item)
    }
  }
}
</script>

<style lang="scss" scoped>
img{
  padding: 12px;
}

#list {
  li {
    padding: 19px 15px;
    border-bottom: 1px solid #292C33;
    cursor: pointer;
    list-style: none;
    padding: 0;
    display: flex;
    justify-content: flex-start;
    &:hover {
      background-color: rgba(255, 255, 255, 0.03);
    }
  }

  li.active { /*注意这个是.不是冒号:*/
    background-color: rgba(255, 255, 255, 0.1);
  }

  .avatar {
    border-radius: 2px;
    width: 30px;
    height: 30px;
    vertical-align: middle;
  }

  .name {
    display: inline-block;
    margin-left: 15px;
  }

  .ulStyle {
    padding: 0;
  }
}
</style>
